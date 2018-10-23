package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CirclesGenerator;

public interface GameLevel {
    Circle[][] generateCircles(final CirclesGenerator circlesGenerator, final int width,
                               final int height);
}
