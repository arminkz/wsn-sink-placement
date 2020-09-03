package algorithm;

import graph.Graph;
import graph.Vertex;
import model.Node;
import model.SensorNode;
import model.SinkNode;
import model.Cover;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class FitnessEvaluator {

    public static final int COST_MULTIPLIER = 500;
    public static final int KC_PENALTY_COEFF = 100;
    public static final int CPU_PENALTY_COEFF = 1;
    public static final int RAM_PENALTY_COEFF = 1;
    public static final int BW_PENALTY_COEFF = 1;

    // returns overall fitness of a configuration
    public static int evaluate(Graph g, int maxCost) {

        HashMap<SensorNode, ArrayList<Cover>> covering = new HashMap<>();
        Dijkstra dij = new Dijkstra(g);

        int fitness = 0;

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
                            //System.out.println("sink " + sink.getName() + " is covering sensor " + sensor.getName());

                            //sink is covering sensor
                            //assumption: only keep k nearest coverings
                            //this assumption may cause false negatives
                            if(covering.get(sensor).size() >= sensor.getKC()) {
                                Cover worstCover = covering.get(sensor).stream()
                                        .max(Comparator.comparingInt(Cover::getCoverDistance)).get();
                                if(worstCover.getCoverDistance() > dij.getDistance(i)) {
                                    //remove the worst cover and add new cover
                                    covering.get(sensor).remove(worstCover);
                                    covering.get(sensor).add(new Cover(sink,dij.getDistance(v2)));
                                }
                            }
                            else {
                                covering.get(sensor).add(new Cover(sink,dij.getDistance(v2)));
                                kc++;
                            }
                        }
                    }
                }

                if(kc < sensor.getKC()) {
                    // apply Kcovered violation penalty
                    fitness -= KC_PENALTY_COEFF * (sensor.getKC() - kc);
                }
            }
        }

        //Print coverings
        //for(SensorNode sensor: covering.keySet()) {
        //    System.out.println("sensor " + sensor.getName() + " (KC=" + sensor.getKC() + ")  :" );
        //    for(Cover cover: covering.get(sensor)) {
        //        System.out.print(cover.getSink().getName() + " (" + cover.getCoverDistance() + ")  ");
        //    }
        //    System.out.println("\n");
        //}

        //Workload check
        HashMap<SinkNode,Integer> sumCpu = new HashMap<>();
        HashMap<SinkNode,Integer> sumRam = new HashMap<>();
        HashMap<SinkNode,Integer> sumBw = new HashMap<>();

        for(SensorNode sensor: covering.keySet()){
            int n = covering.get(sensor).size(); // uniform distribution
            if(n == 0) continue; // this sensor is not covered at all
            int partialCpu = sensor.getTaskCpu() / n;
            int partialRam = sensor.getTaskRam() / n;
            int partialBw = sensor.getTaskBw() / n;
            //System.out.println("sensor " + sensor.getName() + " is covered by " + n + " sinks.");

            for(Cover cover: covering.get(sensor)) {
                if(!sumCpu.containsKey(cover.getSink())) sumCpu.put(cover.getSink(),0);
                if(!sumRam.containsKey(cover.getSink())) sumRam.put(cover.getSink(),0);
                if(!sumBw.containsKey(cover.getSink())) sumBw.put(cover.getSink(),0);

                sumCpu.replace(cover.getSink(),sumCpu.get(cover.getSink()) + partialCpu);
                sumRam.replace(cover.getSink(),sumRam.get(cover.getSink()) + partialRam);
                sumBw.replace(cover.getSink(),sumBw.get(cover.getSink()) + partialBw);
            }
        }

        for(SinkNode sink: sumCpu.keySet()) {
            if(sink.getCpu() < sumCpu.get(sink)) {
                // apply cpu violation penalty
                fitness -= CPU_PENALTY_COEFF * (sumCpu.get(sink)-sink.getCpu());
            }
            if(sink.getRam() < sumRam.get(sink)) {
                // apply ram violation penalty
                fitness -= RAM_PENALTY_COEFF * (sumRam.get(sink)-sink.getRam());
            }
            if(sink.getBandwidth() < sumBw.get(sink)) {
                // apply bandwidth violation penalty
                fitness -= BW_PENALTY_COEFF * (sumBw.get(sink)-sink.getBandwidth());
            }
        }

        if(fitness == 0) {
            // graph is feasible return fitness based on cost of sinks
            int cost = 0;
            for(Vertex v: g.getVertices()) {
                Node n = v.getAssignedNode();
                if (n instanceof SinkNode) {
                    cost += ((SinkNode) n).getCost();
                }
            }
            fitness = 1 + COST_MULTIPLIER*(maxCost - cost);
        }
        return fitness;
    }


}
