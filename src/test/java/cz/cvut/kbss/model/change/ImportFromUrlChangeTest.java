package cz.cvut.kbss.model.change;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportFromUrlChangeTest extends AbstractChangeIntegrationTest {

    private static final String IMPORTED_RESOURCE = "http://ex/imported";

    /** Points to src/test/resources/url-import-data.ttl */
    private String importDataUrl() {
        URL url = Objects.requireNonNull(getClass().getResource("/url-import-data.ttl"),
                "Test resource url-import-data.ttl must be on the classpath");
        return url.toString();
    }

    @Test
    void importsDataIntoDefaultGraph() {
        applyAndCommit(new ImportFromUrlChange(importDataUrl(), null, false));

        assertTrue(askTriple(IMPORTED_RESOURCE, RDF_TYPE, RDFS_CLASS, DEFAULT_GRAPH));
        assertTrue(askLiteral(IMPORTED_RESOURCE, RDFS_LABEL, "Imported class"));
    }

    @Test
    void importsDataIntoNamedGraph() {
        applyAndCommit(new ImportFromUrlChange(importDataUrl(), EXAMPLE_GRAPH_A, true));

        assertTrue(askTriple(IMPORTED_RESOURCE, RDF_TYPE, RDFS_CLASS, EXAMPLE_GRAPH_A),
                "Triple must be imported into the named graph");

        assertFalse(askTriple(IMPORTED_RESOURCE, RDF_TYPE, RDFS_CLASS, DEFAULT_GRAPH),
                "Triple must not be imported into the default graph");
    }
}
