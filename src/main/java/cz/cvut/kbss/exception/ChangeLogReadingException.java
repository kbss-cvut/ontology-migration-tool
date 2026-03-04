package cz.cvut.kbss.exception;

/**
 * Indicates that the changelog file could not be found or read.
 */
public class ChangeLogReadingException extends OntologyMigrationToolException {

    public ChangeLogReadingException(String message) {
        super(message);
    }

    public ChangeLogReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
