package cz.cvut.kbss.executor;

import cz.cvut.kbss.exception.MigrationExecutionException;
import cz.cvut.kbss.logger.MigrationLogger;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.model.ChangeSet;
import cz.cvut.kbss.model.change.Change;
import cz.cvut.kbss.repository.TransactionalRepository;
import cz.cvut.kbss.versioning.VersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Executor {

    private static final Logger LOG = LoggerFactory.getLogger(Executor.class);

    private final TransactionalRepository repository;
    private final VersionManager versionManager;
    private final MigrationLogger logger;

    public Executor(TransactionalRepository repository, MigrationLogger logger) {
        this.repository = repository;
        this.versionManager = new VersionManager(repository);
        this.logger = logger;
    }

    public void execute(ChangeLog changeLog) {
        logger.logStart();
        repository.begin();
        int counter = 0;
        try {
            for (ChangeSet changeSet : changeLog.getChangeSets()) {
                if (versionManager.isApplied(changeSet.getId())) {
                    logger.logSkip(changeSet.getId());
                    continue;
                }
                logger.logChangeSet(changeSet.getId());
                for (Change change : changeSet.getChanges()) {
                    logger.logChange(change.getType(), change.getLogMessage());
                    change.apply(repository);
                }
                counter++;
                versionManager.markApplied(changeSet.getId());
            }
            repository.commit();
        } catch (Exception e) {
            logger.logError("MIGRATION ERROR", e);
            logger.logFailed();
            throw new MigrationExecutionException("Failed to execute migration", e);
        }
        logger.logSuccessfulFinish(counter);
    }

}
