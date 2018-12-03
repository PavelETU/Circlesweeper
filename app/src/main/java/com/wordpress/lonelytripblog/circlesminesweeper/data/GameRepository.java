package com.wordpress.lonelytripblog.circlesminesweeper.data;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;

public interface GameRepository {
    GameLevel getLevelToPlay();
    void setLevelNumber(int levelNumber);
    void setLevelWithParams(int levelNumber, int fieldSize, int amountOfMines);
    int getLastOpenedLevel();
    void incrementLastOpenedLevel();
    void saveGame(GameCell[][] gameCells);
    void loadGame();
}
