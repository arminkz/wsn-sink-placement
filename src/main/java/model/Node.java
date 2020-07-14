package model;

public abstract class Node {
    private final String name;

    public Node(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
