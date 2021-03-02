package optimization;

import algorithm.Fitness;
import graph.Graph;
import model.*;
import report.Report;
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
    private double bestFitness = Double.MAX_VALUE;

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

    public Report solve() {
        long startTime = System.currentTimeMillis();
        solveUtil(root,0);
        if(bestAnswer == null) {
            System.out.println("[BF] there is no feasible answer to this problem.");
            return null;
        } else {
            long time = (System.currentTimeMillis() - startTime);
            System.out.println("[BF] brute force explored " + leafCount + " states.");
            System.out.println("[BF] execution time: " + time + "ms");
            return new Report(bestAnswer, bestFitness, time);
            //ShowGraph.showGraphWithCoverage("[BF] best answer (fitness=" + bestFitness + ")" ,bestAnswer);
        }
    }

    private void solveUtil(Graph graph, int loc) {
        if(loc == nSink) {
            // backtrack reached leaf
            leafCount++;
            // ShowGraph.showGraph("fitness: " + Fitness.calc(graph,maxCost),graph);

            double p = (leafCount / answerSpace) * 100;
            if((int)p != progress) {
                progress = (int)p;
                System.out.print("\r[BF] progress " + progress + "%");
                if(progress == 100) System.out.print("\n");
            }

            double fitness = Fitness.calc(graph, maxCost);
            if (Double.isNaN(fitness)) return;

            if(bestAnswer == null || fitness < bestFitness) {
                bestFitness = fitness;
                bestAnswer = graph;
                System.out.print("\rBest Fitness Updated : " + bestFitness);
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
