package cz.cvut.kbss.runner;

import cz.cvut.kbss.exception.ChangeLogValidationException;
import cz.cvut.kbss.exception.OntologyMigrationToolException;
import cz.cvut.kbss.executor.Executor;
import cz.cvut.kbss.loader.ChangeLogLoader;
import cz.cvut.kbss.logger.MigrationLogger;
import cz.cvut.kbss.logger.Slf4jMigrationLogger;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.repository.OntologyRepository;
import cz.cvut.kbss.repository.RepositoryFactory;

import java.util.Objects;

public class MigrationRunner {

    private final OntologyRepository repo;
    private final ChangeLogLoader loader;
    private final MigrationLogger logger;

    private MigrationRunner(String repositoryUrl, String username, String password, String changelogFile) {
        this.repo = RepositoryFactory.createRepository(repositoryUrl, username, password);
        this.loader = new ChangeLogLoader(changelogFile);
        this.logger = new Slf4jMigrationLogger();
    }

    public void run() {
        try {
            ChangeLog changeLog = loader.loadChangelog();
            Executor executor = new Executor(repo, logger);
            executor.execute(changeLog);
        } catch (ChangeLogValidationException e) {
            logger.logError("Changelog invalid. Errors: " + e.getErrors(), e);
            throw new OntologyMigrationToolException(e);
        } finally {
            repo.close();
        }
    }

    public static MigrationRunnerBuilder repository(String repositoryUrl) {
        return new MigrationRunnerBuilder(repositoryUrl);
    }

    public static class MigrationRunnerBuilder {
        private final String repositoryUrl;
        private String username;
        private String password;
        private String changelogFile = ChangeLogLoader.DEFAULT_CHANGELOG_FILE;

        private MigrationRunnerBuilder(String repositoryUrl) {
            this.repositoryUrl = Objects.requireNonNull(repositoryUrl);
        }

        public MigrationRunnerBuilder username(String username) {
            this.username = username;
            return this;
        }

        public MigrationRunnerBuilder password(String password) {
            this.password = password;
            return this;
        }

        public MigrationRunnerBuilder changelogFile(String changelogFile) {
            this.changelogFile = changelogFile;
            return this;
        }

        public MigrationRunner build() {
            return new MigrationRunner(repositoryUrl, username, password, changelogFile);
        }
    }
}