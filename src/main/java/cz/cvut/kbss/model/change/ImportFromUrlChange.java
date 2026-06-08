package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class ImportFromUrlChange extends Change {

    @JsonProperty("sourceUrl")
    private String sourceUrl;

    @JsonProperty("graph")
    protected String graph;

    @JsonProperty("replace")
    private boolean replace;

    public ImportFromUrlChange() {
    }

    public ImportFromUrlChange(String sourceUrl, String graph, boolean replace) {
        this.sourceUrl = sourceUrl;
        this.graph = graph;
        this.replace = replace;
    }

    @Override
    public void apply(OntologyRepository repository) {
        String targetGraph = graph == null || graph.isBlank() ? null : graph;
        if (replace && targetGraph != null) {
            repository.clearGraph(targetGraph);
        }
        repository.importFromUrl(sourceUrl, targetGraph);
    }

    @Override
    public String getLogMessage() {
        return "Imported ontology from: " + sourceUrl;
    }
}
