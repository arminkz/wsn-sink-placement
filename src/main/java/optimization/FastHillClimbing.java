package optimization;

import algorithm.Dijkstra;
import algorithm.Fitness;
import graph.Graph;
import model.*;
import report.Report;
import visual.ShowGraph;

import java.util.*;
import java.util.stream.Collectors;

public class FastHillClimbing {

    Scenario scenario;
    Graph root;

    Dijkstra dij;

    HashMap<Integer,Integer> open;

    int Q;

    int[][] adj;
    int N;
    int nSink;
    int nOption;

    private final int maxCost;

    private Fitness fitnessUtil;

    HillClimbingStrategy strategy;

    String[] colors = {
            "blue",
            "red",
            "green",
            "yellowgreen",
            "cyan",
            "brown",
            "orange",
            "pink"
    };

    public FastHillClimbing(Scenario scenario, int Q, HillClimbingStrategy strategy) {
        this.scenario = scenario;
        this.strategy = strategy;
        root = scenario.getRootGraph();

        dij = new Dijkstra(root);

        this.Q = Q;

        adj = root.getAdjacencyMatrix();
        N = root.getVertices().size();
        nSink = scenario.getSinkCandidates().size();
        nOption = scenario.getSinkTypes().size();

        maxCost = scenario.getSinkCandidates().size() *
                scenario.getSinkTypes().stream().max(Comparator.comparingInt(SinkConfiguration::getCost)).get().getCost();

        fitnessUtil = new Fitness(root, maxCost);
    }

    public Report solve() {
        long startTime = System.currentTimeMillis();
        //add all sensorNodes to open with their K
        open = new HashMap<>();
        for (int i = 0; i < N; i++) {
            if (root.getVertices().get(i).getAssignedNode() instanceof SensorNode) {
                SensorNode sensor = (SensorNode) root.getVertices().get(i).getAssignedNode();
                open.put(i, sensor.getKC());
            }
        }

        //used to store chosen types
        int[] assignment = new int[scenario.getSinkCandidates().size()];

        //assign to Sink
        int c = 0;
        for (SinkCandidate sc : scenario.getSinkCandidates()) {
            //System.out.println("processing C" + c);
            int sci = sc.getPlacmentVertexIndex();

            //put a record for sinkCandidate
            //assignment

            //set sink color
            root.getVertices().get(sci).addColor(colors[c % colors.length]);

            //get uncovered sensors
            List<Integer> uncovered = open.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            //perform dijkstra
            dij.calc(sci);

            //keep record of workload
            int totalCPU = 0;
            int totalRAM = 0;
            int totalBW = 0;

            int addedCount = 0;
            //try to add a sensor
            while (addedCount < Q && !uncovered.isEmpty()) {
                int o = uncovered.remove(0);
                SensorNode sensorToAdd = (SensorNode) root.getVertices().get(o).getAssignedNode();
                if (sensorToAdd.getMaxL() <= dij.getDistance(o)) continue; //this sensor cannot be used

                //check if there is at least one sinkConf to support adding this sensor
                boolean canBeCovered = false;
                for (SinkConfiguration conf : scenario.getSinkTypes()) {
                    if (conf.getCpu() >= totalCPU + sensorToAdd.getTaskCpu() &&
                            conf.getRam() >= totalRAM + sensorToAdd.getTaskRam() &&
                            conf.getBandwidth() >= totalBW + sensorToAdd.getTaskBw()) {
                        //this sensor could be added
                        //System.out.println("SinkConf " + conf.getModelName() + " can handle this.");
                        assignment[c] = scenario.getSinkTypes().indexOf(conf);
                        canBeCovered = true;
                        break;
                    }
                }

                if (canBeCovered) {
                    totalCPU += sensorToAdd.getTaskCpu();
                    totalRAM += sensorToAdd.getTaskRam();
                    totalBW += sensorToAdd.getTaskBw();

                    addedCount++;

                    //assign in hashmap
                    //assignment.get(sci).add((SensorNode) (root.getVertices().get(o).getAssignedNode()));
                    //set sensor colors
                    root.getVertices().get(o).addColor(colors[c % colors.length]);

                    //reduce the K value in open map
                    open.put(o, open.get(o) - 1);

                    //System.out.println("added sensor " + sensorToAdd.getName() + " to C" + c);
                } else {
                    //System.out.println("skipping sensor " + sensorToAdd.getName());
                }
            }
            c++;
        }

        //show graph after heuristic method
        //ShowGraph.showGraph("Local search start point", root);

        //if there are uncovered sensors. you have messed up
        if (open.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey).count() > 0) {
            System.err.println("[FHC] you have uncovered sensors!");
            System.out.println(open.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .collect(Collectors.toList()));
        }

        //start local search
        System.out.println("[FHC] starting local search");
        HillClimbing.HCState start = new HillClimbing.HCState(nSink);
        for (int j = 0; j < nSink; j++) {
            start.dna[j] = assignment[j] + 1;
        }
        HillClimbing.HCState currentState = start;

        Random rnd = new Random();

        while(true) {
            ArrayList<HillClimbing.HCState> neighbours = getNeighbours(currentState);

            if (strategy == HillClimbingStrategy.BASIC) {

                double bestFitness = Integer.MAX_VALUE;
                HillClimbing.HCState nextState = null;
                for (HillClimbing.HCState n : neighbours) {
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

                ArrayList<HillClimbing.HCState> betterStates = new ArrayList<>();
                for(HillClimbing.HCState n : neighbours) {
                    double f = fitness(n);
                    if (f < fitness(currentState)) {
                        betterStates.add(n);
                    }
                }

                if(betterStates.size() == 0) {
                    break;
                }

                HillClimbing.HCState nextState = betterStates.get(rnd.nextInt(betterStates.size()));
                System.out.println("[HC] updating state (Fitness: " + fitness(nextState) + ")");
                currentState = nextState;
            }
            else {
                System.err.println("[HC] undefined strategy !");
            }

        }

        //Return current state since no better neighbors exist
        System.out.println("[FHC] completed !");
        Graph gg = dnaToGraph(currentState.dna);
        double ff = fitnessUtil.calc(gg);
        long time = (System.currentTimeMillis() - startTime);

        System.out.println("[FHC] time: " + time + "ms");
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

    private ArrayList<HillClimbing.HCState> getNeighbours(HillClimbing.HCState state) {
        ArrayList<HillClimbing.HCState> result = new ArrayList<>();
        for (int i = 0; i < nSink; i++) {
            for (int j = 0; j <= nOption; j++) {
                // change the ith dna to j
                HillClimbing.HCState neighbour = new HillClimbing.HCState(nSink);
                neighbour.dna = Arrays.copyOf(state.dna,nSink);
                neighbour.dna[i] = j;
                result.add(neighbour);
            }
        }
        return result;
    }

    private double fitness(HillClimbing.HCState s) {
        // evaluate the graph
        return fitnessUtil.calc(dnaToGraph(s.dna));
    }

}
