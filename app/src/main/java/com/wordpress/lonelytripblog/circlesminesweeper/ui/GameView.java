package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProvider;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProviderImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsDrawingHelper;

import androidx.annotation.Nullable;

public class GameView extends View {
    private GameCellsDrawingHelper gameCellsDrawingHelper;
    private GameCell[][] gameCells;
    private BitmapProvider bitmapProvider;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Paint paintToUse = new Paint();
        paintToUse.setColor(Color.BLACK);
        paintToUse.setTextAlign(Paint.Align.LEFT);
        paintToUse.setStrokeWidth(20);
        bitmapProvider = new BitmapProviderImpl(getResources());
        gameCellsDrawingHelper = new GameCellsDrawingHelper(bitmapProvider, paintToUse, new Rect());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        gameCellsDrawingHelper.drawCellsOnCanvas(canvas, gameCells);
    }

    public void setGameCells(@Nullable GameCell[][] gameCells) {
        this.gameCells = gameCells;
        if (gameCells != null && gameCells.length != 0) {
            bitmapProvider.updateCacheIfNeeded(gameCells[0][0].getSideLength());
        }
        invalidate();
    }
}
