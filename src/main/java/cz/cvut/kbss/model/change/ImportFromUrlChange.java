package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.repository.OntologyRepository;

public class ImportFromUrlChange extends ChangeWithGraph {

    @JsonProperty("sourceUrl")
    private String sourceUrl;

    @JsonProperty("replace")
    private boolean replace;

    public ImportFromUrlChange() {
    }

    public ImportFromUrlChange(String sourceUrl, String graph, boolean replace) {
        super(graph);
        this.sourceUrl = sourceUrl;
        this.replace = replace;
    }

    @Override
    public void apply(OntologyRepository repository) {
        String targetGraph = isGraphSpecified() ? graph : null;
        if (replace && targetGraph != null) {
            repository.clearGraph(targetGraph);
        }
        repository.importFromUrl(sourceUrl, targetGraph);
    }

    @Override
    public String getLogMessage() {
        if (isGraphSpecified()) {
            return "Imported ontology from: " + sourceUrl + " to graph: <" + graph + ">";
        }
        return "Imported ontology from: " + sourceUrl;
    }
}
