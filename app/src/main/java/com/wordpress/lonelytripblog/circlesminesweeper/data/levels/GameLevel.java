package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;

public interface GameLevel {
    GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height);
    int getMinesAmount();
}
