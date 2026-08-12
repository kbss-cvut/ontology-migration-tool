package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteResourceChangeTest extends AbstractChangeIntegrationTest {
    @Test
    void removesTriplesWhereResourceIsSubjectPredicateOrObject() {
        insertAndCommit("""
                INSERT DATA {
                    <INSTANCE_A> <PROPERTY_A> <INSTANCE_B> .
                    <INSTANCE_B> <INSTANCE_A> <CLASS> .
                    <INSTANCE_B> <PROPERTY_B> <INSTANCE_A> .
                    <UNRELATED> <PROPERTY_B> <CLASS> .
                }
                """
                .replace("INSTANCE_A", EXAMPLE_INSTANCE_A)
                .replace("PROPERTY_A", EXAMPLE_PROPERTY_A)
                .replace("INSTANCE_B", EXAMPLE_INSTANCE_B)
                .replace("CLASS", EXAMPLE_CLASS)
                .replace("PROPERTY_B", EXAMPLE_PROPERTY_B)
                .replace("UNRELATED", UNRELATED)
        );

        applyAndCommit(new DeleteResourceChange(EXAMPLE_INSTANCE_A, null));

        // Triples containing the resource must be removed
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B));
        assertFalse(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_INSTANCE_A, EXAMPLE_CLASS));
        assertFalse(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_PROPERTY_B, EXAMPLE_INSTANCE_A));

        assertTrue(askTriple(UNRELATED, EXAMPLE_PROPERTY_B, EXAMPLE_CLASS),
                "Unrelated triples must not be removed");
    }

    @Test
    void removesTriplesWhereResourceIsSubjectPredicateOrObjectFromNamedGraph() {
        insertAndCommit("""
                INSERT DATA {
                    GRAPH <GRAPH_A> {
                        <INSTANCE_A> <PROPERTY_A> <INSTANCE_B> .
                        <INSTANCE_B> <INSTANCE_A> <CLASS> .
                        <INSTANCE_B> <PROPERTY_B> <INSTANCE_A> .
                        <UNRELATED> <PROPERTY_B> <CLASS> .
                    }
                }
                """
                .replace("GRAPH_A", EXAMPLE_GRAPH_A)
                .replace("INSTANCE_A", EXAMPLE_INSTANCE_A)
                .replace("PROPERTY_A", EXAMPLE_PROPERTY_A)
                .replace("INSTANCE_B", EXAMPLE_INSTANCE_B)
                .replace("CLASS", EXAMPLE_CLASS)
                .replace("PROPERTY_B", EXAMPLE_PROPERTY_B)
                .replace("UNRELATED", UNRELATED)
        );

        applyAndCommit(new DeleteResourceChange(EXAMPLE_INSTANCE_A, EXAMPLE_GRAPH_A));

        // Triples containing the resource must be removed
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_INSTANCE_A, EXAMPLE_CLASS, EXAMPLE_GRAPH_A));
        assertFalse(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_PROPERTY_B, EXAMPLE_INSTANCE_A, EXAMPLE_GRAPH_A));

        assertTrue(askTriple(UNRELATED, EXAMPLE_PROPERTY_B, EXAMPLE_CLASS, EXAMPLE_GRAPH_A),
                "Unrelated triples must not be removed");
    }

    @Test
    void withGraphRemovesOnlyFromThatGraph() {
        insertAndCommit("""
                INSERT DATA {
                    <INSTANCE_A> <PROPERTY> <INSTANCE_B> .
                    GRAPH <GRAPH_A> { <INSTANCE_A> <PROPERTY> <INSTANCE_B> . }
                    GRAPH <GRAPH_B> { <INSTANCE_A> <PROPERTY> <INSTANCE_B> . }
                }
                """
                .replace("GRAPH_A", EXAMPLE_GRAPH_A)
                .replace("GRAPH_B", EXAMPLE_GRAPH_B)
                .replace("INSTANCE_A", EXAMPLE_INSTANCE_A)
                .replace("PROPERTY", EXAMPLE_PROPERTY_A)
                .replace("INSTANCE_B", EXAMPLE_INSTANCE_B)
        );

        applyAndCommit(new DeleteResourceChange(EXAMPLE_INSTANCE_A, EXAMPLE_GRAPH_A));

        assertFalse(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_A),
                "Triple must be removed from targeted graph");

        assertTrue(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, EXAMPLE_GRAPH_B),
                "Triple must remain in unrelated graph");

        assertTrue(askTriple(EXAMPLE_INSTANCE_A, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B, DEFAULT_GRAPH),
                "Triple must remain in default graph");
    }
}
