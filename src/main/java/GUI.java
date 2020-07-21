import algorithm.Checker;
import algorithm.Dijkstra;
import graph.Graph;
import model.SensorNode;
import model.SinkNode;
import visual.GraphViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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

        Graph g = new Graph();

        //init example graph
        g.addNode(new SensorNode("1",1,2,0,0,200));
        g.addNode(new SensorNode("2",2,3,20,500,300));
        g.addNode(new SensorNode("3",2,3,30,500,500));
        g.addNode(new SensorNode("4",1,2,0,0,200));
        g.addNode(new SensorNode("5",1,3,0,0,400));
        g.addNode(new SensorNode("6",2,2,60,600,300));
        g.addNode(new SensorNode("7",2,3,40,700,400));
        g.addNode(new SensorNode("8",2,3,50,700,400));
        g.addNode(new SensorNode("9",1,2,0,0,150));

        g.addNode(new SinkNode("S1",0,0,3000,1));
        g.addNode(new SinkNode("S2",0,0,3000,1));
        g.addNode(new SinkNode("S3",0,0,3000,1));
        g.addNode(new SinkNode("S4",0,0,3000,1));

        g.addEdge(1,5,1);
        g.addEdge(5,7,1);
        g.addEdge(6,7,1);
        g.addEdge(6,8,1);
        g.addEdge(3,5,1);
        g.addEdge(3,6,1);
        g.addEdge(2,3,1);
        g.addEdge(2,8,1);
        g.addEdge(0,4,1);
        g.addEdge(2,4,1);
        g.addEdge(9,8,1);
        g.addEdge(9,7,1);
        g.addEdge(9,6,1);
        g.addEdge(10,8,1);
        g.addEdge(10,2,1);
        g.addEdge(10,0,1);
        g.addEdge(11,7,1);
        g.addEdge(11,5,1);
        g.addEdge(11,1,1);
        g.addEdge(12,1,1);
        g.addEdge(12,2,1);
        g.addEdge(12,3,1);
        g.addEdge(12,4,1);
        g.addEdge(12,5,1);

        graphViz = GraphViewer.viewGraph(g);
        System.out.println("GV done!");

        Dijkstra dij = new Dijkstra(g);
        dij.calc(1);
        int d = dij.getDistance(9);
        System.out.println("distance from 2 to 10(S1) is : " + d);

        for (int i = 0; i < 9; i++) {
            System.out.println("Node " + (i+1) + " K Covered: " + Checker.isKCovered(g,i));
        }
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}