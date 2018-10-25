package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;

public interface GameLevel {
    GameCell[][] generateCircles(final CellsGenerator cellsGenerator, final int width,
                                 final int height);
}
