import algorithm.FeasibilityCheck;
import graph.Graph;
import model.Scenario;
import model.ScenarioLoader;
import visual.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {

    private GUI(){
        Scenario s = ScenarioLoader.loadFromFile("data/toy_example/");
        this.setLayout(new BorderLayout());
        this.add(new GraphPanel(s.getRootGraph()));

        //System.out.println(FeasibilityCheck.checkGraph(g));
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}