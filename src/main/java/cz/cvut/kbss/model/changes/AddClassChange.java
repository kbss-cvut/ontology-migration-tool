package cz.cvut.kbss.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class  AddClassChange extends Change{
    @JsonProperty("iri")
    private String iri;

    @JsonProperty("label")
    private String label;

    public AddClassChange(String iri, String label, String graph) {
        this.iri = iri;
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
                "<http://www.w3.org/2000/01/rdf-schema#Class> . ", iri));
        if(label != null){
            sb.append(String.format("<%s> <http://www.w3.org/2000/01/rdf-schema#label>" +
                    " \"%s\" . ", iri, label));
        }
        if(graph!=null && !graph.isBlank()){
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String getLogMessage() {
        return String.format("Class added: %s", iri);
    }
}
