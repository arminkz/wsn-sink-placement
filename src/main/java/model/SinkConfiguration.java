package model;

public class SinkConfiguration {

    private final String modelName;
    private final int cpu;
    private final int ram;
    private final int bw;
    private final int cost;

    public SinkConfiguration(String modelName,int cpu,int ram,int bw,int cost) {
        this.modelName = modelName;
        this.cpu = cpu;
        this.ram = ram;
        this.bw = bw;
        this.cost = cost;
    }

    public String getModelName() { return modelName; }

    public int getCpu() { return cpu; }
    public int getRam() { return ram; }
    public int getBandwidth() { return bw; }

    public int getCost() { return cost; }
}
