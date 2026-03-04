package cz.cvut.fel.runner;

import cz.cvut.fel.exceptions.ChangeLogValidationException;
import cz.cvut.fel.executor.Executor;
import cz.cvut.fel.loader.ChangeLogLoader;
import cz.cvut.fel.logger.Slf4jMigrationLogger;
import cz.cvut.fel.logger.MigrationLogger;
import cz.cvut.fel.model.ChangeLog;
import cz.cvut.fel.repository.OntologyRepository;
import cz.cvut.fel.repository.RepositoryFactory;

import java.io.IOException;

public class MigrationRunner {
    private final OntologyRepository repo;
    private final ChangeLogLoader loader;
    private final MigrationLogger logger;

    public MigrationRunner(String type, String endpoint, String username, String password) throws IOException {
        this.repo = RepositoryFactory.createRepository(type, endpoint, username, password);
        this.loader = new ChangeLogLoader();
        this.logger = new Slf4jMigrationLogger();
    }

    public void run() throws IOException, ChangeLogValidationException {
        try {
            ChangeLog changeLog = loader.loadFromResource();
            Executor executor = new Executor(repo, logger);
            executor.execute(changeLog);
        } catch (ChangeLogValidationException e) {
            logger.logError("Cannot load changelog", e);
        }
    }

}