package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

import java.util.ArrayList;
import java.util.List;


public class RenameResourceChange extends ChangeWithGraph {
    @JsonProperty("oldIri")
    private String oldIri;

    @JsonProperty("newIri")
    private String newIri;

    public RenameResourceChange() {
        super();
    }

    public RenameResourceChange(String oldIri, String newIri, String graph) {
        super(graph);
        this.oldIri = oldIri;
        this.newIri = newIri;
    }

    @Override
    public void apply(OntologyRepository repository) {
        List<String> updates = new ArrayList<>(2);

        if (isGraphSpecified()) {
            // Target specific named graph
            updates.addAll(buildUpdatesForGraph("<" + graph + ">"));
        } else {
            // Target all named graphs AND default graph
            updates.addAll(buildUpdatesForGraph("?g"));
            updates.addAll(buildUpdatesForGraph(null));
        }

        repository.update(String.join(";\n", updates));
    }

    private List<String> buildUpdatesForGraph(String graph) {
        String oldIriRef = "<" + oldIri + ">";
        String newIriRef = "<" + newIri + ">";

        return List.of(
                // Rename Subjects
                buildSparqlUpdate(graph, oldIriRef + " ?p ?o", newIriRef + " ?p ?o"),
                // Rename Predicates
                buildSparqlUpdate(graph, "?s " + oldIriRef + " ?o", "?s " + newIriRef + " ?o"),
                // Rename Objects
                buildSparqlUpdate(graph, "?s ?p " + oldIriRef, "?s ?p " + newIriRef)
        );
    }

    String buildSparqlUpdate(String graph, String matchPattern, String insertPattern) {
        if (graph != null) {
            return String.format("""
                DELETE { GRAPH %1$s { %2$s } }
                INSERT { GRAPH %1$s { %3$s } }
                WHERE  { GRAPH %1$s { %2$s } }""", graph, matchPattern, insertPattern);
        } else {
            return String.format("""
                DELETE { %1$s }
                INSERT { %2$s }
                WHERE  { %1$s }""", matchPattern, insertPattern);
        }
    }

    @Override
    public String getLogMessage() {
        if (isGraphSpecified()) {
            return String.format("Resource renamed: <%s> → <%s> in graph <%s>", oldIri, newIri, graph);
        }
        return String.format("Resource renamed: <%s> → <%s> in all graphs", oldIri, newIri);
    }
}
