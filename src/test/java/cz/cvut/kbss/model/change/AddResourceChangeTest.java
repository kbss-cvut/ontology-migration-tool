package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddResourceChangeTest extends AbstractChangeIntegrationTest {

    @Test
    void insertsTypeAndLabel() {
        applyAndCommit(new AddResourceChange(EXAMPLE_INSTANCE_A, EXAMPLE_CLASS, "Resource One", null));

        assertTrue(askTriple(EXAMPLE_INSTANCE_A, RDF_TYPE, EXAMPLE_CLASS));
        assertTrue(askLiteral(EXAMPLE_INSTANCE_A, RDFS_LABEL, "Resource One"));
    }

    @Test
    void withGraphInsertsOnlyIntoNamedGraph() {
        applyAndCommit(new AddResourceChange(EXAMPLE_INSTANCE_A, EXAMPLE_CLASS, "Resource Two", EXAMPLE_GRAPH_A));

        assertTrue(askTriple( EXAMPLE_INSTANCE_A, RDF_TYPE, EXAMPLE_CLASS, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, RDF_TYPE, EXAMPLE_CLASS, DEFAULT_GRAPH));
    }
}
