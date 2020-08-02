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

    public void addVertex(Vertex v) {
        vertices.add(v);
    }

    public void addEdge(Vertex v1, Vertex v2, int weight) {
        Edge e = new Edge(v1,v2,weight);
        edges.add(e);
        v1.getEdges().add(e);
        v2.getEdges().add(e);
    }

    //TODO: this code must be reviewed (should vertices and edges clone too?)
//    public Graph clone() {
//        Graph g = new Graph();
//        g.vertices.addAll(this.vertices);
//        g.edges.addAll(this.edges);
//        return g;
//    }


}
