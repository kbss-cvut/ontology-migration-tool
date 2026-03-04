package cz.cvut.fel.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.repository.OntologyRepository;

public class AddPropertyChange extends Change {

    @JsonProperty("propertyURI")
    private String propertyURI;

    @JsonProperty("objectURI")
    private String objectURI;

    @JsonProperty("subjectURI")
    private String subjectURI;

    public AddPropertyChange(){}

    public AddPropertyChange(String propertyURI, String objectURI, String subjectURI,
                             String graph) {
        this.propertyURI = propertyURI;
        this.objectURI = objectURI;
        this.subjectURI = subjectURI;
        this.graph = graph;
    }

    @Override
    public String apply(OntologyRepository repository) { //TODO переписать
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT DATA { ");
        if(graph != null && !graph.isBlank()){
            sb.append("GRAPH <").append(graph).append("> { ");
        }
        if(subjectURI != null && propertyURI != null && objectURI != null){
            sb.append(String.format("<%s> <%s> <%s> . ", subjectURI, propertyURI, objectURI));
        }
        if(graph!=null && !graph.isBlank()){
            sb.append("}");
        }
        sb.append(" }");

        return sb.toString();
    }

    @Override
    public String getLogMessage() {
        return String.format("Property added: <%s> <%s> <%s>", subjectURI, propertyURI, objectURI);
    }


}