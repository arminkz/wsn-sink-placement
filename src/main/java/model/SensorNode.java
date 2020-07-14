package model;

public class SensorNode extends Node {

    private final int KC; //required KCovered
    private final int maxL; //maximum distance to Sink Node

    private final int taskCpu;
    private final int taskRam;
    private final int taskBw;

    public SensorNode(String name,int KC,int maxL,int taskCpu,int taskRam,int taskBw) {
        super(name);
        this.KC = KC;
        this.maxL = maxL;
        this.taskCpu = taskCpu;
        this.taskRam = taskRam;
        this.taskBw = taskBw;
    }

    public int getKC() { return KC; }
    public int getMaxL() { return maxL; }

    public int getTaskCpu() { return taskCpu;}
    public int getTaskRam() { return taskRam; }
    public int getTaskBw() { return taskBw; }

    public boolean isCritical() { return (taskCpu != 0 && taskRam != 0); }
}
