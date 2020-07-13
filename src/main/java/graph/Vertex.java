package graph;

public class Vertex {
    private final String name;
    private final boolean isCritical;

    public Vertex(String name, boolean isCritical) {
        this.name = name;
        this.isCritical = isCritical;
    }

    public String getName() {
        return name;
    }

    public boolean getIsCritical() {
        return isCritical;
    }
}
