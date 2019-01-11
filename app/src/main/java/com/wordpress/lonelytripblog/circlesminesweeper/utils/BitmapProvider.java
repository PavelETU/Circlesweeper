package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;

public interface BitmapProvider {
    void updateCacheIfNeeded(int currentSize);
    Bitmap getBombBitmap(int sizeOfBitmap);
    Bitmap getBangBitmap(int sizeOfBitmap);
    Bitmap getCircleBitmapWithSize(int resourceId, int radiusOfCircle);
}
