package cz.cvut.kbss.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ChangeLog {
    @JsonProperty("changelog")
    private List<ChangeSet> changeSets = new ArrayList<>();
    public List<ChangeSet> getChangeSets() { return changeSets; }
    public ChangeLog(){}
    public void setChangeSets(List<ChangeSet> changeSets) { this.changeSets = changeSets; }
}