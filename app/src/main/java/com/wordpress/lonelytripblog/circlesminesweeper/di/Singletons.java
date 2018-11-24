package com.wordpress.lonelytripblog.circlesminesweeper.di;

import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsToBitmap;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.Mapper;

public class Singletons {

    private static Mapper mapper;

    public static Mapper getMapperWithForSize(CircleSweeperApp app, int width, int height) {
        if (mapper == null) {
            mapper = new Mapper(new GameCellsToBitmap(app), height, width);
        } else {
            mapper.setNewSize(height, width);
        }
        return mapper;
    }

}
