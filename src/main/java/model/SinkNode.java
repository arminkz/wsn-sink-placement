package model;

public class SinkNode extends Node {

    private final int cpu;
    private final int ram;
    private final int bw;
    private final int cost;

    public SinkNode(String name,int cpu,int ram,int bw,int cost) {
        super(name);
        this.cpu = cpu;
        this.ram = ram;
        this.bw = bw;
        this.cost = cost;
    }

    public int getCpu() { return cpu; }
    public int getRam() { return ram; }
    public int getBandwidth() { return bw; }

    public int getCost() { return cost; }
}
