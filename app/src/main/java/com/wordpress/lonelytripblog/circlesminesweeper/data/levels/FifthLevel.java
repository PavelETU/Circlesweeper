package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class FifthLevel implements GameLevel {

    private final int MINES_AMOUNT = 7;

    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int smallerSideLength, int biggerSideLength) {
        return cellsGenerator.generateCellsForField6X10(smallerSideLength, biggerSideLength);
    }

    @Override
    public int getMinesAmount() {
        return MINES_AMOUNT;
    }
}
