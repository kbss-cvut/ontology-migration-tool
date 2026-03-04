package cz.cvut.fel.model;

import cz.cvut.fel.model.changes.Change;

import java.util.ArrayList;
import java.util.List;

public class ChangeSet {
    private String id;
    private List<Change> changes = new ArrayList<>();
    public ChangeSet(String id) { this.id = id; }
    public ChangeSet(){}
    public String getId() { return id; }
    public List<Change> getChanges() { return changes; }
    public void setChanges(List<Change> changes) { this.changes = changes; }

}
