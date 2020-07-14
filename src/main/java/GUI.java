import graph.Edge;
import graph.Graph;
import graph.Vertex;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import model.Node;
import model.SensorNode;
import model.SinkNode;

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
        vertices.add(new Vertex(new SensorNode("1",1,2,0,0,200)));
        vertices.add(new Vertex(new SensorNode("2",2,3,20,500,300)));
        vertices.add(new Vertex(new SensorNode("3",2,3,30,500,500)));
        vertices.add(new Vertex(new SensorNode("4",1,2,0,0,200)));
        vertices.add(new Vertex(new SensorNode("5",1,3,0,0,400)));
        vertices.add(new Vertex(new SensorNode("6",2,2,60,600,300)));
        vertices.add(new Vertex(new SensorNode("7",2,3,40,700,400)));
        vertices.add(new Vertex(new SensorNode("8",2,3,50,700,400)));
        vertices.add(new Vertex(new SensorNode("9",1,2,0,0,150)));

        vertices.add(new Vertex(new SinkNode("S1",0,0,3000,1)));
        vertices.add(new Vertex(new SinkNode("S2",0,0,3000,1)));
        vertices.add(new Vertex(new SinkNode("S3",0,0,3000,1)));
        vertices.add(new Vertex(new SinkNode("S4",0,0,3000,1)));

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

        edges.add(new Edge(vertices.get(9),vertices.get(8),1));
        edges.add(new Edge(vertices.get(9),vertices.get(7),1));
        edges.add(new Edge(vertices.get(9),vertices.get(6),1));
        edges.add(new Edge(vertices.get(10),vertices.get(8),1));
        edges.add(new Edge(vertices.get(10),vertices.get(2),1));
        edges.add(new Edge(vertices.get(10),vertices.get(0),1));
        edges.add(new Edge(vertices.get(11),vertices.get(7),1));
        edges.add(new Edge(vertices.get(11),vertices.get(5),1));
        edges.add(new Edge(vertices.get(11),vertices.get(1),1));
        edges.add(new Edge(vertices.get(12),vertices.get(1),1));
        edges.add(new Edge(vertices.get(12),vertices.get(2),1));
        edges.add(new Edge(vertices.get(12),vertices.get(3),1));
        edges.add(new Edge(vertices.get(12),vertices.get(4),1));
        edges.add(new Edge(vertices.get(12),vertices.get(5),1));

        //init GraphViz Object
        HashMap<Vertex,MutableNode> vHash = new HashMap<>();
        MutableGraph mg = mutGraph("example1");
        for(Vertex v: vertices) {
            MutableNode vgv = mutNode(v.getAssignedNode().getName());
            if(v.getAssignedNode() instanceof SensorNode){
                if(((SensorNode) v.getAssignedNode()).isCritical()) vgv.attrs().add("shape","doublecircle");
                else vgv.attrs().add("shape","circle");
            }
            if(v.getAssignedNode() instanceof SinkNode) {
                vgv.attrs().add("shape","box");
            }

            mg.add(vgv);
            vHash.put(v,vgv);
        }
        for(Edge e: edges) {
            vHash.get(e.getSource()).addLink(vHash.get(e.getDestination()));
        }

        graphViz = Graphviz.fromGraph(mg).render(Format.PNG).toImage();
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