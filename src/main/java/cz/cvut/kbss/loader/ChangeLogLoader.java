package cz.cvut.kbss.loader;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.cvut.kbss.exception.ChangeLogReadingException;
import cz.cvut.kbss.model.AdditionalFileType;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.model.FileInclude;
import cz.cvut.kbss.utils.ChangeLogValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

public class ChangeLogLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ChangeLogLoader.class);

    /**
     * Default changelog file name and location.
     * <p>
     * It is assumed the file is on classpath.
     */
    public static final String DEFAULT_CHANGELOG_FILE = "changelog.yaml";

    private final String changelogFile;

    private final ChangeLogValidator validator;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public ChangeLogLoader(String changelogFile) {
        this.changelogFile = changelogFile;
        this.validator = new ChangeLogValidator(yamlMapper);
    }

    /**
     * Loads and validates the {@link #changelogFile}
     * @return the loaded {@link ChangeLog}
     */
    public ChangeLog loadChangelog() {
        LOG.debug("Loading root changelog: {}", changelogFile);
        final JsonNode validData = parseAndValidateChangelog(loadClassPathFileContents(changelogFile),
                Path.of(changelogFile));
        unpackWrappers(validData);
        ChangeLog result = yamlMapper.convertValue(validData, ChangeLog.class);
        validator.validate(result);
        return result;
    }

    /**
     * Replaces ChangeSetWrappers with direct ChangeSet objects.
     *
     * @param changelog flattened changelog to process
     */
    private void unpackWrappers(JsonNode changelog) {
        ArrayNode changeSets = (ArrayNode) changelog.get(AdditionalFileType.CHANGELOG.getTopLevelKey());
        for (int i = 0; i < changeSets.size(); i++) {
            JsonNode wrapper = changeSets.get(i);
            changeSets.set(i, wrapper.get(AdditionalFileType.CHANGESET.getTopLevelKey()));
        }
    }

    /**
     * Validates and loads the given content of a change log file.
     * <p>
     * Processes changeSet and fileInclude wrappers and loads included files,
     * changeSet wrappers are kept in place.
     *
     * @param content the content of the changelog file
     * @param changelogPath the path to the changelog file for relative path resolving
     * @return validated changelog with included changelogs, wrappers were preserved
     */
    private JsonNode parseAndValidateChangelog(String content, Path changelogPath) {
        LOG.debug("Loading changelog file {}", changelogPath);
        final JsonNode changelog = validator.validate(content);
        final ArrayNode entries = (ArrayNode) changelog.get(AdditionalFileType.CHANGELOG.getTopLevelKey());
        for (int i = 0; i < entries.size(); i++) {
            final JsonNode wrapper = entries.get(i);
            if (wrapper.has(AdditionalFileType.CHANGESET.getTopLevelKey())) {
                entries.set(i, wrapper);
            } else if (wrapper.has("include")) {
                // load additional file, can be either changelog or changeset
                loadAndValidateIncludedFile(entries, i, changelogPath);
            } else {
                StringBuilder sb = new StringBuilder("Unknown changelog entry with fields: ");
                Iterator<String> fieldNameIt = wrapper.fieldNames();
                while (fieldNameIt.hasNext()) {
                    sb.append(fieldNameIt.next());
                    sb.append("; ");
                }
                throw new ChangeLogReadingException(sb.toString());
            }
        }
        return changelog;
    }

    private void loadAndValidateIncludedFile(ArrayNode changelogEntries, int index, Path changelogPath) {
        final JsonNode includeWrapper = changelogEntries.get(index);
        final FileInclude include = yamlMapper.convertValue(includeWrapper.get("include"), FileInclude.class);
        final Path newPath = include.relativeToChangelogFile() ?
                changelogPath.getParent().resolve(include.file()) :
                Path.of(include.file());

        final String contents = loadClassPathFileContents(newPath.toString());
        switch (resolveFileContentType(contents, newPath)) {
            case CHANGESET ->  {
                JsonNode changeSet = parseAndValidateChangeSet(contents, newPath);
                // replacing the original include wrapper
                changelogEntries.set(index, changeSet);
            }
            case CHANGELOG ->  {
                ArrayNode includedChangelog = (ArrayNode) parseAndValidateChangelog(contents, newPath)
                        .get(AdditionalFileType.CHANGELOG.getTopLevelKey());

                // inserting all entries from the included changelog into the current one
                // replacing the original include wrapper
                insertAtIndex(changelogEntries, index, includedChangelog);
            }
        };
    }

    /**
     * Inserts all items from {@code toInsert} into {@code array} <b>replacing the item at the given index</b>.
     *
     * @param array array to which elements should be inserted
     * @param index index in {@code array} at which elements should be inserted
     * @param toInsert elements that should be inserted into {@code array}
     */
    private void insertAtIndex(ArrayNode array, int index, ArrayNode toInsert) {
        ArrayList<JsonNode> newChildren = new ArrayList<>(array.size() + toInsert.size());
        for (int i = 0; i < index; i++) {
            newChildren.add(array.get(i));
        }
        for (JsonNode node : toInsert) {
            newChildren.add(node);
        }
        // + 1 not inserting the original element at the index
        for (int i = index + 1; i < array.size(); i++) {
            newChildren.add(array.get(i));
        }
        array.removeAll();
        array.addAll(new ArrayNode(yamlMapper.getNodeFactory(), newChildren));
    }

    /**
     * Probe the given file contents and resolve {@link AdditionalFileType} based on first field name.
     * <p>
     * Starts parsing file contents until first field name is reached.
     *
     * @param fileContents file contents to probe
     * @param filePath file path to report in exception when type is not found
     * @return resolved {@link AdditionalFileType}
     */
    private AdditionalFileType resolveFileContentType(String fileContents, Path filePath) {
        try (JsonParser parser = yamlMapper.getFactory().createParser(fileContents)) {
            while (parser.nextToken() != null) {
                if (parser.currentToken() == JsonToken.FIELD_NAME) {
                    String fieldName = parser.currentName();
                    return AdditionalFileType.forKey(fieldName);
                }
            }
        } catch (IOException e) {
            throw new ChangeLogReadingException("Error occurred while parsing YAML/JSON file", e);
        }
        throw new ChangeLogReadingException("Unable to determine included file type: " + filePath.toString());
    }

    private JsonNode parseAndValidateChangeSet(String contents, Path path) {
        LOG.debug("Loading change set file {}", path);
        return validator.validateChangeSet(contents);
    }


    /**
     * Loads contents of a file on class path.
     *
     * @param classPathFileName the name of the file on classpath
     * @return contents from the file
     */
    private String loadClassPathFileContents(String classPathFileName) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathFileName)) {
            if (is == null) {
                throw new ChangeLogReadingException("Could not find file on classpath: " + classPathFileName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ChangeLogReadingException("Unable to load changelog.", e);
        }
    }
}