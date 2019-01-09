package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class FirstTutorialLevel implements TutorialLevel {
    @Override
    public int getMessageResToDisplay() {
        return R.string.first_tutorial_message;
    }

    @Override
    public int getMinScoreToWin() {
        return 0;
    }

    @Override
    public int getMessageResToDisplayIfLost() {
        return EMPTY_MESSAGE;
    }

    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        GameCell[][] gameCells = cellsGenerator.generateEmptyCells(width, height, 4, 4);
        gameCells[0][0].setCircleAliveWithColor(R.drawable.red_ball);
        gameCells[3][3].setCircleAliveWithColor(R.drawable.red_ball);
        return gameCells;
    }

    @Override
    public int getMinesAmount() {
        return 0;
    }
}
