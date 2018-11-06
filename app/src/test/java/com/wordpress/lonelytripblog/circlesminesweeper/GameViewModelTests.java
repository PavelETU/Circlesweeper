package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel3X4;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel4X6;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.CustomLevel6X10;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FifthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FourthLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.SecondLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.ThirdLevel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
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
    private final int defaultWidth = 100;
    private final int defaultHeight = 150;
    private GameCell mockCell = mock(GameCell.class);
    private GameCell mockCell2 = mock(GameCell.class);
    private GameCell[][] mockCells;
    private GameCell[][] gameCells = new GameCell[1][2];

    @Before
    public void setUp() {
        viewModel = new GameViewModel(cellsGenerator);
        circleObserver = (Observer<GameCell[][]>) mock(Observer.class);
        viewModel.getGameCells().observeForever(circleObserver);
        Observer<Integer> scoreObserver = (Observer<Integer>) mock(Observer.class);
        viewModel.getScore().observeForever(scoreObserver);
        Observer<Integer> gameConditionObserver = (Observer<Integer>) mock(Observer.class);
        viewModel.getGameCondition().observeForever(gameConditionObserver);
    }

    @Test
    public void circleLiveDataInitiallyEmpty() {
        Assert.assertNull(viewModel.getGameCells().getValue());
    }

    @Test(expected = RuntimeException.class)
    public void ifStartGameCalledWithoutWidthAndHeightExceptionIsThrown() {
        viewModel.startGame();
    }

    @Test
    public void firstLevelSetup() {
        createLevelWithDefaultWindow(new FirstLevel());

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField3X4(defaultWidth, defaultHeight, 0);
    }

    @Test
    public void secondLevelSetup() {
        createLevelWithDefaultWindow(new SecondLevel());

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField3X4(defaultWidth, defaultHeight, 1);
    }

    @Test
    public void thirdLevelSetup() {
        createLevelWithDefaultWindow(new ThirdLevel());

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField4X6(defaultWidth, defaultHeight, 3);
    }

    @Test
    public void fourthLevelSetup() {
        createLevelWithDefaultWindow(new FourthLevel());

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField6X10(defaultWidth, defaultHeight, 5);
    }

    @Test
    public void verifyFifthLevelSetup() {
        createLevelWithDefaultWindow(new FifthLevel());

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField6X10(defaultWidth, defaultHeight, 7);
    }

    @Test
    public void customLevel3X4Setup() {
        createLevelWithDefaultWindow(new CustomLevel3X4(4));

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField3X4(defaultWidth, defaultHeight, 4);
    }

    @Test
    public void customLevel4X6Setup() {
        createLevelWithDefaultWindow(new CustomLevel4X6(5));

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField4X6(defaultWidth, defaultHeight, 5);
    }

    @Test
    public void customLevel6X10Setup() {
        createLevelWithDefaultWindow(new CustomLevel6X10(6));

        viewModel.startGame();

        verify(cellsGenerator).generateCellsForField6X10(defaultWidth, defaultHeight, 6);
    }

    @Test
    public void initialValueOfLiveDataIsCorrect() {
        startGameWithGameCells();

        assertEquals(gameCells, viewModel.getGameCells().getValue());
    }

    @Test
    public void singleCircleMovesOnActionDown() {
        startGameWithMockCell1x1();
        teachMockCellSoItWillInclude(mockCell, 60, 90);

        viewModel.actionDown(60, 90);

        verify(mockCell).makeCircleSmaller();
        verify(mockCell).moveCircleTo(60, 90);
        verify(circleObserver, times(2)).onChanged(any());
    }

    @Test
    public void clickOutOfCirclesBounds() {
        startGameWithMockCell1x1();
        teachMockCellSoItWillExclude(mockCell, 60, 90);

        viewModel.actionDown(60, 90);

        verify(mockCell, times(0)).moveCircleTo(anyInt(), anyInt());
        verify(mockCell, times(0)).makeCircleBigger();
        verify(circleObserver, times(1)).onChanged(any());
    }

    @Test
    public void singleCircleMovesToDefaultOnActionUp() {
        startGameWithMockCell1x1();
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
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);

        viewModel.actionDown(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        viewModel.actionMove(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);

        verify(mockCells[0][1]).eliminateCircle();
        verify(mockCells[1][1]).eliminateCircle();
    }

    @Test
    public void scoreCorrectForTwoCircles() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);

        viewModel.actionDown(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        viewModel.actionMove(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);

        assertEquals(20, (int) viewModel.getScore().getValue());
    }

    @Test
    public void scoreDoublesIfBothSwappedCirclesGone() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);
        when(mockCells[0][0].isColorTheSame(mockCells[1][0])).thenReturn(true);

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
        startGameWithMockCells1x2WithNoBombs();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isColorTheSame(mockCell2)).thenReturn(false);
        when(mockCell.isWithMine()).thenReturn(false);
        when(mockCell2.isWithMine()).thenReturn(false);

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
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);
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

        verify(mockCell, Mockito.never()).setMarked(true);
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
        when(mockCell.isColorTheSame(mockCell2)).thenReturn(false);
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
        when(mockCell.isColorTheSame(mockCell2)).thenReturn(false);
        when(mockCell.isWithMine()).thenReturn(true);
        when(mockCell2.isWithMine()).thenReturn(false);

        viewModel.markClicked();
        viewModel.actionDown(100, 100);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void movingToOutsideOfWindowReturnsToDefaultPosition() {
        startGameWithMockCell1x1();
        teachMockCellSoItWillInclude(mockCell, 100, 100);

        viewModel.actionDown(100, 100);
        viewModel.actionMove(1, 1);

        verify(mockCell).moveCircleToDefaultPosition();
        verify(mockCell).makeCircleBigger();
    }

    private void startGameWithMockCells2x2WithDefaultCoords() {
        startGameWithMockCells2x2();
        teachMockCellSoItWillInclude(mockCells[0][0], DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        teachMockCellSoItWillInclude(mockCells[0][1], DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        teachMockCellSoItWillInclude(mockCells[1][0], DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_SECOND_ROW);
        teachMockCellSoItWillInclude(mockCells[1][1], DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_SECOND_ROW);
    }

    private void createLevelWithDefaultWindow(final GameLevel level) {
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(defaultWidth, defaultHeight);
    }

    private void startGameWithMockCell1x1() {
        GameCell[][] gameCells = new GameCell[1][1];
        gameCells[0][0] = mockCell;

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        GameLevel level = new FirstLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(200, 200);
        viewModel.startGame();
    }

    private void startGameWithMockCells1x2WithNoBombs() {
        startGameWithMockCells1X2WithLevel(new FirstLevel());
    }

    private void startGameWithMockCells1x2WithOneBomb() {
        startGameWithMockCells1X2WithLevel(new SecondLevel());
    }

    private void startGameWithMockCells1X2WithLevel(final GameLevel level) {
        GameCell[][] gameCells = new GameCell[1][2];
        gameCells[0][0] = mockCell;
        gameCells[0][1] = mockCell2;

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(200, 200);
        viewModel.startGame();
    }

    private void startGameWithMockCells2x2() {
        mockCells = new GameCell[2][2];
        for (int i = 0; i < mockCells.length; i++) {
            for (int j = 0; j < mockCells[0].length; j++) {
                mockCells[i][j] = mock(GameCell.class);
            }
        }

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(mockCells);

        GameLevel level = new FirstLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(200, 200);
        viewModel.startGame();
    }

    private void startGameWithGameCells() {
        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        GameLevel level = new FirstLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(200, 200);
        viewModel.startGame();
    }

    private void teachMockCellSoItWillExclude(GameCell mockCellToTeach, final int x, final int y) {
        when(mockCellToTeach.contains(x, y)).thenReturn(false);
    }

    private void teachMockCellSoItWillInclude(GameCell mockCellToTeach, final int x, final int y) {
        when(mockCellToTeach.contains(x, y)).thenReturn(true);
    }

}
