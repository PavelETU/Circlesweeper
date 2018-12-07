package com.wordpress.lonelytripblog.circlesminesweeper.data;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface GameRepository {
    GameLevel getLevelToPlay();
    void setLevelNumber(int levelNumber);
    void setLevelWithParams(int fieldSize, int amountOfMines);
    int getLastOpenedLevel();
    void incrementLastOpenedLevel();
    boolean isCurrentLevelRequiresDialog();
    LiveData<List<Score>> getScores();
    boolean thisScoreBeatsRecord(int score);
    void updateScore(int newScore);
    void saveGame(GameCell[][] gameCells);
    void loadGame();
}
