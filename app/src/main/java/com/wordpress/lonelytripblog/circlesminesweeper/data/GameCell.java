package com.wordpress.lonelytripblog.circlesminesweeper.data;

import android.support.annotation.VisibleForTesting;

public class GameCell {

    private static final float PERCENTAGE_FOR_SMALLER_CIRCLE = 0.85f;
    private static final float PERCENTAGE_FOR_BIGGER_CIRCLE = 1.15f;
    @VisibleForTesting
    Circle circle;
    private int minesNear = 0;
    private boolean animated = false;
    private boolean circleInsideAlive = true;
    private boolean withMine;
    private boolean marked = false;
    private final int topLeftX;
    private final int topLeftY;
    private final int sizeLength;

    public GameCell(Circle circle, boolean initWithMine) {
        this.circle = circle;
        int radius = circle.getRadius();
        topLeftX = circle.getX() - radius;
        topLeftY = circle.getY() - radius;
        sizeLength = radius * 2;
        withMine = initWithMine;
    }

    public void moveCircleTo(int x, int y) {
        circle.setX(x);
        circle.setY(y);
    }

    public void moveCircleToDefaultPosition() {
        moveCircleTo(topLeftX + sizeLength / 2, topLeftY + sizeLength / 2);
    }

    public void makeCircleSmaller() {
        circle.setRadius((int) (circle.getRadius() * PERCENTAGE_FOR_SMALLER_CIRCLE));
    }

    public void makeCircleBigger() {
        circle.setRadius((int) (circle.getRadius() * PERCENTAGE_FOR_BIGGER_CIRCLE));
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

    public boolean contains(final int x, final int y) {
        return topLeftX + sizeLength > x && topLeftX < x
                && topLeftY + sizeLength > y && topLeftY < y;
    }

    public void swapCirclesWith(final GameCell gameCellToSwapBy) {
        Circle circleToSwap = circle;
        circle = gameCellToSwapBy.circle;
        gameCellToSwapBy.circle = circleToSwap;
    }

}