package cz.cvut.kbss.logger;

public interface MigrationLogger {
    void logStart();
    void logEnd(boolean success);
    void logChangeSet(String changeSet);
    void logChange(String change, String msg);
    void logSkip(String changeSet);
    void logError(String message, Exception e);
}
