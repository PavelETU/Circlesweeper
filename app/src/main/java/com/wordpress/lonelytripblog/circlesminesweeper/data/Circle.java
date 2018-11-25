package com.wordpress.lonelytripblog.circlesminesweeper.data;

// Circle DATA STRUCTURE. I could go with public fields, but stick to the Beans convention
public class Circle {

    private static final float PERCENTAGE_FOR_SMALLER_CIRCLE = 0.85f;
    private int x;
    private int y;
    private final int colorDrawableSrc;
    private final int defaultRadius;
    private final int smallRadius;
    private int radius;
    private boolean isAlive;

    public Circle(int x, int y, int radius, int colorDrawableSrc) {
        this(x, y, radius, colorDrawableSrc, true);
    }

    private Circle(int x, int y, int radius, int colorDrawableSrc, boolean isAlive) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        defaultRadius = radius;
        smallRadius = (int) (radius * PERCENTAGE_FOR_SMALLER_CIRCLE);
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

    public void makeCircleSmall() {
        radius = smallRadius;
    }

    public void makeCircleBig() {
        radius = defaultRadius;
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
