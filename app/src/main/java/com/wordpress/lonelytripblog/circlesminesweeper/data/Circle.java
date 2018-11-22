package com.wordpress.lonelytripblog.circlesminesweeper.data;

// Circle DATA STRUCTURE. I could go with public fields, but stick to the Beans convention
public class Circle {
    private int x;
    private int y;
    private int colorDrawableSrc;
    private int radius;
    private boolean isAlive;

    public Circle(int x, int y, int radius, int colorDrawableSrc) {
        this(x, y, radius, colorDrawableSrc, true);
    }

    private Circle(int x, int y, int radius, int colorDrawableSrc, boolean isAlive) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.colorDrawableSrc = colorDrawableSrc;
        this.isAlive = isAlive;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColorDrawableSrc() {
        return colorDrawableSrc;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
