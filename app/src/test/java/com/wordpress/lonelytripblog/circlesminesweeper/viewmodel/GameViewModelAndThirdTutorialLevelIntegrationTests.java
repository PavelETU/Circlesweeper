package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.ThirdTutorialLevel;
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

public class GameViewModelAndThirdTutorialLevelIntegrationTests {
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
        gameTutorialLevel = new ThirdTutorialLevel();
        when(gameRepository.getLevelToPlay()).thenReturn(gameTutorialLevel);
        viewModel = new GameViewModel(cellsGenerator, handler, gameRepository);
    }

    @Test
    public void toWinGameMoveLeftUpCircleToRight() {
        viewModel.getGameCells(400, 400).observeForever(gameCellsObserver);

        viewModel.actionDown(150, 150);
        viewModel.actionMove(250, 150);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void toLoseGameBangCirclesOneByOne() {
        viewModel.getGameCells(400, 400).observeForever(gameCellsObserver);

        viewModel.actionDown(250, 150);
        viewModel.actionMove(150, 350);

        viewModel.actionDown(150, 150);
        viewModel.actionMove(250, 150);

        assertEquals(GameViewModel.GAME_LOST, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void afterLoosingMessageDisplayed() {
        viewModel.getGameCells(400, 400).observeForever(gameCellsObserver);

        viewModel.actionDown(250, 150);
        viewModel.actionMove(150, 350);

        viewModel.actionDown(150, 150);
        viewModel.actionMove(250, 150);

        assertEquals(R.string.third_tutorial_level_lost_message,
                (int) viewModel.getToastEvent().getValue().getValueOrNull());
    }
}
