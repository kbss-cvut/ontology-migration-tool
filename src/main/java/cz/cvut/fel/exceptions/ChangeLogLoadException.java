package cz.cvut.fel.exceptions;

public class ChangeLogLoadException extends RuntimeException {
    public ChangeLogLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
