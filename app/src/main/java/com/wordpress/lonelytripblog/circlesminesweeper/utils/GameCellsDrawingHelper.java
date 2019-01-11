package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameCellsDrawingHelper {
    private static final int SCORE_OUT_FRAME_PADDING = 10;
    private BitmapProvider bitmapProvider;
    private Paint paintToUse;
    private Rect rectForTextMeasurement;
    private int xTopLeftMostOfBangedCircles;
    private int yTopLeftMostOfBangedCircles;
    private int xBottomRightMostOfBangedCircles;
    private int yBottomRightMostOfBangedCircles;
    private int centerOfAllBangsX;
    private int centerOfAllBangsY;

    public GameCellsDrawingHelper(BitmapProvider bitmapProvider, Paint paint, Rect rectForTextMeasurement) {
        this.bitmapProvider = bitmapProvider;
        paintToUse = paint;
        this.rectForTextMeasurement = rectForTextMeasurement;
    }

    public void drawCellsOnCanvas(Canvas canvasToDraw, @Nullable GameCell[][] gameCells, @Nullable String score) {
        if (gameCells == null || gameCells.length == 0) return;
        adjustPaintToLength(gameCells[0][0].getSideLength());
        setCoordsForScoreToDefault();
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
        drawScoreInTheMiddleOfBangedCircles(canvasToDraw, score);
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
                drawBangBitmapAndUpdateCoordsForScore(canvasToDrawIn, cellToDraw.getSideLength(),
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

    private void adjustPaintToLength(int length) {
        paintToUse.setTextSize(length / 2);
        paintToUse.setStrokeWidth(length / 15);
    }

    private void drawBangBitmapAndUpdateCoordsForScore(Canvas canvasToDraw, int length, int x, int y) {
        canvasToDraw.drawBitmap(bitmapProvider.getBangBitmap(length), x, y, paintToUse);
        updateCoordsForScore(length, x, y);
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

    private void setCoordsForScoreToDefault() {
        xTopLeftMostOfBangedCircles = Integer.MAX_VALUE;
        yTopLeftMostOfBangedCircles = Integer.MAX_VALUE;
        xBottomRightMostOfBangedCircles = -1;
        yBottomRightMostOfBangedCircles = -1;
    }

    private void updateCoordsForScore(int length, int x, int y) {
        int bottomX = x + length;
        int bottomY = y + length;
        if (xTopLeftMostOfBangedCircles > x) {
            xTopLeftMostOfBangedCircles = x;
        }
        if (yTopLeftMostOfBangedCircles > y) {
            yTopLeftMostOfBangedCircles = y;
        }
        if (xBottomRightMostOfBangedCircles < bottomX) {
            xBottomRightMostOfBangedCircles = bottomX;
        }
        if (yBottomRightMostOfBangedCircles < bottomY) {
            yBottomRightMostOfBangedCircles = bottomY;
        }
    }

    private void drawScoreInTheMiddleOfBangedCircles(Canvas canvas, @Nullable String score) {
        if (score == null || score.isEmpty() || xBottomRightMostOfBangedCircles == -1) return;
        makePaintTextSizeTwoTimesSmaller();
        calculateCenterOfAllBangBitmaps();
        drawScoreOnCanvas(canvas, score);
    }

    private void makePaintTextSizeTwoTimesSmaller() {
        paintToUse.setTextSize(paintToUse.getTextSize() / 2);
    }

    private void calculateCenterOfAllBangBitmaps() {
        centerOfAllBangsX = xTopLeftMostOfBangedCircles
                + (xBottomRightMostOfBangedCircles - xTopLeftMostOfBangedCircles) / 2;
        centerOfAllBangsY = yTopLeftMostOfBangedCircles
                + (yBottomRightMostOfBangedCircles - yTopLeftMostOfBangedCircles) / 2;
    }

    private void drawScoreOnCanvas(@NonNull Canvas canvas, @NonNull String score) {
        int transparentBlackColor = Color.argb(150, 0, 0, 0);
        String[] scoreOutput = score.split("\n");
        if (scoreOutput.length == 2) {
            drawTextLineAboveCenter(canvas, scoreOutput[0], transparentBlackColor);
            drawTextLineBelowCenter(canvas, scoreOutput[1], transparentBlackColor);
        } else {
            drawTextInTheCenter(canvas, score, transparentBlackColor);
        }
        paintToUse.setColor(Color.BLACK);
    }

    private void drawTextLineAboveCenter(Canvas canvas, String lineToDraw, int transparentBlackColor) {
        paintToUse.getTextBounds(lineToDraw, 0, lineToDraw.length(), rectForTextMeasurement);
        float xPosition = centerOfAllBangsX - rectForTextMeasurement.width() / 2 - rectForTextMeasurement.left;
        float yPosition = centerOfAllBangsY - rectForTextMeasurement.bottom - SCORE_OUT_FRAME_PADDING;
        drawTextWithBackground(canvas, lineToDraw, transparentBlackColor, xPosition, yPosition);
    }

    private void drawTextLineBelowCenter(Canvas canvas, String lineToDraw, int transparentBlackColor) {
        paintToUse.getTextBounds(lineToDraw, 0, lineToDraw.length(), rectForTextMeasurement);
        float xPosition = centerOfAllBangsX - rectForTextMeasurement.width() / 2 - rectForTextMeasurement.left;
        float yPosition = centerOfAllBangsY - rectForTextMeasurement.top + SCORE_OUT_FRAME_PADDING;
        drawTextWithBackground(canvas, lineToDraw, transparentBlackColor, xPosition, yPosition);
    }

    private void drawTextInTheCenter(Canvas canvas, String lineToDraw, int transparentBlackColor) {
        paintToUse.getTextBounds(lineToDraw, 0, lineToDraw.length(), rectForTextMeasurement);
        float xPosition = centerOfAllBangsX - rectForTextMeasurement.width() / 2 - rectForTextMeasurement.left;
        float yPosition = centerOfAllBangsY + rectForTextMeasurement.height() / 2 - rectForTextMeasurement.bottom;
        drawTextWithBackground(canvas, lineToDraw, transparentBlackColor, xPosition, yPosition);
    }

    private void drawTextWithBackground(Canvas canvas, String lineToDraw, int transparentBlackColor,
                                        float xPosition, float yPosition) {
        paintToUse.setColor(transparentBlackColor);
        canvas.drawRect(xPosition + rectForTextMeasurement.left - SCORE_OUT_FRAME_PADDING,
                yPosition + rectForTextMeasurement.top - SCORE_OUT_FRAME_PADDING,
                xPosition + rectForTextMeasurement.right + SCORE_OUT_FRAME_PADDING,
                yPosition + rectForTextMeasurement.bottom + SCORE_OUT_FRAME_PADDING, paintToUse);
        paintToUse.setColor(Color.WHITE);
        canvas.drawText(lineToDraw, xPosition, yPosition, paintToUse);
    }

}
