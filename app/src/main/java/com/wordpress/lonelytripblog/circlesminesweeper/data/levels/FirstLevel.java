package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;

public class FirstLevel implements GameLevel {
    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        return cellsGenerator.generateCellsForField3X4(width, height, 0);
    }
}
