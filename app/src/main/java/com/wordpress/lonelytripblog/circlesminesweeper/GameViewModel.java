package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

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
    private MutableLiveData<Circle[][]> circlesLiveData = new MutableLiveData<>();

    public GameViewModel(final CirclesGenerator circlesGenerator) {
        this.circlesGenerator = circlesGenerator;
    }

    // For convenience get circles as array[row][col],
    // where row and col always as in landscape orientation.
    // For example for the first level field is 3X4,
    // so array with size 3X4 returned even in portrait orientation,
    // while technically it should be 4x3
    public LiveData<Circle[][]> getCircles() {
        return circlesLiveData;
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
        circlesLiveData.setValue(circles);
    }

    private Circle[][] getCirclesForLevel() {
        return level.generateCircles(circlesGenerator, width, height);
    }

    private void throwExceptionIfSizeNotSet() {
        if (width <= 0 || height <= 0) {
            throw new RuntimeException("Specify width and height for game window");
        }
    }

    public void actionDown(final int x, final int y) {
        Pair<Integer, Integer> rowAndColumn = findRowAndColumnForPosition(x, y);
        if (rowAndColumn == null) return;
        moveCircleAndUpdateLiveData(x, y, rowAndColumn.first, rowAndColumn.second);
    }

    @Nullable
    private Pair<Integer, Integer> findRowAndColumnForPosition(final int x, final int y) {
        Circle[][] currentCircles = circlesLiveData.getValue();
        if (currentCircles == null) return null;
        // TODO optimize algorithm from O(n^2) to O(1)
        for (int i = 0; i < currentCircles.length; i++) {
            for (int j = 0; j < currentCircles[0].length; j++) {
                Circle circle = currentCircles[i][j];
                if (circle.getX() + circle.getRadius() > x && circle.getX() - circle.getRadius() < x
                        && circle.getY() + circle.getRadius() > y && circle.getY() - circle.getRadius() < y) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    private void moveCircleAndUpdateLiveData(final int x, final int y,
                                             final int row, final int column) {
        Circle[][] currentCircles = circlesLiveData.getValue();
        Circle ourCircle = currentCircles[row][column];
        int oldRadius = ourCircle.getRadius();
        ourCircle.setX(x);
        ourCircle.setY(y);
        ourCircle.setRadius(oldRadius - (oldRadius / 8));
        circlesLiveData.setValue(currentCircles);
    }

}
