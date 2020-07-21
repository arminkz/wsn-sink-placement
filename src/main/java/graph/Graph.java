package graph;

import model.Node;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final ArrayList<Vertex> vertices;
    private final ArrayList<Edge> edges;

    public Graph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public ArrayList<Vertex> getVertices() { return vertices; }
    public ArrayList<Edge> getEdges() { return edges; }

    public void addNode(Node node) {
        vertices.add(new Vertex(node));
    }

    public void addEdge(int from,int to,int weight) {
        Edge e = new Edge(vertices.get(from),vertices.get(to),weight);
        edges.add(e);
        vertices.get(from).getEdges().add(e);
        vertices.get(to).getEdges().add(e);
    }


}
