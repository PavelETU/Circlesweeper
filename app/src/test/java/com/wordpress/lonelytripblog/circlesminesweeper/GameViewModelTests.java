package com.wordpress.lonelytripblog.circlesminesweeper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
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

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameViewModelTests {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private GameViewModel viewModel;
    private CellsGenerator cellsGenerator = mock(CellsGenerator.class);
    private Observer<GameCell[][]> circleObserver;
    private final int defaultWidth = 100;
    private final int defaultHeight = 150;
    private GameCell mockCell = mock(GameCell.class);
    // blue_ball green_ball
    // green_ball blue_ball
    GameCell[][] gameCells = new GameCell[2][2];
    {
        gameCells[0][0] = new GameCell(new Circle(50, 50, 50, R.color.blue_ball_center), false);
        gameCells[0][1] = new GameCell(new Circle(150, 50, 50, R.color.green_ball_center), false);
        gameCells[1][0] = new GameCell(new Circle(50, 150, 50, R.color.green_ball_center), false);
        gameCells[1][1] = new GameCell(new Circle(150, 150, 50, R.color.blue_ball_center), false);
    }

    @Before
    public void setUp() {
        viewModel = new GameViewModel(cellsGenerator);
        circleObserver = (Observer<GameCell[][]>) mock(Observer.class);
        viewModel.getGameCells().observeForever(circleObserver);
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

        viewModel.startGame();

        assertEquals(gameCells, viewModel.getGameCells().getValue());
    }

    @Test
    public void singleCircleMovesOnActionDown() {
        startGameWithMockCell();
        teachMockCellSoItWillInclude(60, 90);

        viewModel.actionDown(60, 90);

        verify(mockCell).makeCircleSmaller();
        verify(mockCell).moveCircleTo(60, 90);
        verify(circleObserver, times(2)).onChanged(any());
    }

    @Test
    public void clickOutOfCirclesBounds() {
        startGameWithMockCell();
        teachMockCellSoItWillExclude(60, 90);

        viewModel.actionDown(60, 90);

        verify(mockCell, times(0)).moveCircleTo(anyInt(), anyInt());
        verify(mockCell, times(0)).makeCircleBigger();
        verify(circleObserver, times(1)).onChanged(any());
    }

    @Test
    public void clickOnCirclesBounds() {
        startGameWithMockCell();
        teachMockCellSoItWillBeOnEdgeOfXAndY(15, 25);

        viewModel.actionDown(15, 25);

        verify(mockCell, times(0)).moveCircleTo(anyInt(), anyInt());
        verify(mockCell, times(0)).makeCircleBigger();
        verify(circleObserver, times(1)).onChanged(any());
    }

    @Test
    public void singleCircleMovesToDefaultOnActionUp() {
        startGameWithMockCell();
        teachMockCellSoItWillInclude(60, 120);

        viewModel.actionDown(60, 120);
        viewModel.actionUp();

        mockCell.makeCircleBigger();
        mockCell.makeCircleSmaller();
        mockCell.moveCircleTo(60, 120);
        mockCell.moveCircleToDefaultPosition();
        verify(circleObserver, times(3)).onChanged(any());
    }

    private void createLevelWithDefaultWindow(final GameLevel level) {
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(defaultWidth, defaultHeight);
    }

    private void startGameWithMockCell() {
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

    private void startGameWithGameCells() {
        // This method is generic and not restricting the size of returned gameCells,
        // so don't be fool by 3X4 at the end, it's just for readability of CellsGenerator
        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt(), anyInt()))
                .thenReturn(gameCells);

        GameLevel level = new FirstLevel();
        viewModel.setLevel(level);
        viewModel.setSizeOfGameWindow(200, 200);
    }

    private void teachMockCellSoItWillExclude(final int x, final int y) {
        when(mockCell.getLeft()).thenReturn(x + 10);
        when(mockCell.getRight()).thenReturn(x + 20);
        when(mockCell.getTop()).thenReturn(y + 10);
        when(mockCell.getBottom()).thenReturn(y + 20);
    }

    private void teachMockCellSoItWillInclude(final int x, final int y) {
        when(mockCell.getLeft()).thenReturn(x - 10);
        when(mockCell.getRight()).thenReturn(x + 10);
        when(mockCell.getTop()).thenReturn(y - 10);
        when(mockCell.getBottom()).thenReturn(y + 10);
    }

    private void teachMockCellSoItWillBeOnEdgeOfXAndY(final int x, final int y) {
        when(mockCell.getLeft()).thenReturn(x);
        when(mockCell.getRight()).thenReturn(x + 5);
        when(mockCell.getTop()).thenReturn(y);
        when(mockCell.getBottom()).thenReturn(y + 5);
    }

}
