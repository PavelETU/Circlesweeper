package com.wordpress.lonelytripblog.circlesminesweeper.data;

import android.content.SharedPreferences;

import com.wordpress.lonelytripblog.circlesminesweeper.data.database.GameCellsDao;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LevelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GameRepositoryImpl implements GameRepository {

    private static final int LAST_LEVEL = 6;
    private static final String KEY_FOR_BEST_SCORE = "best_score";
    private static final String KEY_FOR_DATE_OF_BEST_SCORE = "data_of_best_score";
    private LevelFactory levelFactory;
    private SharedPreferences sharedPreferences;
    private int level;
    private int fieldSizeCustom;
    private int minesCustom;
    private MutableLiveData<List<Score>> scoreLiveData = new MutableLiveData<>();
    private GameCellsDao gameCellsDao;

    @Inject
    public GameRepositoryImpl(LevelFactory levelFactory, SharedPreferences sharedPreferences,
                              GameCellsDao gameCellsDao) {
        this.levelFactory = levelFactory;
        this.sharedPreferences = sharedPreferences;
        this.gameCellsDao = gameCellsDao;
    }

    @Override
    public GameLevel getLevelToPlay() {
        return levelFactory.makeLevel(level, fieldSizeCustom, minesCustom);
    }

    @Override
    public void setLevelNumber(int levelNumber) {
        level = levelNumber;
    }

    @Override
    public void setLevelWithParams(int fieldSize, int amountOfMines) {
        fieldSizeCustom = fieldSize;
        minesCustom = amountOfMines;
    }

    @Override
    public int getLastOpenedLevel() {
        return sharedPreferences.getInt("opened_levels", 1);
    }

    @Override
    public void incrementLastOpenedLevel() {
        if (level != LAST_LEVEL) {
            level = level + 1;
            if (getLastOpenedLevel() < level) {
                sharedPreferences.edit().putInt("opened_levels", level).apply();
            }
        }
    }

    @Override
    public boolean isCurrentLevelRequiresDialog() {
        return level == LAST_LEVEL;
    }

    @Override
    public LiveData<List<Score>> getScores() {
        List<Score> scores = new ArrayList<>();
        for (int i = 0; i < LAST_LEVEL - 1; i++) {
            int correspondingLevel = i + 1;
            int bestScore = sharedPreferences.getInt(KEY_FOR_BEST_SCORE
                    + String.valueOf(correspondingLevel), 0);
            if (bestScore > 0) {
                scores.add(new Score(correspondingLevel,
                        sharedPreferences.getString(KEY_FOR_DATE_OF_BEST_SCORE
                                + String.valueOf(correspondingLevel), "-"),
                        bestScore));
            }
        }
        scoreLiveData.setValue(scores);
        return scoreLiveData;
    }

    @Override
    public boolean thisScoreBeatsRecord(int score) {
        int bestScoreSoFar = sharedPreferences.getInt(KEY_FOR_BEST_SCORE
                + String.valueOf(level), 0);
        return score > bestScoreSoFar;
    }

    @Override
    public void updateScore(int newScore) {
        String dateString = new SimpleDateFormat("dd.MM.yy, H:mm", Locale.getDefault()).format(new Date());
        sharedPreferences.edit().putInt(KEY_FOR_BEST_SCORE + String.valueOf(level), newScore)
                .putString(KEY_FOR_DATE_OF_BEST_SCORE + String.valueOf(level), dateString).apply();
    }

    @Override
    public void saveGame(GameCell[][] gameCells, int width, int height, int score, int leftMines, boolean markState) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public GameCell[][] loadGame() {
        throw new RuntimeException("Not implemented");
    }
}
