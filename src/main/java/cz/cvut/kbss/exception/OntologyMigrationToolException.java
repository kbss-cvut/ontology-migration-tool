package cz.cvut.kbss.exception;

public class OntologyMigrationToolException extends RuntimeException {

    public OntologyMigrationToolException(String message) {
        super(message);
    }

    public OntologyMigrationToolException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyMigrationToolException(Throwable cause) {
        super(cause);
    }
}
