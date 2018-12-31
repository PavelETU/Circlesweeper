package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;

public interface BitmapProvider {
    Bitmap getBitmapByResourceId(int resourceId, int sizeOfBitmap);
}
