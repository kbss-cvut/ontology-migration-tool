package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteClassChangeTest extends AbstractChangeIntegrationTest {
    static final String HAS_VALUE = "http://ex/hasValue";
    static final String LITERAL = "literal Value";

    @Test
    void removesInstanceAndClassTriples() {
        insertAndCommit("""
                INSERT DATA {
                    <INSTANCE_A> a <EXAMPLE_CLASS> .
                    <INSTANCE_A> <HAS_VALUE> "LITERAL" .
                    <INSTANCE_B> <PREDICATE> <INSTANCE_A> .

                    <EXAMPLE_CLASS> <RDFS_LABEL> "Class One" .
                    <UNRELATED> <PREDICATE> <INSTANCE_B> .
                }
                """
                .replace("INSTANCE_A", EXAMPLE_INSTANCE_A)
                .replace("EXAMPLE_CLASS", EXAMPLE_CLASS)
                .replace("LITERAL", LITERAL)
                .replace("HAS_VALUE", HAS_VALUE)
                .replace("INSTANCE_B", EXAMPLE_INSTANCE_B)
                .replace("PREDICATE", EXAMPLE_PROPERTY_A)
                .replace("RDFS_LABEL", RDFS_LABEL)
                .replace("UNRELATED", UNRELATED)
        );
        
        applyAndCommit(new DeleteClassChange(EXAMPLE_CLASS));

        // All instances of the class and related triples were removed
        assertFalse(askTriple(EXAMPLE_INSTANCE_A, RDF_TYPE, EXAMPLE_CLASS));
        assertFalse(askLiteral(EXAMPLE_INSTANCE_A, HAS_VALUE, LITERAL));
        assertFalse(askTriple(EXAMPLE_INSTANCE_B, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_A));

        assertFalse(askLiteral(EXAMPLE_CLASS, RDFS_LABEL, "Class One"),
                "Properties of the class must be removed");

        assertTrue(askTriple(UNRELATED, EXAMPLE_PROPERTY_A, EXAMPLE_INSTANCE_B),
                "Unrelated triples must not be removed");
    }
}
