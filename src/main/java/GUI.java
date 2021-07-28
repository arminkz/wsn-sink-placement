import algorithm.AllPairDijkstra;
import algorithm.Fitness;
import model.*;
import visual.ShowGraph;

import javax.swing.*;
import java.util.Comparator;

public class GUI extends JPanel {

    private GUI(){

//        RandomScenarioGenerator.generate("WSN_grid_2",
//                5,2,500,500,250,4);

        Scenario s = ScenarioLoader.loadFromFile("data/toy_example/");

//        int c = 1;
//        SinkConfiguration conf = s.getSinkTypes().get(2);
//        for (SinkCandidate sc : s.getSinkCandidates()) {
//            int pi = sc.getPlacmentVertexIndex();
//            s.getRootGraph().getVertices().get(pi).setNode(new SinkNode("S" + c, conf));
//            c++;
//        }

//        Dijkstra dij = new Dijkstra(s.getRootGraph());
//        for (int i = 0; i < s.getRootGraph().getVertices().size(); i++) {
//            dij.calc(i);
//            for (int j = 0; j < s.getRootGraph().getVertices().size(); j++) {
//                String src = "null (" + String.valueOf(i) + ")";
//                if(s.getRootGraph().getVertices().get(i).getAssignedNode() != null) {
//                    src = s.getRootGraph().getVertices().get(i).getAssignedNode().getName();
//                }
//                String dest = "null (" + String.valueOf(j) + ")";
//                if(s.getRootGraph().getVertices().get(j).getAssignedNode() != null) {
//                    dest = s.getRootGraph().getVertices().get(j).getAssignedNode().getName();
//                }
//                int dist = dij.getDistance(j);
//                System.out.println(src + "->" + dest + ": " + dist);
//            }
//        }

        int maxCost = s.getSinkCandidates().size() *
                s.getSinkTypes().stream().max(Comparator.comparingInt(SinkConfiguration::getCost)).get().getCost();

        Fitness fitnessUtil = new Fitness(s.getRootGraph(), maxCost);
        System.out.println("fitness value: " + fitnessUtil.calc(s.getRootGraph(), 1));

        ShowGraph.showGraph("WSN300", s.getRootGraph());



//        this.setLayout(new BorderLayout());
//        this.add(new GraphPanel(s.getRootGraph()));

        //System.out.println("Fitness: " + Fitness.calc(s.getRootGraph(), maxCost, true));

//        FastHillClimbing fhc = new FastHillClimbing(s,7);
//        fhc.solve();



//          GeneticAlgorithm ga = new GeneticAlgorithm(s,10,0.05,0.1,20);
//          ga.solve();

//        HillClimbing hc = new HillClimbing(s,STOCHASTIC);
//        hc.solve();
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}