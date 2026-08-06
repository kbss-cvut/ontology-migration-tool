package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for changes that can target a specific named graph.
 */
public abstract class ChangeWithGraph extends Change {
    @JsonProperty("graph")
    protected String graph;

    public ChangeWithGraph() {
    }

    public ChangeWithGraph(String graph) {
        this.graph = graph;
    }

    protected boolean isGraphSpecified() {
        return graph != null && !graph.isBlank();
    }
}
