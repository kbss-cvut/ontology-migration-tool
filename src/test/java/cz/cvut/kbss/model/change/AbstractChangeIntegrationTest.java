package cz.cvut.kbss.model.change;

import cz.cvut.kbss.repository.Rdf4jRepository;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for integration tests for validating a {@link Change} against an in-memory RDF4J repository.
 */
abstract class AbstractChangeIntegrationTest {

    static final String RDF_TYPE = RDF.TYPE.stringValue();
    static final String RDFS_CLASS = RDFS.CLASS.stringValue();
    static final String RDFS_LABEL = RDFS.LABEL.stringValue();

    static final String DEFAULT_GRAPH = RDF4J.NIL.stringValue();

    static final String EXAMPLE_CLASS = "http://ex/ClassOne";
    static final String EXAMPLE_GRAPH_A = "http://ex/ExampleGraphA";
    static final String EXAMPLE_GRAPH_B = "http://ex/ExampleGraphB";

    static final String EXAMPLE_INSTANCE_A = "http://ex/ExampleInstanceA";
    static final String EXAMPLE_INSTANCE_B = "http://ex/ExampleInstanceB";

    static final String EXAMPLE_PROPERTY_A = "http://ex/ExamplePropertyA";
    static final String EXAMPLE_PROPERTY_B = "http://ex/examplePropertyB";

    static final String UNRELATED = "http://ex/unrelated";


    Rdf4jRepository repository;

    @BeforeEach
    void setUpRepository() {
        repository = new Rdf4jRepository(new SailRepository(new MemoryStore()));
    }

    @AfterEach
    void tearDownRepository() {
        repository.close();
    }

    /**
     * Applies the given change inside a transaction.
     *
     * @param change The change instance to apply
     */
    void applyAndCommit(Change change) {
        repository.begin();
        change.apply(repository);
        repository.commit();
    }

    /**
     * Executes a raw SPARQL Update in its own transaction.
     *
     * @param sparqlUpdate The SPARQL Update query to execute
     */
    void insertAndCommit(String sparqlUpdate) {
        repository.begin();
        repository.update(sparqlUpdate);
        repository.commit();
    }

    boolean askTriple(String subject, String predicate, String object) {
        return repository.ask(String.format("ASK { <%s> <%s> <%s> }", subject, predicate, object));
    }

    boolean askTriple(String subject, String predicate, String object, String graph) {
        return repository.ask(String.format("ASK { GRAPH <%s> { <%s> <%s> <%s> } }", graph, subject, predicate, object));
    }

    boolean askLiteral(String subject, String predicate, String literal) {
        return repository.ask(String.format("ASK { <%s> <%s> \"%s\" }", subject, predicate, literal));
    }

    boolean askLiteral(String subject, String predicate, String literal, String graph) {
        return repository.ask(String.format("ASK { GRAPH <%s> { <%s> <%s> \"%s\" } }", graph, subject, predicate, literal));
    }
}
