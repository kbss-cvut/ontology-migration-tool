package cz.cvut.fel.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.repository.OntologyRepository;

public class AddResourceChange extends Change{
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("classUri")
    private String classUri;
    @JsonProperty("label")
    private String label;

    public AddResourceChange(String uri, String classUri, String label) {
        this.uri = uri;
        this.classUri = classUri;
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
        if (classUri != null) {
            sb.append(String.format("<%s> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> . ", uri, classUri));
        }
        if (label != null) {
            sb.append(String.format("<%s> <http://www.w3.org/2000/01/rdf-schema#label> \"%s\" . ", uri, label));
        }
        if(graph!=null && !graph.isBlank()){
            sb.append("}");
        }
        sb.append(" }");
        return sb.toString();
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource added: %s", uri);
    }
}
