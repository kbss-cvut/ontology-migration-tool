package cz.cvut.fel.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jMigrationLogger implements MigrationLogger {
    private static final Logger log = LoggerFactory.getLogger(Slf4jMigrationLogger.class);
    @Override
    public void logStart() {
        log.info("Start migration.");
    }

    @Override
    public void logEnd(boolean success) {
        log.info("Migration finished: {}", success ? "SUCCESS" : "FAILURE");
    }

    @Override
    public void logChangeSet(String changeSet) {
        log.info("Applying ChangeSet: {}", changeSet);
    }

    @Override
    public void logChange(String change, String msg) {
        log.debug("Change type: {}, {}", change, msg);
    }

    @Override
    public void logSkip(String changeSet) {
        log.info("Skipping already applied changeSet: {}", changeSet);
    }

    @Override
    public void logError(String message, Exception e) {
        log.error(message, e);
    }
}
