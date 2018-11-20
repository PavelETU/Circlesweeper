package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import androidx.annotation.VisibleForTesting;

public class GameCellsToBitmap {

    private GameCellsToBitmap() {}

    public static Bitmap gameCellsToBitmap(GameCell[][] gameCells, float initHeight, float initWidth,
                                           float height, float width, boolean invert) {
        Bitmap originalGameBitmap = createGameBitmapInOriginalSize(gameCells, initHeight, initWidth);
        return transformOriginalGameBitmapToCurrentSizeAndOrientation(originalGameBitmap, height, width, invert);
    }

    private static Bitmap createGameBitmapInOriginalSize(GameCell[][] gameCells, float initHeight, float initWidth) {
        Bitmap bitmapForGame = Bitmap.createBitmap((int) initWidth, (int) initHeight, Bitmap.Config.ARGB_8888);
        drawCellsOnCanvas(new Canvas(bitmapForGame), gameCells);
        return bitmapForGame;
    }

    private static Bitmap transformOriginalGameBitmapToCurrentSizeAndOrientation(Bitmap originalBitmap,
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
    static void drawCellsOnCanvas(Canvas canvasToDraw, GameCell[][] gameCells) {
        Paint paintToUse = new Paint();
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                GameCell currentCell = gameCells[row][col];
                if (!currentCell.isCircleInsideAlive()) {
                    canvasToDraw.drawText(String.valueOf(currentCell.getMinesNear()),
                            currentCell.getTopLeftX(), currentCell.getTopLeftY(), paintToUse);
                }
            }
        }
    }

}
