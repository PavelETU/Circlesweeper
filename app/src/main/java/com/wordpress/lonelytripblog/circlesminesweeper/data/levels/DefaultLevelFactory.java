package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.ui.CustomLevelDialogFragment;

public class DefaultLevelFactory implements LevelFactory {
    @Override
    public GameLevel makeLevel(int level, int customFieldSize, int customMines) {
        switch (level) {
            case 1:
                return new FirstTutorialLevel();
            case 2:
                return new SecondTutorialLevel();
            case 3:
                return new ThirdTutorialLevel();
            case 4:
                return new FirstLevel();
            case 5:
                return new SecondLevel();
            case 6:
                return new ThirdLevel();
            case 7:
                return new FourthLevel();
            case 8:
                return new FifthLevel();
            case 9:
                return getLevelForCustom(customFieldSize, customMines);
            default:
                throw new UnsupportedOperationException("Unknown level");
        }
    }

    @Override
    public int getAmountOfLevels() {
        return 9;
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
