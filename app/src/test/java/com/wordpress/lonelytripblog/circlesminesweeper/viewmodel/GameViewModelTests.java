package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel3X4;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel4X6;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel6X10;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FifthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FourthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.SecondLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.ThirdLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsToBitmap;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameViewModelTests {

    private final int DEFAULT_X_FOR_FIRST_COLUMN = 100;
    private final int DEFAULT_Y_FOR_FIRST_ROW = 100;
    private final int DEFAULT_X_FOR_SECOND_COLUMN = 150;
    private final int DEFAULT_Y_FOR_SECOND_ROW = 150;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private GameViewModel viewModel;
    private CellsGenerator cellsGenerator = mock(CellsGenerator.class);
    private Observer<GameCell[][]> circleObserver;
    private final int defaultWidth = 250;
    private final int defaultHeight = 200;
    private GameCell mockCell = mock(GameCell.class);
    private GameCell mockCell2 = mock(GameCell.class);
    private GameCell[][] mockCells;
    private GameCell[][] gameCells = new GameCell[1][2];
    private GameRepository mockRepo = mock(GameRepository.class);

    @Test
    public void circleLiveDataInitiallyEmpty() {
        startGameWithLevel(new FirstLevel());

        Assert.assertNull(viewModel.getGameCells(defaultWidth, defaultHeight).getValue());
    }

    @Test(expected = RuntimeException.class)
    public void ifStartGameCalledWithoutWidthAndHeightExceptionIsThrown() {
        viewModel.getGameCells(defaultWidth, defaultHeight);
    }

    @Test
    public void firstLevelSetup() {
        startGameWithLevel(new FirstLevel());

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField3X4(defaultWidth, defaultHeight, 0);
    }

    @Test
    public void secondLevelSetup() {
        startGameWithLevel(new SecondLevel());

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField3X4(defaultWidth, defaultHeight, 1);
    }

    @Test
    public void thirdLevelSetup() {
        startGameWithLevel(new ThirdLevel());

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField4X6(defaultWidth, defaultHeight, 3);
    }

    @Test
    public void fourthLevelSetup() {
        startGameWithLevel(new FourthLevel());

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField6X10(defaultWidth, defaultHeight, 5);
    }

    @Test
    public void verifyFifthLevelSetup() {
        startGameWithLevel(new FifthLevel());

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField6X10(defaultWidth, defaultHeight, 7);
    }

    @Test
    public void customLevel3X4Setup() {
        startGameWithLevel(new CustomLevel3X4(4));

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField3X4(defaultWidth, defaultHeight, 4);
    }

    @Test
    public void customLevel4X6Setup() {
        startGameWithLevel(new CustomLevel4X6(5));

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField4X6(defaultWidth, defaultHeight, 5);
    }

    @Test
    public void customLevel6X10Setup() {
        startGameWithLevel(new CustomLevel6X10(6));

        viewModel.getGameCells(defaultWidth, defaultHeight);

        verify(cellsGenerator).generateCellsForField6X10(defaultWidth, defaultHeight, 6);
    }

    @Test
    public void initialValueOfLiveDataIsCorrect() {
        startGameWithGameCells();

        assertArrayEquals(gameCells, viewModel.getGameCells(defaultWidth, defaultHeight).getValue());
    }

    @Test
    public void singleCircleMovesOnActionDown() {
        startGameWithAliveMockCell1x1();
        teachMockCellSoItWillInclude(mockCell, 60, 90);

        viewModel.actionDown(60, 90);

        verify(mockCell).moveCircleTo(60, 90);
        verify(circleObserver, times(2)).onChanged(any());
    }

    @Test
    public void clickOutOfCirclesBounds() {
        startGameWithAliveMockCell1x1();
        teachMockCellSoItWillExclude(mockCell, 60, 90);

        viewModel.actionDown(60, 90);

        verify(mockCell, times(0)).moveCircleTo(anyInt(), anyInt());
        verify(mockCell, times(0)).makeCircleBigger();
        verify(circleObserver, times(1)).onChanged(any());
    }

    @Test
    public void singleCircleMovesToDefaultOnActionUp() {
        startGameWithAliveMockCell1x1();
        teachMockCellSoItWillInclude(mockCell, 60, 120);

        viewModel.actionDown(60, 120);
        viewModel.actionUp();

        mockCell.makeCircleBigger();
        mockCell.makeCircleSmaller();
        mockCell.moveCircleTo(60, 120);
        mockCell.moveCircleToDefaultPosition();
        verify(circleObserver, times(3)).onChanged(any());
    }

    @Test
    public void movingCircleWorks() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 60, 120);

        viewModel.actionDown(60, 120);
        viewModel.actionMove(65, 130);

        verify(mockCell).moveCircleTo(60, 120);
        verify(mockCell).moveCircleTo(65, 130);
        verify(circleObserver, times(3)).onChanged(any());
    }

    @Test
    public void verifySwappingCircles() {
        startGameWithMockCells1x2WithNoBombs();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        teachMockCellSoItWillInclude(mockCell2, 150, 100);

        viewModel.actionDown(100, 100);
        viewModel.actionMove(150, 100);

        verify(mockCell).swapCirclesWith(mockCell2);
    }

    @Test
    public void circlesWithTheSameColorWillBeEliminated() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSameAndCellsNotMarked(mockCells[1][1])).thenReturn(true);

        viewModel.actionDown(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        viewModel.actionMove(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);

        verify(mockCells[0][1]).eliminateCircleWithAnimation();
        verify(mockCells[1][1]).eliminateCircleWithAnimation();
    }

    @Test
    public void scoreCorrectForTwoCircles() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSameAndCellsNotMarked(mockCells[1][1])).thenReturn(true);

        viewModel.actionDown(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        viewModel.actionMove(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);

        assertEquals(20, (int) viewModel.getScore().getValue());
    }

    @Test
    public void scoreDoublesIfBothSwappedCirclesGone() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSameAndCellsNotMarked(mockCells[1][1])).thenReturn(true);
        when(mockCells[0][0].isColorTheSameAndCellsNotMarked(mockCells[1][0])).thenReturn(true);

        viewModel.actionDown(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        viewModel.actionMove(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);

        assertEquals(80, (int) viewModel.getScore().getValue());
    }

    @Test
    public void defaultGameConditionIsGameInProcess() {
        startGameWithGameCells();

        assertEquals(GameViewModel.GAME_IN_PROCESS, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void gameWonAfterMoveIfNoMoreSameCircleAndMines() {
        start1X2GameWithNoBombsAndDifferentColors();

        viewModel.actionDown(100, 100);
        viewModel.actionMove(101, 100);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void gameOverIfBombTouched() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isWithMine()).thenReturn(true);

        viewModel.actionDown(100, 100);

        assertEquals(GameViewModel.GAME_LOST, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void gameOverIfCircleWithBombEliminated() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSameAndCellsNotMarked(mockCells[1][1])).thenReturn(true);
        when(mockCells[1][1].isWithMine()).thenReturn(true);

        viewModel.actionDown(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        viewModel.actionMove(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);

        assertEquals(GameViewModel.GAME_LOST, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void verifyDefaultMines() {
        startGameWithMockCells1x2WithOneBomb();

        assertEquals(1, (int) viewModel.getMinesToDisplay().getValue());
    }

    @Test
    public void verifyLeftMinesGetsSmallerOnChoose() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);

        viewModel.markClicked();
        viewModel.actionDown(100, 100);

        verify(mockCell).setMarked(true);
    }

    @Test
    public void afterTogglingMarkedNotCalled() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);

        viewModel.markClicked();
        viewModel.markClicked();
        viewModel.actionDown(100, 100);

        verify(mockCell, never()).setMarked(true);
    }

    @Test
    public void toggleWorks() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(false).thenReturn(true);

        viewModel.markClicked();
        viewModel.actionDown(100, 100);
        viewModel.markClicked();
        viewModel.actionDown(100, 100);

        verify(mockCell).setMarked(true);
        verify(mockCell).setMarked(false);
    }

    @Test
    public void defaultMinesAmountDisplayed() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);

        assertEquals(1, (int) viewModel.getMinesToDisplay().getValue());
    }

    @Test
    public void bombAmountDecreaseAfterMarking() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(false).thenReturn(true);

        viewModel.markClicked();
        viewModel.actionDown(100, 100);

        assertEquals(0, (int) viewModel.getMinesToDisplay().getValue());
    }

    @Test
    public void bombAmountIncreaseAfterUnmarking() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(true).thenReturn(false);

        viewModel.markClicked();
        viewModel.actionDown(100, 100);

        assertEquals(2, (int) viewModel.getMinesToDisplay().getValue());
    }

    @Test
    public void markedCircleDoesNotMove() {
        startGameWithMockCells1x2WithNoBombs();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(true);

        viewModel.actionDown(100, 100);
        viewModel.actionMove(101, 100);

        verify(mockCell, never()).makeCircleSmaller();
        verify(mockCell, never()).moveCircleTo(anyInt(), anyInt());
    }

    @Test
    public void gameContinuesWithWrongMarkedMine() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(true);
        when(mockCell.isColorTheSameAndCellsNotMarked(mockCell2)).thenReturn(false);
        when(mockCell.isWithMine()).thenReturn(false);
        when(mockCell2.isWithMine()).thenReturn(true);

        viewModel.actionDown(100, 100);
        viewModel.actionMove(101, 100);

        assertEquals(GameViewModel.GAME_IN_PROCESS, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void gameWonWhenRightCellGetsMarked() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(false).thenReturn(true);
        when(mockCell.isColorTheSameAndCellsNotMarked(mockCell2)).thenReturn(false);
        when(mockCell.isWithMine()).thenReturn(true);
        when(mockCell2.isWithMine()).thenReturn(false);

        viewModel.markClicked();
        viewModel.actionDown(100, 100);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void movingToOutsideOfWindowReturnsToDefaultPosition() {
        startGameWithAliveMockCell1x1();
        teachMockCellSoItWillInclude(mockCell, 100, 100);

        viewModel.actionDown(100, 100);
        viewModel.actionMove(1, 1);

        verify(mockCell).moveCircleToDefaultPosition();
        verify(mockCell).makeCircleBigger();
    }

    @Test
    public void leftCirclesEliminatedAfterGameIsLost() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isWithMine()).thenReturn(true);
        when(mockCell.isCircleInsideAlive()).thenReturn(true);
        when(mockCell2.isCircleInsideAlive()).thenReturn(true);

        viewModel.actionDown(100, 100);

        verify(mockCell).eliminateCircle();
        verify(mockCell2).eliminateCircle();
    }

    @Test
    public void leftCirclesEliminatedAfterGameIsWon() {
        start1X2GameWithNoBombsAndDifferentColors();

        viewModel.actionDown(100, 100);
        viewModel.actionMove(101, 100);

        verify(mockCell).eliminateCircle();
        verify(mockCell2).eliminateCircle();
    }

    @Test
    public void swappingCircleByCircleWithMineEndsGame() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell2.isWithMine()).thenReturn(true);
        when(mockCell2.contains(150, 100)).thenReturn(true);

        viewModel.actionDown(100, 100);
        viewModel.actionMove(150, 100);

        assertEquals(GameViewModel.GAME_LOST, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void startLevelThatRequiresDialog() {
        when(mockRepo.isCurrentLevelRequiresDialog()).thenReturn(true);

        startGameWithAliveMockCell1x1();

        assertEquals(GameViewModel.SHOW_CUSTOM_LEVEL_DIALOG, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void showMessageIfNewRecordSet() {
        start1X2GameWithNoBombsAndDifferentColors();
        when(mockRepo.thisScoreBeatsRecord(0)).thenReturn(true);

        viewModel.actionDown(100, 100);
        viewModel.actionMove(101, 100);

        verify(mockRepo).updateScore(0);
        assertEquals(R.string.new_record_set, (int) viewModel.getToastEvent().getValue().getValueOrNull());
    }

    private void start1X2GameWithNoBombsAndDifferentColors() {
        startGameWithMockCells1x2WithNoBombs();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isColorTheSameAndCellsNotMarked(mockCell2)).thenReturn(false);
        when(mockCell.getDrawableForCircle()).thenReturn(1);
        when(mockCell2.getDrawableForCircle()).thenReturn(0);
        when(mockCell.isWithMine()).thenReturn(false);
        when(mockCell2.isWithMine()).thenReturn(false);
    }

    private void startGameWithLevel(GameLevel level) {
        Handler mockHandler = mock(Handler.class);
        GameCellsToBitmap gameCellsToBitmap = mock(GameCellsToBitmap.class);
        when(mockRepo.getLevelToPlay()).thenReturn(level);
        viewModel = new GameViewModel(cellsGenerator, mockHandler, gameCellsToBitmap, mockRepo);
        viewModel.getCheckButtonSrc();
        circleObserver = (Observer<GameCell[][]>) mock(Observer.class);
        viewModel.getGameCells(defaultWidth, defaultHeight).observeForever(circleObserver);
        Observer<Integer> scoreObserver = (Observer<Integer>) mock(Observer.class);
        viewModel.getScore().observeForever(scoreObserver);
        Observer<Integer> gameConditionObserver = (Observer<Integer>) mock(Observer.class);
        viewModel.getGameCondition().observeForever(gameConditionObserver);
    }

    private void startGameWithMockCells2x2WithDefaultCoords() {
        startGameWithAliveMockCells2x2();
        teachMockCellSoItWillInclude(mockCells[0][0], DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        teachMockCellSoItWillInclude(mockCells[0][1], DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        teachMockCellSoItWillInclude(mockCells[1][0], DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_SECOND_ROW);
        teachMockCellSoItWillInclude(mockCells[1][1], DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_SECOND_ROW);
    }

    private void startGameWithAliveMockCell1x1() {
        GameCell[][] gameCells = new GameCell[1][1];
        when(mockCell.isCircleInsideAlive()).thenReturn(true);
        gameCells[0][0] = mockCell;

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        startGameWithLevel(new FirstLevel());
    }

    private void startGameWithMockCells1x2WithNoBombs() {
        startGameWithAliveMockCells1X2WithLevel(new FirstLevel());
    }

    private void startGameWithMockCells1x2WithOneBomb() {
        startGameWithAliveMockCells1X2WithLevel(new SecondLevel());
    }

    private void startGameWithAliveMockCells1X2WithLevel(GameLevel level) {
        GameCell[][] gameCells = new GameCell[1][2];
        when(mockCell.isCircleInsideAlive()).thenReturn(true);
        when(mockCell2.isCircleInsideAlive()).thenReturn(true);
        gameCells[0][0] = mockCell;
        gameCells[0][1] = mockCell2;

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        startGameWithLevel(level);
    }

    private void startGameWithAliveMockCells2x2() {
        mockCells = new GameCell[2][2];
        for (int i = 0; i < mockCells.length; i++) {
            for (int j = 0; j < mockCells[0].length; j++) {
                mockCells[i][j] = mock(GameCell.class);
                when(mockCells[i][j].isCircleInsideAlive()).thenReturn(true);
            }
        }

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(mockCells);

        startGameWithLevel(new FirstLevel());
    }

    private void startGameWithGameCells() {
        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        startGameWithLevel(new FirstLevel());
    }

    private void teachMockCellSoItWillExclude(GameCell mockCellToTeach, int x, int y) {
        when(mockCellToTeach.contains(x, y)).thenReturn(false);
    }

    private void teachMockCellSoItWillInclude(GameCell mockCellToTeach, int x, int y) {
        when(mockCellToTeach.contains(x, y)).thenReturn(true);
    }

}
