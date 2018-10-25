package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;

public class CustomLevel6X10 implements GameLevel {

    private final int minesAmount;

    public CustomLevel6X10(int minesAmount) {
        this.minesAmount = minesAmount;
    }

    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        return cellsGenerator.generateCellsForField6X10(width, height, minesAmount);
    }
}
