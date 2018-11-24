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

    private BitmapProvider bitmapProvider;
    private Paint paintToUse = new Paint();
    private SparseArrayCompat<Bitmap> bitmapCache = new SparseArrayCompat<>();

    public GameCellsToBitmap(BitmapProvider bitmapProvider) {
        this.bitmapProvider = bitmapProvider;
    }

    public Bitmap gameCellsToBitmap(GameCell[][] gameCells, float initHeight, float initWidth,
                                    float height, float width, boolean invert) {
        Bitmap originalGameBitmap = createGameBitmapInOriginalSize(gameCells, initHeight, initWidth);
        return transformOriginalGameBitmapToCurrentSizeAndOrientation(originalGameBitmap, height, width, invert);
    }

    private Bitmap createGameBitmapInOriginalSize(GameCell[][] gameCells, float initHeight, float initWidth) {
        Bitmap bitmapForGame = Bitmap.createBitmap((int) initWidth, (int) initHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapForGame);
        drawCellsOnCanvas(canvas, gameCells);
        return bitmapForGame;
    }

    private Bitmap transformOriginalGameBitmapToCurrentSizeAndOrientation(Bitmap originalBitmap,
                                                                          float height, float width,
                                                                          boolean invert) {
        Matrix matrix = new Matrix();
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        if (originalWidth != width || originalHeight != height) {
            float sx = width / width;
            float sy = height / height;
            matrix.setScale(sx, sy);
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
                        canvasToDraw.drawBitmap(getBitmapByResource(R.drawable.bang, currentCell.getSideLength()),
                                currentCell.getTopLeftX(), currentCell.getTopLeftY(), paintToUse);
                    } else {
                        canvasToDraw.drawText(String.valueOf(currentCell.getMinesNear()),
                                currentCell.getTopLeftX(), currentCell.getTopLeftY(), paintToUse);
                    }
                } else {
                    canvasToDraw.drawBitmap(getBitmapByResource(currentCell.getDrawableForCircle(),
                            currentCell.getSideLength()),
                            currentCell.getTopLeftX(), currentCell.getTopLeftY(), paintToUse);
                    if (currentCell.isMarked()) {
                        canvasToDraw.drawLine(currentCell.getTopLeftX(), currentCell.getTopLeftY(),
                                currentCell.getTopLeftX() + currentCell.getSideLength(),
                                currentCell.getTopLeftY() + currentCell.getSideLength(), paintToUse);
                        canvasToDraw.drawLine(currentCell.getTopLeftX() + currentCell.getSideLength(),
                                currentCell.getTopLeftY(), currentCell.getTopLeftX(),
                                currentCell.getTopLeftY() + currentCell.getSideLength(), paintToUse);
                    }
                }
            }
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

}
