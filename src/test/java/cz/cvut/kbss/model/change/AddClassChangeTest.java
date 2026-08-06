package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddClassChangeTest extends AbstractChangeIntegrationTest {

    @Test
    void insertsClassTypeAndLabel() {
        applyAndCommit(new AddClassChange(EXAMPLE_CLASS, "Class One", null));

        assertTrue(askTriple(EXAMPLE_CLASS, RDF_TYPE, RDFS_CLASS, DEFAULT_GRAPH),
                "Class should be inserted into the default graph");

        assertTrue(askLiteral(EXAMPLE_CLASS, RDFS_LABEL, "Class One", DEFAULT_GRAPH),
                "Class label should be inserted into the default graph");
    }

    @Test
    void withGraphInsertsOnlyIntoNamedGraph() {
        applyAndCommit(new AddClassChange(EXAMPLE_CLASS, "Class Two", EXAMPLE_GRAPH_A));

        assertTrue(askTriple(EXAMPLE_CLASS, RDF_TYPE, RDFS_CLASS, EXAMPLE_GRAPH_A),
                "Class should be inserted into the specified graph");

        assertFalse(askTriple(EXAMPLE_CLASS, RDF_TYPE, RDFS_CLASS, DEFAULT_GRAPH),
                "Class should not be inserted into the default graph");
    }
}
