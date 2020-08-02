package visual;

import graph.Graph;

import javax.swing.*;
import java.awt.*;

public class ShowGraph extends JPanel {

    public static void showGraph(String title, Graph g) {
        JPanel parent = new JPanel(new BorderLayout());
        parent.add(new GraphPanel(g));

        JFrame window = new JFrame(title);
        window.setSize(500,500);
        window.add(parent);
        window.setVisible(true);
    }

}
