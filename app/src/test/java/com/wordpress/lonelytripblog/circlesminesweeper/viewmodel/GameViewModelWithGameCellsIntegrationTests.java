package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.graphics.Color;
import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.Circle;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.SecondLevel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameViewModelWithGameCellsIntegrationTests {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private GameViewModel viewModel;
    private CellsGenerator cellsGenerator;
    private GameCell[][] gameCells;

    @Before
    public void setUp() {
        cellsGenerator = mock(CellsGenerator.class);
        Handler handler = mock(Handler.class);
        when(handler.postDelayed(any(), anyLong())).thenAnswer((Answer<Void>) invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        });
        GameRepository gameRepository = mock(GameRepository.class);
        when(gameRepository.getLevelToPlay()).thenReturn(new SecondLevel());
        viewModel = new GameViewModel(cellsGenerator, handler, gameRepository);
        viewModel.getCheckButtonSrc();
    }

    @Test
    public void initiallyMineNotSet() {
        setGameCells();
        when(cellsGenerator.generateMines(gameCells, 1)).then(invocation -> {
            gameCells[0][1].setWithMine(true);
            return null;
        });
        viewModel.getGameCells(250, 250).observeForever(mock(Observer.class));

        assertFalse(gameCells[0][1].isWithMine());
    }

    @Test
    public void gameInProcessUnlessMinesGenerated() {
        setGameCells();
        when(cellsGenerator.generateMines(gameCells, 1)).then(invocation -> {
            gameCells[0][1].setWithMine(true);
            return null;
        });
        viewModel.getGameCells(250, 250).observeForever(mock(Observer.class));

        viewModel.actionDown(100, 100);
        viewModel.actionMove(100, 151);

        assertEquals(GameViewModel.GAME_IN_PROCESS, (int) viewModel.getGameCondition().getValue());
        assertTrue(gameCells[0][1].isWithMine());
    }

    @Test
    public void gameWonAfterRightCellGetsMarked() {
        setGameCells();
        when(cellsGenerator.generateMines(gameCells, 1)).then(invocation -> {
            gameCells[0][1].setWithMine(true);
            return null;
        });
        viewModel.getGameCells(250, 250).observeForever(mock(Observer.class));

        viewModel.actionDown(100, 100);
        viewModel.actionMove(100, 200);
        viewModel.markClicked();
        viewModel.actionDown(200, 100);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void gameWonAfterRightCellGetsMarkedEvenIfMarkedFirsts() {
        setGameCells();
        when(cellsGenerator.generateMines(gameCells, 1)).then(invocation -> {
            gameCells[0][1].setWithMine(true);
            return null;
        });
        viewModel.getGameCells(250, 250).observeForever(mock(Observer.class));

        viewModel.markClicked();
        viewModel.actionDown(200, 100);
        viewModel.actionDown(100, 100);
        viewModel.actionMove(100, 200);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    private void setGameCells() {
        gameCells = new GameCell[2][2];

        Circle redCircle = new Circle(100, 100, 50, Color.RED);
        Circle blueCircle = new Circle(200, 100, 50, Color.BLUE);
        Circle yellowCircle = new Circle(100, 200, 50, Color.YELLOW);
        Circle redCircleBottomRight = new Circle(200, 200, 50, Color.RED);

        gameCells[0][0] = new GameCell(redCircle, false, 0);
        gameCells[0][1] = new GameCell(blueCircle, false, 0);
        gameCells[1][0] = new GameCell(yellowCircle, false, 0);
        gameCells[1][1] = new GameCell(redCircleBottomRight, false, 0);

        when(cellsGenerator.generateCellsForField3X4(anyInt(), anyInt()))
                .thenReturn(gameCells);
    }

}
