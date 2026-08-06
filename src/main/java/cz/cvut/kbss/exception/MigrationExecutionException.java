package cz.cvut.kbss.exception;

public class MigrationExecutionException extends OntologyMigrationToolException {
    public MigrationExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationExecutionException(String message) {
        super(message);
    }
}
