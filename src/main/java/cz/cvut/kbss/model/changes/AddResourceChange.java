package cz.cvut.kbss.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class AddResourceChange extends Change{
    @JsonProperty("iri")
    private String iri;
    @JsonProperty("classIri")
    private String classIri;
    @JsonProperty("label")
    private String label;

    public AddResourceChange(String iri, String classIri, String label) {
        this.iri = iri;
        this.classIri = classIri;
        this.label = label;
    }
    public AddResourceChange(){}

    @Override
    public String apply(OntologyRepository repository) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT DATA { ");
        if(graph != null && !graph.isBlank()){
            sb.append("GRAPH <").append(graph).append("> { ");
        }
        if (classIri != null) {
            sb.append(String.format("<%s> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> . ", iri, classIri));
        }
        if (label != null) {
            sb.append(String.format("<%s> <http://www.w3.org/2000/01/rdf-schema#label> \"%s\" . ", iri, label));
        }
        if(graph!=null && !graph.isBlank()){
            sb.append("}");
        }
        sb.append(" }");
        return sb.toString();
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource added: %s", iri);
    }
}
