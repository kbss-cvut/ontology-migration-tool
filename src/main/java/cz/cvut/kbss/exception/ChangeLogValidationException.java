package cz.cvut.kbss.exception;

import com.networknt.schema.ValidationMessage;

import java.util.Set;

public class ChangeLogValidationException extends OntologyMigrationToolException {

    private final Set<ValidationMessage> errors;

    public ChangeLogValidationException(String message, Set<ValidationMessage> errors) {
        super(message);
        this.errors = errors;
    }

    public Set<ValidationMessage> getErrors() {
        return errors;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage())
                .append(" Errors:\n");
        for (ValidationMessage error : errors) {
            sb.append(" - ").append(error.getMessage()).append("\n");
        }
        return sb.toString();
    }
}
