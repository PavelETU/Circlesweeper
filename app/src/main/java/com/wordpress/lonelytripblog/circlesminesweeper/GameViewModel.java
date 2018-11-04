package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;
import android.support.v4.util.SparseArrayCompat;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

public class GameViewModel extends ViewModel {

    public static final int GAME_IN_PROCESS = 0;
    public static final int GAME_WON = 1;
    public static final int GAME_LOST = 2;

    private final CellsGenerator cellsGenerator;
    private GameLevel level;
    private GameCell[][] gameCells;
    private int gameWindowWidth;
    private int gameWindowHeight;
    private MutableLiveData<GameCell[][]> cellsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> gameScore = new MutableLiveData<>();
    private MutableLiveData<Integer> gameCondition = new MutableLiveData<>();
    private MutableLiveData<Integer> leftMinesLiveData = new MutableLiveData<>();
    private GameCell takenGameCell;
    private Pair<Integer, Integer> takenGameCellPosition;
    private Pair<Integer, Integer> swappedCirclePosition;
    private int minesLeft;
    private boolean circleWithBombWasEliminated;
    private boolean markState;

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

    public LiveData<Integer> getScore() {
        return gameScore;
    }

    public LiveData<Integer> getGameCondition() {
        return gameCondition;
    }

    public LiveData<Integer> getLeftMines() {
        return leftMinesLiveData;
    }

    public void setLevel(final GameLevel level) {
        this.level = level;
    }

    public void setSizeOfGameWindow(final int width, final int height) {
        gameWindowWidth = width;
        gameWindowHeight = height;
    }

    public void startGame() {
        throwExceptionIfSizeNotSet();
        gameCells = getCirclesForLevel();
        gameScore.setValue(0);
        gameCondition.setValue(GAME_IN_PROCESS);
        minesLeft = level.getMinesAmount();
        updateMinesLiveData();
        updateCellsLiveData();
    }

    public void markClicked() {
        markState = !markState;
    }

    public void actionDown(final int x, final int y) {
        takenGameCellPosition = findPositionForCellThatContainsPosition(x, y);
        if (takenGameCellPosition == null) return;
        if (markState) {
            gameCells[takenGameCellPosition.first][takenGameCellPosition.second].setMarked(true);
            markState = false;
            return;
        }
        takenGameCell = gameCells[takenGameCellPosition.first][takenGameCellPosition.second];
        if (takenGameCell.isWithMine()) {
            endGameWithLoosing();
        }
        moveCircleAndUpdateLiveData(x, y);
    }

    public void actionMove(final int x, final int y) {
        if (takenGameCell == null) return;
        swapCirclesIfTheyOverlappedAndCachedItsLocations(x, y);
        eliminateNeighborsWithSameColorAndUpdateScore();
        if (circleWithBombWasEliminated) {
            circleWithBombWasEliminated = false;
            endGameWithLoosing();
            return;
        }
        if (gameWon()) {
            endGameWithWinning();
            return;
        }
        takenGameCell.moveCircleTo(x, y);
        updateCellsLiveData();
    }

    public void actionUp() {
        if (takenGameCell == null) return;
        takenGameCell.moveCircleToDefaultPosition();
        takenGameCell.makeCircleBigger();
        takenGameCell = null;
        updateCellsLiveData();
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

    private void eliminateNeighborsWithSameColorAndUpdateScore() {
        if (swappedCirclePosition != null) {
            int countForFirst = eliminateCirclesAndReturnEliminatedAmount(
                    swappedCirclePosition.first, swappedCirclePosition.second);
            int countForSecond = eliminateCirclesAndReturnEliminatedAmount(
                    takenGameCellPosition.first, takenGameCellPosition.second);
            updateScoreBasedOnGoneCircles(countForFirst, countForSecond);
            swappedCirclePosition = null;
        }
    }

    private void updateScoreBasedOnGoneCircles(final int circlesGoneBecauseOfFirstCircle,
                                               final int circlesGoneBecauseOfSecondCircle) {
        int scoreToAdd = circlesGoneBecauseOfFirstCircle * 10 + circlesGoneBecauseOfSecondCircle * 10;
        if (circlesGoneBecauseOfFirstCircle != 0 && circlesGoneBecauseOfSecondCircle != 0) {
            scoreToAdd *= 2;
        }
        addToScoreLiveData(scoreToAdd);
    }

    private void addToScoreLiveData(final int scoreToAdd) {
        gameScore.setValue(gameScore.getValue() + scoreToAdd);
    }

    private boolean gameWon() {
        return minesLeft == 0 && noSpareCirclesWithSameColor();
    }

    private boolean noSpareCirclesWithSameColor() {
        SparseArrayCompat<Integer> colorMap = new SparseArrayCompat<>();
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[0].length; j++) {
                GameCell currentCell = gameCells[i][j];
                if (!currentCell.isWithMine() && currentCell.isCircleInsideAlive()) {
                    colorMap.put(currentCell.getColor(),
                            getValueFromSparseArrayAtKey(colorMap, currentCell.getColor()) + 1);
                }
            }
        }
        for (int i = 0; i < colorMap.size(); i++) {
            int countForColor = colorMap.valueAt(i);
            if (countForColor > 1) {
                return false;
            }
        }
        return true;
    }

    private int getValueFromSparseArrayAtKey(SparseArrayCompat<Integer> sparseArray, int key) {
        Integer rawValue = sparseArray.get(key);
        if (rawValue != null) {
            return rawValue;
        }
        return 0;
    }

    private void endGameWithWinning() {
        gameCondition.setValue(GAME_WON);
    }

    private void endGameWithLoosing() {
        gameCondition.setValue(GAME_LOST);
    }

    // Breaks Command-Query separation
    private int eliminateCirclesAndReturnEliminatedAmount(final int row, final int col) {
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
            eliminatedCirclesCount++;
        }
        return eliminatedCirclesCount;
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
        GameCell cellToEliminate = gameCells[row][col];
        if (cellToEliminate.isWithMine()) {
            circleWithBombWasEliminated = true;
        }
        cellToEliminate.eliminateCircle();
    }

    private GameCell[][] getCirclesForLevel() {
        return level.generateCircles(cellsGenerator, gameWindowWidth, gameWindowHeight);
    }

    private void throwExceptionIfSizeNotSet() {
        if (gameWindowWidth <= 0 || gameWindowHeight <= 0) {
            throw new RuntimeException("Specify width and height for game window");
        }
    }

    private void moveCircleAndUpdateLiveData(final int x, final int y) {
        takenGameCell.moveCircleTo(x, y);
        takenGameCell.makeCircleSmaller();
        updateCellsLiveData();
    }

    private void updateCellsLiveData() {
        cellsLiveData.setValue(gameCells);
    }

    private void updateMinesLiveData() {
        leftMinesLiveData.setValue(minesLeft);
    }

}
