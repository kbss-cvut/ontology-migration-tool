package integration;

import cz.cvut.kbss.executor.Executor;
import cz.cvut.kbss.logger.MigrationLogger;
import cz.cvut.kbss.logger.Slf4jMigrationLogger;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.model.ChangeSet;
import cz.cvut.kbss.model.changes.AddClassChange;
import cz.cvut.kbss.model.changes.AddPropertyChange;
import cz.cvut.kbss.model.changes.AddResourceChange;
import cz.cvut.kbss.model.changes.DeleteResourceChange;
import cz.cvut.kbss.model.changes.RenameResourceChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MigrationTest {
    private TestRepository repository;
    private Executor executor;

    @BeforeEach
    void setUp() {
        repository = new TestRepository();
        MigrationLogger logger = new Slf4jMigrationLogger();
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
        renameChange.setOldIri("http://ex/old");
        renameChange.setNewIri("http://ex/new");
        cs3.setChanges(List.of(renameChange));
        ChangeLog log = new ChangeLog();
        log.setChangeSets(List.of(cs1, cs2, cs3));
        executor.execute(log);
        assertFalse(repository.getUpdates().isEmpty());
        assertTrue(repository.getUpdates().stream().anyMatch(s -> s.contains("http://ex/r1")));
        assertTrue(repository.getUpdates().stream().anyMatch(s -> s.contains("http://ex/C1")));
        assertTrue(repository.getUpdates().stream().anyMatch(s ->
                                                                     s.contains(
                                                                             "<http://ex/s1> <http://ex/p1> <http://ex/o1>")));
        assertTrue(repository.getUpdates().stream().anyMatch(s -> s.contains("http://ex/old")));
    }
}
