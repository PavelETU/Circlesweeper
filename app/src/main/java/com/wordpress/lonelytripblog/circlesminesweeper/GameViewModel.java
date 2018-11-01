package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

public class GameViewModel extends ViewModel {

    public static final int FIELD_3X4 = 0;
    public static final int FIELD_4X6 = 1;
    public static final int FIELD_6X10 = 2;
    private final CellsGenerator cellsGenerator;
    private GameLevel level;
    private GameCell[][] gameCells;
    private int width;
    private int height;
    private MutableLiveData<GameCell[][]> cellsLiveData = new MutableLiveData<>();
    private GameCell takenGameCell;
    private Pair<Integer, Integer> takenGameCellPosition;
    private Pair<Integer, Integer> swappedCirclePosition;

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
        gameCells = getCirclesForLevel();
        updateLiveData();
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
        takenGameCellPosition = findPositionForCellThatContainsPosition(x, y);
        if (takenGameCellPosition == null) return;
        takenGameCell = gameCells[takenGameCellPosition.first][takenGameCellPosition.second];
        moveCircleAndUpdateLiveData(x, y);
    }

    public void actionMove(final int x, final int y) {
        if (takenGameCell == null) return;
        swapCirclesIfTheyOverlappedAndCachedItsLocations(x, y);
        eliminateSwappedCirclesIfTheyHaveNeighborsWithSameColor();
        takenGameCell.moveCircleTo(x, y);
        updateLiveData();
    }

    public void actionUp() {
        if (takenGameCell == null) return;
        takenGameCell.moveCircleToDefaultPosition();
        takenGameCell.makeCircleBigger();
        takenGameCell = null;
        updateLiveData();
    }

    private GameCell findCellThatContainsPosition(final int x, final int y) {
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[0].length; j++) {
                GameCell gameCell = gameCells[i][j];
                if (gameCell.contains(x, y)) {
                    return gameCell;
                }
            }
        }
        return null;
    }

    private Pair<Integer, Integer> findPositionForCellThatContainsPosition(final int x, final int y) {
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[0].length; j++) {
                GameCell gameCell = gameCells[i][j];
                if (gameCell.contains(x, y)) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    private void swapCirclesIfTheyOverlappedAndCachedItsLocations(final int x, final int y) {
        if (!takenGameCell.contains(x, y)) {
            Pair<Integer, Integer> gameCellPosition = findPositionForCellThatContainsPosition(x, y);
            if (gameCellPosition != null) {
                GameCell cellToSwapBy = gameCells[gameCellPosition.first][gameCellPosition.second];
                takenGameCell.makeCircleBigger();
                takenGameCell.moveCircleToDefaultPosition();
                swappedCirclePosition = takenGameCellPosition;
                takenGameCell.swapCirclesWith(cellToSwapBy);
                takenGameCell = cellToSwapBy;
                takenGameCellPosition = gameCellPosition;
            }
        }
    }

    private void eliminateSwappedCirclesIfTheyHaveNeighborsWithSameColor() {
        if (swappedCirclePosition != null) {
            eliminateAllNeighborsWithSameColor(swappedCirclePosition.first, swappedCirclePosition.second);
            eliminateAllNeighborsWithSameColor(takenGameCellPosition.first, takenGameCellPosition.second);
            swappedCirclePosition = null;
        }
    }

    private void eliminateAllNeighborsWithSameColor(final int row, final int col) {
        int eliminatedCirclesCount = 0;
        if (cellToTheLeftIsSameColor(row, col)) {
            eliminateCircleAtPosition(row, col - 1);
            eliminatedCirclesCount++;
        }
        if (cellToTheRightIsSameColor(row, col)) {
            eliminateCircleAtPosition(row, col + 1);
            eliminatedCirclesCount++;
        }
        if (cellToTheBottomIsSameColor(row, col)) {
            eliminateCircleAtPosition(row + 1, col);
            eliminatedCirclesCount++;
        }
        if (cellToTheTopIsSameColor(row, col)) {
            eliminateCircleAtPosition(row - 1, col);
            eliminatedCirclesCount++;
        }
        if (eliminatedCirclesCount > 0) {
            eliminateCircleAtPosition(row, col);
        }
    }

    private boolean cellToTheLeftIsSameColor(final int row, final int column) {
        if (column == 0) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row][column - 1]);
    }

    private boolean cellToTheRightIsSameColor(final int row, final int column) {
        if (column + 1 == gameCells[0].length) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row][column + 1]);
    }

    private boolean cellToTheBottomIsSameColor(final int row, final int column) {
        if (row + 1 == gameCells.length) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row + 1][column]);
    }

    private boolean cellToTheTopIsSameColor(final int row, final int column) {
        if (row == 0) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row - 1][column]);
    }

    private void eliminateCircleAtPosition(final int row, final int col) {
        gameCells[row][col].eliminateCircle();
    }

    private void moveCircleAndUpdateLiveData(final int x, final int y) {
        takenGameCell.moveCircleTo(x, y);
        takenGameCell.makeCircleSmaller();
        updateLiveData();
    }

    private void updateLiveData() {
        cellsLiveData.setValue(gameCells);
    }

}
