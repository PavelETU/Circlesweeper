package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class FifthLevel implements GameLevel {

    private final int MINES_AMOUNT = 7;

    @Override
    public GameCell[][] generateCircles(final CellsGenerator cellsGenerator, final int width,
                                        final int height) {
        return cellsGenerator.generateCellsForField6X10(width, height, MINES_AMOUNT);
    }

    @Override
    public int getMinesAmount() {
        return MINES_AMOUNT;
    }
}
