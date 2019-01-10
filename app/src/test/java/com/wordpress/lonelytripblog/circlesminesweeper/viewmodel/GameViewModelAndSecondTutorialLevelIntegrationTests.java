package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.SecondTutorialLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.TutorialLevel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameViewModelAndSecondTutorialLevelIntegrationTests {
    @Rule
    public InstantTaskExecutorRule architectureRule = new InstantTaskExecutorRule();
    private GameViewModel viewModel;
    private GameRepository gameRepository;
    private Observer<GameCell[][]> gameCellsObserver;
    private TutorialLevel gameTutorialLevel;

    @Before
    public void setUp() {
        gameCellsObserver = (Observer<GameCell[][]>) mock(Observer.class);
        CellsGenerator cellsGenerator = new CellsGeneratorImpl();
        Handler handler = mock(Handler.class);
        when(handler.postDelayed(any(), anyLong())).thenAnswer((Answer<Void>) invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        });
        gameRepository = mock(GameRepository.class);
        gameTutorialLevel = new SecondTutorialLevel();
        when(gameRepository.getLevelToPlay()).thenReturn(gameTutorialLevel);
        viewModel = new GameViewModel(cellsGenerator, handler, gameRepository);
        viewModel.getCheckButtonSrc();
    }

    @Test
    public void toWinMarkBomb() {
        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);

        viewModel.markClicked();
        viewModel.actionDown(250, 250);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void toLoseGameTouchTheBomb() {
        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);

        viewModel.actionDown(250, 250);

        assertEquals(GameViewModel.GAME_LOST, (int) viewModel.getGameCondition().getValue());
    }
}
