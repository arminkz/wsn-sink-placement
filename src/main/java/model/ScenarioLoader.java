package model;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

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

            Graph g = new Graph();
            HashMap<Integer, Vertex> vertexMap = new HashMap<>();

            // load Graph
            scn = new Scanner(graphFile, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                String[] segments = scn.nextLine().split(",");
                int src = Integer.parseInt(segments[0]);
                int to = Integer.parseInt(segments[1]);
                int weight = Integer.parseInt(segments[2]);

                //create vertices if they are not present
                if(!vertexMap.containsKey(src)) {
                    Vertex v = new Vertex();
                    vertexMap.put(src, v);
                    g.addVertex(v);
                }
                if(!vertexMap.containsKey(to)) {
                    Vertex v = new Vertex();
                    vertexMap.put(to, v);
                    g.addVertex(v);
                }
                //add edges
                g.addEdge(vertexMap.get(src),vertexMap.get(to),weight);
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
                vertexMap.get(vertexIndex).setNode(sensor);
            }

            // load sink indexes (don't add node in graph but store in candidate object)
            File sinksFile = new File(path + "sinks.csv");
            scn = new Scanner(sinksFile, StandardCharsets.UTF_8);
            scn.nextLine(); //discard header
            while(scn.hasNextLine()){
                int vertexIndex = Integer.parseInt(scn.nextLine());
                // build SinkCandidate object
                SinkCandidate sc = new SinkCandidate();
                sc.setPlacmentVertex(vertexIndex);
                sinkCandidates.add(sc);
            }

            return new Scenario(g,sinkTypes,sinkCandidates);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("error loading scenario");
    }

}
