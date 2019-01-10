package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstTutorialLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.SecondTutorialLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.ThirdTutorialLevel;

public class TutorialLevelFactory implements LevelFactory {
    @Override
    public GameLevel makeLevel(int level, int customFieldSize, int customMines) {
        switch (level) {
            case 1:
                return new FirstTutorialLevel();
            case 2:
                return new SecondTutorialLevel();
            case 3:
                return new ThirdTutorialLevel();
            default:
                throw new UnsupportedOperationException("Unknown level");
        }
    }

    @Override
    public int getAmountOfLevels() {
        return 3;
    }
}
