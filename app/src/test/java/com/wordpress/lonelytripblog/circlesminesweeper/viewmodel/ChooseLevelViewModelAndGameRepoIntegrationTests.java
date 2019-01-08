package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.content.SharedPreferences;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepositoryImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database.GameDatabase;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LevelFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Executor;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ChooseLevelViewModelAndGameRepoIntegrationTests {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private SharedPreferences sharedPreferences;
    private GameRepository gameRepository;
    private LevelFactory levelFactory;
    private ChooseLevelViewModel chooseLevelViewModel;

    @Before
    public void setUp() {
        levelFactory = mock(LevelFactory.class);
        sharedPreferences = mock(SharedPreferences.class);
        GameDatabase db = mock(GameDatabase.class);
        Executor executor = mock(Executor.class);
        gameRepository = new GameRepositoryImpl(levelFactory, sharedPreferences, db, executor);
        chooseLevelViewModel = new ChooseLevelViewModel(gameRepository);
    }

    @Test
    public void requestForAmountOfLevelsCallsLevelFactory() {
        chooseLevelViewModel.getLevelsAmount();

        verify(levelFactory).getAmountOfLevels();
    }

    @Test
    public void lastLevelNumberTakenFromSharedPreference() {
        chooseLevelViewModel.getLastLevelNumber();

        verify(sharedPreferences).getInt("opened_levels", 1);
    }

}
