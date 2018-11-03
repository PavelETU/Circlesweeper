package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;

public class CustomLevel3X4 implements GameLevel {

    private final int minesAmount;

    public CustomLevel3X4(int minesAmount) {
        this.minesAmount = minesAmount;
    }

    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        return cellsGenerator.generateCellsForField3X4(width, height, minesAmount);
    }

    @Override
    public int getMinesAmount() {
        return minesAmount;
    }

}
