package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CirclesGenerator;

import static com.wordpress.lonelytripblog.circlesminesweeper.GameViewModel.FIELD_3X4;
import static com.wordpress.lonelytripblog.circlesminesweeper.GameViewModel.FIELD_4X6;
import static com.wordpress.lonelytripblog.circlesminesweeper.GameViewModel.FIELD_6X10;

public class CustomLevel implements GameLevel {

    private final int minesAmount;
    private final int fieldSize;

    public CustomLevel(int fieldSize, int minesAmount) {
        this.minesAmount = minesAmount;
        this.fieldSize = fieldSize;
    }

    @Override
    public Circle[][] generateCircles(CirclesGenerator circlesGenerator, int width, int height) {
        return generateCirclesForCustomLevel(circlesGenerator, width, height);
    }

    private Circle[][] generateCirclesForCustomLevel(CirclesGenerator circlesGenerator, int width, int height) {
        switch (fieldSize) {
            case FIELD_3X4:
                return circlesGenerator.generateCirclesForField3X4(width, height, minesAmount);
            case FIELD_4X6:
                return circlesGenerator.generateCirclesForField4X6(width, height, minesAmount);
            case FIELD_6X10:
                return circlesGenerator.generateCirclesForField6X10(width, height, minesAmount);
            default:
                throw new UnsupportedOperationException("Unknown Field Size");
        }
    }

}
