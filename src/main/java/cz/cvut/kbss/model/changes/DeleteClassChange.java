package cz.cvut.kbss.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

/**
 * Deletes a class and all its instances.
 */
public class DeleteClassChange extends Change {

    @JsonProperty("iri")
    private String iri;

    public DeleteClassChange() {
    }

    public DeleteClassChange(String iri) {
        this.iri = iri;
    }

    @Override
    public String apply(OntologyRepository repository) {
        return String.format("DELETE WHERE { ?x a <%s> . ?x ?y ?z . ?zz ?yy ?x . };" +
                                     "DELETE WHERE { ?x a <%s> . GRAPH ?g { ?x ?y ?z . } GRAPH ?gg { ?zz ?yy ?x . } };",
                             iri, iri
        ) + new DeleteResourceChange(iri).apply(repository);
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource deleted: %s", iri);
    }
}
