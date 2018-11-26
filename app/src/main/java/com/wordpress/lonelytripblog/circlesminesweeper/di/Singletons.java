package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.graphics.Color;
import android.graphics.Paint;

import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsToBitmap;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.Mapper;

public class Singletons {

    private static Mapper mapper;

    public static Mapper getMapperWithForSize(CircleSweeperApp app, int width, int height) {
        if (mapper == null) {
            Paint paintToUse = new Paint();
            paintToUse.setColor(Color.BLACK);
            paintToUse.setTextSize(100);
            paintToUse.setTextAlign(Paint.Align.CENTER);
            mapper = new Mapper(new GameCellsToBitmap(app, paintToUse), height, width);
        } else {
            mapper.setNewSize(height, width);
        }
        return mapper;
    }

}
