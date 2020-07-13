import graph.Edge;
import graph.Graph;
import graph.Vertex;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends JPanel {

    BufferedImage graphViz;
    Graph g;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,getWidth(),getHeight());
        if(graphViz != null) {
            int x = (this.getWidth() - graphViz.getWidth())/2;
            int y = (this.getHeight() - graphViz.getHeight())/2;
            g2d.drawImage(graphViz,null,x,y);
        }
    }

    private GUI(){

        //init example graph
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();
        vertices.add(new Vertex("1",false));
        vertices.add(new Vertex("2",true));
        vertices.add(new Vertex("3",true));
        vertices.add(new Vertex("4",false));
        vertices.add(new Vertex("5",false));
        vertices.add(new Vertex("6",true));
        vertices.add(new Vertex("7",true));
        vertices.add(new Vertex("8",true));
        vertices.add(new Vertex("9",false));


        edges.add(new Edge(vertices.get(1),vertices.get(5),1));
        edges.add(new Edge(vertices.get(5),vertices.get(7),1));
        edges.add(new Edge(vertices.get(6),vertices.get(7),1));
        edges.add(new Edge(vertices.get(6),vertices.get(8),1));
        edges.add(new Edge(vertices.get(3),vertices.get(5),1));
        edges.add(new Edge(vertices.get(3),vertices.get(6),1));
        edges.add(new Edge(vertices.get(2),vertices.get(3),1));
        edges.add(new Edge(vertices.get(2),vertices.get(8),1));
        edges.add(new Edge(vertices.get(0),vertices.get(4),1));
        edges.add(new Edge(vertices.get(2),vertices.get(4),1));


        //init GraphViz Object
        HashMap<Vertex,MutableNode> vHash = new HashMap<>();
        MutableGraph mg = mutGraph("example1");
        for(Vertex v: vertices) {
            MutableNode vgv = mutNode(v.getName());
            if(v.getIsCritical()) vgv.attrs().add("shape","doublecircle");
            else vgv.attrs().add("shape","circle");
            mg.add(vgv);
            vHash.put(v,vgv);
        }
        for(Edge e: edges) {
            vHash.get(e.getSource()).addLink(vHash.get(e.getDestination()));
        }

        graphViz = Graphviz.fromGraph(mg).width(this.getWidth()).render(Format.PNG).toImage();
        System.out.println("GV done!");
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}