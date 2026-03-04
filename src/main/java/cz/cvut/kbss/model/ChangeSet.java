package cz.cvut.kbss.model;

import cz.cvut.kbss.model.changes.Change;

import java.util.ArrayList;
import java.util.List;

public class ChangeSet {

    private String id;

    private List<Change> changes = new ArrayList<>();

    public ChangeSet() {
    }

    public ChangeSet(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

}
