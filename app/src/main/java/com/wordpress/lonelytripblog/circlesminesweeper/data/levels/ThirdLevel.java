package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CirclesGenerator;

public class ThirdLevel implements GameLevel {
    @Override
    public Circle[][] generateCircles(CirclesGenerator circlesGenerator, int width, int height) {
        return circlesGenerator.generateCirclesForField4X6(width, height, 3);
    }
}
