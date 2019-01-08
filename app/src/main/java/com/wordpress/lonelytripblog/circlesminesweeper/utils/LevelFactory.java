package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

public interface LevelFactory {
    GameLevel makeLevel(int level, int customFieldSize, int customMines);

    int getAmountOfLevels();
}
