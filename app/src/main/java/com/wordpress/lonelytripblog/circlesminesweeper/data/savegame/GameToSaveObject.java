package com.wordpress.lonelytripblog.circlesminesweeper.data.savegame;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class GameToSaveObject {
    public static final String WIDTH_KEY = "width";
    public static final String HEIGHT_KEY = "height";
    public static final String SCORE_KEY = "score";
    public static final String LEFT_MINES_KEY = "left_mines";
    public static final String MARK_STATE_KEY = "mark_state";
    public static final String MINES_GENERATED_KEY = "mines_generated";
    public static final String UNCOVERED_MINES_AMOUNT_KEY = "uncovered_mines";
    private final GameCell[][] gameCells;
    private final int width;
    private final int height;
    private final int score;
    private final int leftMines;
    private final boolean markState;
    private final boolean minesGenerated;
    private final int uncoveredMinesAmount;

    public GameToSaveObject(GameCell[][] gameCells, int width, int height, int score, int leftMines,
                            boolean markState, boolean minesGenerated, int uncoveredMinesAmount) {
        this.gameCells = gameCells;
        this.width = width;
        this.height = height;
        this.score = score;
        this.leftMines = leftMines;
        this.markState = markState;
        this.minesGenerated = minesGenerated;
        this.uncoveredMinesAmount = uncoveredMinesAmount;
    }

    public GameCell[][] getGameCells() {
        return gameCells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getScore() {
        return score;
    }

    public int getLeftMines() {
        return leftMines;
    }

    public boolean isMarkState() {
        return markState;
    }

    public boolean isMinesGenerated() {
        return minesGenerated;
    }

    public int getUncoveredMinesAmount() {
        return uncoveredMinesAmount;
    }
}
