package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class Mapper {

    @VisibleForTesting
    final float initialGameWindowHeight;
    @VisibleForTesting
    final float initialGameWindowWidth;
    private float currentHeight;
    private float currentWidth;
    private GameCellsToBitmap gameCellsToBitmap;

    public Mapper(GameCellsToBitmap gameCellsToBitmap, float height, float width) {
        this.gameCellsToBitmap = gameCellsToBitmap;
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
        xForPoint = Math.round(xForPoint * factorForX);
        yForPoint = Math.round(yForPoint * factorForY);
        return new Point(xForPoint, yForPoint);
    }

    public LiveData<Bitmap> getGameImageLiveData(LiveData<GameCell[][]> gameCellsLiveData) {
        return Transformations.map(gameCellsLiveData,
                gameCells -> gameCellsToBitmap.gameCellsToBitmap(gameCells, initialGameWindowHeight,
                        initialGameWindowWidth, currentHeight, currentWidth, swapXAndY()));
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

    public float getInitialGameWindowHeight() {
        return initialGameWindowHeight;
    }

    public float getInitialGameWindowWidth() {
        return initialGameWindowWidth;
    }
}
