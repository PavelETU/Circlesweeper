package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {

    static final int FIELD_3X4 = 0;
    static final int FIELD_4X6 = 1;
    static final int FIELD_6X10 = 2;
    private final CirclesGenerator circlesGenerator;
    private int level;
    private int width;
    private int height;
    private int customFieldSize;
    private int customMines;

    public GameViewModel(final CirclesGenerator circlesGenerator) {
        this.circlesGenerator = circlesGenerator;
    }

    // For convenience get circles as array[row][col],
    // where row and col always as in landscape orientation.
    // For example for the first level field is 3X4,
    // so array with size 3X4 returned even in portrait orientation,
    // while technically it should be 4x3
    public LiveData<Circle[][]> getCircles() {
        return new MutableLiveData<>();
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setCustomLevel(final int fieldSize, final int mines) {
        level = 5;
        customFieldSize = fieldSize;
        customMines = mines;
    }

    public void setSizeOfGameWindow(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public void startGame() {
        throwExceptionIfSizeNotSet();
        Circle[][] circles = getCirclesForLevel();
    }

    private Circle[][] getCirclesForLevel() {
        switch (level) {
            case 0:
                return circlesGenerator.generateCirclesForField3X4(width, height, 0);
            case 1:
                return circlesGenerator.generateCirclesForField3X4(width, height, 1);
            case 2:
                return circlesGenerator.generateCirclesForField4X6(width, height, 3);
            case 3:
                return circlesGenerator.generateCirclesForField6X10(width, height, 5);
            case 4:
                return circlesGenerator.generateCirclesForField6X10(width, height, 7);
            case 5:
                return generateCirclesForCustomLevel();
        }
        return null;
    }

    private Circle[][] generateCirclesForCustomLevel() {
        switch (customFieldSize) {
            case FIELD_3X4:
                return circlesGenerator.generateCirclesForField3X4(width, height, customMines);
            case FIELD_4X6:
                return circlesGenerator.generateCirclesForField4X6(width, height, customMines);
            case FIELD_6X10:
                return circlesGenerator.generateCirclesForField6X10(width, height, customMines);
            default:
                throw new UnsupportedOperationException("Unknown Field Size");
        }
    }

    private void throwExceptionIfSizeNotSet() {
        if (width == 0 || height == 0) {
            throw new RuntimeException("Specify width and height for game window");
        }
    }

}
