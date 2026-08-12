package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

/**
 * Deletes a resource identified by {@link #iri} from the given {@link #graph}.
 * <p>
 * Removes matching triples where the resource appears as subject, predicate, or object.
 */
public class DeleteResourceChange extends ChangeWithGraph {
    @JsonProperty("iri")
    private String iri;

    public DeleteResourceChange() {
        super();
    }

    public DeleteResourceChange(String iri, String graph) {
        super(graph);
        this.iri = iri;
    }

    @Override
    public void apply(OntologyRepository repository) {
        final String update = deleteQuery();
        repository.update(update);
    }

    String deleteQuery() {
        if (isGraphSpecified()) {
            return String.format("DELETE WHERE { GRAPH <%1$s> { <%2$s> ?p ?o } }; " +
                                    "DELETE WHERE { GRAPH <%1$s> { ?s <%2$s> ?o } }; " +
                                         "DELETE WHERE { GRAPH <%1$s> { ?s ?p <%2$s> } }; ", graph, iri);
        }
        return
                buildSparqlDeleteForAllGraphs("<%1$s> ?p ?o", iri) +
                buildSparqlDeleteForAllGraphs("?s <%1$s> ?o", iri) +
                buildSparqlDeleteForAllGraphs("?s ?p <%1$s>", iri);
    }

    String buildSparqlDeleteForAllGraphs(String matchPattern, String iri) {
        return String.format("DELETE WHERE { " + matchPattern + " }; " +
                "DELETE WHERE { GRAPH ?g { " + matchPattern + " } }; ", iri);
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource deleted: %s", iri);
    }
}
