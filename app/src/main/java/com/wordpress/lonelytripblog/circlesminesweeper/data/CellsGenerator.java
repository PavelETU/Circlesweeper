package com.wordpress.lonelytripblog.circlesminesweeper.data;

public interface CellsGenerator {

    GameCell[][] generateCellsForField3X4(int smallerSide, int biggerSide, int bombsAmount);

    GameCell[][] generateCellsForField4X6(int smallerSide, int biggerSide, int bombsAmount);

    GameCell[][] generateCellsForField6X10(int smallerSide, int biggerSide, int bombsAmount);

}
