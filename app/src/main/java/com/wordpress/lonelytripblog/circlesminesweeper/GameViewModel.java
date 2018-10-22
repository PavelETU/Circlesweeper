package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class GameViewModel extends ViewModel {

    private final CirclesGenerator circlesGenerator;
    private int level;
    private int width;
    private int height;

    public GameViewModel(final CirclesGenerator circlesGenerator) {
        this.circlesGenerator = circlesGenerator;
    }

    public LiveData<List<Circle>> getCircles() {
        return new MutableLiveData<>();
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setSizeOfGameWindow(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public void startGame() {
        throwExceptionIfSizeNotSet();
        circlesGenerator.generateCirclesForField3X4(width, height, 0);
    }

    public void throwExceptionIfSizeNotSet() {
        if (width  == 0 || height == 0) {
            throw new RuntimeException("Specify width and height for game window");
        }
    }

}
