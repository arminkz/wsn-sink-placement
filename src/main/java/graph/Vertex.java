package graph;

import model.Node;

public class Vertex {
    private final Node node;

    public Vertex(Node node) {
        this.node = node;
    }

    public Node getAssignedNode() {
        return node;
    }
}
