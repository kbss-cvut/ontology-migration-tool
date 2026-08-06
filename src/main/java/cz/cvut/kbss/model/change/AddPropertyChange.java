package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class AddPropertyChange extends ChangeWithGraph {

    @JsonProperty("propertyIRI")
    private String propertyIRI;

    @JsonProperty("objectIRI")
    private String objectIRI;

    @JsonProperty("subjectIRI")
    private String subjectIRI;

    public AddPropertyChange() {
        super();
    }

    public AddPropertyChange(String propertyIRI, String objectIRI, String subjectIRI,
                             String graph) {
        super(graph);
        this.propertyIRI = propertyIRI;
        this.objectIRI = objectIRI;
        this.subjectIRI = subjectIRI;
    }

    @Override
    public void apply(OntologyRepository repository) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT DATA { ");
        if (isGraphSpecified()) {
            sb.append("GRAPH <").append(graph).append("> { ");
        }
        if (subjectIRI != null && propertyIRI != null && objectIRI != null) {
            sb.append(String.format("<%s> <%s> <%s> . ", subjectIRI, propertyIRI, objectIRI));
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
            return String.format("Property added: <%s> <%s> <%s> to graph %s", subjectIRI, propertyIRI, objectIRI, graph);
        }
        return String.format("Property added: <%s> <%s> <%s>", subjectIRI, propertyIRI, objectIRI);
    }


}