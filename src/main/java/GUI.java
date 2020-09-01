import algorithm.Evaluator;
import graph.Graph;
import model.*;
import optimization.BruteForce;
import optimization.GeneticAlgorithm;
import visual.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {

    private GUI(){
//        Scenario s = ScenarioLoader.loadFromFile("data/toy_example/");
//
//        this.setLayout(new BorderLayout());
//        this.add(new GraphPanel(s.getRootGraph()));
//
////        BruteForce bf = new BruteForce(s);
////        bf.solve();
//
//        GeneticAlgorithm ga = new GeneticAlgorithm(s,500,0.2,0.1,20);
//        ga.solve();
        Graph ex1 = GenerateGraph.realistic(60,30,200,100,20);
        this.setLayout(new BorderLayout());
        this.add(new GraphPanel(ex1));
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}