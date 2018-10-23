package com.wordpress.lonelytripblog.circlesminesweeper.data;

public interface CirclesGenerator {

    Circle[][] generateCirclesForField3X4(int width, int height, int bombsAmount);

    Circle[][] generateCirclesForField4X6(int width, int height, int bombsAmount);

    Circle[][] generateCirclesForField6X10(int width, int height, int bombsAmount);

}
