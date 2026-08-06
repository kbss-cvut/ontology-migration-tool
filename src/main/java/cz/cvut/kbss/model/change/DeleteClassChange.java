package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

/**
 * Deletes a class and all its instances.
 */
public class DeleteClassChange extends ChangeWithGraph {

    @JsonProperty("iri")
    private String iri;

    public DeleteClassChange() {
    }

    public DeleteClassChange(String iri) {
        this.iri = iri;
    }

    @Override
    public void apply(OntologyRepository repository) {
        String update;
        if (isGraphSpecified()) {
            update = String.format("DELETE WHERE { GRAPH <%s> { ?x a <%s> . ?x ?y ?z . ?zz ?yy ?x . } };", graph, iri);
        } else {
            update = String.format("DELETE WHERE { ?x a <%s> . ?x ?y ?z . ?zz ?yy ?x . };" +
                            "DELETE WHERE { ?x a <%s> . GRAPH ?g { ?x ?y ?z . } GRAPH ?gg { ?zz ?yy ?x . } };",
                    iri, iri);
        }
        update += new DeleteResourceChange(iri, graph).deleteQuery();
        repository.update(update);
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource deleted: %s", iri);
    }
}
