package integration;

import cz.cvut.fel.executor.Executor;
import cz.cvut.fel.logger.MigrationLogger;
import cz.cvut.fel.logger.Slf4jMigrationLogger;
import cz.cvut.fel.model.ChangeLog;
import cz.cvut.fel.model.ChangeSet;
import cz.cvut.fel.model.changes.*;
import cz.cvut.fel.repository.OntologyRepository;
import cz.cvut.fel.versioning.VersionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class MigrationTest {
    private TestRepository repository;
    private VersionManager versionManager;
    private Executor executor;
    private MigrationLogger logger;

    @BeforeEach
    void setUp() {
        repository = new TestRepository();
        versionManager = new VersionManager(repository);
        logger  = new Slf4jMigrationLogger();
        executor = new Executor(repository, logger);
    }

    @Test
    void applyFullMigration() {
        ChangeSet cs1 = new ChangeSet("cs-1");
        cs1.setChanges(List.of(
                new AddResourceChange("http://ex/r1", "http://ex/C1", "Label1"),
                new AddClassChange("http://ex/C1", "ClassLabel1", null)
        ));
        ChangeSet cs2 = new ChangeSet("cs-2");
        cs2.setChanges(List.of(
                new AddPropertyChange("http://ex/p1", "http://ex/o1",
                        "http://ex/s1", null),
                new DeleteResourceChange("http://ex/r2")
        ));
        ChangeSet cs3 = new ChangeSet("cs-3");
        RenameResourceChange renameChange = new RenameResourceChange();
        renameChange.setOldName("http://ex/old");
        renameChange.setNewName("http://ex/new");
        cs3.setChanges(List.of(renameChange));
        ChangeLog log = new ChangeLog();
        log.setChangeSets(List.of(cs1, cs2, cs3));
        executor.execute(log);
        assertFalse(repository.getUpdates().isEmpty(), "No SPARQL updates applied");
        assertTrue(repository.getUpdates().stream().anyMatch(s -> s.contains("http://ex/r1")),
                "AddResourceChange not applied");
        assertTrue(repository.getUpdates().stream().anyMatch(s -> s.contains("http://ex/C1")),
                "AddClassChange not applied");
        assertTrue(repository.getUpdates().stream().anyMatch(s ->
                s.contains("<http://ex/s1> <http://ex/p1> <http://ex/o1>")), "AddPropertyChange not applied");
        assertTrue(repository.getUpdates().stream().anyMatch(s -> s.contains("http://ex/old")),
                "RenameResourceChange not applied");
        executor.execute(log);
        assertEquals(repository.getUpdates().size(), repository.getUpdates().size(),
                "Duplicate changes applied");
    }
}
