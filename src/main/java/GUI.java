import algorithm.Checker;
import graph.Graph;
import graph.GraphLoader;
import visual.GraphPanel;
import visual.GraphViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GUI extends JPanel {

    private GUI(){
        Graph g = GraphLoader.loadFromFile("data/toy_example/");
        this.setLayout(new BorderLayout());
        this.add(new GraphPanel(g));
    }

    public static void run() {
        JFrame window = new JFrame("SinkPlacement");
        window.setSize(500,500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new GUI());
        window.setVisible(true);
    }
}