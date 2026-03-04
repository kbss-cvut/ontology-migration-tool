package cz.cvut.fel.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import cz.cvut.fel.exceptions.ChangeLogValidationException;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ChangeLogValidator {
    private final JsonSchema jsonSchema;

    public ChangeLogValidator() throws IOException {
        InputStream schema = ChangeLogValidator.class.getResourceAsStream("/changelog-scheme.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(schema);
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        this.jsonSchema = factory.getSchema(node);
    }

    public void validate(InputStream input) throws IOException, ChangeLogValidationException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode changeLogNode = mapper.readTree(input);
        Set<ValidationMessage>  errors = jsonSchema.validate(changeLogNode);
        if(!errors.isEmpty()) {
            throw new ChangeLogValidationException("ChangeLogValidation error", errors);
        }


    }


}
