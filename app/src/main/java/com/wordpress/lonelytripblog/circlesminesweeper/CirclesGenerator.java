package com.wordpress.lonelytripblog.circlesminesweeper;

import java.util.List;

public interface CirclesGenerator {

    List<Circle> generateCirclesForField3X4(int width, int height, int bombsAmount);

}
