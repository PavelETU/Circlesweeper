package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.TutorialLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.GameToSaveObject;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LiveEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.collection.SparseArrayCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {

    public static final int GAME_IN_PROCESS = 0;
    public static final int GAME_WON = 1;
    public static final int GAME_LOST = 2;
    public static final int SHOW_CUSTOM_LEVEL_DIALOG = 3;

    private final CellsGenerator cellsGenerator;
    private final Handler mainHandler;
    private final Runnable stopAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            for (GameCell gameCell : eliminatedCells) {
                gameCell.setAnimated(false);
            }
            eliminatedCells.clear();
            updateCellsLiveData();
        }
    };
    private GameLevel level;
    private GameCell[][] gameCells;
    private int gameWindowWidth;
    private int gameWindowHeight;
    private MutableLiveData<GameCell[][]> cellsLiveData;
    private MutableLiveData<Integer> gameScore = new MutableLiveData<>();
    private MutableLiveData<Integer> gameCondition = new MutableLiveData<>();
    private MutableLiveData<Integer> minesToDisplayLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> checkButtonSrc;
    private MutableLiveData<LiveEvent<Integer>> toastEvent = new MutableLiveData<>();
    private MutableLiveData<String> scoreToDisplayInGameView = new MutableLiveData<>();
    private MutableLiveData<Integer> snackbarMessage = new MutableLiveData<>();
    private GameCell takenGameCell;
    private Pair<Integer, Integer> takenGameCellPosition;
    private Pair<Integer, Integer> swappedCirclePosition;
    private int minesCountToDisplayToTheUser;
    private int notMarkedCellsWithMines;
    private boolean circleWithBombWasEliminated;
    private boolean markState;
    private List<GameCell> eliminatedCells = new ArrayList<>();
    private GameRepository gameRepository;
    private boolean minesGenerated;

    @Inject
    GameViewModel(CellsGenerator cellsGenerator, Handler mainHandler, GameRepository gameRepository) {
        this.cellsGenerator = cellsGenerator;
        this.mainHandler = mainHandler;
        this.gameRepository = gameRepository;
    }

    // For convenience get circles as array[row][col],
    // where row and col always as in landscape orientation.
    // For example for the first level field is 3X4,
    // so array with size 3X4 returned even in portrait orientation,
    // while technically it should be 4x3
    public LiveData<GameCell[][]> getGameCells(int width, int height) {
        if (cellsLiveData == null) {
            gameWindowWidth = width;
            gameWindowHeight = height;
            cellsLiveData = new MutableLiveData<>();
            startGame();
        }
        if (width != gameWindowWidth || height != gameWindowHeight) {
            recreateCellsWithNewSize(width, height);
            gameWindowWidth = width;
            gameWindowHeight = height;
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

    public LiveData<Integer> getCheckButtonSrc() {
        if (checkButtonSrc == null) {
            checkButtonSrc = new MutableLiveData<>();
            checkButtonSrc.setValue(markState ? R.drawable.bomb_check_pressed : R.drawable.bomb_check);
        }
        return checkButtonSrc;
    }

    public void markClicked() {
        moveToMarkUnmarkState();
    }

    public void nextRepeatClicked() {
        startGame();
    }

    public void actionDown(int x, int y) {
        takenGameCellPosition = findPositionForCellThatContainsPosition(x, y);
        if (takenGameCellPosition == null) return;
        GameCell gameCell = gameCells[takenGameCellPosition.first][takenGameCellPosition.second];
        if (markState && gameCell.isCircleInsideAlive()) {
            boolean marked = !gameCell.isMarked();
            gameCell.setMarked(marked);
            updateMinesCount(gameCell);
            if (!marked) {
                int countForFirst = eliminateCirclesAndReturnEliminatedAmount(
                        takenGameCellPosition.first, takenGameCellPosition.second);
                updateScoreBasedOnGoneCircles(countForFirst, 0);
                setUpAnimationIfCirclesWereEliminated();
            }
            endGameIfWon();
            updateCellsLiveData();
            moveToMarkUnmarkState();
            return;
        }
        if (gameCell.isMarked() || !gameCell.isCircleInsideAlive()) return;
        takenGameCell = gameCell;
        if (takenGameCell.isWithMine()) {
            endGameWithLoosing();
            return;
        }
        stopAnimationImmediatelyIfItsPending();
        moveCircleAndUpdateLiveData(x, y);
    }

    public void actionMove(int x, int y) {
        if (takenGameCell == null) return;
        if (positionOutsideGameMetrics(x, y) || positionOnMarkedCircle(x, y)) {
            returnTakenCircleToDefaultPositionAndZeroingIt();
            updateCellsLiveData();
            return;
        }
        swapCirclesIfTheyOverlappedAndCachedItsLocations(x, y);
        if (gameCondition.getValue() != GAME_IN_PROCESS) {
            return;
        }
        eliminateNeighborsWithSameColorAndUpdateScore();
        if (circleWithBombWasEliminated) {
            circleWithBombWasEliminated = false;
            endGameWithLoosing();
            return;
        }
        if (takenGameCell.isCircleInsideAlive()) {
            takenGameCell.moveCircleTo(x, y);
        } else {
            takenGameCell = null;
        }
        updateCellsLiveData();
        endGameIfWon();
    }

    public void actionUp() {
        if (takenGameCell == null) return;
        returnTakenCircleToDefaultPositionAndZeroingIt();
        updateCellsLiveData();
    }

    public void onCustomLevelParamsChosen(int fieldSize, int minesAmount) {
        gameRepository.setLevelWithParams(fieldSize, minesAmount);
        startGameWithoutDialog();
    }

    public LiveData<LiveEvent<Integer>> getToastEvent() {
        return toastEvent;
    }

    private void startGame() {
        if (gameRepository.shouldLoadGame()) {
            loadLastGame();
        } else {
            startGameForCurrentLevel();
        }
    }

    private void startGameForCurrentLevel() {
        if (gameRepository.isCurrentLevelRequiresDialog()) {
            showDialog();
        } else {
            startGameWithoutDialog();
        }
    }

    private void startGameWithoutDialog() {
        minesGenerated = false;
        setLevel(gameRepository.getLevelToPlay());
        setupSpecificToTutorialLevel();
        gameCells = getCirclesForLevel();
        gameScore.setValue(0);
        gameCondition.setValue(GAME_IN_PROCESS);
        minesCountToDisplayToTheUser = level.getMinesAmount();
        notMarkedCellsWithMines = level.getMinesAmount();
        updateMinesLiveData();
        updateCellsLiveData();
    }

    private void setupSpecificToTutorialLevel() {
        if (level instanceof TutorialLevel) {
            minesGenerated = true;
            if (!gameRepository.messageForThisTutorialLevelWasShown()) {
                snackbarMessage.setValue(((TutorialLevel) level).getMessageResToDisplay());
            }
        }
    }

    private void showDialog() {
        gameCondition.setValue(SHOW_CUSTOM_LEVEL_DIALOG);
    }

    private void setLevel(GameLevel level) {
        this.level = level;
    }

    private void recreateCellsWithNewSize(int newWidth, int newHeight) {
        if (gameCells == null) return;
        gameCells = cellsGenerator.regenerateCellsForNewSize(gameCells, newWidth, newHeight);
        updateCellsLiveData();
    }

    private void moveToMarkUnmarkState() {
        markState = !markState;
        updateCheckButton();
    }

    private void updateCheckButton() {
        checkButtonSrc.setValue(markState ? R.drawable.bomb_check_pressed : R.drawable.bomb_check);
    }

    private void endGameIfWon() {
        if (gameWon()) {
            endGameWithWinning();
        }
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

    private boolean positionOnMarkedCircle(int x, int y) {
        if (!takenGameCell.contains(x, y)) {
            Pair<Integer, Integer> gameCellPosition = findPositionForCellThatContainsPosition(x, y);
            if (gameCellPosition != null) {
                return gameCells[gameCellPosition.first][gameCellPosition.second].isMarked();
            }
        }
        return false;
    }

    private void swapCirclesIfTheyOverlappedAndCachedItsLocations(int x, int y) {
        if (!takenGameCell.contains(x, y)) {
            Pair<Integer, Integer> gameCellPosition = findPositionForCellThatContainsPosition(x, y);
            if (gameCellPosition != null) {
                GameCell cellToSwapBy = gameCells[gameCellPosition.first][gameCellPosition.second];
                if (cellToSwapBy.isWithMine()) {
                    endGameWithLoosing();
                    return;
                }
                swappedCirclePosition = takenGameCellPosition;
                takenGameCell.swapCirclesWith(cellToSwapBy);
                takenGameCell.moveCircleToDefaultPosition();
                takenGameCell = cellToSwapBy;
                takenGameCell.moveCircleToDefaultPosition();
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
            setUpAnimationIfCirclesWereEliminated();
            swappedCirclePosition = null;
        }
    }

    private void updateScoreBasedOnGoneCircles(int circlesGoneBecauseOfFirstCircle,
                                               int circlesGoneBecauseOfSecondCircle) {
        StringBuilder scoreToDisplay = new StringBuilder();
        int scoreToAdd = circlesGoneBecauseOfFirstCircle * 10 + circlesGoneBecauseOfSecondCircle * 10;
        if (circlesGoneBecauseOfFirstCircle != 0 && circlesGoneBecauseOfSecondCircle != 0) {
            scoreToDisplay.append("Combo!\n");
            scoreToAdd *= 2;
        }
        scoreToDisplay.append('+').append(scoreToAdd);
        scoreToDisplayInGameView.setValue(scoreToDisplay.toString());
        addToScoreLiveData(scoreToAdd);
    }

    private void setUpAnimationIfCirclesWereEliminated() {
        if (eliminatedCells.size() != 0) {
            mainHandler.postDelayed(stopAnimationRunnable, 1000);
        }
    }

    private void addToScoreLiveData(int scoreToAdd) {
        if (scoreToAdd != 0 && !minesGenerated) {
            cellsGenerator.generateMines(gameCells, notMarkedCellsWithMines);
            updateNotMarkedMinesCount();
            minesGenerated = true;
        }
        gameScore.setValue(gameScore.getValue() + scoreToAdd);
    }

    private void updateNotMarkedMinesCount() {
        if (notMarkedCellsWithMines != 0) {
            for (int row = 0; row < gameCells.length; row++) {
                for (int col = 0; col < gameCells[0].length; col++) {
                    if (gameCells[row][col].isWithMine() && gameCells[row][col].isMarked()) {
                        notMarkedCellsWithMines--;
                    }
                }
            }
        }
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
        if (gameRepository.thisScoreBeatsRecord(gameScore.getValue())) {
            gameRepository.updateScore(gameScore.getValue());
            toastEvent.setValue(new LiveEvent<>(R.string.new_record_set));
        }
        gameRepository.openNextLevelIfItsExist();
        endGameWithStatus(GAME_WON);
    }

    private void endGameWithLoosing() {
        endGameWithStatus(GAME_LOST);
    }

    private void endGameWithStatus(int status) {
        takenGameCell = null;
        eliminateAllCircles();
        updateCellsLiveData();
        gameCondition.setValue(status);
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

    private void eliminateAllCircles() {
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[0].length; j++) {
                GameCell currentCell = gameCells[i][j];
                if (currentCell.isCircleInsideAlive()) {
                    currentCell.eliminateCircle();
                }
            }
        }
    }

    private boolean cellToTheLeftIsSameColor(int row, int column) {
        if (column == 0) return false;
        return gameCells[row][column].isColorTheSameAndCellsNotMarked(gameCells[row][column - 1]);
    }

    private boolean cellToTheRightIsSameColor(int row, int column) {
        if (column + 1 == gameCells[0].length) return false;
        return gameCells[row][column].isColorTheSameAndCellsNotMarked(gameCells[row][column + 1]);
    }

    private boolean cellToTheBottomIsSameColor(int row, int column) {
        if (row + 1 == gameCells.length) return false;
        return gameCells[row][column].isColorTheSameAndCellsNotMarked(gameCells[row + 1][column]);
    }

    private boolean cellToTheTopIsSameColor(int row, int column) {
        if (row == 0) return false;
        return gameCells[row][column].isColorTheSameAndCellsNotMarked(gameCells[row - 1][column]);
    }

    private void eliminateCircleAtPosition(int row, int col) {
        GameCell cellToEliminate = gameCells[row][col];
        if (cellToEliminate.isWithMine()) {
            circleWithBombWasEliminated = true;
        }
        eliminatedCells.add(cellToEliminate);
        cellToEliminate.eliminateCircleWithAnimation();
    }

    private GameCell[][] getCirclesForLevel() {
        return level.generateCircles(cellsGenerator, gameWindowWidth, gameWindowHeight);
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

    private void loadLastGame() {
        final LiveData<GameToSaveObject> liveData = gameRepository.loadGame();
        Observer<GameToSaveObject> observer = new Observer<GameToSaveObject>() {
            @Override
            public void onChanged(GameToSaveObject gameToSaveObject) {
                liveData.removeObserver(this);
                gameScore.setValue(gameToSaveObject.getScore());
                minesCountToDisplayToTheUser = gameToSaveObject.getLeftMines();
                updateMinesLiveData();
                notMarkedCellsWithMines = gameToSaveObject.getUncoveredMinesAmount();
                minesGenerated = gameToSaveObject.isMinesGenerated();
                markState = gameToSaveObject.isMarkState();
                updateCheckButton();
                gameCells = gameToSaveObject.getGameCells();
                gameCondition.setValue(GAME_IN_PROCESS);
                if (gameWindowWidth != gameToSaveObject.getWidth() || gameWindowHeight != gameToSaveObject.getHeight()) {
                    recreateCellsWithNewSize(gameWindowWidth, gameWindowHeight);
                } else {
                    updateCellsLiveData();
                }
            }
        };
        liveData.observeForever(observer);
    }

    public void beforeGameGoAway() {
        if (!(level instanceof TutorialLevel)) {
            if (gameCondition.getValue() == GAME_IN_PROCESS) {
                gameRepository.saveGame(new GameToSaveObject(gameCells, gameWindowWidth, gameWindowHeight,
                        gameScore.getValue(), minesToDisplayLiveData.getValue(), markState,
                        minesGenerated, notMarkedCellsWithMines));
            } else {
                gameRepository.nothingToLoadNextTime();
            }
        }
    }

    public MutableLiveData<String> getScoreToDisplayInGameView() {
        return scoreToDisplayInGameView;
    }

    public MutableLiveData<Integer> getSnackbarMessage() {
        return snackbarMessage;
    }

    public void onSnackbarMessageClicked() {
        snackbarMessage.setValue(null);
        gameRepository.saveThatMessageForTutorialLevelWasShown();
    }

    private void stopAnimationImmediatelyIfItsPending() {
        if (mainHandler.hasMessages(0)) {
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler.post(stopAnimationRunnable);
        }
    }
}
