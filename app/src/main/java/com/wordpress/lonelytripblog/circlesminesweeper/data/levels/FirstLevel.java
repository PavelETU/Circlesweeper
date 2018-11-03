package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;

public class FirstLevel implements GameLevel {
    private final int MINES_AMOUNT = 0;

    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        return cellsGenerator.generateCellsForField3X4(width, height, MINES_AMOUNT);
    }

    @Override
    public int getMinesAmount() {
        return MINES_AMOUNT;
    }
}
