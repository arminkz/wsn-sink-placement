package algorithm;

import graph.Graph;
import model.SensorNode;

import java.util.HashMap;

public class AllPairDijkstra {

    // Memoization
    static HashMap<String,int[][]> cache;

    public static int[][] getAllPairDist(Graph g) {
        return getAllPairDist(g,true);
    }

    public static int[][] getAllPairDist(Graph g, boolean verbose) {

        if (cache == null) cache = new HashMap<>();

        if (cache.containsKey(g.getName())){
            if (verbose) System.out.println("[APD] Using cached result for " + g.getName());
            return cache.get(g.getName());
        }

        int N = g.getVertices().size();
        Dijkstra dij = new Dijkstra(g);
        int[][] dist = new int[N][N];

        // init dist
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }

        // Note: Sink nodes cannot act as routers in graph
        // (they don't pass messages as an intermediate)
        // Calculate sensor-to-sensor distances
        int dijCount = 1;
        for (int i = 0; i < N; i++) {
            if (g.getVertices().get(i).getAssignedNode() instanceof SensorNode){
                if (verbose) System.out.println("[APD] Performing Dijkstra... (" + dijCount + "/" + N + ")");
                dijCount++;
                dij.calc(i);
                for (int j = 0; j < N; j++) {
                    if (g.getVertices().get(j).getAssignedNode() instanceof SensorNode){
                        dist[i][j] = dij.getDistance(j);
                    }
                }
            }
        }

        // Calculate sink-to-sensor distances
        for (int i = 0; i < N; i++) {
            if (!(g.getVertices().get(i).getAssignedNode() instanceof SensorNode)) {
               if (verbose) System.out.println("[APD] Performing Dijkstra... (" + dijCount + "/" + N + ")");
                dijCount++;
                dij.calc(i);
                for (int j = 0; j < N; j++) {
                    if (g.getVertices().get(j).getAssignedNode() instanceof SensorNode){
                        dist[i][j] = dij.getDistance(j);
                        dist[j][i] = dij.getDistance(j);
                    }
                }
            }
        }

        // memorize the result
        cache.put(g.getName(), dist);

        return dist;
    }

}
