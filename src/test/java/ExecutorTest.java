import cz.cvut.kbss.executor.Executor;
import cz.cvut.kbss.logger.Slf4jMigrationLogger;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.model.ChangeSet;
import cz.cvut.kbss.model.changes.AddClassChange;
import cz.cvut.kbss.model.changes.AddPropertyChange;
import cz.cvut.kbss.model.changes.AddResourceChange;
import cz.cvut.kbss.model.changes.Change;
import cz.cvut.kbss.model.changes.DeleteResourceChange;
import cz.cvut.kbss.model.changes.ImportFromUrlChange;
import cz.cvut.kbss.model.changes.RenameResourceChange;
import cz.cvut.kbss.model.changes.SparqlUpdateChange;
import integration.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExecutorTest {
    private TestRepository repository;
    private Executor executor;

    @BeforeEach
    void setUp() {
        repository = spy(new TestRepository());
        executor = new Executor(repository, new Slf4jMigrationLogger());
    }

    @Test
    void testAddResourceChange() {
        AddResourceChange change = new AddResourceChange("http://ex/r1", "http://ex/C1", "Label1");
        repository.begin();
        change.apply(repository);
        assertEquals(1, repository.getUpdates().size());
        final String sparql = repository.getUpdates().get(0);
        assertTrue(sparql.contains("INSERT DATA"));
        assertTrue(sparql.contains("<http://www.w3.org/2000/01/rdf-schema#label>"));
        assertTrue(sparql.contains("Label1"));
        assertTrue(change.getLogMessage().contains("r1"));
    }

    @Test
    void testDeleteResourceChange() {
        DeleteResourceChange change = new DeleteResourceChange("http://ex/r2");
        repository.begin();
        change.apply(repository);
        assertEquals(1, repository.getUpdates().size());
        final String sparql = repository.getUpdates().get(0);
        assertTrue(sparql.contains("DELETE WHERE"));
        assertTrue(change.getLogMessage().contains("r2"));
    }

    @Test
    void testRenameResourceChange() {
        RenameResourceChange change = new RenameResourceChange();
        change.setOldIri("http://ex/old");
        change.setNewIri("http://ex/new");
        repository.begin();
        change.apply(repository);
        assertEquals(1, repository.getUpdates().size());
        final String sparql = repository.getUpdates().get(0);
        assertTrue(sparql.contains("DELETE"));
        assertTrue(sparql.contains("INSERT"));
        assertTrue(change.getLogMessage().contains("old"));
    }

    @Test
    void testAddClassChange() {
        AddClassChange change = new AddClassChange("http://ex/Class2", "ClassLabel", null);
        repository.begin();
        change.apply(repository);
        assertEquals(1, repository.getUpdates().size());
        final String sparql = repository.getUpdates().get(0);
        assertTrue(sparql.contains("INSERT DATA"));
        assertTrue(sparql.contains("<http://www.w3.org/2000/01/rdf-schema#label>"));
        assertTrue(change.getLogMessage().contains("Class2"));
    }

    @Test
    void testAddPropertyChange() {
        AddPropertyChange change = new AddPropertyChange(
                "http://ex/p", "http://ex/o", "http://ex/s", null
        );
        repository.begin();
        change.apply(repository);
        assertEquals(1, repository.getUpdates().size());
        final String sparql = repository.getUpdates().get(0);
        assertTrue(sparql.contains("INSERT DATA"));
        assertTrue(sparql.contains("<http://ex/s> <http://ex/p> <http://ex/o>"));
        assertTrue(change.getLogMessage().contains("p"));
    }

    @Test
    void testSparqlUpdateChange() {
        SparqlUpdateChange change = new SparqlUpdateChange("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o }");
        repository.begin();
        change.apply(repository);
        assertEquals(1, repository.getUpdates().size());
        final String sparql = repository.getUpdates().get(0);
        assertTrue(sparql.contains("DELETE"));
        assertTrue(change.getLogMessage().contains("DELETE"));
    }

    @Test
    void importFromUrlChangePassesSourceUrlAndTargetGraphToRepository() {
        final String ontoIri = "http://example.com/ontology";
        final ImportFromUrlChange change = new ImportFromUrlChange(ontoIri, ontoIri, false);
        repository.begin();
        change.apply(repository);
        verify(repository).importFromUrl(ontoIri, ontoIri);
    }

    @Test
    void importFromUrlPassesNullAsTargetGraphWhenItIsNotSpecified() {
        final String ontoIri = "http://example.com/ontology";
        final ImportFromUrlChange change = new ImportFromUrlChange(ontoIri, null, false);
        repository.begin();
        change.apply(repository);
        verify(repository).importFromUrl(ontoIri, null);
    }

    @Test
    void importFromUrlClearsTargetGraphWhenReplaceIsSpecified() {
        final String ontoIri = "http://example.com/ontology";
        final ImportFromUrlChange change = new ImportFromUrlChange(ontoIri, ontoIri, true);
        repository.begin();
        change.apply(repository);
        verify(repository).clearGraph(ontoIri);
        verify(repository).importFromUrl(ontoIri, ontoIri);
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
        change.setOldIri(oldName);
        change.setNewIri(newName);
        return change;
    }
}
