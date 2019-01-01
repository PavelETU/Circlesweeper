package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;

public class GameCellsDrawingHelper {

    private BitmapProvider bitmapProvider;
    private SparseArrayCompat<Bitmap> bitmapCache = new SparseArrayCompat<>();
    private int bitmapSideLength;
    private Paint paintToUse;
    private Rect rectForTextMeasurement;

    public GameCellsDrawingHelper(BitmapProvider bitmapProvider, Paint paint, Rect rectForTextMeasurement) {
        this.bitmapProvider = bitmapProvider;
        paintToUse = paint;
        this.rectForTextMeasurement = rectForTextMeasurement;
    }

    public void drawCellsOnCanvas(Canvas canvasToDraw, @Nullable GameCell[][] gameCells) {
        if (gameCells == null || gameCells.length == 0) return;
        adjustPaintTextSizeToGameCells(gameCells);
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                GameCell currentCell = gameCells[row][col];
                if (currentCell.isCircleInsideAlive()) {
                    if (currentCell.isMarked()) {
                        drawMarkedCircle(canvasToDraw, currentCell.getDrawableForCircle(),
                                currentCell.getCircleSideLength(),
                                currentCell.getCircleTopLeftX(), currentCell.getCircleTopLeftY());
                    } else {
                        drawCircle(canvasToDraw, currentCell.getDrawableForCircle(),
                                currentCell.getCircleSideLength(),
                                currentCell.getCircleTopLeftX(), currentCell.getCircleTopLeftY());
                    }
                } else {
                    if (currentCell.isAnimated()) {
                        drawBangBitmap(canvasToDraw, currentCell.getSideLength(),
                                currentCell.getTopLeftX(), currentCell.getTopLeftY());
                    } else {
                        if (currentCell.isWithMine()) {
                            drawBombBitmap(canvasToDraw, currentCell.getSideLength(),
                                    currentCell.getTopLeftX(), currentCell.getTopLeftY());
                        } else {
                            drawMinesNumber(canvasToDraw, currentCell.getSideLength(),
                                    currentCell.getTopLeftX(), currentCell.getTopLeftY(),
                                    currentCell.getMinesNear());
                        }
                    }
                }
            }
        }
    }

    private void adjustPaintTextSizeToGameCells(GameCell[][] gameCells) {
        paintToUse.setTextSize(gameCells[0][0].getSideLength() / 2);
    }

    private void drawBangBitmap(Canvas canvasToDraw, int length, int x, int y) {
        canvasToDraw.drawBitmap(getBitmapByResource(R.drawable.bang, length), x, y, paintToUse);
    }

    private void drawBombBitmap(Canvas canvasToDraw, int length, int x, int y) {
        canvasToDraw.drawBitmap(getBitmapByResource(R.drawable.bomb, length), x, y, paintToUse);
    }

    private void drawMinesNumber(Canvas canvasToDraw, int length, int x, int y, int minesNumber) {
        String textToDisplay = String.valueOf(minesNumber);
        paintToUse.getTextBounds(textToDisplay, 0, textToDisplay.length(), rectForTextMeasurement);
        float xPosition = (x + length / 2) - rectForTextMeasurement.width() / 2 - rectForTextMeasurement.left;
        float yPosition = (y + length / 2) + rectForTextMeasurement.height() / 2 - rectForTextMeasurement.bottom;
        canvasToDraw.drawText(textToDisplay, xPosition, yPosition, paintToUse);
    }

    private void drawMarkedCircle(Canvas canvasToDraw, int drawableSrc, int length, int topX, int topY) {
        drawCircle(canvasToDraw, drawableSrc, length, topX, topY);
        drawLinesAboveCircle(canvasToDraw, length, topX, topX);
    }

    private void drawCircle(Canvas canvasToDraw, int drawableSrc, int length, int topX, int topY) {
        canvasToDraw.drawBitmap(getBitmapByResource(drawableSrc, length), topX, topY, paintToUse);
    }

    private void drawLinesAboveCircle(Canvas canvasToDraw, int length, int topX, int topY) {
        int radius = length / 2;
        int x = topX + radius;
        int y = topY + radius;
        canvasToDraw.drawLine(x + radius * (float) Math.cos(Math.PI / 4),
                y + radius * (float) Math.sin(Math.PI / 4),
                x + radius * (float) Math.cos(5 * Math.PI / 4),
                y + radius * (float) Math.sin(5 * Math.PI / 4), paintToUse);
        canvasToDraw.drawLine(x + radius * (float) Math.cos(7 * Math.PI / 4),
                y + radius * (float) Math.sin(7 * Math.PI / 4),
                x + radius * (float) Math.cos(3 * Math.PI / 4),
                y + radius * (float) Math.sin(3 * Math.PI / 4), paintToUse);
    }

    private Bitmap getBitmapByResource(int resourceId, int sizeOfSide) {
        clearCacheIfSizeUpdated(sizeOfSide);
        Bitmap bitmap = bitmapCache.get(resourceId);
        if (bitmap == null) {
            bitmap = bitmapProvider.getBitmapByResourceId(resourceId, sizeOfSide);
            bitmapCache.put(resourceId, bitmap);
        }
        return bitmap;
    }

    private void clearCacheIfSizeUpdated(int sizeOfSide) {
        if (sizeOfSide != bitmapSideLength) {
            bitmapCache.clear();
            bitmapSideLength = sizeOfSide;
        }
    }

}
