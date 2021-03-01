package report;

import graph.Graph;

public class Report {

    private final long time;
    private final double fitness;
    private final Graph graph;

    public Report(Graph graph,double fitness,long time){
        this.graph = graph;
        this.fitness = fitness;
        this.time = time;
    }

    public Graph getGraph() {
        return graph;
    }

    public double getFitness() {
        return fitness;
    }

    public long getTime() {
        return time;
    }
}
