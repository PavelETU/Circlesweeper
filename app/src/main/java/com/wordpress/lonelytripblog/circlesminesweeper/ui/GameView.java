package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProvider;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProviderImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsDrawingHelper;

import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;

public class GameView extends View implements BitmapProvider {
    private GameCellsDrawingHelper gameCellsDrawingHelper;
    private GameCell[][] gameCells;
    private BitmapProvider bitmapProvider;
    private SparseArrayCompat<Bitmap> bitmapCache = new SparseArrayCompat<>();
    private int bitmapSideLength;

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
        gameCellsDrawingHelper = new GameCellsDrawingHelper(this, paintToUse, new Rect());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        gameCellsDrawingHelper.drawCellsOnCanvas(canvas, gameCells);
    }

    public void setGameCells(@Nullable GameCell[][] gameCells) {
        this.gameCells = gameCells;
        cacheAllBitmaps();
        invalidate();
    }

    private void cacheAllBitmaps() {
        int size = gameCells[0][0].getSideLength();
        clearCacheIfSizeUpdated(size);
        if (bitmapCache.size() == 8) return;

        cacheBitmap(R.drawable.bomb, size);
        cacheBitmap(R.drawable.bang, size);

        cacheBitmap(R.drawable.blue_ball, size);
        cacheBitmap(R.drawable.green_ball, size);
        cacheBitmap(R.drawable.purple_ball, size);
        cacheBitmap(R.drawable.yellow_ball, size);
        cacheBitmap(R.drawable.orange_ball, size);
        cacheBitmap(R.drawable.red_ball, size);
    }

    private void cacheBitmap(int resourceId, int sizeOfSide) {
        Bitmap bitmap = bitmapProvider.getBitmapByResourceId(resourceId, sizeOfSide);
        bitmapCache.put(resourceId, bitmap);
    }

    @Override
    public Bitmap getBitmapByResourceId(int resourceId, int sizeOfSide) {
        Bitmap bitmap = bitmapCache.get(resourceId);
        if (bitmap == null) {
            throw new RuntimeException("Please, add bitmap with id " + resourceId
                    + " in cacheAllBitmaps method, so it won't be created in onDraw method");
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
