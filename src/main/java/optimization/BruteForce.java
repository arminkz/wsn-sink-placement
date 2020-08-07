package optimization;

import algorithm.Evaluator;
import graph.Graph;
import graph.Vertex;
import model.*;
import visual.ShowGraph;

import java.sql.SQLOutput;

public class BruteForce {

    Scenario scenario;
    Graph root;

    int nSink;
    int nOption;

    private int leafCount = 0;
    private Graph bestAnswer = null;
    private int bestCost = Integer.MAX_VALUE;

    public BruteForce(Scenario scenario) {
        this.scenario = scenario;
        root = scenario.getRootGraph();
        nSink = scenario.getSinkCandidates().size();
        nOption = scenario.getSinkTypes().size();
    }

    public void solve() {
        solveUtil(root,0);
        if(bestAnswer == null) {
            System.out.println("there is no feasible answer to this problem.");
        } else {
            System.out.println("brute force explored " + leafCount + " states.");
            ShowGraph.showGraph("best answer (cost=" + bestCost + ")" ,bestAnswer);
        }

    }

    private void solveUtil(Graph graph, int loc) {
        if(loc == nSink) {
            // backtrack reached leaf
            leafCount++;

            int cost = Evaluator.evaluate(graph);
            if(cost != Integer.MAX_VALUE) {
                // solution is feasible
                System.out.println("feasible graph reached with cost " + cost);
                if(bestAnswer == null || cost < bestCost) {
                    bestCost = cost;
                    bestAnswer = graph;
                }
            }
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
