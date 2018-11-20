package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Canvas;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameCellsToBitmapTest {

    private GameCell[][] gameCells = new GameCell[2][2];

    @Test
    public void gameCellsToBitmap() {
        Canvas canvas = mock(Canvas.class);
        teachCellsToReturnNumbers();

        GameCellsToBitmap.drawCellsOnCanvas(canvas, gameCells);

        verify(canvas, times(4)).drawText(any(), anyFloat(), anyFloat(), any());
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

}