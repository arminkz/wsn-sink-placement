package graph;

import model.Node;

import java.util.ArrayList;

public class Vertex {
    private Node node;
    private ArrayList<Edge> edges;

    public Vertex() {
        edges = new ArrayList<>();
    }

    public Vertex(Node node) {
        this.node = node;
        edges = new ArrayList<>();
    }

    public void setNode(Node node) { this.node = node; }

    public Node getAssignedNode() {
        return node;
    }

    public ArrayList<Edge> getEdges() { return edges; }
}
