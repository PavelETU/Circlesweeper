package com.wordpress.lonelytripblog.circlesminesweeper;

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
import com.wordpress.lonelytripblog.circlesminesweeper.di.CircleSweeperApp;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.Point;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModel;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
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
    private CircleSweeperApp app = mock(CircleSweeperApp.class);
    private Observer<GameCell[][]> circleObserver;
    private final int defaultWidth = 250;
    private final int defaultHeight = 200;
    private GameCell mockCell = mock(GameCell.class);
    private GameCell mockCell2 = mock(GameCell.class);
    private GameCell[][] mockCells;
    private GameCell[][] gameCells = new GameCell[1][2];

    @Test
    public void circleLiveDataInitiallyEmpty() {
        startGameWithLevel(new FirstLevel());

        assertNull(viewModel.getGameCells().getValue());
    }

    @Test(expected = RuntimeException.class)
    public void ifStartGameCalledWithoutWidthAndHeightExceptionIsThrown() {
        viewModel.getGameCells();
    }

    @Test
    public void firstLevelSetup() {
        startGameWithLevel(new FirstLevel());

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField3X4(defaultHeight, defaultWidth, 0);
    }

    @Test
    public void secondLevelSetup() {
        startGameWithLevel(new SecondLevel());

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField3X4(defaultHeight, defaultWidth, 1);
    }

    @Test
    public void thirdLevelSetup() {
        startGameWithLevel(new ThirdLevel());

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField4X6(defaultHeight, defaultWidth, 3);
    }

    @Test
    public void fourthLevelSetup() {
        startGameWithLevel(new FourthLevel());

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField6X10(defaultHeight, defaultWidth, 5);
    }

    @Test
    public void verifyFifthLevelSetup() {
        startGameWithLevel(new FifthLevel());

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField6X10(defaultHeight, defaultWidth, 7);
    }

    @Test
    public void customLevel3X4Setup() {
        startGameWithLevel(new CustomLevel3X4(4));

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField3X4(defaultHeight, defaultWidth, 4);
    }

    @Test
    public void customLevel4X6Setup() {
        startGameWithLevel(new CustomLevel4X6(5));

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField4X6(defaultHeight, defaultWidth, 5);
    }

    @Test
    public void customLevel6X10Setup() {
        startGameWithLevel(new CustomLevel6X10(6));

        viewModel.getGameCells();

        verify(cellsGenerator).generateCellsForField6X10(defaultHeight, defaultWidth, 6);
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

        viewModel.actionDown(new Point(60, 90));

        verify(mockCell).makeCircleSmaller();
        verify(mockCell).moveCircleTo(60, 90);
        verify(circleObserver, times(2)).onChanged(any());
    }

    @Test
    public void clickOutOfCirclesBounds() {
        startGameWithMockCell1x1();
        teachMockCellSoItWillExclude(mockCell, 60, 90);

        viewModel.actionDown(new Point(60, 90));

        verify(mockCell, times(0)).moveCircleTo(anyInt(), anyInt());
        verify(mockCell, times(0)).makeCircleBigger();
        verify(circleObserver, times(1)).onChanged(any());
    }

    @Test
    public void singleCircleMovesToDefaultOnActionUp() {
        startGameWithMockCell1x1();
        teachMockCellSoItWillInclude(mockCell, 60, 120);

        viewModel.actionDown(new Point(60, 120));
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

        viewModel.actionDown(new Point(60, 120));
        viewModel.actionMove(new Point(65, 130));

        verify(mockCell).moveCircleTo(60, 120);
        verify(mockCell).moveCircleTo(65, 130);
        verify(circleObserver, times(3)).onChanged(any());
    }

    @Test
    public void verifySwappingCircles() {
        startGameWithMockCells1x2WithNoBombs();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        teachMockCellSoItWillInclude(mockCell2, 150, 100);

        viewModel.actionDown(new Point(100, 100));
        viewModel.actionMove(new Point(150, 100));

        verify(mockCell).swapCirclesWith(mockCell2);
    }

    @Test
    public void circlesWithTheSameColorWillBeEliminated() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);

        viewModel.actionDown(new Point(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));
        viewModel.actionMove(new Point(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));

        verify(mockCells[0][1]).eliminateCircle();
        verify(mockCells[1][1]).eliminateCircle();
    }

    @Test
    public void scoreCorrectForTwoCircles() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);

        viewModel.actionDown(new Point(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));
        viewModel.actionMove(new Point(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));

        assertEquals(20, (int) viewModel.getScore().getValue());
    }

    @Test
    public void scoreDoublesIfBothSwappedCirclesGone() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);
        when(mockCells[0][0].isColorTheSame(mockCells[1][0])).thenReturn(true);

        viewModel.actionDown(new Point(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));
        viewModel.actionMove(new Point(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));

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

        viewModel.actionDown(new Point(100, 100));
        viewModel.actionMove(new Point(101, 100));

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void gameOverIfBombTouched() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isWithMine()).thenReturn(true);

        viewModel.actionDown(new Point(100, 100));

        assertEquals(GameViewModel.GAME_LOST, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void gameOverIfCircleWithBombEliminated() {
        startGameWithMockCells2x2WithDefaultCoords();
        when(mockCells[0][1].isColorTheSame(mockCells[1][1])).thenReturn(true);
        when(mockCells[1][1].isWithMine()).thenReturn(true);

        viewModel.actionDown(new Point(DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));
        viewModel.actionMove(new Point(DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW));

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
        viewModel.actionDown(new Point(100, 100));

        verify(mockCell).setMarked(true);
    }

    @Test
    public void afterTogglingMarkedNotCalled() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);

        viewModel.markClicked();
        viewModel.markClicked();
        viewModel.actionDown(new Point(100, 100));

        verify(mockCell, Mockito.never()).setMarked(true);
    }

    @Test
    public void toggleWorks() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(false).thenReturn(true);

        viewModel.markClicked();
        viewModel.actionDown(new Point(100, 100));
        viewModel.markClicked();
        viewModel.actionDown(new Point(100, 100));

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
        viewModel.actionDown(new Point(100, 100));

        assertEquals(0, (int) viewModel.getMinesToDisplay().getValue());
    }

    @Test
    public void bombAmountIncreaseAfterUnmarking() {
        startGameWithMockCells1x2WithOneBomb();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(true).thenReturn(false);

        viewModel.markClicked();
        viewModel.actionDown(new Point(100, 100));

        assertEquals(2, (int) viewModel.getMinesToDisplay().getValue());
    }

    @Test
    public void markedCircleDoesNotMove() {
        startGameWithMockCells1x2WithNoBombs();
        teachMockCellSoItWillInclude(mockCell, 100, 100);
        when(mockCell.isMarked()).thenReturn(true);

        viewModel.actionDown(new Point(100, 100));
        viewModel.actionMove(new Point(101, 100));

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

        viewModel.actionDown(new Point(100, 100));
        viewModel.actionMove(new Point(101, 100));

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
        viewModel.actionDown(new Point(100, 100));

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void movingToOutsideOfWindowReturnsToDefaultPosition() {
        startGameWithMockCell1x1();
        teachMockCellSoItWillInclude(mockCell, 100, 100);

        viewModel.actionDown(new Point(100, 100));
        viewModel.actionMove(new Point(1, 1));

        verify(mockCell).moveCircleToDefaultPosition();
        verify(mockCell).makeCircleBigger();
    }

    private void startGameWithLevel(GameLevel level) {
        viewModel = new GameViewModel(app, cellsGenerator);
        viewModel.setSizeOfGameWindow(defaultWidth, defaultHeight);
        viewModel.setLevel(level);
        circleObserver = (Observer<GameCell[][]>) mock(Observer.class);
        viewModel.getGameCells().observeForever(circleObserver);
        Observer<Integer> scoreObserver = (Observer<Integer>) mock(Observer.class);
        viewModel.getScore().observeForever(scoreObserver);
        Observer<Integer> gameConditionObserver = (Observer<Integer>) mock(Observer.class);
        viewModel.getGameCondition().observeForever(gameConditionObserver);
    }

    private void startGameWithMockCells2x2WithDefaultCoords() {
        startGameWithMockCells2x2();
        teachMockCellSoItWillInclude(mockCells[0][0], DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        teachMockCellSoItWillInclude(mockCells[0][1], DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_FIRST_ROW);
        teachMockCellSoItWillInclude(mockCells[1][0], DEFAULT_X_FOR_FIRST_COLUMN, DEFAULT_Y_FOR_SECOND_ROW);
        teachMockCellSoItWillInclude(mockCells[1][1], DEFAULT_X_FOR_SECOND_COLUMN, DEFAULT_Y_FOR_SECOND_ROW);
    }

    private void startGameWithMockCell1x1() {
        GameCell[][] gameCells = new GameCell[1][1];
        gameCells[0][0] = mockCell;

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        startGameWithLevel(new FirstLevel());
    }

    private void startGameWithMockCells1x2WithNoBombs() {
        startGameWithMockCells1X2WithLevel(new FirstLevel());
    }

    private void startGameWithMockCells1x2WithOneBomb() {
        startGameWithMockCells1X2WithLevel(new SecondLevel());
    }

    private void startGameWithMockCells1X2WithLevel(GameLevel level) {
        GameCell[][] gameCells = new GameCell[1][2];
        gameCells[0][0] = mockCell;
        gameCells[0][1] = mockCell2;

        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        startGameWithLevel(level);
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
