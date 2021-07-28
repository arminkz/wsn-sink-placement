package visual;

import algorithm.AllPairDijkstra;
import algorithm.Dijkstra;
import graph.Graph;
import graph.Vertex;
import model.SensorNode;
import model.SinkNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ShowGraph extends JPanel {

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

    public static void showGraph(String title, Graph g) {
        JPanel parent = new JPanel(new BorderLayout());
        GraphPanel graphPanel = new GraphPanel(g);
        parent.add(graphPanel);

        JFrame window = new JFrame(title);
        window.setSize(graphPanel.getOptimalSize());
        window.add(parent);
        window.setVisible(true);
    }

    public static void showGraphWithCoverage(String title, Graph g) {
        // Hash to get Index in vertices list
        HashMap<Integer,Integer> sinksVI = new HashMap<>();
        HashMap<Integer,Integer> sensorsVI = new HashMap<>();
        // Hash to get Index in sink/sensor list
        HashMap<SinkNode,Integer> sinksI = new HashMap<>();
        HashMap<SensorNode,Integer> sensorsI = new HashMap<>();
        int sinkCount = 0;
        int sensorCount = 0;

        int[][] dist = AllPairDijkstra.getAllPairDist(g);

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

        showGraph(title,g);
    }

}
