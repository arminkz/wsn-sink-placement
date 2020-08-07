package model;

public class SinkNode extends Node {

    private final String sinkModel;
    private final int cpu;
    private final int ram;
    private final int bw;
    private final int cost;

    public SinkNode(String name,String sinkModel,int cpu,int ram,int bw,int cost) {
        super(name);
        this.sinkModel = sinkModel;
        this.cpu = cpu;
        this.ram = ram;
        this.bw = bw;
        this.cost = cost;
    }

    public SinkNode(String name, SinkConfiguration config) {
        super(name);
        this.sinkModel = config.getModelName();
        this.cpu = config.getCpu();
        this.ram = config.getRam();
        this.bw = config.getBandwidth();
        this.cost = config.getCost();
    }

    public String getModelName() { return sinkModel; }

    public int getCpu() { return cpu; }
    public int getRam() { return ram; }
    public int getBandwidth() { return bw; }

    public int getCost() { return cost; }
}
