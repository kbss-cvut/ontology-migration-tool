package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

/**
 * Creates a new class with the given {@link #label} in the given {@link #graph}.
 * <p>
 * Inserts the given {@link #iri} as an instance of {@link org.eclipse.rdf4j.model.vocabulary.RDFS#CLASS RDFS Class}
 * with the given {@link #label} as {@link org.eclipse.rdf4j.model.vocabulary.RDFS#LABEL RDFS Label}.
 */
public class AddClassChange extends ChangeWithGraph {
    @JsonProperty("iri")
    private String iri;

    @JsonProperty("label")
    private String label;

    public AddClassChange() {
        super();
    }

    public AddClassChange(String iri, String label, String graph) {
        super(graph);
        this.iri = iri;
        this.label = label;
    }

    @Override
    public void apply(OntologyRepository repository) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT DATA { ");
        if (isGraphSpecified()) {
            sb.append("GRAPH <").append(graph).append("> { ");
        }
        sb.append(String.format("<%s> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " +
                                        "<http://www.w3.org/2000/01/rdf-schema#Class> . ", iri));
        if (label != null) {
            sb.append(String.format("<%s> <http://www.w3.org/2000/01/rdf-schema#label>" +
                                            " \"%s\" . ", iri, label));
        }
        if (isGraphSpecified()) {
            sb.append("}");
        }
        sb.append("}");
        repository.update(sb.toString());
    }

    @Override
    public String getLogMessage() {
        if (isGraphSpecified()) {
            return String.format("Class added: %s to graph %s", iri, graph);
        }
        return String.format("Class added: %s", iri);
    }
}
