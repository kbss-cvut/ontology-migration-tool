package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

/**
 * Creates a new resource with the given {@link #iri} in the given {@link #graph}.
 * <p>
 * Optionally inserts the resource type using {@link #classIri} and a human-readable label
 * using {@link #label}.
 */
public class AddResourceChange extends ChangeWithGraph {
    @JsonProperty("iri")
    private String iri;

    @JsonProperty("classIri")
    private String classIri;

    @JsonProperty("label")
    private String label;

    public AddResourceChange() {
        super();
    }

    public AddResourceChange(String iri, String classIri, String label, String graph) {
        super(graph);
        this.iri = iri;
        this.classIri = classIri;
        this.label = label;
    }

    @Override
    public void apply(OntologyRepository repository) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT DATA { ");
        if (isGraphSpecified()) {
            sb.append("GRAPH <").append(graph).append("> { ");
        }
        if (classIri != null) {
            sb.append(String.format("<%s> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> . ", iri, classIri));
        }
        if (label != null) {
            sb.append(String.format("<%s> <http://www.w3.org/2000/01/rdf-schema#label> \"%s\" . ", iri, label));
        }
        if (isGraphSpecified()) {
            sb.append("}");
        }
        sb.append(" }");

        repository.update(sb.toString());
    }

    @Override
    public String getLogMessage() {
        if (isGraphSpecified()) {
            return String.format("Resource added: %s to graph %s", iri, graph);
        }
        return String.format("Resource added: %s", iri);
    }
}
