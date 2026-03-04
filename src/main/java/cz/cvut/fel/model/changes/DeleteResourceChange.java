package cz.cvut.fel.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.repository.OntologyRepository;

public class DeleteResourceChange extends Change{
    @JsonProperty("uri")
    private String uri;
    public DeleteResourceChange(String uri) {
        this.uri = uri;
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
                    uri, uri, uri, uri, uri, uri
            );
    }
    @Override
    public String getLogMessage() {
        return String.format("Resource deleted: %s", uri);
    }
}
