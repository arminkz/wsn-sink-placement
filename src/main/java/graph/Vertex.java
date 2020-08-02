package graph;

import model.Node;

public class Vertex {
    private Node node;

    public Vertex() {
    }

    public Vertex(Node node) {
        this.node = node;
    }

    public void setNode(Node node) { this.node = node; }
    public Node getAssignedNode() {
        return node;
    }
}
