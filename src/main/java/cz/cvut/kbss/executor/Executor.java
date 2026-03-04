package cz.cvut.kbss.executor;

import cz.cvut.kbss.exceptions.MigrationExecutionException;
import cz.cvut.kbss.logger.MigrationLogger;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.model.ChangeSet;
import cz.cvut.kbss.model.changes.Change;
import cz.cvut.kbss.repository.OntologyRepository;
import cz.cvut.kbss.versioning.VersionManager;


public class Executor {
    private final OntologyRepository repository;
    private final VersionManager versionManager;
    private final MigrationLogger logger;

    public Executor(OntologyRepository repository, MigrationLogger logger) {
        this.repository = repository;
        this.versionManager = new VersionManager(repository);
        this.logger = logger;
    }

    public void execute(ChangeLog changeLog) {
        logger.logStart();
        repository.begin();
        try {
            for (ChangeSet changeSet : changeLog.getChangeSets()) {
                if(versionManager.isApplied(changeSet.getId())){
                    logger.logSkip(changeSet.getId());
                    continue;
                }
                logger.logChangeSet(changeSet.getId());
                for (Change change : changeSet.getChanges()) {
                    logger.logChange(change.getType(), change.getLogMessage());
                    String sparql = change.apply(repository);
                    repository.update(sparql);
                }
                versionManager.markApplied(changeSet.getId());
            }
            repository.commit();
        } catch (Exception e) {
            logger.logError("MIGRATION ERROR", e);
            logger.logEnd(false);
            throw new MigrationExecutionException("Failed to execute migration", e);
        }
    logger.logEnd(true);

    }

}
