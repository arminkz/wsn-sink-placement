package optimization;

import algorithm.Fitness;
import graph.Graph;
import model.Scenario;
import model.SinkCandidate;
import model.SinkConfiguration;
import model.SinkNode;
import report.Report;
import visual.ShowGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class HillClimbing {

    static class HCState {
        public int[] dna;

        public HCState(int dna_size) {
            this.dna = new int[dna_size];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("| ");
            for(int value : dna) {
                sb.append(value);
                sb.append(" | ");
            }
            return sb.toString();
        }
    }

    Scenario scenario;
    HillClimbingStrategy strategy;
    Graph root;
    int nSink;
    int nOption;

    private final int maxCost;

    private Fitness fitnessUtil;

    public HillClimbing(Scenario scenario, HillClimbingStrategy strategy) {
        this.scenario = scenario;
        this.strategy = strategy;
        root = scenario.getRootGraph();
        nSink = scenario.getSinkCandidates().size();
        nOption = scenario.getSinkTypes().size();

        maxCost = scenario.getSinkCandidates().size() *
                scenario.getSinkTypes().stream().max(Comparator.comparingInt(SinkConfiguration::getCost)).get().getCost();

        fitnessUtil = new Fitness(root, maxCost);
    }

    public Report solve() {
        long startTime = System.currentTimeMillis();
        Random rnd = new Random();

        HCState currentState = initialState();

        while(true) {
            ArrayList<HCState> neighbours = getNeighbours(currentState);

            if (strategy == HillClimbingStrategy.BASIC) {

                double bestFitness = Integer.MAX_VALUE;
                HCState nextState = null;
                for (HCState n : neighbours) {
                    double f = fitness(n);
                    if (f < bestFitness) {
                        nextState = n;
                        bestFitness = f;
                    }
                }

                if (bestFitness >= fitness(currentState)) {
                    break;
                }

                System.out.println("[HC] updating state (Fitness: " + bestFitness + ")");
                currentState = nextState;

            }
            else if(strategy == HillClimbingStrategy.STOCHASTIC) {

                ArrayList<HCState> betterStates = new ArrayList<>();
                for(HCState n : neighbours) {
                    double f = fitness(n);
                    if (f < fitness(currentState)) {
                        betterStates.add(n);
                    }
                }

                if(betterStates.size() == 0) {
                    break;
                }

                HCState nextState = betterStates.get(rnd.nextInt(betterStates.size()));
                System.out.println("[HC] updating state (Fitness: " + fitness(nextState) + ")");
                currentState = nextState;
            }
            else {
                System.err.println("[HC] undefined strategy !");
            }

        }

        //Return current state since no better neighbors exist
        System.out.println("[HC] completed !");
        Graph gg = dnaToGraph(currentState.dna);
        double ff = fitnessUtil.calc(gg);
        long time = (System.currentTimeMillis() - startTime);

        System.out.println("[HC] time: " + time + "ms");

        //ShowGraph.showGraphWithCoverage("[HC] best answer (fitness: " + Fitness.calc(gg,maxCost) + ")",gg);
        return new Report(gg,ff,time);

    }

    private Graph dnaToGraph(int[] dna) {
        Graph pGraph = root.clone();
        // create graph based on dna
        for (int i = 0; i < dna.length; i++) {
            int dnaValue = dna[i];
            // if dna value is 0 we don't need to place anything
            if(dnaValue != 0) {
                // the candidate we are placing now
                SinkCandidate candidate = scenario.getSinkCandidates().get(i);
                // the sink type we're placing
                SinkConfiguration config = scenario.getSinkTypes().get(dnaValue-1);
                // place sink
                int vi = candidate.getPlacmentVertexIndex();
                pGraph.getVertices().get(vi).setNode(new SinkNode("S" + (i + 1), config));
            }
        }
        return pGraph;
    }

    private HCState initialState() {
        Random rnd = new Random();
        HCState s = new HCState(nSink);
        for (int j = 0; j < nSink; j++) {
            s.dna[j] = rnd.nextInt(nOption+1);
        }
        return s;
    }

    private ArrayList<HCState> getNeighbours(HCState state) {
        ArrayList<HCState> result = new ArrayList<>();
        for (int i = 0; i < nSink; i++) {
            for (int j = 0; j <= nOption; j++) {
                // change the ith dna to j
                HCState neighbour = new HCState(nSink);
                neighbour.dna = Arrays.copyOf(state.dna,nSink);
                neighbour.dna[i] = j;
                result.add(neighbour);
            }
        }
        return result;
    }

    private double fitness(HCState s) {
        // evaluate the graph
        return fitnessUtil.calc(dnaToGraph(s.dna));
    }
}
