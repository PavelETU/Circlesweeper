package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GameCellsToBitmapTest {

    private GameCellsDrawingHelper gameCellsToBitmap;
    private Canvas canvas = mock(Canvas.class);
    private GameCell[][] gameCells = new GameCell[2][2];

    @Before
    public void setUp() {
        BitmapProvider bitmapProvider = mock(BitmapProvider.class);
        gameCellsToBitmap = new GameCellsDrawingHelper(bitmapProvider, mock(Paint.class), mock(Rect.class));
    }

    @Test
    public void whenCircleNotAliveTextWithMinesDrawn() {
        teachCellsToReturnNumbers();

        gameCellsToBitmap.drawCellsOnCanvas(canvas, gameCells, null);

        verify(canvas, times(4)).drawText(any(), anyFloat(), anyFloat(), any());
        verifyNoMoreInteractions(canvas);
    }

    @Test
    public void circlesDrawnAsBitmapWhenTheyArePresent() {
        teachCellsToReturnDrawables();

        gameCellsToBitmap.drawCellsOnCanvas(canvas, gameCells, null);

        verify(canvas, times(4)).drawBitmap(any(), anyFloat(), anyFloat(), any());
        verifyNoMoreInteractions(canvas);
    }

    @Test
    public void bombDrawnWhenCircleIsNotAliveAndCellAnimated() {
        teachCellsToBeAnimated();

        gameCellsToBitmap.drawCellsOnCanvas(canvas, gameCells, null);

        verify(canvas, times(4)).drawBitmap(any(), anyFloat(), anyFloat(), any());
        verifyNoMoreInteractions(canvas);
    }

    @Test
    public void circlesAndLinesAboveDrawnWhenCirclesPresentAndMarked() {
        teachCellsToReturnDrawablesAndMarked();

        gameCellsToBitmap.drawCellsOnCanvas(canvas, gameCells, null);

        verify(canvas, times(4)).drawBitmap(any(), anyFloat(), anyFloat(), any());
        verify(canvas, times(8)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any());
        verifyNoMoreInteractions(canvas);
    }

    private void teachCellsToReturnNumbers() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                gameCells[i][j] = mock(GameCell.class);
                when(gameCells[i][j].isCircleInsideAlive()).thenReturn(false);
                when(gameCells[i][j].getMinesNear()).thenReturn(0);
            }
        }
    }

    private void teachCellsToReturnDrawables() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                gameCells[i][j] = mock(GameCell.class);
                when(gameCells[i][j].isCircleInsideAlive()).thenReturn(true);
                when(gameCells[i][j].getDrawableForCircle()).thenReturn(0);
            }
        }
    }

    private void teachCellsToBeAnimated() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                gameCells[i][j] = mock(GameCell.class);
                when(gameCells[i][j].isCircleInsideAlive()).thenReturn(false);
                when(gameCells[i][j].isAnimated()).thenReturn(true);
            }
        }
    }

    private void teachCellsToReturnDrawablesAndMarked() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                gameCells[i][j] = mock(GameCell.class);
                when(gameCells[i][j].isCircleInsideAlive()).thenReturn(true);
                when(gameCells[i][j].isMarked()).thenReturn(true);
            }
        }
    }

}