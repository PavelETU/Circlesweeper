package com.wordpress.lonelytripblog.circlesminesweeper.data;

public interface CellsGenerator {

    GameCell[][] regenerateCellsForNewSize(GameCell[][] gameCells,
                                           int newWidth, int newHeight);

    GameCell[][] generateCellsForField3X4(int width, int height);

    GameCell[][] generateCellsForField4X6(int width, int height);

    GameCell[][] generateCellsForField6X10(int width, int height);

    GameCell[][] generateEmptyCells(int width, int height, int amountOnSmallerSide, int amountOnBiggerSide);

    GameCell[][] generateMines(GameCell[][] gameCells, int amountOfMines);

}
