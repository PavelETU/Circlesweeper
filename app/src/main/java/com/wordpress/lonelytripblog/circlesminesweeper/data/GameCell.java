package com.wordpress.lonelytripblog.circlesminesweeper.data;

import androidx.annotation.VisibleForTesting;

public class GameCell {

    private static final float PERCENTAGE_FOR_SMALLER_CIRCLE = 0.85f;
    private static final float PERCENTAGE_FOR_BIGGER_CIRCLE = 1.15f;
    private Circle circle;
    private boolean animated = false;
    private final boolean withMine;
    private boolean marked = false;
    private final int minesNear;
    private final int topLeftX;
    private final int topLeftY;
    private final int sideLength;

    public GameCell(Circle circle, boolean initWithMine, int minesNear) {
        this.circle = circle;
        int radius = circle.getRadius();
        topLeftX = circle.getX() - radius;
        topLeftY = circle.getY() - radius;
        sideLength = radius * 2;
        withMine = initWithMine;
        this.minesNear = minesNear;
    }

    public void moveCircleTo(int x, int y) {
        circle.setX(x);
        circle.setY(y);
    }

    public void moveCircleToDefaultPosition() {
        moveCircleTo(topLeftX + sideLength / 2, topLeftY + sideLength / 2);
    }

    public void makeCircleSmaller() {
        circle.setRadius((int) (circle.getRadius() * PERCENTAGE_FOR_SMALLER_CIRCLE));
    }

    public void makeCircleBigger() {
        circle.setRadius((int) (circle.getRadius() * PERCENTAGE_FOR_BIGGER_CIRCLE));
    }

    public void eliminateCircle() {
        circle.setAlive(false);
        animated = true;
    }

    public boolean contains(int x, int y) {
        return topLeftX + sideLength > x && topLeftX < x
                && topLeftY + sideLength > y && topLeftY < y;
    }

    public void swapCirclesWith(GameCell gameCellToSwapBy) {
        Circle circleToSwap = circle;
        circle = gameCellToSwapBy.circle;
        gameCellToSwapBy.circle = circleToSwap;
    }

    public boolean isColorTheSame(GameCell gameCell) {
        // Does this violate the Law of Demeter? I don't think so:)
        if (!circle.isAlive() || !gameCell.circle.isAlive()) return false;
        return circle.getColor() == gameCell.circle.getColor();
    }

    public boolean isWithMine() {
        return withMine;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getColor() {
        return circle.getColor();
    }

    public boolean isCircleInsideAlive() {
        return circle.isAlive();
    }

    @VisibleForTesting
    int getTopLeftX() {
        return topLeftX;
    }

    @VisibleForTesting
    int getTopLeftY() {
        return topLeftY;
    }

    @VisibleForTesting
    int getSideLength() {
        return sideLength;
    }

    @VisibleForTesting
    public Circle getCircle() {
        return circle;
    }

    public int getMinesNear() {
        return minesNear;
    }

}