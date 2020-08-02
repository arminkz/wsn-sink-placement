package model;

import com.kitfox.svg.A;
import graph.Edge;
import graph.Vertex;

import java.util.ArrayList;

public class SinkCandidate {

    private Vertex placmentVertex;
    private final ArrayList<Edge> placmentEdges;

    public SinkCandidate() {
        placmentEdges = new ArrayList<>();
    }

    public Vertex getPlacmentVertex() { return placmentVertex; }
    public void setPlacmentVertex(Vertex v) { this.placmentVertex = v; }

    public ArrayList<Edge> getPlacmentEdges() { return placmentEdges; }

}
