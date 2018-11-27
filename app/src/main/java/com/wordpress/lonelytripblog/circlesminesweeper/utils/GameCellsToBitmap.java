package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import androidx.annotation.VisibleForTesting;
import androidx.collection.SparseArrayCompat;

public class GameCellsToBitmap {

    private static final int paddingForMinesNumberImage = 20;

    private BitmapProvider bitmapProvider;
    private Paint paintToUse;
    private SparseArrayCompat<Bitmap> bitmapCache = new SparseArrayCompat<>();
    private SparseArrayCompat<Bitmap> invertedBitmapCache = new SparseArrayCompat<>();
    private boolean invert;

    public GameCellsToBitmap(BitmapProvider bitmapProvider, Paint paint) {
        this.bitmapProvider = bitmapProvider;
        paintToUse = paint;
    }

    public Bitmap gameCellsToBitmap(GameCell[][] gameCells, float initHeight, float initWidth,
                                    float height, float width, boolean invert) {
        this.invert = invert;
        Bitmap originalGameBitmap = createGameBitmapInOriginalSize(gameCells, initHeight, initWidth);
        return transformOriginalGameBitmapToCurrentSizeAndOrientation(originalGameBitmap, height, width);
    }

    private Bitmap createGameBitmapInOriginalSize(GameCell[][] gameCells, float initHeight, float initWidth) {
        Bitmap bitmapForGame = Bitmap.createBitmap((int) initWidth, (int) initHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapForGame);
        drawCellsOnCanvas(canvas, gameCells);
        return bitmapForGame;
    }

    private Bitmap transformOriginalGameBitmapToCurrentSizeAndOrientation(Bitmap originalBitmap,
                                                                          float height, float width) {
        Matrix matrix = new Matrix();
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        if (originalWidth != width || originalHeight != height) {
            float sx = width / (float) originalWidth;
            float sy = height / (float) originalHeight;
            float maxScale = Math.max(sx, sy);
            matrix.setScale(maxScale, maxScale);
        }
        if (invert) {
            matrix.postRotate(90);
        }

        return Bitmap.createBitmap(originalBitmap, 0, 0, originalWidth, originalHeight, matrix, false);
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
                        drawMinesNumber(canvasToDraw, currentCell.getSideLength(),
                                currentCell.getTopLeftX(), currentCell.getTopLeftY(), currentCell.getMinesNear());
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
        Bitmap bitmap;
        if (invert) {
            bitmap = getInvertedBitmapByResource(R.drawable.bang, length);
        } else {
            bitmap = getBitmapByResource(R.drawable.bang, length);
        }
        canvasToDraw.drawBitmap(bitmap, x, y, paintToUse);
    }

    private void drawMinesNumber(Canvas canvasToDraw, int length, int x, int y, int minesNumber) {
        Bitmap bitmap;
        if (invert) {
            bitmap = getInvertedBitmapByResource(getNumberDrawableByNumber(minesNumber),
                    length - paddingForMinesNumberImage);
        } else {
            bitmap = getBitmapByResource(getNumberDrawableByNumber(minesNumber),
                    length - paddingForMinesNumberImage);
        }
        canvasToDraw.drawBitmap(bitmap, x + paddingForMinesNumberImage / 2,
                y + paddingForMinesNumberImage / 2, paintToUse);
    }

    private void drawCircleThatCouldBeMarked(Canvas canvasToDraw, int drawableSrc, int length, int x, int y,
                                             boolean drawLinesAbove) {
        canvasToDraw.drawBitmap(getBitmapByResource(drawableSrc, length), x, y, paintToUse);
        if (drawLinesAbove) {
            canvasToDraw.drawLine(x, y, x + length, y + length, paintToUse);
            canvasToDraw.drawLine(x + length, y, x, y + length, paintToUse);
        }
    }

    private int getNumberDrawableByNumber(int number) {
        switch (number) {
            case 0:
                return R.drawable.zero;
            case 1:
                return R.drawable.one;
            case 2:
                return R.drawable.two;
            case 3:
                return R.drawable.three;
            case 4:
                return R.drawable.four;
            case 5:
                return R.drawable.five;
            case 6:
                return R.drawable.six;
            case 7:
                return R.drawable.seven;
            case 8:
                return R.drawable.eight;
            default:
                return 0;
        }
    }

    private Bitmap getBitmapByResource(int resourceId, int sizeOfSide) {
        Bitmap bitmap = bitmapCache.get(resourceId);
        if (bitmap == null) {
            bitmap = bitmapProvider.getBitmapByResourceId(resourceId, sizeOfSide);
            bitmapCache.put(resourceId, bitmap);
        }
        return bitmap;
    }

    private Bitmap getInvertedBitmapByResource(int resourceId, int sizeOfSide) {
        Bitmap bitmap = invertedBitmapCache.get(resourceId);
        if (bitmap == null) {
            bitmap = bitmapProvider.getInvertedBitmapByResourceId(resourceId, sizeOfSide);
            invertedBitmapCache.put(resourceId, bitmap);
        }
        return bitmap;
    }

}
