package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SparqlUpdateChangeTest extends AbstractChangeIntegrationTest {

    @Test
    void executesRawSparqlUpdate() {
        applyAndCommit(new SparqlUpdateChange(
                "INSERT DATA { <http://ex/rawS> <http://ex/rawP> <http://ex/rawO> . }"));

        assertTrue(askTriple("http://ex/rawS", "http://ex/rawP", "http://ex/rawO"));
    }
}
