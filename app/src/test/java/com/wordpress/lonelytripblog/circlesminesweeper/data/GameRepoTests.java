package com.wordpress.lonelytripblog.circlesminesweeper.data;

import android.content.SharedPreferences;

import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database.GameDatabase;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.LevelFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class GameRepoTests {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private GameRepository gameRepository;
    private LevelFactory levelFactory;

    @Before
    public void setUp() {
        levelFactory = mock(LevelFactory.class);
        sharedPreferences = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putBoolean(any(), anyBoolean())).thenReturn(editor);
        when(editor.putInt(any(), anyInt())).thenReturn(editor);
        GameDatabase db = mock(GameDatabase.class);
        Executor executor = mock(Executor.class);
        gameRepository = new GameRepositoryImpl(levelFactory, sharedPreferences, db, executor);
    }

    @Test
    public void ifAllScoresSetArrayContainsScoresForAllLevelsApartLast() {
        when(levelFactory.getAmountOfLevels()).thenReturn(80);
        when(sharedPreferences.getInt(anyString(), anyInt())).thenReturn(100);

        List<Score> scoreList = gameRepository.getScores().getValue();

        Assert.assertEquals(79, scoreList.size());
    }

    @Test
    public void nothingBeatsScoreForLastLevel() {
        when(levelFactory.getAmountOfLevels()).thenReturn(6);
        when(sharedPreferences.getInt(anyString(), anyInt())).thenReturn(0);
        gameRepository.setLevelNumber(6);

        boolean result = gameRepository.thisScoreBeatsRecord(Integer.MAX_VALUE);

        assertFalse(result);
    }

    @Test
    public void scoreBeatsRecordWorksAsExpectedIfNothingSaved() {
        when(sharedPreferences.getInt(anyString(), anyInt())).thenReturn(0);
        gameRepository.setLevelNumber(1);

        assertFalse(gameRepository.thisScoreBeatsRecord(0));
        assertTrue(gameRepository.thisScoreBeatsRecord(1));
        assertTrue(gameRepository.thisScoreBeatsRecord(10));
    }

    @Test
    public void scoreBeatsRecordWorksAsExpectedIfValueSaved() {
        when(sharedPreferences.getInt(anyString(), anyInt())).thenReturn(100);
        gameRepository.setLevelNumber(1);

        assertFalse(gameRepository.thisScoreBeatsRecord(0));
        assertFalse(gameRepository.thisScoreBeatsRecord(50));
        assertFalse(gameRepository.thisScoreBeatsRecord(99));
        assertFalse(gameRepository.thisScoreBeatsRecord(100));

        assertTrue(gameRepository.thisScoreBeatsRecord(101));
        assertTrue(gameRepository.thisScoreBeatsRecord(150));
        assertTrue(gameRepository.thisScoreBeatsRecord(200));
    }

    @Test
    public void messageWasShownSavedWithProperKey() {
        gameRepository.setLevelNumber(80);
        gameRepository.saveThatMessageForTutorialLevelWasShown();

        verify(editor).putBoolean("message_tutorial_80", true);
        verify(editor).apply();
    }

    @Test
    public void messageWasShownLoadedWithProperKey() {
        gameRepository.setLevelNumber(80);
        gameRepository.messageForThisTutorialLevelWasShown();

        verify(sharedPreferences).getBoolean("message_tutorial_80", false);
    }

    @Test
    public void newLevelGetsOpenedWhenItShouldBe() {
        when(levelFactory.getAmountOfLevels()).thenReturn(256);
        when(sharedPreferences.getInt("opened_levels", 1)).thenReturn(255);
        gameRepository.setLevelNumber(255);

        gameRepository.openNextLevelIfItsExist();

        verify(editor).putInt("opened_levels", 256);
    }

    @Test
    public void ifThereAreNoLevelsNothingGetsOpened() {
        when(levelFactory.getAmountOfLevels()).thenReturn(255);
        when(sharedPreferences.getInt("opened_levels", 1)).thenReturn(255);
        gameRepository.setLevelNumber(255);

        gameRepository.openNextLevelIfItsExist();

        verifyZeroInteractions(editor);
    }

    @Test
    public void ifLevelOpenedIfWontBeOpenedAgain() {
        when(levelFactory.getAmountOfLevels()).thenReturn(256);
        when(sharedPreferences.getInt("opened_levels", 1)).thenReturn(256);
        gameRepository.setLevelNumber(255);

        gameRepository.openNextLevelIfItsExist();

        verifyZeroInteractions(editor);
    }
}
