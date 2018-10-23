package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CirclesGenerator;

public class FifthLevel implements GameLevel {
    @Override
    public Circle[][] generateCircles(final CirclesGenerator circlesGenerator, final int width,
                                      final int height) {
        return circlesGenerator.generateCirclesForField6X10(width, height, 7);
    }
}
