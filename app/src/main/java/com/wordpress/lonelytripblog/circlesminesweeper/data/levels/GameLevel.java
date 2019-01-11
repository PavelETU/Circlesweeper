package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public interface GameLevel {
    GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height);

    int getMinesAmount();
}
