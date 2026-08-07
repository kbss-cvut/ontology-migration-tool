package cz.cvut.kbss.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.BaseJsonValidator;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import cz.cvut.kbss.exception.ChangeLogReadingException;
import cz.cvut.kbss.exception.ChangeLogValidationException;
import cz.cvut.kbss.exception.IdentifierNotUniqueException;
import cz.cvut.kbss.exception.OntologyMigrationToolException;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.model.ChangeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeLogValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeLogValidator.class);

    /**
     * YAML object mapper
     */
    private final ObjectMapper yamlMapper;

    /**
     * Root JSON Schema for full ChangeLog validation
     */
    private final JsonSchema jsonSchema;

    /**
     * JSON sub-Schema for validating only a single ChangeSet
     */
    private final JsonSchema changeSetSchema;

    /**
     * @param yamlMapper {@link ObjectMapper} capable of loading YAML
     * @see com.fasterxml.jackson.dataformat.yaml.YAMLFactory
     */
    public ChangeLogValidator(ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
        this.jsonSchema = loadRootSchema();
        this.changeSetSchema = getChangeSetSchema(jsonSchema);
    }

    /**
     * Loads the root JSON schema
     *
     * @return the loaded root JSON Schema
     */
    private static JsonSchema loadRootSchema() {
        try (InputStream schema = ChangeLogValidator.class.getResourceAsStream("/changelog-scheme.json")) {
            // using new ObjectMapper to load JSON
            JsonNode node = new ObjectMapper().readTree(schema);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            return factory.getSchema(node);
        } catch (IOException e) {
            throw new OntologyMigrationToolException("Unable to load changelog schema for changelog validation.");
        }
    }

    /**
     * Retrieves the JSON sub-Schema for validating a single ChangeSetWrapper
     *
     * @param rootSchema the root JSON Schema
     * @return the ChangeSet sub-Schema
     */
    private static JsonSchema getChangeSetSchema(JsonSchema rootSchema) {
        return rootSchema.getRefSchema(
                BaseJsonValidator.JsonNodePathJsonPointer.getInstance()
                        .append("$defs")
                        .append("changeSetWrapper")
        );
    }

    /**
     * Validates the given ChangeLog
     *
     * @param input YAML serialized ChangeLog to validate
     * @return parsed validated ChangeLog
     */
    public JsonNode validate(String input) {
        LOG.debug("Validating changelog against schema.");
        return validate(input, jsonSchema);
    }

    /**
     * Validates a single ChangeSet
     *
     * @param input YAML serialized ChangeSet to validate
     * @return parsed validated ChangeSet
     */
    public JsonNode validateChangeSet(String input) {
        LOG.debug("Validating change set against schema.");
        return validate(input, changeSetSchema);
    }

    /**
     * Validates the given input with the given {@link JsonSchema}.
     *
     * @param input YAML serialized data to validate
     * @param schema JSON Schema to use for validation
     * @return parsed validated input
     */
    private JsonNode validate(String input, JsonSchema schema) {
        try {
            final JsonNode jsonNode = yamlMapper.readTree(input);
            Set<ValidationMessage> errors = schema.validate(jsonNode);
            if (!errors.isEmpty()) {
                throw new ChangeLogValidationException("ChangeLogValidation error", errors);
            }
            return jsonNode;
        } catch (JsonProcessingException e) {
            throw new ChangeLogReadingException("Unable to read input for validation.", e);
        }
    }

    /**
     * Validate deserialized changelog
     *
     * @param changeLog changelog to validate
     */
    public void validate(ChangeLog changeLog) {
        ensureChangeSetIdsUnique(changeLog);
    }

    /**
     * Ensures that each {@link ChangeSet#id} is unique within the changelog
     * @param changeLog changelog to iterate
     */
    private void ensureChangeSetIdsUnique(ChangeLog changeLog) {
        List<ChangeSet> changeSets = changeLog.getChangeSets();
        Set<String> ids = new HashSet<>();

        for (ChangeSet changeSet : changeSets) {
            if (!ids.add(changeSet.getId())) {
                throw new IdentifierNotUniqueException("ChangeSet ID is not unique: " + changeSet.getId());
            }
        }
    }
}
