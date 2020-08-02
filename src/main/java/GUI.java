import algorithm.FeasibilityCheck;
import graph.Graph;
import model.*;
import optimization.BruteForce;
import visual.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {

    private GUI(){
        Scenario s = ScenarioLoader.loadFromFile("data/toy_example_2/");

        //this.setLayout(new BorderLayout());
        //this.add(new GraphPanel(s.getRootGraph()));

        System.out.println("Number of Sink Types: " + s.getSinkTypes().size() );
        System.out.println("Number of Placement Locations: " + s.getSinkCandidates().size() );

        BruteForce bf = new BruteForce(s);
        bf.solve();

        System.out.println(bf.leafCount);
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        //window.setVisible(true);
    }
}