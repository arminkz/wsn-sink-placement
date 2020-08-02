package model;

import graph.Graph;
import java.util.ArrayList;

public class Scenario {

    private final Graph root;
    private final ArrayList<SinkConfiguration> sinkTypes;
    private final ArrayList<SinkCandidate> sinkCandidates;

    public Scenario(Graph root, ArrayList<SinkConfiguration> sinkTypes, ArrayList<SinkCandidate> sinkCandidates) {
        this.root = root;
        this.sinkTypes = sinkTypes;
        this.sinkCandidates = sinkCandidates;
    }

    public Graph getRootGraph() { return root; }
    public ArrayList<SinkConfiguration> getSinkTypes() { return sinkTypes; }
    public ArrayList<SinkCandidate> getSinkCandidates() { return sinkCandidates; }
}
