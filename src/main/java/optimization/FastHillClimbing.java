package optimization;

import algorithm.Dijkstra;
import graph.Graph;
import model.*;
import visual.ShowGraph;

import java.util.*;
import java.util.stream.Collectors;

public class FastHillClimbing {

    Scenario scenario;
    Graph root;

    Dijkstra dij;

    HashMap<Integer,Integer> open;

    HashMap<Integer,ArrayList<SensorNode>> assignment;

    int Q;

    int[][] adj;
    int N;
    int nSink;
    int nOption;

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

    public FastHillClimbing(Scenario scenario, int Q) {
        this.scenario = scenario;
        root = scenario.getRootGraph();

        dij = new Dijkstra(root);

        this.Q = Q;

        adj = root.getAdjacencyMatrix();
        N = root.getVertices().size();
        nSink = scenario.getSinkCandidates().size();
        nOption = scenario.getSinkTypes().size();

        //assignment = new HashMap<>();
    }

    public void solve() {

        //add all sensorNodes to open with their K
        open = new HashMap<>();
        for (int i = 0; i < N; i++) {
            if (root.getVertices().get(i).getAssignedNode() instanceof SensorNode) {
                SensorNode sensor = (SensorNode) root.getVertices().get(i).getAssignedNode();
                open.put(i, sensor.getKC());
            }
        }

        //assign to Sink
        int c = 0;
        for (SinkCandidate sc : scenario.getSinkCandidates()) {
            System.out.println("processing C" + c);
            c++;
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
                        System.out.println("SinkConf " + conf.getModelName() + " can handle this.");
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

                    System.out.println("added sensor " + sensorToAdd.getName() + " to C" + c);
                } else {
                    System.out.println("skipping sensor " + sensorToAdd.getName());
                }
            }
        }

        //show graph after heuristic method
        ShowGraph.showGraph("Heuristic Result", root);

        //if there is uncovered sensor your are messed up
        if (open.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey).count() > 0) {
            System.err.println("you have uncovered sensors!");
            System.out.println(open.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .collect(Collectors.toList()));
        }
    }

}
