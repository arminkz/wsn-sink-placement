package graph;

import model.Node;

import java.awt.*;

public class Vertex {
    private Node node;

    //optional position for better drawing
    Point pos;

    public Vertex() {
    }

    public Vertex(Node node) {
        this.node = node;
    }

    public void setNode(Node node) { this.node = node; }
    public Node getAssignedNode() {
        return node;
    }

    public void setPos(Point pos) { this.pos = pos; }
    public Point getPos() { return pos; }
}
