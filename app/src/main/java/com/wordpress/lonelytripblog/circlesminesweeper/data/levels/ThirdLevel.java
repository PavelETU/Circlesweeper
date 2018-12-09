package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class ThirdLevel implements GameLevel {

    private final int MINES_AMOUNT = 3;

    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int smallerSideLength, int biggerSideLength) {
        return cellsGenerator.generateCellsForField4X6(smallerSideLength, biggerSideLength);
    }

    @Override
    public int getMinesAmount() {
        return MINES_AMOUNT;
    }
}
