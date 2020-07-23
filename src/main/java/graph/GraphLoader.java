package graph;

import model.SensorNode;
import model.SinkNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GraphLoader {

    public static Graph loadFromFile(String path) {
        try {
            File sensors = new File(path + "sensors.csv");
            File sinks = new File(path + "sinks.csv");
            File edges = new File(path + "edges.csv");
            Scanner scn;
            Graph g = new Graph();
            //Load Sensors
            scn = new Scanner(sensors, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                g.addNode(new SensorNode(
                        segments[0],
                        Integer.parseInt(segments[1]),
                        Integer.parseInt(segments[2]),
                        Integer.parseInt(segments[3]),
                        Integer.parseInt(segments[4]),
                        Integer.parseInt(segments[5])));
            }
            //Load Sinks
            scn = new Scanner(sinks, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                g.addNode(new SinkNode(
                        segments[0],
                        Integer.parseInt(segments[1]),
                        Integer.parseInt(segments[2]),
                        Integer.parseInt(segments[3]),
                        Integer.parseInt(segments[4])));
            }
            //Load Edges
            scn = new Scanner(edges, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                g.addEdge(
                        Integer.parseInt(segments[0]),
                        Integer.parseInt(segments[1]),
                        Integer.parseInt(segments[2]));
            }
            return g;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
