package com.anready.chess.models;

public class Figure {
    private final int points;
    private final int resource;

    public Figure(int points, int resource) {
        this.points = points;
        this.resource = resource;
    }

    public int getPoints() {
        return points;
    }

    public int getResource() {
        return resource;
    }
}
