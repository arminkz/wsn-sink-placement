import model.RandomScenarioGenerator;
import model.Scenario;
import model.ScenarioLoader;
import optimization.BruteForce;
import optimization.GeneticAlgorithm;
import optimization.HillClimbing;
import visual.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {

    private GUI(){

//      RandomScenarioGenerator.generate("WSN2",45,30,200,150,29);

        Scenario s = ScenarioLoader.loadFromFile("data/WSN2/");

        this.setLayout(new BorderLayout());
        this.add(new GraphPanel(s.getRootGraph()));

//      BruteForce bf = new BruteForce(s);
//      bf.solve();

//      GeneticAlgorithm ga = new GeneticAlgorithm(s,500,0.05,0.1,20);
//      ga.solve();

        HillClimbing hc = new HillClimbing(s);
        hc.solve();
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}