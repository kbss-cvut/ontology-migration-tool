package cz.cvut.kbss.repository;

import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Represents a repository whose content is to be migrated.
 */
public interface OntologyRepository {

    /**
     * Executes the specified SPARQL Update query.
     *
     * @param sparql Query to execute
     */
    void update(String sparql);

    /**
     * Executes the specified SPARQL ASK query.
     *
     * @param sparql Query to execute
     * @return Result of the ASK query
     */
    boolean ask(String sparql);

    /**
     * Clears the named graph with the specified identifier.
     *
     * @param graph Named graph identifier
     */
    void clearGraph(String graph);

    /**
     * Imports RDF data from the specified URL to the specified named graph.
     *
     * @param url   URL containing RDF data to import
     * @param graph Target named graph identifier, possibly {@code null}
     */
    default void importFromUrl(String url, String graph) {
        importFromUrl(url, graph, null);
    }

    /**
     * Imports RDF data from the specified URL to the specified named graph using the specified format.
     *
     * @param url   URL containing RDF data to import
     * @param graph Target named graph identifier, possibly {@code null}
     * @param format The RDF format of data, possibly {@code null}
     */
    void importFromUrl(String url, String graph, RDFFormat format);
}
