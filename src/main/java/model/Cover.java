package model;

public class Cover {
    SinkNode sink;
    int coverDistance;

    public Cover(SinkNode sink,int coverDistance) {
        this.sink = sink;
        this.coverDistance = coverDistance;
    }

    public SinkNode getSink() { return sink; }
    public int getCoverDistance() { return coverDistance; }
}