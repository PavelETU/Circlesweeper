package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class GameCellsDrawingHelper {

    private BitmapProvider bitmapProvider;
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
        List<GameCell> cellsToDrawLater = new ArrayList<>();
        for (int row = 0; row < gameCells.length; row++) {
            for (int col = 0; col < gameCells[0].length; col++) {
                GameCell currentCell = gameCells[row][col];
                if (currentCell.drawCellLast()) {
                    cellsToDrawLater.add(currentCell);
                } else {
                    drawCell(currentCell, canvasToDraw);
                }
            }
        }
        for (GameCell gameCell : cellsToDrawLater) {
            drawCell(gameCell, canvasToDraw);
        }
    }

    private void drawCell(GameCell cellToDraw, Canvas canvasToDrawIn) {
        if (cellToDraw.isCircleInsideAlive()) {
            if (cellToDraw.isMarked()) {
                drawMarkedCircle(canvasToDrawIn, cellToDraw.getDrawableForCircle(),
                        cellToDraw.getCircleSideLength(),
                        cellToDraw.getCircleTopLeftX(), cellToDraw.getCircleTopLeftY());
            } else {
                drawCircle(canvasToDrawIn, cellToDraw.getDrawableForCircle(),
                        cellToDraw.getCircleSideLength(),
                        cellToDraw.getCircleTopLeftX(), cellToDraw.getCircleTopLeftY());
            }
        } else {
            if (cellToDraw.isAnimated()) {
                drawBangBitmap(canvasToDrawIn, cellToDraw.getSideLength(),
                        cellToDraw.getTopLeftX(), cellToDraw.getTopLeftY());
            } else {
                if (cellToDraw.isWithMine()) {
                    drawBombBitmap(canvasToDrawIn, cellToDraw.getSideLength(),
                            cellToDraw.getTopLeftX(), cellToDraw.getTopLeftY());
                } else {
                    drawMinesNumber(canvasToDrawIn, cellToDraw.getSideLength(),
                            cellToDraw.getTopLeftX(), cellToDraw.getTopLeftY(),
                            cellToDraw.getMinesNear());
                }
            }
        }
    }

    private void adjustPaintTextSizeToGameCells(GameCell[][] gameCells) {
        paintToUse.setTextSize(gameCells[0][0].getSideLength() / 2);
    }

    private void drawBangBitmap(Canvas canvasToDraw, int length, int x, int y) {
        canvasToDraw.drawBitmap(bitmapProvider.getBangBitmap(length), x, y, paintToUse);
    }

    private void drawBombBitmap(Canvas canvasToDraw, int length, int x, int y) {
        canvasToDraw.drawBitmap(bitmapProvider.getBombBitmap(length), x, y, paintToUse);
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
        drawLinesAboveCircle(canvasToDraw, length, topX, topY);
    }

    private void drawCircle(Canvas canvasToDraw, int drawableSrc, int length, int topX, int topY) {
        canvasToDraw.drawBitmap(bitmapProvider.getCircleBitmapWithSize(drawableSrc, length), topX, topY, paintToUse);
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

}
