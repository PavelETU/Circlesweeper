package com.wordpress.lonelytripblog.circlesminesweeper.data;

import android.content.SharedPreferences;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LevelFactory;

import javax.inject.Inject;

public class GameRepositoryImpl implements GameRepository {

    private LevelFactory levelFactory;
    private SharedPreferences sharedPreferences;
    private int level;
    private int fieldSizeCustom;
    private int minesCustom;

    @Inject
    public GameRepositoryImpl(LevelFactory levelFactory, SharedPreferences sharedPreferences) {
        this.levelFactory = levelFactory;
        this.sharedPreferences = sharedPreferences;
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
    public void setLevelWithParams(int levelNumber, int fieldSize, int amountOfMines) {
        level = levelNumber;
        fieldSizeCustom = fieldSize;
        minesCustom = amountOfMines;
    }

    @Override
    public int getLastOpenedLevel() {
        return sharedPreferences.getInt("opened_levels", 1);
    }

    @Override
    public void incrementLastOpenedLevel() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void saveGame(GameCell[][] gameCells) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void loadGame() {
        throw new RuntimeException("Not implemented");
    }
}
