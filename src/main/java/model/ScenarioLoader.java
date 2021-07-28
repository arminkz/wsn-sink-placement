package model;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ScenarioLoader {

    public static Scenario loadFromFile(String path) {

        ArrayList<SinkConfiguration> sinkTypes = new ArrayList<>();
        ArrayList<SinkCandidate> sinkCandidates = new ArrayList<>();

        try {
            Scanner scn;
            // load WSN graph
            // this file stores graph sparsely
            File graphFile = new File(path + "graph.csv");
            String scnName = path.substring(path.substring(0,path.length()-1).lastIndexOf('/')+1,path.length()-1);
            System.out.println("[ScenarioLoader] Loading " + scnName);

            Graph g = new Graph(scnName);
            //HashMap<Integer, Vertex> vertexMap = new HashMap<>();
            int maxSeenIndex=-1;

            // load Graph
            scn = new Scanner(graphFile, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                int src = Integer.parseInt(segments[0]);
                int to = Integer.parseInt(segments[1]);
                int weight = Integer.parseInt(segments[2]);

                //create vertices if they are not present
                if(Math.max(src,to) > maxSeenIndex) {
                    for (int i = 0; i < Math.max(src,to) - maxSeenIndex; i++) {
                        g.addVertex(new Vertex());
                    }
                    maxSeenIndex = Math.max(src,to);
                }

                //add edges
                g.addEdge(g.getVertices().get(src),g.getVertices().get(to),weight);
            }

            // load Sinks Types
            File sinkTypesFile = new File(path + "sink_types.csv");
            scn = new Scanner(sinkTypesFile, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                sinkTypes.add(new SinkConfiguration(
                        segments[0],
                        Integer.parseInt(segments[1]),
                        Integer.parseInt(segments[2]),
                        Integer.parseInt(segments[3]),
                        Integer.parseInt(segments[4])));
            }

            // load Sensors
            File sensorsFile = new File(path + "sensors.csv");
            scn = new Scanner(sensorsFile, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                int vertexIndex = Integer.parseInt(segments[0]);
                SensorNode sensor = new SensorNode(
                        segments[1],
                        Integer.parseInt(segments[2]),
                        Integer.parseInt(segments[3]),
                        Integer.parseInt(segments[4]),
                        Integer.parseInt(segments[5]),
                        Integer.parseInt(segments[6]));
                g.getVertices().get(vertexIndex).setNode(sensor);
                // optional pos
                if(segments.length > 8) {
                    g.getVertices().get(vertexIndex)
                            .setPos(new Point(Integer.parseInt(segments[7]),Integer.parseInt(segments[8])));
                }
            }

            // load sink indexes (don't add node in graph but store in candidate object)
            File sinksFile = new File(path + "sinks.csv");
            scn = new Scanner(sinksFile, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                int vertexIndex = Integer.parseInt(segments[0]);
                // build SinkCandidate object
                SinkCandidate sc = new SinkCandidate();
                sc.setPlacmentVertex(vertexIndex);
                sinkCandidates.add(sc);
                // optional pos
                if(segments.length > 2) {
                    g.getVertices().get(vertexIndex)
                            .setPos(new Point(Integer.parseInt(segments[1]),Integer.parseInt(segments[2])));
                }
            }

            return new Scenario(g,sinkTypes,sinkCandidates);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("error loading scenario");
    }

}
