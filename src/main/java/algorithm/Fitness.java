package algorithm;

import graph.Graph;
import graph.Vertex;
import model.SensorNode;
import model.SinkNode;
import visual.ShowGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Fitness {

    private static final String[] colors = {
            "blue",
            "red",
            "green",
            "yellowgreen",
            "cyan",
            "brown",
            "orange",
            "pink"
    };

    private static void visualizeY(Graph g, int[][] Y) {
        // Hash to get Index in vertices list
        HashMap<Integer,Integer> sinksVI = new HashMap<>();
        HashMap<Integer,Integer> sensorsVI = new HashMap<>();
        int sinkCount = 0;
        int sensorCount = 0;

        for (int i = 0; i < g.getVertices().size(); i++) {
            Vertex v = g.getVertices().get(i);
            if(v.getAssignedNode() instanceof SinkNode) {
                sinksVI.put(sinkCount,i);
                sinkCount++;
            }
            if(v.getAssignedNode() instanceof SensorNode) {
                sensorsVI.put(sensorCount,i);
                sensorCount++;
            }
        }

        //Set Sink Colors
        for (int i = 0; i < sinkCount; i++) {
            Vertex v = g.getVertices().get(sinksVI.get(i));
            v.addColor(colors[i % colors.length]);
        }

        //Set Sensor Colors
        for (int i = 0; i < sensorCount; i++) {
            for (int j = 0; j < sinkCount; j++) {
                if(Y[i][j] == 1){
                    Vertex v = g.getVertices().get(sensorsVI.get(i));
                    v.addColor(colors[j % colors.length]);
                }
            }
        }

        ShowGraph.showGraph("Y",g);
    }
    /////////////////////// Actual Class ///////////////////////
    private final int maxCost;
    //store AllPairDijkstra so that it wouldn't be essential to calculate distance each round
    private final int[][] dist;

    //coefficients
    private final int[] coeff ={1,10,1,1,1};

    public Fitness(Graph g, int maxCost) {
        this.maxCost = maxCost;
        dist = AllPairDijkstra.getAllPairDist(g);
    }

    // Verbose
    // 0: not verbose
    // 1: print Penalty values
    // 2: print full log

    public double calc(Graph g) {
        return calc(g, 0);
    }

    public double calc(Graph g, int verbose) {
        long startTime = System.currentTimeMillis();
        // Hash to get Index in vertices list
        HashMap<Integer,Integer> sinksVI = new HashMap<>();
        HashMap<Integer,Integer> sensorsVI = new HashMap<>();
        // Hash to get Index in sink/sensor list
        HashMap<SinkNode,Integer> sinksI = new HashMap<>();
        HashMap<SensorNode,Integer> sensorsI = new HashMap<>();
        int sinkCount = 0;
        int sensorCount = 0;

        for (int i = 0; i < g.getVertices().size(); i++) {
            Vertex v = g.getVertices().get(i);
            if(v.getAssignedNode() instanceof SinkNode) {
                sinksVI.put(sinkCount,i);
                sinksI.put((SinkNode)v.getAssignedNode(),sinkCount);
                sinkCount++;
            }
            if(v.getAssignedNode() instanceof SensorNode) {
                sensorsVI.put(sensorCount,i);
                sensorsI.put((SensorNode)v.getAssignedNode(),sensorCount);
                sensorCount++;
            }
        }
        if(verbose >= 2) {
            long time = (System.currentTimeMillis() - startTime)/1000;
            System.out.println("[Fitness] Sink and Sensor hashes initiated. (" + time + "s)");
            System.out.println("[Fitness] Graph has " + sensorCount + " sensors and " + sinkCount + " sinks.");
            startTime = System.currentTimeMillis();
        }

        // Z describes order of visiting sensors in order to initialize Y
        int[] Z = new int[sensorCount];
        // init Z
        for (int i = 0; i < sensorCount; i++) {
            Z[i] = i;
        }

        if(verbose >= 2) {
            System.out.println("Z initiated: ");
            for (int i = 0; i < sensorCount; i++) {
                System.out.print(Z[i] + " ");
            }
            System.out.print("\n");
        }

        // Y matrix describes cover relation between sinks and sensors
        int[][] Y = new int[sensorCount][sinkCount];
        for (int i = 0; i < sensorCount; i++) {
            for (int j = 0; j < sinkCount; j++) {
                Y[i][j] = 0;
            }
        }

        // track load on Sinks on building Y
        HashMap<SinkNode,Integer> loadCPU = new HashMap<>();
        HashMap<SinkNode,Integer> loadRAM = new HashMap<>();
        HashMap<SinkNode,Integer> loadBW = new HashMap<>();
        for (int i = 0; i < sinkCount; i++) {
            SinkNode sk = (SinkNode)(g.getVertices().get(sinksVI.get(i)).getAssignedNode());
            loadCPU.put(sk,0);
            loadRAM.put(sk,0);
            loadBW.put(sk,0);
        }

        // build Y
        long roundStartTime = System.currentTimeMillis();
        for (int i = 0; i < sensorCount; i++) {
            int sensorIndex = Z[i];
            Vertex v = g.getVertices().get(sensorsVI.get(sensorIndex));
            SensorNode sn = (SensorNode)v.getAssignedNode();

            if (verbose >= 2){
                if (i>0) {
                    long roundTime = (System.currentTimeMillis() - roundStartTime)/1000;
                    System.out.println("[Fitness] round took " + roundTime + "ms");
                    roundStartTime = System.currentTimeMillis();
                }
                System.out.println("[Fitness] calculating covering set for sensor " + v.getAssignedNode().getName());
            }

            ArrayList<Vertex> fetchedSinks = new ArrayList<>();
            for (int j = 0; j < sinkCount; j++) {
                Vertex v2 = g.getVertices().get(sinksVI.get(j));
                int distance = dist[sensorsVI.get(sensorIndex)][sinksVI.get(j)];
                if (distance <= sn.getMaxL()) {
                    fetchedSinks.add(v2);
                }
            }

            // sort by distance
            fetchedSinks.sort(Comparator.comparingInt((o) ->
                dist[sensorsVI.get(sensorIndex)][g.getVertices().indexOf(o)]
            ));
            // 1st element is the closest sink
            if (verbose >= 2) {
                System.out.println("[Fitness] " + fetchedSinks.size() + " sinks are in range for covering." );
            }

            ArrayList<Vertex> rejectedBecauseOfLoad = new ArrayList<>();

            int elected = 0;
            while (elected < sn.getKC() && !fetchedSinks.isEmpty()) {
                Vertex v2 = fetchedSinks.remove(0);
                SinkNode sk = (SinkNode)v2.getAssignedNode();
                // elect if by adding this cover; sink doesn't get overloaded
                if(sn.getTaskCpu() + loadCPU.get(sk) <= sk.getCpu()
                && sn.getTaskRam() + loadRAM.get(sk) <= sk.getRam()
                && sn.getTaskBw() + loadBW.get(sk) <= sk.getBandwidth()) {
                    elected++;
                    // Update loads
                    loadCPU.put(sk,sn.getTaskCpu() + loadCPU.get(sk));
                    loadRAM.put(sk,sn.getTaskRam() + loadRAM.get(sk));
                    loadBW.put(sk,sn.getTaskBw() + loadBW.get(sk));
                    // Mark As Covering in Y
                    Y[sensorsI.get(sn)][sinksI.get(sk)] = 1;
                }else{
                    rejectedBecauseOfLoad.add(v2);
                }
            }
            // after the loop we have elected at most k sinks considering load
            // if we are still short on sinks ignore load and just add them
            if (verbose >= 2) {
                System.out.println("[Fitness] " + rejectedBecauseOfLoad.size() + " sinks were rejected because of load constraints." );
            }
            while(elected < sn.getKC() && !rejectedBecauseOfLoad.isEmpty()) {
                Vertex v2 = rejectedBecauseOfLoad.remove(0);
                SinkNode sk = (SinkNode)v2.getAssignedNode();

                elected++;
                // Update loads (will overload them)
                loadCPU.put(sk,sn.getTaskCpu() + loadCPU.get(sk));
                loadRAM.put(sk,sn.getTaskRam() + loadRAM.get(sk));
                loadBW.put(sk,sn.getTaskBw() + loadBW.get(sk));
                // Mark As Covering in Y
                Y[sensorsI.get(sn)][sinksI.get(sk)] = 1;
            }
            if (verbose >= 2) {
                System.out.println("[Fitness] eventually " + elected + " sinks got selected for covering.");
            }
        }

        if(verbose >= 2) {
            if (Y[0].length == 0) {
                System.out.println("Y is empty because there isn't any sinks on the graph!");
            }
            else {
                System.out.println("Y: ");
                for (int i = 0; i < sensorCount; i++) {
                    for (int j = 0; j < sinkCount; j++) {
                        System.out.print(Y[i][j] + " ");
                    }
                    System.out.print("\n");
                }
            }
            visualizeY(g, Y);
        }

        if(verbose >= 2) {
            long time = (System.currentTimeMillis() - startTime)/1000;
            System.out.println("[Fitness] Y matrix initiated. (" + time + "s)");
            startTime = System.currentTimeMillis();
        }

        //Now we have Y ready we can calculate fitness
        //Sensor Based Penalties
        int P1 = 0;
        int P1_max = 0;
        for (int i = 0; i < sensorCount; i++) {
            SensorNode sn = (SensorNode)(g.getVertices().get(sensorsVI.get(i)).getAssignedNode());
            int sum = 0;
            for (int j = 0; j < sinkCount; j++) {
                sum += Y[i][j];
            }
            P1 += Math.max(0, sn.getKC() - sum);
            P1_max += sn.getKC();
        }

        //Sink Based Penalties
        int cost = 0;
        int P2 = 0;
        int P2_max = 0;
        int P3 = 0;
        int P3_max = 0;
        int P4 = 0;
        int P4_max = 0;
        for (int i = 0; i < sinkCount; i++) {
            SinkNode sk = (SinkNode)(g.getVertices().get(sinksVI.get(i)).getAssignedNode());
            cost += sk.getCost();
            P2 += Math.max(0, loadCPU.get(sk) - sk.getCpu());
            P2_max += loadCPU.get(sk);
            P3 += Math.max(0, loadRAM.get(sk) - sk.getRam());
            P3_max += loadRAM.get(sk);
            P4 += Math.max(0, loadBW.get(sk) - sk.getBandwidth());
            P4_max += loadBW.get(sk);
        }

        double cost_normal = (double)cost / maxCost;
        double P1_normal = (P1 != 0) ? (double)P1 / P1_max : 0;
        double P2_normal = (P2 != 0) ? (double)P2 / P2_max : 0;
        double P3_normal = (P3 != 0) ? (double)P3 / P3_max : 0;
        double P4_normal = (P4 != 0) ? (double)P4 / P4_max : 0;

        if(verbose >= 2) {
            long time = (System.currentTimeMillis() - startTime)/1000;
            System.out.println("[Fitness] Penalties calculated. (" + time + "s)");
            startTime = System.currentTimeMillis();
        }

        //print normalized sub-scores
        if(verbose >= 1) {
            System.out.println("[Fitness] Cost Score: " + cost_normal);
            System.out.println("[Fitness] P1 Score: " + P1_normal);
            System.out.println("[Fitness] P2 Score: " + P2_normal);
            System.out.println("[Fitness] P3 Score: " + P3_normal);
            System.out.println("[Fitness] P4 Score: " + P4_normal);
        }

        return (coeff[0] * cost_normal + coeff[1] * P1_normal +
                coeff[2] * P2_normal + coeff[3] * P3_normal +
                coeff[4] * P4_normal) / Arrays.stream(coeff).sum();
    }

}
