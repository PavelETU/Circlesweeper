package com.wordpress.lonelytripblog.circlesminesweeper.data;

import android.content.SharedPreferences;

import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.GameLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.GameCellsAdapter;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.GameToSaveObject;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database.GameDatabase;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LevelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class GameRepositoryImpl implements GameRepository {

    private static final int LAST_LEVEL = 6;
    private static final String KEY_FOR_BEST_SCORE = "best_score";
    private static final String KEY_FOR_DATE_OF_BEST_SCORE = "data_of_best_score";
    private static final String LEVEL_OF_SAVED_GAME = "level_of_saved_game";
    private static final String LAST_GAME_WAS_SAVED_KEY = "last_game_was_saved";
    private static final String MINES_TO_GENERATE_KEY = "generate_this_amount";
    private LevelFactory levelFactory;
    private SharedPreferences sharedPreferences;
    private int level;
    private int fieldSizeCustom;
    private int minesCustom;
    private MutableLiveData<List<Score>> scoreLiveData = new MutableLiveData<>();
    private GameDatabase db;
    private Executor executor;
    private boolean openLastGame;
    private int minesToGenerate;

    @Inject
    public GameRepositoryImpl(LevelFactory levelFactory, SharedPreferences sharedPreferences,
                              GameDatabase db, Executor executor) {
        this.levelFactory = levelFactory;
        this.sharedPreferences = sharedPreferences;
        this.db = db;
        this.executor = executor;
    }

    @Override
    public GameLevel getLevelToPlay() {
        GameLevel gameLevel = levelFactory.makeLevel(level, fieldSizeCustom, minesCustom);
        minesToGenerate = gameLevel.getMinesAmount();
        return gameLevel;
    }

    @Override
    public void setLevelNumber(int levelNumber) {
        openLastGame = false;
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
    public int getMinesForCurrentLevel() {
        return minesToGenerate;
    }

    @Override
    public void saveGame(GameToSaveObject gameToSaveObject) {
        executor.execute(() -> {
            db.clearAllTables();
            db.gameCellsDao().saveCells(GameCellsAdapter.toCellsForSaving(gameToSaveObject.getGameCells()));
        });
        sharedPreferences.edit().putBoolean(LAST_GAME_WAS_SAVED_KEY, true)
                .putInt(LEVEL_OF_SAVED_GAME, level)
                .putInt(MINES_TO_GENERATE_KEY, minesToGenerate)
                .putInt(GameToSaveObject.WIDTH_KEY, gameToSaveObject.getWidth())
                .putInt(GameToSaveObject.HEIGHT_KEY, gameToSaveObject.getHeight())
                .putInt(GameToSaveObject.SCORE_KEY, gameToSaveObject.getScore())
                .putInt(GameToSaveObject.LEFT_MINES_KEY, gameToSaveObject.getLeftMines())
                .putBoolean(GameToSaveObject.MARK_STATE_KEY, gameToSaveObject.isMarkState())
                .putBoolean(GameToSaveObject.MINES_GENERATED_KEY, gameToSaveObject.isMinesGenerated())
                .putInt(GameToSaveObject.UNCOVERED_MINES_AMOUNT_KEY, gameToSaveObject.getUncoveredMinesAmount())
                .apply();
    }

    @Override
    public LiveData<GameToSaveObject> loadGame() {
        openLastGame = false;
        level = sharedPreferences.getInt(LEVEL_OF_SAVED_GAME, 0);
        minesToGenerate = sharedPreferences.getInt(MINES_TO_GENERATE_KEY, 0);
        return Transformations.map(db.gameCellsDao().getCells(), gameCells ->
                new GameToSaveObject(GameCellsAdapter.toGameCells(gameCells),
                        sharedPreferences.getInt(GameToSaveObject.WIDTH_KEY, -1),
                        sharedPreferences.getInt(GameToSaveObject.HEIGHT_KEY, -1),
                        sharedPreferences.getInt(GameToSaveObject.SCORE_KEY, -1),
                        sharedPreferences.getInt(GameToSaveObject.LEFT_MINES_KEY, -1),
                        sharedPreferences.getBoolean(GameToSaveObject.MARK_STATE_KEY, false),
                        sharedPreferences.getBoolean(GameToSaveObject.MINES_GENERATED_KEY, false),
                        sharedPreferences.getInt(GameToSaveObject.UNCOVERED_MINES_AMOUNT_KEY, 0))
        );
    }

    @Override
    public boolean lastGameWasSaved() {
        return sharedPreferences.getBoolean(LAST_GAME_WAS_SAVED_KEY, false);
    }

    @Override
    public boolean shouldLoadGame() {
        return openLastGame;
    }

    @Override
    public void openSavedGame() {
        openLastGame = true;
    }

    @Override
    public void nothingToLoadNextTime() {
        sharedPreferences.edit().putBoolean(LAST_GAME_WAS_SAVED_KEY, false).apply();
    }

    @Override
    public boolean messageForThisTutorialLevelWasShown() {
        return false;
    }

    @Override
    public void saveThatMessageForTutorialLevelWasShown() {

    }
}
