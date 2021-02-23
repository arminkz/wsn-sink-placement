package algorithm;

import graph.Graph;
import graph.Vertex;
import model.SensorNode;
import model.SinkNode;
import visual.ShowGraph;

import java.util.ArrayList;
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

    public static double calc(Graph g, int maxCost) {
        return calc(g, maxCost, false);
    }

    public static double calc(Graph g, int maxCost, boolean verbose) {
        // Hash to get Index in vertices list
        HashMap<Integer,Integer> sinksVI = new HashMap<>();
        HashMap<Integer,Integer> sensorsVI = new HashMap<>();
        // Hash to get Index in sink/sensor list
        HashMap<SinkNode,Integer> sinksI = new HashMap<>();
        HashMap<SensorNode,Integer> sensorsI = new HashMap<>();
        int sinkCount = 0;
        int sensorCount = 0;

        Dijkstra dij = new Dijkstra(g);

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

        // Z describes order of visiting sensors in order to initialize Y
        int[] Z = new int[sensorCount];
        // init Z
        for (int i = 0; i < sensorCount; i++) {
            Z[i] = i;
        }

        if(verbose) {
            System.out.println("Z:");
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
        for (int i = 0; i < sensorCount; i++) {
            int sensorIndex = Z[i];
            Vertex v = g.getVertices().get(sensorsVI.get(sensorIndex));
            SensorNode sn = (SensorNode)v.getAssignedNode();

            dij.calc(v);

            ArrayList<Vertex> fetchedSinks = new ArrayList<>();
            for (int j = 0; j < sinkCount; j++) {
                Vertex v2 = g.getVertices().get(sinksVI.get(j));
                if (dij.getDistance(v2) <= sn.getMaxL()) {
                    fetchedSinks.add(v2);
                }
            }
            // sort by distance
            fetchedSinks.sort(Comparator.comparingInt(dij::getDistance));
            // 1st element is the closest sink

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
        }

        if(verbose) {
            System.out.println("Y: ");
            for (int i = 0; i < sensorCount; i++) {
                for (int j = 0; j < sinkCount; j++) {
                    System.out.print(Y[i][j] + " ");
                }
                System.out.print("\n");
            }
            visualizeY(g, Y);
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

        //TODO: coeff for each nominal
        double cost_normal = (double)cost / maxCost;
        double P1_normal = (double)P1 / P1_max;
        double P2_normal = (double)P2 / P2_max;
        double P3_normal = (double)P3 / P3_max;
        double P4_normal = (double)P4 / P4_max;

        //print normalized sub-scores
        if(verbose) {
            System.out.println("[Fitness] Cost Score: " + cost_normal);
            System.out.println("[Fitness] P1 Score: " + P1_normal);
            System.out.println("[Fitness] P2 Score: " + P2_normal);
            System.out.println("[Fitness] P3 Score: " + P3_normal);
            System.out.println("[Fitness] P4 Score: " + P4_normal);
        }
        return (cost_normal + P1_normal + P2_normal + P3_normal + P4_normal) / 5;
    }

}
