package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import javax.inject.Inject;

import androidx.annotation.VisibleForTesting;
import androidx.collection.SparseArrayCompat;

public class GameCellsToBitmap {

    private BitmapProvider bitmapProvider;
    private SparseArrayCompat<Bitmap> bitmapCache = new SparseArrayCompat<>();
    private int bitmapSideLength;
    private Paint paintToUse;

    @Inject
    public GameCellsToBitmap(BitmapProvider bitmapProvider, Paint paint) {
        this.bitmapProvider = bitmapProvider;
        paintToUse = paint;
    }

    public Bitmap gameCellsToBitmap(GameCell[][] gameCells, float width, float height) {
        return createGameBitmapInOriginalSize(gameCells, width, height);
    }

    private Bitmap createGameBitmapInOriginalSize(GameCell[][] gameCells, float initWidth, float initHeight) {
        Bitmap bitmapForGame = Bitmap.createBitmap((int) initWidth, (int) initHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapForGame);
        drawCellsOnCanvas(canvas, gameCells);
        return bitmapForGame;
    }

    @VisibleForTesting
    void drawCellsOnCanvas(Canvas canvasToDraw, GameCell[][] gameCells) {
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                GameCell currentCell = gameCells[row][col];
                if (!currentCell.isCircleInsideAlive()) {
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
                } else {
                    drawCircleThatCouldBeMarked(canvasToDraw, currentCell.getDrawableForCircle(),
                            currentCell.getCircleSideLength(),
                            currentCell.getCircleTopLeftX(), currentCell.getCircleTopLeftY(), currentCell.isMarked());
                }
            }
        }
    }

    private void drawBangBitmap(Canvas canvasToDraw, int length, int x, int y) {
        canvasToDraw.drawBitmap(getBitmapByResource(R.drawable.bang, length), x, y, paintToUse);
    }

    private void drawBombBitmap(Canvas canvasToDraw, int length, int x, int y) {
        canvasToDraw.drawBitmap(getBitmapByResource(R.drawable.bomb, length), x, y, paintToUse);
    }

    private void drawMinesNumber(Canvas canvasToDraw, int length, int x, int y, int minesNumber) {
        canvasToDraw.drawText(String.valueOf(minesNumber), x + length / 2, y + length / 2, paintToUse);
    }

    private void drawCircleThatCouldBeMarked(Canvas canvasToDraw, int drawableSrc, int length, int topX, int topY,
                                             boolean drawLinesAbove) {
        canvasToDraw.drawBitmap(getBitmapByResource(drawableSrc, length), topX, topY, paintToUse);
        if (drawLinesAbove) {
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
