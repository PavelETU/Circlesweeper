package com.wordpress.lonelytripblog.circlesminesweeper.data;

// Circle DATA STRUCTURE. I could go with public fields, but stick to the Beans convention
public class Circle {

    public static final float PERCENTAGE_FOR_SMALLER_CIRCLE = 0.85f;
    private int x;
    private int y;
    private int colorDrawableSrc;
    private int defaultRadius;
    private int smallRadius;
    private int radius;
    private boolean isAlive;
    private boolean isMoving;

    public Circle(int x, int y, int radius, int colorDrawableSrc) {
        this(x, y, radius, colorDrawableSrc, true);
    }

    public Circle(int x, int y, int radius, int colorDrawableSrc, boolean isAlive) {
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
        isMoving = true;
        radius = smallRadius;
    }

    public void makeCircleBig() {
        isMoving = false;
        radius = defaultRadius;
    }

    public int getColorDrawableSrc() {
        return colorDrawableSrc;
    }

    public int getRadius() {
        return radius;
    }

    public void setNewRadius(int radius) {
        this.radius = radius;
        defaultRadius = radius;
        smallRadius = (int) (radius * PERCENTAGE_FOR_SMALLER_CIRCLE);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setColorDrawableSrc(int colorDrawableSrc) {
        this.colorDrawableSrc = colorDrawableSrc;
    }
}
