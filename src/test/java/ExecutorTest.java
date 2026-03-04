import cz.cvut.fel.executor.Executor;
import cz.cvut.fel.logger.MigrationLogger;
import cz.cvut.fel.model.ChangeLog;
import cz.cvut.fel.model.ChangeSet;
import cz.cvut.fel.model.changes.*;
import cz.cvut.fel.repository.OntologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;

public class ExecutorTest {
    private OntologyRepository repository;
    private MigrationLogger logger;
    private Executor executor;

    @BeforeEach
    void setUp() {
        repository = mock(OntologyRepository.class);
        logger = mock(MigrationLogger.class);
        executor = new Executor(repository, logger);
    }

    @Test
    void testAddResourceChange() {
        AddResourceChange change = new AddResourceChange("http://ex/r1", "http://ex/C1", "Label1");
        String sparql = change.apply(repository);
        assertTrue(sparql.contains("INSERT DATA"));
        assertTrue(sparql.contains("<http://www.w3.org/2000/01/rdf-schema#label>"));
        assertTrue(sparql.contains("Label1"));
        assertTrue(change.getLogMessage().contains("r1"));
    }

    @Test
    void testDeleteResourceChange() {
        DeleteResourceChange change = new DeleteResourceChange("http://ex/r2");
        String sparql = change.apply(repository);
        assertTrue(sparql.contains("DELETE WHERE"));
        assertTrue(change.getLogMessage().contains("r2"));
    }

    @Test
    void testRenameResourceChange() {
        RenameResourceChange change = new RenameResourceChange();
        change.setOldName("http://ex/old");
        change.setNewName("http://ex/new");
        String sparql = change.apply(repository);
        assertTrue(sparql.contains("DELETE"));
        assertTrue(sparql.contains("INSERT"));
        assertTrue(change.getLogMessage().contains("old"));
    }

    @Test
    void testAddClassChange() {
        AddClassChange change = new AddClassChange("http://ex/Class2", "ClassLabel", null);
        String sparql = change.apply(repository);
        assertTrue(sparql.contains("INSERT DATA"));
        assertTrue(sparql.contains("<http://www.w3.org/2000/01/rdf-schema#label>"));
        assertTrue(change.getLogMessage().contains("Class2"));
    }

    @Test
    void testAddPropertyChange() {
        AddPropertyChange change = new AddPropertyChange(
                "http://ex/p", "http://ex/o", "http://ex/s", null
        );
        String sparql = change.apply(repository);
        assertTrue(sparql.contains("INSERT DATA"));
        assertTrue(sparql.contains("<http://ex/s> <http://ex/p> <http://ex/o>"));
        assertTrue(change.getLogMessage().contains("p"));
    }

    @Test
    void testSparqlUpdateChange() {
        SparqlUpdateChange change = new SparqlUpdateChange("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o }");
        String sparql = change.apply(repository);
        assertTrue(sparql.contains("DELETE"));
        assertTrue(change.getLogMessage().contains("DELETE"));
    }

    @Test
    void testExecutorWithAllChanges() {
        List<Change> changes = List.of(
                new AddResourceChange("http://ex/r1", "http://ex/C1", "Label1"),
                new DeleteResourceChange("http://ex/r2"),
                createRenameChange("http://ex/old", "http://ex/new"),
                new AddClassChange("http://ex/C2", "Label2", null),
                new AddPropertyChange("http://ex/p", "http://ex/o", "http://ex/s", null),
                new SparqlUpdateChange("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o }")
        );
        ChangeSet changeSet = new ChangeSet("cs-1");
        changeSet.setChanges(changes);
        ChangeLog log = new ChangeLog();
        log.setChangeSets(List.of(changeSet));
        when(repository.ask(anyString())).thenReturn(false);
        executor.execute(log);
        verify(repository).begin();
        verify(repository, atLeast(changes.size())).update(anyString());
        verify(repository).commit();
    }

    private RenameResourceChange createRenameChange(String oldName, String newName) {
        RenameResourceChange change = new RenameResourceChange();
        change.setOldName(oldName);
        change.setNewName(newName);
        return change;
    }
}
