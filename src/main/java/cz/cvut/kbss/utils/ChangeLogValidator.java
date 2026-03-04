package cz.cvut.kbss.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import cz.cvut.kbss.exception.ChangeLogReadingException;
import cz.cvut.kbss.exception.ChangeLogValidationException;
import cz.cvut.kbss.exception.OntologyMigrationToolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ChangeLogValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeLogValidator.class);

    private final JsonSchema jsonSchema;

    public ChangeLogValidator() {
        try (InputStream schema = ChangeLogValidator.class.getResourceAsStream("/changelog-scheme.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(schema);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            this.jsonSchema = factory.getSchema(node);
        } catch (IOException e) {
            throw new OntologyMigrationToolException("Unable to load changelog schema for changelog validation.");
        }
    }

    public void validate(String input) {
        LOG.debug("Validating changelog against schema.");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            final JsonNode changeLogNode = mapper.readTree(input);
            Set<ValidationMessage> errors = jsonSchema.validate(changeLogNode);
            if (!errors.isEmpty()) {
                throw new ChangeLogValidationException("ChangeLogValidation error", errors);
            }
        } catch (JsonProcessingException e) {
            throw new ChangeLogReadingException("Unable to read changelog for validation.", e);
        }
    }
}
