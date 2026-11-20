package cz.cvut.kbss.exception;

/**
 * The application failed to resolve the given MIME type to a known RDF Format
 */
public class UnknownRdfFormat extends OntologyMigrationToolException {
    public UnknownRdfFormat(String mimeType) {
        super("Failed to resolve known RDF Format for MIME type: " + mimeType);
    }
}
