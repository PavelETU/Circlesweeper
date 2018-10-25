package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class SecondLevel implements GameLevel {
    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        return cellsGenerator.generateCellsForField3X4(width, height, 1);
    }
}
