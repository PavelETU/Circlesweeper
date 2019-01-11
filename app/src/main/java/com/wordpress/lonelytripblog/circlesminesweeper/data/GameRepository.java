package com.wordpress.lonelytripblog.circlesminesweeper.data;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.GameToSaveObject;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface GameRepository {
    GameLevel getLevelToPlay();
    void setLevelNumber(int levelNumber);
    void setLevelWithParams(int fieldSize, int amountOfMines);
    int getLastOpenedLevel();
    void openNextLevelIfItsExist();
    boolean isCurrentLevelRequiresDialog();
    LiveData<List<Score>> getScores();
    boolean thisScoreBeatsRecord(int score);
    void updateScore(int newScore);
    void saveGame(GameToSaveObject gameToSaveObject);
    LiveData<GameToSaveObject> loadGame();
    boolean lastGameWasSaved();
    boolean shouldLoadGame();
    void openSavedGame();
    void nothingToLoadNextTime();
    boolean messageForThisTutorialLevelWasShown();
    void saveThatMessageForTutorialLevelWasShown();
    int getLevelsAmount();
}
