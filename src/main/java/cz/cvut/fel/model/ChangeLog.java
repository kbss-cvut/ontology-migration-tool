package cz.cvut.fel.model;

import cz.cvut.fel.model.ChangeSet;

import java.util.ArrayList;
import java.util.List;

public class ChangeLog {
    private List<ChangeSet> changeSets = new ArrayList<>();
    public List<ChangeSet> getChangeSets() { return changeSets; }
    public ChangeLog(){}
    public void setChangeSets(List<ChangeSet> changeSets) { this.changeSets = changeSets; }
}