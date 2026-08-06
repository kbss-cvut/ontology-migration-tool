package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RenameResourceChangeTest extends AbstractChangeIntegrationTest {
    static final String INSTANCE_NEW = "http://ex/newInstance";

    @Test
    void withGraphRenamesOnlyWithinThatGraph() {
        insertAndCommit("""
                INSERT DATA {
                    GRAPH <GRAPH_A> { <INSTANCE_A> <PROPERTY> <INSTANCE_B> . }
                    GRAPH <GRAPH_B> { <INSTANCE_A> <PROPERTY> <INSTANCE_B> . }
                }
                """
                .replace("GRAPH_A", EXAMPLE_GRAPH_A)
                .replace("GRAPH_B", EXAMPLE_GRAPH_B)
                .replace("INSTANCE_A", EXAMPLE_INSTANCE_A)
                .replace("INSTANCE_B", EXAMPLE_INSTANCE_B)
                .replace("PROPERTY", EXAMPLE_PROPERTY_A)
        );

        applyAndCommit(new RenameResourceChange(EXAMPLE_INSTANCE_A, INSTANCE_NEW, EXAMPLE_GRAPH_A));

        assertTrue(askTriple(INSTANCE_NEW, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_A));

        assertTrue(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_B),
                "Unrelated graph must not be changed");
    }

    @Test
    void withoutGraphRenamesAcrossDefaultAndNamedGraphsAndAllTriplePositions() {
        insertAndCommit("""
                INSERT DATA {
                    <INSTANCE_A> <PROPERTY> <INSTANCE_B> .
                    <CLASS> <INSTANCE_A> <INSTANCE_B> .
                    <INSTANCE_B> <PROPERTY> <INSTANCE_A> .
                    GRAPH <GRAPH_A> {
                        <INSTANCE_A> <PROPERTY> <INSTANCE_B> .
                        <INSTANCE_B> <INSTANCE_A> <CLASS> .
                        <CLASS> <PROPERTY> <INSTANCE_A> .
                    }
                }
                """
                .replace("INSTANCE_A", EXAMPLE_INSTANCE_A)
                .replace("INSTANCE_B", EXAMPLE_INSTANCE_B)
                .replace("PROPERTY", EXAMPLE_PROPERTY_A)
                .replace("CLASS", EXAMPLE_CLASS)
                .replace("GRAPH_A", EXAMPLE_GRAPH_A)
        );

        applyAndCommit(new RenameResourceChange(EXAMPLE_INSTANCE_A, INSTANCE_NEW, null));

        // Renamed as subject in the default graph
        assertTrue(askTriple(INSTANCE_NEW, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, DEFAULT_GRAPH));
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, DEFAULT_GRAPH));
        // Renamed as predicate in the default graph
        assertTrue(askTriple(EXAMPLE_CLASS, INSTANCE_NEW, EXAMPLE_INSTANCE_B, DEFAULT_GRAPH));
        assertFalse(askTriple(EXAMPLE_CLASS, EXAMPLE_INSTANCE_A, EXAMPLE_INSTANCE_B, DEFAULT_GRAPH));
        // Renamed as object in the default graph
        assertTrue(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_PROPERTY_A, INSTANCE_NEW, DEFAULT_GRAPH));
        assertFalse(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_A, DEFAULT_GRAPH));
        // Renamed as subject inside a named graph
        assertTrue(askTriple(INSTANCE_NEW, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_A));
        // Renamed as predicate inside a named graph
        assertTrue(askTriple(EXAMPLE_INSTANCE_B, INSTANCE_NEW, EXAMPLE_CLASS, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_INSTANCE_A, EXAMPLE_CLASS, EXAMPLE_GRAPH_A));
        // Renamed as object inside a named graph
        assertTrue(askTriple(EXAMPLE_CLASS, EXAMPLE_PROPERTY_A, INSTANCE_NEW, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_CLASS, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_A, EXAMPLE_GRAPH_A));
    }
}
