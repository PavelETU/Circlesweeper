package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

public class GameViewModel extends ViewModel {

    public static final int FIELD_3X4 = 0;
    public static final int FIELD_4X6 = 1;
    public static final int FIELD_6X10 = 2;
    private final CellsGenerator cellsGenerator;
    private GameLevel level;
    private int width;
    private int height;
    private MutableLiveData<GameCell[][]> cellsLiveData = new MutableLiveData<>();
    private GameCell takenGameCell;

    public GameViewModel(final CellsGenerator cellsGenerator) {
        this.cellsGenerator = cellsGenerator;
    }

    // For convenience get circles as array[row][col],
    // where row and col always as in landscape orientation.
    // For example for the first level field is 3X4,
    // so array with size 3X4 returned even in portrait orientation,
    // while technically it should be 4x3
    public LiveData<GameCell[][]> getGameCells() {
        return cellsLiveData;
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
        GameCell[][] gameCells = getCirclesForLevel();
        cellsLiveData.setValue(gameCells);
    }

    private GameCell[][] getCirclesForLevel() {
        return level.generateCircles(cellsGenerator, width, height);
    }

    private void throwExceptionIfSizeNotSet() {
        if (width <= 0 || height <= 0) {
            throw new RuntimeException("Specify width and height for game window");
        }
    }

    public void actionDown(final int x, final int y) {
        if (coordsOutOfCircles(x, y)) return;
        updateTakenCircle(x, y);
        moveCircleAndUpdateLiveData(x, y);
    }

    private boolean coordsOutOfCircles(final int x, final int y) {
        GameCell[][] currentGameCells = cellsLiveData.getValue();
        GameCell firstGameCell = currentGameCells[0][0];
        GameCell lastGameCell = currentGameCells[currentGameCells.length - 1][currentGameCells[0].length - 1];
        return firstGameCell.getLeft() >= x || firstGameCell.getTop() >= y
                || lastGameCell.getRight() <= x || lastGameCell.getBottom() <= y;
    }

    private void updateTakenCircle(final int x, final int y) {
        GameCell[][] currentGameCells = cellsLiveData.getValue();
        // TODO optimize algorithm from O(n^2) to O(1)
        for (int i = 0; i < currentGameCells.length; i++) {
            for (int j = 0; j < currentGameCells[0].length; j++) {
                GameCell gameCell = currentGameCells[i][j];
                if (gameCell.getRight() > x && gameCell.getLeft() < x && gameCell.getBottom() > y
                        && gameCell.getTop() < y) {
                    takenGameCell = gameCell;
                    return;
                }
            }
        }
    }

    public void actionUp() {
        takenGameCell.moveCircleToDefaultPosition();
        takenGameCell.makeCircleBigger();
        updateLiveData();
    }

    private void moveCircleAndUpdateLiveData(final int x, final int y) {
        takenGameCell.moveCircleTo(x, y);
        takenGameCell.makeCircleSmaller();
        updateLiveData();
    }

    private void updateLiveData() {
        // TODO this looks kind of ugly. Think how to do better
        cellsLiveData.setValue(cellsLiveData.getValue());
    }

}
