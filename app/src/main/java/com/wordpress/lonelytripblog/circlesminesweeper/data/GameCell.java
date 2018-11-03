package com.wordpress.lonelytripblog.circlesminesweeper.data;

import android.support.annotation.VisibleForTesting;

public class GameCell {

    private static final float PERCENTAGE_FOR_SMALLER_CIRCLE = 0.85f;
    private static final float PERCENTAGE_FOR_BIGGER_CIRCLE = 1.15f;
    @VisibleForTesting
    Circle circle;
    private int minesNear = 0;
    private boolean animated = false;
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

    public void eliminateCircle() {
        circle.setAlive(false);
        animated = true;
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

    public boolean isColorTheSame(final GameCell gameCell) {
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
        return minesNear;
    }

    public boolean isCircleInsideAlive() {
        return circle.isAlive();
    }
}