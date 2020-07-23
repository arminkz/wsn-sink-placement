package algorithm;

import graph.Graph;
import graph.Vertex;
import model.Node;
import model.SensorNode;
import model.SinkNode;

import java.util.ArrayList;
import java.util.HashMap;

public class Checker {

    public static boolean isKCovered(Graph g,int src) {

        Node node = g.getVertices().get(src).getAssignedNode();
        if(node instanceof SensorNode) {
            SensorNode sensor = (SensorNode)node;

            Dijkstra dij = new Dijkstra(g);
            dij.calc(src);

            // count sinks covering this sensor
            int kc = 0;
            for (int i = 0; i < g.getVertices().size(); i++) {
                Vertex v = g.getVertices().get(i);
                if(v.getAssignedNode() instanceof SinkNode) {
                    if(dij.getDistance(i) <= sensor.getMaxL()) kc++;
                }
            }

            return kc >= sensor.getKC();
        }else{
            throw new RuntimeException("KCovered must be checked on sensor node");
        }
    }

    public static boolean checkGraph(Graph g) {

        HashMap<SensorNode, ArrayList<SinkNode>> covering = new HashMap<>();
        Dijkstra dij = new Dijkstra(g);

        for(Vertex v: g.getVertices()) {
            Node n = v.getAssignedNode();
            if(n instanceof SensorNode) {
                SensorNode sensor = (SensorNode)n;
                dij.calc(v); //dijkstra source
                covering.put(sensor,new ArrayList<>()); //covering list

                // check KCovered
                // count sinks covering this sensor
                int kc = 0;
                for (int i = 0; i < g.getVertices().size(); i++) {
                    Vertex v2 = g.getVertices().get(i);
                    if(v2.getAssignedNode() instanceof SinkNode) {
                        SinkNode sink = (SinkNode)v2.getAssignedNode();
                        if(dij.getDistance(i) <= sensor.getMaxL()) {
                            System.out.println("sink " + sink.getName() + " is covering sensor " + sensor.getName());
                            //sink is covering sensor
                            covering.get(sensor).add(sink);
                            kc++;
                        }
                    }
                }

                if(kc < sensor.getKC()) {
                    System.out.println("sensor " + sensor.getName() + " is not K covered.");
                    return false;
                }
            }
        }

        HashMap<SinkNode,Integer> sumCpu = new HashMap<>();
        HashMap<SinkNode,Integer> sumRam = new HashMap<>();
        HashMap<SinkNode,Integer> sumBw = new HashMap<>();

        for(SensorNode sensor: covering.keySet()){
            int n = covering.get(sensor).size(); // uniform distribution
            int partialCpu = sensor.getTaskCpu() / n;
            int partialRam = sensor.getTaskRam() / n;
            int partialBw = sensor.getTaskBw() / n;
            System.out.println("sensor " + sensor.getName() + " is covered by " + n + " sinks.");

            for(SinkNode sink: covering.get(sensor)) {
                if(!sumCpu.containsKey(sink)) sumCpu.put(sink,0);
                if(!sumRam.containsKey(sink)) sumRam.put(sink,0);
                if(!sumBw.containsKey(sink)) sumBw.put(sink,0);

                sumCpu.replace(sink,sumCpu.get(sink) + partialCpu);
                sumRam.replace(sink,sumRam.get(sink) + partialRam);
                sumBw.replace(sink,sumBw.get(sink) + partialBw);
            }
        }

        for(SinkNode sink: sumCpu.keySet()) {
            if(sink.getCpu() < sumCpu.get(sink)) {
                System.out.println("sink " + sink.getName() + " has " + sink.getCpu() + " cpu, but assigned with " + sumCpu.get(sink));
                System.out.println("sink " + sink.getName() + " has insufficient CPU.");
                return false;
            }
            if(sink.getRam() < sumRam.get(sink)) {
                System.out.println("sink " + sink.getName() + " has insufficient RAM.");
                return false;
            }
            if(sink.getBandwidth() < sumBw.get(sink)) {
                System.out.println("sink " + sink.getName() + " has insufficient Bandwidth.");
                return false;
            }
        }

        return true;
    }


}
