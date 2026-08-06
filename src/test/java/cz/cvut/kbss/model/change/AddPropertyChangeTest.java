package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddPropertyChangeTest extends AbstractChangeIntegrationTest {

    @Test
    void insertsTriple() {
        applyAndCommit(new AddPropertyChange(EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_INSTANCE_A, null));

        assertTrue(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B));
    }

    @Test
    void withGraphInsertsOnlyIntoNamedGraph() {
        applyAndCommit(new AddPropertyChange(EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_INSTANCE_A, EXAMPLE_GRAPH_A));

        assertTrue(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, DEFAULT_GRAPH));
    }
}
