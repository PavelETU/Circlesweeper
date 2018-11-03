package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class CustomLevel4X6 implements GameLevel {

    private final int minesAmount;

    public CustomLevel4X6(int minesAmount) {
        this.minesAmount = minesAmount;
    }


    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        return cellsGenerator.generateCellsForField4X6(width, height, minesAmount);
    }

    @Override
    public int getMinesAmount() {
        return minesAmount;
    }
}
