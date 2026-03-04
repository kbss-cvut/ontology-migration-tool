package cz.cvut.kbss.loader;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.cvut.kbss.exceptions.ChangeLogValidationException;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.utils.ChangeLogValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChangeLogLoader {
    private static final String CHANGELOG_PATH = "changelog.yaml";
    private final ChangeLogValidator validator;

    public ChangeLogLoader() throws IOException {
        this.validator = new ChangeLogValidator();
    }
    public ChangeLog load(InputStream input) throws IOException, ChangeLogValidationException {

        byte[] data = input.readAllBytes();
        validator.validate(new ByteArrayInputStream(data));
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new ByteArrayInputStream(data), ChangeLog.class);
    }

    public ChangeLog loadFromResource() throws IOException, ChangeLogValidationException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CHANGELOG_PATH);
        if(is == null){
            throw new IOException("Could not load changelog.yaml");
        }
        return load(is);
    }
}