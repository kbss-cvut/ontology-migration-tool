package cz.cvut.kbss.exception;

/**
 * Exception indicating an invalid custom change.
 */
public class InvalidCustomChangeException extends OntologyMigrationToolException {

    public InvalidCustomChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
