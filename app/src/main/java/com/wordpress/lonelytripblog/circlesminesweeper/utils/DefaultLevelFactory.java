package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel3X4;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel4X6;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel6X10;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FifthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FourthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.SecondLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.ThirdLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.ui.CustomLevelDialogFragment;

public class DefaultLevelFactory implements LevelFactory {
    @Override
    public GameLevel makeLevel(int level, int customFieldSize, int customMines) {
        switch (level) {
            case 1:
                return new FirstLevel();
            case 2:
                return new SecondLevel();
            case 3:
                return new ThirdLevel();
            case 4:
                return new FourthLevel();
            case 5:
                return new FifthLevel();
            case 6:
                return getLevelForCustom(customFieldSize, customMines);
            default:
                throw new UnsupportedOperationException("Unknown level");
        }
    }

    @Override
    public int getAmountOfLevels() {
        return 6;
    }

    private GameLevel getLevelForCustom(int customFieldSize, int customMines) {
        switch (customFieldSize) {
            case CustomLevelDialogFragment.FIELD_3_4:
                return new CustomLevel3X4(customMines);
            case CustomLevelDialogFragment.FIELD_4_6:
                return new CustomLevel4X6(customMines);
            case CustomLevelDialogFragment.FIELD_6_10:
                return new CustomLevel6X10(customMines);
            default:
                throw new UnsupportedOperationException("Unknown field size");
        }
    }
}
