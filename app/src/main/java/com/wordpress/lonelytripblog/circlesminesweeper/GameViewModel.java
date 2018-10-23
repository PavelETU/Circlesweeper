package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CirclesGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

public class GameViewModel extends ViewModel {

    public static final int FIELD_3X4 = 0;
    public static final int FIELD_4X6 = 1;
    public static final int FIELD_6X10 = 2;
    private final CirclesGenerator circlesGenerator;
    private GameLevel level;
    private int width;
    private int height;

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

    public void setLevel(final GameLevel level) {
        this.level = level;
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
        level.generateCircles(circlesGenerator, width, height);
        return null;
    }

    private void throwExceptionIfSizeNotSet() {
        if (width == 0 || height == 0) {
            throw new RuntimeException("Specify width and height for game window");
        }
    }

}
