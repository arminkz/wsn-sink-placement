package graph;

import model.Node;

import java.util.ArrayList;

public class Vertex {
    private final Node node;
    private ArrayList<Edge> edges;

    public Vertex(Node node) {
        this.node = node;
        edges = new ArrayList<>();
    }

    public Node getAssignedNode() {
        return node;
    }

    public ArrayList<Edge> getEdges() { return edges; }
}
