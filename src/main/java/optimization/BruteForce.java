package optimization;

import graph.Graph;
import graph.Vertex;
import model.*;
import visual.ShowGraph;

public class BruteForce {

    Scenario scenario;
    Graph root;

    int nSink;
    int nOption;

    public int leafCount = 0;

    public BruteForce(Scenario scenario) {
        this.scenario = scenario;
        root = scenario.getRootGraph();
        nSink = scenario.getSinkCandidates().size();
        nOption = scenario.getSinkTypes().size();
    }

    public void solve() {
        solveUtil(root,0);
    }

    public void solveUtil(Graph graph, int loc) {
        if(loc == nSink) {
            // backtrack completed
            leafCount++;


            ShowGraph.showGraph("leaf : " + leafCount,graph);

            for(SinkCandidate sc: scenario.getSinkCandidates()){
                Vertex v = graph.getVertices().get(sc.getPlacmentVertexIndex());
                System.out.print(v.getAssignedNode() + " ");
            }
            System.out.println("");

            return;
        }

        // the candidate we are placing now
        SinkCandidate candidate = scenario.getSinkCandidates().get(loc);

        // try advancing backtrack without placing Sink
        solveUtil(graph,loc+1);

        // place a sink and proceed
        for(SinkConfiguration config: scenario.getSinkTypes()){
            Graph pGraph = graph.clone();
            // place sink
            int vi = candidate.getPlacmentVertexIndex();
            pGraph.getVertices().get(vi).setNode(new SinkNode("S"+(loc+1),config));
            // advance recursive backtracking
            solveUtil(pGraph,loc+1);
        }
    }

}
