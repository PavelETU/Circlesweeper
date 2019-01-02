package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

import androidx.collection.SparseArrayCompat;

import static com.wordpress.lonelytripblog.circlesminesweeper.data.Circle.PERCENTAGE_FOR_SMALLER_CIRCLE;

public class BitmapProviderImpl implements BitmapProvider {
    private static final int AMOUNT_OF_ALL_DRAWABLES = 8;
    private Resources resources;
    private SparseArrayCompat<Bitmap> bitmapCache = new SparseArrayCompat<>();
    private SparseArrayCompat<Bitmap> bitmapCacheForSmallerCircles = new SparseArrayCompat<>();
    private int bitmapSideLength;

    public BitmapProviderImpl(Resources resources) {
        this.resources = resources;
    }

    private Bitmap getBitmapByResourceId(int resourceId, int sizeOfBitmap) {
        Drawable shape = resources.getDrawable(resourceId);
        Bitmap bitmap = Bitmap.createBitmap(sizeOfBitmap, sizeOfBitmap, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        shape.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        shape.draw(canvas);
        return bitmap;
    }

    @Override
    public void updateCacheIfNeeded(int currentSize) {
        clearCacheIfSizeUpdated(currentSize);
        if (bitmapCache.size() != AMOUNT_OF_ALL_DRAWABLES) {
            cacheAllBitmaps(currentSize);
        }
    }

    @Override
    public Bitmap getBombBitmap(int sizeOfBitmap) {
        return bitmapCache.get(R.drawable.bomb);
    }

    @Override
    public Bitmap getBangBitmap(int sizeOfBitmap) {
        return bitmapCache.get(R.drawable.bang);
    }

    @Override
    public Bitmap getCircleBitmapWithSize(int resourceId, int radiusOfCircle) {
        return getBitmapByResourceIdFromCache(resourceId, radiusOfCircle);
    }

    private void cacheAllBitmaps(int size) {
        int smallerSize = (int) (size * PERCENTAGE_FOR_SMALLER_CIRCLE);

        cacheBombBitmap(size);
        cacheBangBitmap(size);

        cacheAllCircleBitmapsForSize(size, bitmapCache);
        cacheAllCircleBitmapsForSize(smallerSize, bitmapCacheForSmallerCircles);
    }

    private void cacheAllCircleBitmapsForSize(int size, SparseArrayCompat<Bitmap> sparseArrayCompat) {
        cacheIntoSpecificSparseArray(R.drawable.blue_ball, size, sparseArrayCompat);
        cacheIntoSpecificSparseArray(R.drawable.green_ball, size, sparseArrayCompat);
        cacheIntoSpecificSparseArray(R.drawable.purple_ball, size, sparseArrayCompat);
        cacheIntoSpecificSparseArray(R.drawable.yellow_ball, size, sparseArrayCompat);
        cacheIntoSpecificSparseArray(R.drawable.orange_ball, size, sparseArrayCompat);
        cacheIntoSpecificSparseArray(R.drawable.red_ball, size, sparseArrayCompat);
    }

    private void cacheIntoSpecificSparseArray(int resourceId, int size, SparseArrayCompat<Bitmap> sparseArrayCompat) {
        Bitmap bitmap = getBitmapByResourceId(resourceId, size);
        sparseArrayCompat.put(resourceId, bitmap);
    }

    private void cacheBombBitmap(int sizeOfSide) {
        Bitmap bitmap = getBitmapByResourceId(R.drawable.bomb, sizeOfSide);
        bitmapCache.put(R.drawable.bomb, bitmap);
    }

    private void cacheBangBitmap(int sizeOfSide) {
        Bitmap bitmap = getBitmapByResourceId(R.drawable.bang, sizeOfSide);
        bitmapCache.put(R.drawable.bang, bitmap);
    }

    private void clearCacheIfSizeUpdated(int sizeOfSide) {
        if (sizeOfSide != bitmapSideLength) {
            bitmapCache.clear();
            bitmapCacheForSmallerCircles.clear();
            bitmapSideLength = sizeOfSide;
        }
    }

    private Bitmap getBitmapByResourceIdFromCache(int resourceId, int sizeOfSide) {
        Bitmap bitmap;
        if (sizeOfSide < bitmapSideLength) {
            bitmap = bitmapCacheForSmallerCircles.get(resourceId);
        } else {
            bitmap = bitmapCache.get(resourceId);
        }
        if (bitmap == null) {
            throw new RuntimeException("Please, add bitmap with id " + resourceId
                    + " in cacheAllBitmaps method, so it won't be created in onDraw method");
        }
        return bitmap;
    }
}
