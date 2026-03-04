package cz.cvut.kbss.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class AddPropertyChange extends Change {

    @JsonProperty("propertyIRI")
    private String propertyIRI;

    @JsonProperty("objectIRI")
    private String objectIRI;

    @JsonProperty("subjectIRI")
    private String subjectIRI;

    public AddPropertyChange(){}

    public AddPropertyChange(String propertyIRI, String objectIRI, String subjectIRI,
                             String graph) {
        this.propertyIRI = propertyIRI;
        this.objectIRI = objectIRI;
        this.subjectIRI = subjectIRI;
        this.graph = graph;
    }

    @Override
    public String apply(OntologyRepository repository) { //TODO переписать
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT DATA { ");
        if(graph != null && !graph.isBlank()){
            sb.append("GRAPH <").append(graph).append("> { ");
        }
        if(subjectIRI != null && propertyIRI != null && objectIRI != null){
            sb.append(String.format("<%s> <%s> <%s> . ", subjectIRI, propertyIRI, objectIRI));
        }
        if(graph!=null && !graph.isBlank()){
            sb.append("}");
        }
        sb.append(" }");

        return sb.toString();
    }

    @Override
    public String getLogMessage() {
        return String.format("Property added: <%s> <%s> <%s>", subjectIRI, propertyIRI, objectIRI);
    }


}