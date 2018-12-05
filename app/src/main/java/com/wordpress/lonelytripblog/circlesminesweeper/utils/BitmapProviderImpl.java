package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;

import javax.inject.Inject;

public class BitmapProviderImpl implements BitmapProvider {

    private Resources resources;

    @Inject
    public BitmapProviderImpl(Resources resources) {
        this.resources = resources;
    }

    @Override
    public Bitmap getBitmapByResourceId(int resourceId, int sizeOfBitmap) {
        Drawable shape = resources.getDrawable(resourceId);
        Bitmap bitmap = Bitmap.createBitmap(sizeOfBitmap, sizeOfBitmap, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        shape.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        shape.draw(canvas);
        return bitmap;
    }

    @Override
    public Bitmap getInvertedBitmapByResourceId(int resourceId, int sizeOfBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        return Bitmap.createBitmap(getBitmapByResourceId(resourceId, sizeOfBitmap), 0, 0,
                sizeOfBitmap, sizeOfBitmap, matrix, false);
    }

}