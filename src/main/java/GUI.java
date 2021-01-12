import algorithm.Fitness;
import model.*;
import optimization.FastHillClimbing;
import visual.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {

    private GUI(){

        //RandomScenarioGenerator.generate("WSN3",40,20,150,150,29);

        Scenario s = ScenarioLoader.loadFromFile("data/toy_example/");

        int c = 1;
        SinkConfiguration conf = s.getSinkTypes().get(0);
        for (SinkCandidate sc : s.getSinkCandidates()) {
            int pi = sc.getPlacmentVertexIndex();
            s.getRootGraph().getVertices().get(pi).setNode(new SinkNode("S" + c, conf));
            c++;
        }

        this.setLayout(new BorderLayout());
        this.add(new GraphPanel(s.getRootGraph()));

        System.out.println("Fitness: " + Fitness.calc(s.getRootGraph(),true));

//      FastHillClimbing fhc = new FastHillClimbing(s,7);
//      fhc.solve();

//      BruteForce bf = new BruteForce(s);
//      bf.solve();

//      GeneticAlgorithm ga = new GeneticAlgorithm(s,500,0.05,0.1,20);
//      ga.solve();

//      HillClimbing hc = new HillClimbing(s);
//      hc.solve();
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}