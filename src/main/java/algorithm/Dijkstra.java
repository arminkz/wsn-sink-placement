package algorithm;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

import java.util.*;

public class Dijkstra {

    class DijNode implements Comparable<DijNode> {

        public Vertex vertex;
        public int cost;

        public DijNode(Vertex vertex,int cost) {
            this.vertex = vertex;
            this.cost = cost;
        }

        @Override
        public int compareTo(DijNode o) {
            return Integer.compare(this.cost, o.cost);
        }
    }

    private final Graph graph;
    private final int N;
    private final int[][] adj;

    private HashMap<Vertex,Integer> dist;

    public Dijkstra(Graph graph) {
        this.graph = graph;
        N = graph.getVertices().size();
        // create adjacency matrix
        adj = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                adj[i][j] = -1; //not connected
            }
        }
        for(Edge e: graph.getEdges()) {
            int v1 = graph.getVertices().indexOf(e.getSource());
            int v2 = graph.getVertices().indexOf(e.getDestination());
            adj[v1][v2] = e.getWeight();
            adj[v2][v1] = e.getWeight();
        }
    }

    public void calc(int src){
        calc(graph.getVertices().get(src));
    }

    public void calc(Vertex src) {
        PriorityQueue<DijNode> pq = new PriorityQueue<>(N);
        Set<Vertex> visited = new HashSet<>();
        dist = new HashMap<>();
        // init dist
        for(Vertex v: graph.getVertices()){
            dist.put(v,Integer.MAX_VALUE);
        }
        // Distance to the source is 0
        dist.replace(src,0);
        // Add source vertex to priorityQueue
        pq.add(new DijNode(src,0));

        while(visited.size() != N) {

            Vertex v = pq.remove().vertex;
            // vertex is visited
            visited.add(v);


            // process neighbours
            int vIndex = graph.getVertices().indexOf(v);
            for (int i = 0; i < N; i++) {
                if(adj[vIndex][i] != -1) {
                    Vertex neighbour = graph.getVertices().get(i);
                    int w = adj[vIndex][i];

                    if(!visited.contains(neighbour) && neighbour.getAssignedNode() != null) {
                        int newDist = dist.get(v) + w;
                        if(newDist < dist.get(neighbour)){
                            // update neighbour distance
                            dist.replace(neighbour,newDist);
                        }
                        // add neighbour to priorityQueue
                        pq.add(new DijNode(neighbour,dist.get(neighbour)));
                    }
                }
            }
        }
    }

    public int getDistance(int target) {
        return getDistance(graph.getVertices().get(target));
    }
    public int getDistance(Vertex target) {
        return dist.get(target);
    }


}
