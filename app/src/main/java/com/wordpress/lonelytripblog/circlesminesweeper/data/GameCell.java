package com.wordpress.lonelytripblog.circlesminesweeper.data;

import androidx.annotation.VisibleForTesting;

public class GameCell {

    private Circle circle;
    private boolean animated = false;
    private boolean withMine;
    private boolean marked = false;
    private int minesNear;
    private int topLeftX;
    private int topLeftY;
    private int sideLength;

    public GameCell(Circle circle, boolean initWithMine, int minesNear) {
        this(circle, initWithMine, minesNear, false);
    }

    public GameCell(Circle circle, boolean initWithMine, int minesNear, boolean marked) {
        this.circle = circle;
        int radius = circle.getRadius();
        topLeftX = circle.getX() - radius;
        topLeftY = circle.getY() - radius;
        sideLength = radius * 2;
        withMine = initWithMine;
        this.minesNear = minesNear;
        this.marked = marked;
    }

    public void moveCircleTo(int x, int y) {
        circle.setX(x);
        circle.setY(y);
    }

    public void moveCircleToDefaultPosition() {
        moveCircleTo(topLeftX + sideLength / 2, topLeftY + sideLength / 2);
    }

    public void makeCircleSmaller() {
        circle.makeCircleSmall();
    }

    public void makeCircleBigger() {
        circle.makeCircleBig();
    }

    public void eliminateCircle() {
        circle.setAlive(false);
    }

    public void eliminateCircleWithAnimation() {
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

    public boolean isColorTheSameAndCellsNotMarked(GameCell gameCell) {
        // Does this violate the Law of Demeter? I don't think so:)
        if (!circle.isAlive() || !gameCell.circle.isAlive()) return false;
        return circle.getColorDrawableSrc() == gameCell.circle.getColorDrawableSrc() && !marked && !gameCell.marked;
    }

    public void setNewCenterPositionAndRadius(int x, int y, int radius) {
        circle.setX(x);
        circle.setY(y);
        circle.setNewRadius(radius);
        sideLength = radius * 2;
        topLeftX = x - radius;
        topLeftY = y - radius;
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

    public int getDrawableForCircle() {
        return circle.getColorDrawableSrc();
    }

    public boolean isCircleInsideAlive() {
        return circle.isAlive();
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public int getCircleTopLeftX() {
        return circle.getX() - circle.getRadius();
    }

    public int getCircleSideLength() {
        return circle.getRadius() * 2;
    }

    public int getCircleTopLeftY() {
        return circle.getY() - circle.getRadius();
    }

    public int getSideLength() {
        return sideLength;
    }

    @VisibleForTesting
    public Circle getCircle() {
        return circle;
    }

    public int getMinesNear() {
        return minesNear;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void setWithMine(boolean withMine) {
        this.withMine = withMine;
    }

    public void setMinesNear(int minesNear) {
        this.minesNear = minesNear;
    }

    public boolean drawCellLast() {
        return circle.isMoving();
    }

    public void setCircleAliveWithColor(int color) {
        circle.setAlive(true);
        circle.setColorDrawableSrc(color);
    }
}