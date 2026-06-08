package cz.cvut.kbss.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class SparqlUpdateChange extends Change {
    @JsonProperty("query")
    private String query;

    public SparqlUpdateChange(String query) {
        this.query = query;
    }

    public SparqlUpdateChange() {
    }

    @Override
    public void apply(OntologyRepository repository) {
        repository.update(query);
    }

    @Override
    public String getLogMessage() {
        return "Executing SPARQL update: " + (query.length() > 100 ? query.substring(0, 100) + "..." : query);
    }
}
