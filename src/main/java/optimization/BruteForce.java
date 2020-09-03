package optimization;

import algorithm.FitnessEvaluator;
import graph.Graph;
import model.*;
import visual.ShowGraph;

import java.util.Comparator;

public class BruteForce {

    Scenario scenario;
    Graph root;

    int nSink;
    int nOption;

    private final double answerSpace;
    private double leafCount = 0;
    private int progress = -1;

    private Graph bestAnswer = null;
    private int bestFitness = Integer.MIN_VALUE;

    private final int maxCost;

    public BruteForce(Scenario scenario) {
        this.scenario = scenario;
        root = scenario.getRootGraph();
        nSink = scenario.getSinkCandidates().size();
        nOption = scenario.getSinkTypes().size();
        answerSpace = Math.pow(nOption+1,nSink);

        maxCost = scenario.getSinkCandidates().size() *
                scenario.getSinkTypes().stream().max(Comparator.comparingInt(SinkConfiguration::getCost)).get().getCost();
    }

    public void solve() {
        solveUtil(root,0);
        if(bestAnswer == null) {
            System.out.println("[BF] there is no feasible answer to this problem.");
        } else {
            System.out.println("[BF] brute force explored " + leafCount + " states.");
            ShowGraph.showGraph("[BF] best answer (fitness=" + bestFitness + ")" ,bestAnswer);
        }

    }

    private void solveUtil(Graph graph, int loc) {
        if(loc == nSink) {
            // backtrack reached leaf
            leafCount++;

            double p = (leafCount / answerSpace) * 100;
            if((int)p != progress) {
                progress = (int)p;
                System.out.println("[BF] progress " + progress + "%");
            }

            int fitness = FitnessEvaluator.evaluate(graph,maxCost);
            if(fitness > 0) {
                // solution is feasible
                System.out.println("[BF] feasible graph reached with fitness " + fitness);
            }
            if(bestAnswer == null || fitness > bestFitness) {
                bestFitness = fitness;
                bestAnswer = graph;
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
