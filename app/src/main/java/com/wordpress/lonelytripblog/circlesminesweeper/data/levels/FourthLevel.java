package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CirclesGenerator;

public class FourthLevel implements GameLevel {
    @Override
    public Circle[][] generateCircles(CirclesGenerator circlesGenerator, int width, int height) {
        return circlesGenerator.generateCirclesForField6X10(width, height, 5);
    }
}
