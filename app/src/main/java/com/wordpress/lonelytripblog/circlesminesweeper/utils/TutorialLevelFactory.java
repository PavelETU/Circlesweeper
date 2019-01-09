package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstTutorialLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

public class TutorialLevelFactory implements LevelFactory {
    @Override
    public GameLevel makeLevel(int level, int customFieldSize, int customMines) {
        switch (level) {
            case 1:
                return new FirstTutorialLevel();
            default:
                throw new UnsupportedOperationException("Unknown level");
        }
    }

    @Override
    public int getAmountOfLevels() {
        return 1;
    }
}
