package algorithm;

import graph.Graph;
import graph.Vertex;
import model.Node;
import model.SensorNode;
import model.SinkNode;

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


}
