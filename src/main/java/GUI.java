import algorithm.Fitness;
import model.*;
import optimization.BruteForce;
import optimization.FastHillClimbing;
import optimization.GeneticAlgorithm;
import optimization.HillClimbing;
import visual.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

public class GUI extends JPanel {

    private GUI(){

//        RandomScenarioGenerator.generate("WSN_grid_2",
//                5,2,500,500,250,4);

        Scenario s = ScenarioLoader.loadFromFile("data/wsn_100/");

//        int c = 1;
//        SinkConfiguration conf = s.getSinkTypes().get(2);
//        for (SinkCandidate sc : s.getSinkCandidates()) {
//            int pi = sc.getPlacmentVertexIndex();
//            s.getRootGraph().getVertices().get(pi).setNode(new SinkNode("S" + c, conf));
//            c++;
//        }

//        int maxCost = s.getSinkCandidates().size() *
//                s.getSinkTypes().stream().max(Comparator.comparingInt(SinkConfiguration::getCost)).get().getCost();

        this.setLayout(new BorderLayout());
        this.add(new GraphPanel(s.getRootGraph()));

//        System.out.println("Fitness: " + Fitness.calc(s.getRootGraph(), maxCost, true));

//        FastHillClimbing fhc = new FastHillClimbing(s,7);
//        fhc.solve();

//        BruteForce bf = new BruteForce(s);
//        bf.solve();

//      GeneticAlgorithm ga = new GeneticAlgorithm(s,100,0.05,0.1,20);
//      ga.solve();

//        HillClimbing hc = new HillClimbing(s);
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