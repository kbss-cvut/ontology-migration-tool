package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class DeleteResourceChange extends Change {
    @JsonProperty("iri")
    private String iri;

    public DeleteResourceChange() {
    }

    public DeleteResourceChange(String iri) {
        this.iri = iri;
    }

    @Override
    public void apply(OntologyRepository repository) {
        final String update = deleteQuery();
        repository.update(update);
    }

    String deleteQuery() {
        return String.format("DELETE WHERE { GRAPH ?g { <%s> ?p ?o } }; " +
                                     "DELETE WHERE { GRAPH ?g { ?s ?p <%s> } }; " +
                                     "DELETE WHERE { GRAPH ?g { ?s <%s> ?o } }; " +
                                     "DELETE WHERE { <%s> ?p ?o }; " +
                                     "DELETE WHERE { ?s <%s> ?o }; " +
                                     "DELETE WHERE { ?s ?p <%s> }",
                             iri, iri, iri, iri, iri, iri
        );
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource deleted: %s", iri);
    }
}
