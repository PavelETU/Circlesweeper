package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.support.annotation.VisibleForTesting;

public class Mapper {

    @VisibleForTesting
    final float initialGameWindowHeight;
    @VisibleForTesting
    final float initialGameWindowWidth;
    private float currentHeight;
    private float currentWidth;

    public Mapper(float height, float width) {
        if (height <= width) {
            this.initialGameWindowHeight = height;
            this.initialGameWindowWidth = width;
        } else {
            this.initialGameWindowHeight = width;
            this.initialGameWindowWidth = height;
        }
        currentHeight = height;
        currentWidth = width;
    }

    public void setNewSize(float height, float width) {
        currentHeight = height;
        currentWidth = width;
    }

    public Point mapXYFromViewToGameWindow(float x, float y) {
        float xForPoint = x;
        float yForPoint = y;
        if (swapXAndY()) {
            xForPoint = y;
            yForPoint = initialGameWindowHeight - x - 1;
        }
        float factorForX = calculateMultipleFactorForX();
        float factorForY = calculateMultipleFactorForY();
        xForPoint = Math.round( xForPoint * factorForX);
        yForPoint = Math.round( yForPoint * factorForY);
        return new Point(xForPoint, yForPoint);
    }

    private boolean swapXAndY() {
        return currentHeight > currentWidth;
    }

    private float calculateMultipleFactorForX() {
        if (swapXAndY()) {
            return initialGameWindowWidth / currentHeight;
        } else {
            return initialGameWindowWidth / currentWidth;
        }
    }

    private float calculateMultipleFactorForY() {
        if (swapXAndY()) {
            return initialGameWindowHeight / currentWidth;
        } else {
            return initialGameWindowHeight / currentHeight;
        }
    }

}
