//Created by Armin (github.com/arminkz)

import org.apache.commons.math3.random.HaltonSequenceGenerator;

import graph.Graph;
import graph.Vertex;
import model.SensorNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GenerateGraph {

    public GenerateGraph() {
    }

    //Generate Example Scenario Using WSN wireless range model
    public static Graph realistic(int sensor_count, int sc_count, int tx, int ty, double wireless_range){
        Graph g = new Graph();
        ArrayList<Point> placmentPoints = new ArrayList<>();
        int n = sensor_count + sc_count;
        Random rnd = new Random();
        HaltonSequenceGenerator halton = new HaltonSequenceGenerator(2);

        for (int i = 0; i < n; i++) {
            double[] vec = halton.nextVector();
            int px = (int)(vec[0] * tx);
            int py = (int)(vec[1] * ty);
            Point pt = new Point(px,py);
            placmentPoints.add(pt);
            Vertex v = new Vertex();
            v.setPos(pt);
            g.addVertex(v);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                Point p1 = placmentPoints.get(i);
                Point p2 = placmentPoints.get(j);
                double dist = Math.hypot(p2.x-p1.x,p2.y-p1.y);
                if(dist <= wireless_range) {
                    //create edge
                    g.addEdge(g.getVertices().get(i),g.getVertices().get(j),1);
                }
            }
        }

        //place sink candidates
        ArrayList<Integer> possibleSCs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            possibleSCs.add(i);
        }
        for (int i = 0; i < sc_count; i++) {
            int k = rnd.nextInt(possibleSCs.size());
            int index = possibleSCs.get(k);
            possibleSCs.remove(k);
            //place a sink candidate on index
            //g.getVertices().get(index).setNode();
        }
        //place sensors
        for (int i = 0; i < possibleSCs.size(); i++) {
            int index = possibleSCs.get(i);
            g.getVertices().get(index).setNode(new SensorNode("S"+i,3,3,0,0,0));
        }

        return g;

    }

    //Generate Example Scenario Using Erdős–Rényi model
    public static Graph erdos(int sensor_count, int sc_count, double p) {
        Graph g = new Graph();
        int n = sensor_count + sc_count;
        ArrayList<Vertex> vertices = new ArrayList<>();
        Random rnd = new Random();

        //create vertices
        for (int i = 0; i < n; i++) {
            g.addVertex(new Vertex());
        }

        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                double s = rnd.nextDouble();
                if(s < p) {
                    //create edge
                    g.addEdge(g.getVertices().get(i),g.getVertices().get(j),1);
                }
            }
        }

        //place sink candidates
        ArrayList<Integer> possibleSCs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            possibleSCs.add(i);
        }
        for (int i = 0; i < sc_count; i++) {
            int k = rnd.nextInt(possibleSCs.size());
            int index = possibleSCs.get(k);
            possibleSCs.remove(k);
            //place a sink candidate on index
            //g.getVertices().get(index).setNode();
        }
        //place sensors
        for (int i = 0; i < possibleSCs.size(); i++) {
            int index = possibleSCs.get(i);
            g.getVertices().get(index).setNode(new SensorNode("S"+i,3,3,0,0,0));
        }

        return g;
    }

}
