package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstTutorialLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.TutorialLevel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameViewModelAndTutorialLevelsIntegrationTests {
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
        when(handler.postDelayed(any(), anyLong())).thenAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable) invocation.getArgument(0)).run();
                return null;
            }
        });
        gameRepository = mock(GameRepository.class);
        gameTutorialLevel = new FirstTutorialLevel();
        when(gameRepository.getLevelToPlay()).thenReturn(gameTutorialLevel);
        viewModel = new GameViewModel(cellsGenerator, handler, gameRepository);
    }

    @Test
    public void snackbarMessageDisplayedProperly() {
        when(gameRepository.messageForThisTutorialLevelWasShown()).thenReturn(false);

        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);

        assertEquals(gameTutorialLevel.getMessageResToDisplay(),
                (int) viewModel.getSnackbarMessage().getValue().getValueOrNull());
    }

    @Test
    public void snackbarMessageDoNotShowingUpIfItWasAlreadyShown() {
        when(gameRepository.messageForThisTutorialLevelWasShown()).thenReturn(true);

        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);

        assertNull(viewModel.getSnackbarMessage().getValue());
    }

    @Test
    public void whenSnackbarMessageClickedItsSavedAsShown() {
        when(gameRepository.messageForThisTutorialLevelWasShown()).thenReturn(false);

        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);
        viewModel.onSnackbarMessageClicked();

        verify(gameRepository).saveThatMessageForTutorialLevelWasShown();
    }

    @Test
    public void initiallyGameInProcess() {
        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);

        assertEquals(GameViewModel.GAME_IN_PROCESS, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void toWinGameMoveCircleToAnother() {
        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);

        viewModel.actionDown(50, 50);
        viewModel.actionMove(350, 450);

        assertEquals(GameViewModel.GAME_WON, (int) viewModel.getGameCondition().getValue());
    }

    @Test
    public void noAttemptToSaveTutorialLevel() {
        viewModel.getGameCells(500, 500).observeForever(gameCellsObserver);

        viewModel.beforeGameGoAway();

        verify(gameRepository, never()).saveGame(any());
    }
}
