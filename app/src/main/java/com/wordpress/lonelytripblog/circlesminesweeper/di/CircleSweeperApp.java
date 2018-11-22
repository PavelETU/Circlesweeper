package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProvider;

public class CircleSweeperApp extends Application implements BitmapProvider {

    private static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
    }

    @Override
    public Bitmap getBitmapByResourceId(int resourceId) {
        return BitmapFactory.decodeResource(resources, resourceId);
    }

}
