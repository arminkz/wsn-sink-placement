package graph;

import model.SinkCandidate;
import java.util.ArrayList;

public class Graph {

    private final ArrayList<Vertex> vertices;
    private final ArrayList<Edge> edges;

    public Graph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public Graph(int[][] adj) {
        int N = adj.length;
        // add vertices
        this.vertices = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            this.vertices.add(new Vertex());
        }
        // add edges
        this.edges = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = i+1; j < N; j++) {
                if(adj[i][j] != -1) {
                    Vertex v1 = vertices.get(i);
                    Vertex v2 = vertices.get(j);
                    Edge e = new Edge(v1,v2,adj[i][j]);
                    this.edges.add(e);
                }
            }
        }
    }

    public int[][] getAdjacencyMatrix() {
        int N = vertices.size();
        int[][] adj = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                adj[i][j] = -1; //not connected
            }
        }
        for(Edge e: getEdges()) {
            int v1 = getVertices().indexOf(e.getSource());
            int v2 = getVertices().indexOf(e.getDestination());
            adj[v1][v2] = e.getWeight();
            adj[v2][v1] = e.getWeight();
        }
        return adj;
    }

    public ArrayList<Vertex> getVertices() { return vertices; }

    public ArrayList<Edge> getEdges() { return edges; }

    public void addVertex(Vertex v) {
        vertices.add(v);
    }

    public void addEdge(Vertex v1, Vertex v2, int weight) {
        Edge e = new Edge(v1,v2,weight);
        edges.add(e);
    }

    @Override
    public Graph clone() {
        Graph g = new Graph(this.getAdjacencyMatrix());
        // fix node assignments
        for (int i = 0; i < vertices.size(); i++) {
            g.vertices.get(i).setNode(this.vertices.get(i).getAssignedNode());
        }
        return g;
    }






}
