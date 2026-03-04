package cz.cvut.kbss.loader;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.cvut.kbss.exception.ChangeLogReadingException;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.utils.ChangeLogValidator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ChangeLogLoader {
    /**
     * Default changelog file name and location.
     * <p>
     * It is assumed the file is on classpath.
     */
    public static final String DEFAULT_CHANGELOG_FILE = "changelog.yaml";

    private final String changelogFile;

    private final ChangeLogValidator validator;

    public ChangeLogLoader(String changelogFile) {
        this.changelogFile = changelogFile;
        this.validator = new ChangeLogValidator();
    }

    private ChangeLog load(InputStream input) throws IOException {
        final String changelogStr = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        validator.validate(changelogStr);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(changelogStr, ChangeLog.class);
    }

    public ChangeLog loadChangelog() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(changelogFile)) {
            if (is == null) {
                throw new ChangeLogReadingException("Could not find changelog.yaml");
            }
            return load(is);
        } catch (IOException e) {
            throw new ChangeLogReadingException("Unable to load changelog.", e);
        }
    }
}