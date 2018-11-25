package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.di.CircleSweeperApp;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.Point;

import androidx.collection.SparseArrayCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GameViewModel extends AndroidViewModel {

    public static final int GAME_IN_PROCESS = 0;
    public static final int GAME_WON = 1;
    public static final int GAME_LOST = 2;

    private final CellsGenerator cellsGenerator;
    private GameLevel level;
    private GameCell[][] gameCells;
    private int gameWindowWidth;
    private int gameWindowHeight;
    private MutableLiveData<GameCell[][]> cellsLiveData;
    private MutableLiveData<Integer> gameScore = new MutableLiveData<>();
    private MutableLiveData<Integer> gameCondition = new MutableLiveData<>();
    private MutableLiveData<Integer> minesToDisplayLiveData = new MutableLiveData<>();
    private GameCell takenGameCell;
    private Pair<Integer, Integer> takenGameCellPosition;
    private Pair<Integer, Integer> swappedCirclePosition;
    private int minesCountToDisplayToTheUser;
    private int notMarkedCellsWithMines;
    private boolean circleWithBombWasEliminated;
    private boolean markState;

    public GameViewModel(CircleSweeperApp application, CellsGenerator cellsGenerator) {
        super(application);
        this.cellsGenerator = cellsGenerator;
    }

    // For convenience get circles as array[row][col],
    // where row and col always as in landscape orientation.
    // For example for the first level field is 3X4,
    // so array with size 3X4 returned even in portrait orientation,
    // while technically it should be 4x3
    public LiveData<GameCell[][]> getGameCells() {
        if (cellsLiveData == null) {
            cellsLiveData = new MutableLiveData<>();
            startGame();
        }
        return cellsLiveData;
    }

    public LiveData<Integer> getScore() {
        return gameScore;
    }

    public LiveData<Integer> getGameCondition() {
        return gameCondition;
    }

    public LiveData<Integer> getMinesToDisplay() {
        return minesToDisplayLiveData;
    }

    public void setLevel(GameLevel level) {
        this.level = level;
    }

    public void setSizeOfGameWindow(int width, int height) {
        gameWindowWidth = width;
        gameWindowHeight = height;
    }

    private void startGame() {
        throwExceptionIfSizeNotSet();
        gameCells = getCirclesForLevel();
        gameScore.setValue(0);
        gameCondition.setValue(GAME_IN_PROCESS);
        minesCountToDisplayToTheUser = level.getMinesAmount();
        notMarkedCellsWithMines = level.getMinesAmount();
        updateMinesLiveData();
        updateCellsLiveData();
    }

    public void markClicked() {
        markState = !markState;
    }

    public void actionDown(Point point) {
        int x = (int) point.getX();
        int y = (int) point.getY();
        takenGameCellPosition = findPositionForCellThatContainsPosition(x, y);
        if (takenGameCellPosition == null) return;
        GameCell gameCell = gameCells[takenGameCellPosition.first][takenGameCellPosition.second];
        if (markState) {
            gameCell.setMarked(!gameCell.isMarked());
            updateMinesCount(gameCell);
            markState = false;
            endGameIfWon();
            return;
        }
        if (gameCell.isMarked()) return;
        takenGameCell = gameCell;
        if (takenGameCell.isWithMine()) {
            endGameWithLoosing();
        }
        moveCircleAndUpdateLiveData(x, y);
    }

    public void actionMove(Point point) {
        int x = (int) point.getX();
        int y = (int) point.getY();
        if (takenGameCell == null) return;
        if (positionOutsideGameMetrics(x, y)) {
            returnTakenCircleToDefaultPositionAndZeroingIt();
            updateCellsLiveData();
            return;
        }
        swapCirclesIfTheyOverlappedAndCachedItsLocations(x, y);
        eliminateNeighborsWithSameColorAndUpdateScore();
        if (circleWithBombWasEliminated) {
            circleWithBombWasEliminated = false;
            endGameWithLoosing();
            return;
        }
        takenGameCell.moveCircleTo(x, y);
        updateCellsLiveData();
        endGameIfWon();
    }

    private void endGameIfWon() {
        if (gameWon()) {
            endGameWithWinning();
        }
    }

    public void actionUp() {
        if (takenGameCell == null) return;
        returnTakenCircleToDefaultPositionAndZeroingIt();
        updateCellsLiveData();
    }

    private Pair<Integer, Integer> findPositionForCellThatContainsPosition(int x, int y) {
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

    private void swapCirclesIfTheyOverlappedAndCachedItsLocations(int x, int y) {
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

    private void updateScoreBasedOnGoneCircles(int circlesGoneBecauseOfFirstCircle,
                                               int circlesGoneBecauseOfSecondCircle) {
        int scoreToAdd = circlesGoneBecauseOfFirstCircle * 10 + circlesGoneBecauseOfSecondCircle * 10;
        if (circlesGoneBecauseOfFirstCircle != 0 && circlesGoneBecauseOfSecondCircle != 0) {
            scoreToAdd *= 2;
        }
        addToScoreLiveData(scoreToAdd);
    }

    private void addToScoreLiveData(int scoreToAdd) {
        gameScore.setValue(gameScore.getValue() + scoreToAdd);
    }

    private boolean gameWon() {
        return notMarkedCellsWithMines == 0 && noSpareCirclesWithSameColor();
    }

    private boolean noSpareCirclesWithSameColor() {
        SparseArrayCompat<Integer> colorMap = new SparseArrayCompat<>();
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[0].length; j++) {
                GameCell currentCell = gameCells[i][j];
                if (!currentCell.isWithMine() && currentCell.isCircleInsideAlive()) {
                    colorMap.put(currentCell.getDrawableForCircle(),
                            getValueFromSparseArrayAtKey(colorMap, currentCell.getDrawableForCircle()) + 1);
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
    private int eliminateCirclesAndReturnEliminatedAmount(int row, int col) {
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

    private boolean cellToTheLeftIsSameColor(int row, int column) {
        if (column == 0) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row][column - 1]);
    }

    private boolean cellToTheRightIsSameColor(int row, int column) {
        if (column + 1 == gameCells[0].length) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row][column + 1]);
    }

    private boolean cellToTheBottomIsSameColor(int row, int column) {
        if (row + 1 == gameCells.length) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row + 1][column]);
    }

    private boolean cellToTheTopIsSameColor(int row, int column) {
        if (row == 0) return false;
        return gameCells[row][column].isColorTheSame(gameCells[row - 1][column]);
    }

    private void eliminateCircleAtPosition(int row, int col) {
        GameCell cellToEliminate = gameCells[row][col];
        if (cellToEliminate.isWithMine()) {
            circleWithBombWasEliminated = true;
        }
        cellToEliminate.eliminateCircle();
    }

    private GameCell[][] getCirclesForLevel() {
        throwExceptionIfHeightBiggerThanWidth();
        return level.generateCircles(cellsGenerator, gameWindowHeight, gameWindowWidth);
    }

    private void throwExceptionIfSizeNotSet() {
        if (gameWindowWidth <= 0 || gameWindowHeight <= 0) {
            throw new RuntimeException("Specify width and height for game window");
        }
    }

    private void throwExceptionIfHeightBiggerThanWidth() {
        if (gameWindowWidth < gameWindowHeight) {
            throw new RuntimeException("Height cannot be bigger than width in the game window");
        }
    }

    private boolean positionOutsideGameMetrics(int x, int y) {
        return x <= 1 || y <= 1 || x >= gameWindowWidth - 1 || y >= gameWindowHeight - 1;
    }

    private void returnTakenCircleToDefaultPositionAndZeroingIt() {
        takenGameCell.moveCircleToDefaultPosition();
        takenGameCell.makeCircleBigger();
        takenGameCell = null;
    }

    private void moveCircleAndUpdateLiveData(int x, int y) {
        takenGameCell.moveCircleTo(x, y);
        takenGameCell.makeCircleSmaller();
        updateCellsLiveData();
    }

    private void updateCellsLiveData() {
        cellsLiveData.setValue(gameCells);
    }

    private void updateMinesCount(GameCell gameCell) {
        if (gameCell.isMarked()) {
            minesCountToDisplayToTheUser--;
            if (gameCell.isWithMine()) {
                notMarkedCellsWithMines--;
            }
        } else {
            minesCountToDisplayToTheUser++;
            if (gameCell.isWithMine()) {
                notMarkedCellsWithMines++;
            }
        }
        updateMinesLiveData();
    }

    private void updateMinesLiveData() {
        minesToDisplayLiveData.setValue(minesCountToDisplayToTheUser);
    }

}
