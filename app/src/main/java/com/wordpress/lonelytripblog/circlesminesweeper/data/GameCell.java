package com.wordpress.lonelytripblog.circlesminesweeper.data;

public class GameCell {

    private static final float PERCENT_TO_TAKE_FOR_MOVING_CIRCLE_RADIUS_FROM_OLD_ONE = 0.85f;
    private Circle circle;
    private int minesNear = 0;
    private boolean animated = false;
    private boolean circleInsideAlive = true;
    private boolean withMine;
    private boolean marked = false;

    public GameCell(Circle circle, boolean initWithMine) {
        this.circle = circle;
        withMine = initWithMine;
    }

    public void moveCircleTo(int x, int y) {
        circle.setX(x);
        circle.setY(y);
    }

    public void moveCircleToDefaultPosition() {
        new RuntimeException("Not implemented");
    }

    public void makeCircleSmaller() {
        circle.setRadius((int) (circle.getRadius() * PERCENT_TO_TAKE_FOR_MOVING_CIRCLE_RADIUS_FROM_OLD_ONE));
    }

    public void makeCircleBigger() {

    }

    public int getMinesNear() {
        return minesNear;
    }

    public void setMinesNear(int minesNear) {
        this.minesNear = minesNear;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public boolean isCircleInsideAlive() {
        return circleInsideAlive;
    }

    public void setCircleInsideAlive(boolean circleInsideAlive) {
        this.circleInsideAlive = circleInsideAlive;
    }

    public boolean isWithMine() {
        return withMine;
    }

    public void setWithMine(boolean withMine) {
        this.withMine = withMine;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getLeft() {
        return circle.getX() - circle.getRadius();
    }

    public int getRight() {
        return circle.getX() + circle.getRadius();
    }

    public int getTop() {
        return circle.getY() - circle.getRadius();
    }

    public int getBottom() {
        return circle.getY() + circle.getRadius();
    }


}