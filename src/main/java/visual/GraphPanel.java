package visual;

import graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphPanel extends JPanel {

    private final Graph graph;
    private BufferedImage plot;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        plot = GraphPainter.viewGraph(graph);
    }

    public void refresh() {
        plot = GraphPainter.viewGraph(graph);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,getWidth(),getHeight());
        if(plot != null) {
            int x = (this.getWidth() - plot.getWidth())/2;
            int y = (this.getHeight() - plot.getHeight())/2;
            g2d.drawImage(plot,null,x,y);
        }
    }
}
