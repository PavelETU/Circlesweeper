package com.wordpress.lonelytripblog.circlesminesweeper.data;

public interface CellsGenerator {

    GameCell[][] generateCellsForField3X4(int width, int height, int bombsAmount);

    GameCell[][] generateCellsForField4X6(int width, int height, int bombsAmount);

    GameCell[][] generateCellsForField6X10(int width, int height, int bombsAmount);

}
