package graph;

import model.Node;

import java.awt.*;
import java.util.ArrayList;

public class Vertex {
    private Node node;

    //optional position for better drawing
    Point pos;

    //optional color for debugging purposes
    ArrayList<String> colors;

    public Vertex() {
        colors = new ArrayList<>();
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

    public void addColor(String color) { this.colors.add(color); }
    public void resetColor() { this.colors = new ArrayList<>(); }
    public ArrayList<String> getColor() { return colors; }
}
