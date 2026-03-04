package cz.cvut.fel.exceptions;

public class MigrationExecutionException extends RuntimeException {
    public MigrationExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
