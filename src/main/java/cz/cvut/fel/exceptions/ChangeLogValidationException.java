package cz.cvut.fel.exceptions;

import com.networknt.schema.ValidationMessage;

import java.util.Set;

public class ChangeLogValidationException extends Exception {
    private final Set<ValidationMessage> errors;
    public ChangeLogValidationException(String message, Set<ValidationMessage> errors) {
        super(message);
        this.errors = errors;
    }
    public Set<ValidationMessage> getErrors() {
        return errors;
    }

}
