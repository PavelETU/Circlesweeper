package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProvider;

public class CircleSweeperApp extends Application implements BitmapProvider {

    private static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
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

}
