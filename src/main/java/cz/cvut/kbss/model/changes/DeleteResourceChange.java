package cz.cvut.kbss.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class DeleteResourceChange extends Change{
    @JsonProperty("iri")
    private String iri;
    public DeleteResourceChange(String iri) {
        this.iri = iri;
    }
    public DeleteResourceChange(){}

    @Override
    public String apply(OntologyRepository repository) {
            return String.format(
                    "DELETE WHERE { GRAPH ?g { <%s> ?p ?o } }; " +
                            "DELETE WHERE { GRAPH ?g { ?s ?p <%s> } };" +
                            "DELETE WHERE { <%s> ?p ?o }; " +
                            "DELETE WHERE { ?s ?p <%s> }" +
                            "DELETE WHERE { GRAPH ?g { ?s <%s> ?o } }; " +
                            "DELETE WHERE { ?s <%s> ?o }"
                    ,
                    iri, iri, iri, iri, iri, iri
            );
    }
    @Override
    public String getLogMessage() {
        return String.format("Resource deleted: %s", iri);
    }
}
