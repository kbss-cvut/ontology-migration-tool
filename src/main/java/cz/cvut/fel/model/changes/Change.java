package cz.cvut.fel.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import cz.cvut.fel.repository.OntologyRepository;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RenameResourceChange.class, name = "renameResource"),
        @JsonSubTypes.Type(value = AddClassChange.class, name = "addClass"),
        @JsonSubTypes.Type(value = AddResourceChange.class, name = "addResource"),
        @JsonSubTypes.Type(value = DeleteResourceChange.class, name = "deleteResource"),
        @JsonSubTypes.Type(value = AddPropertyChange.class, name = "addProperty"),
        @JsonSubTypes.Type(value = SparqlUpdateChange.class, name = "sparqlUpdate")
})
public abstract class Change {
    @JsonProperty("graph")
    protected String graph;

    public String getGraph() {
        return graph;
    }

    public String getType() {
        JsonTypeName annotation = this.getClass().getAnnotation(JsonTypeName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return this.getClass().getSimpleName();
    }

    public abstract String apply(OntologyRepository repository);

    public abstract String getLogMessage();
}