package cz.cvut.fel.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.repository.OntologyRepository;

public class  AddClassChange extends Change{
    @JsonProperty("uri")
    private String uri;

    @JsonProperty("label")
    private String label;

    public AddClassChange(String uri, String label, String graph) {
        this.uri = uri;
        this.label = label;
        this.graph = graph;
    }
    public AddClassChange(){}

    @Override
    public String apply(OntologyRepository repository) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT DATA { ");
        if(graph != null && !graph.isBlank()){
            sb.append("GRAPH <").append(graph).append("> { ");
        }
        sb.append(String.format("<%s> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " +
                "<http://www.w3.org/2000/01/rdf-schema#Class> . ", uri));
        if(label != null){
            sb.append(String.format("<%s> <http://www.w3.org/2000/01/rdf-schema#label>" +
                    " \"%s\" . ", uri, label));
        }
        if(graph!=null && !graph.isBlank()){
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String getLogMessage() {
        return String.format("Class added: %s", uri);
    }
}
