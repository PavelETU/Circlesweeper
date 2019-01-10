package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class ThirdTutorialLevel implements TutorialLevel {
    @Override
    public int getMessageResToDisplay() {
        return R.string.third_tutorial_message;
    }

    @Override
    public int getMinScoreToWin() {
        return 80;
    }

    @Override
    public int getMessageResToDisplayIfLost() {
        return R.string.third_tutorial_level_lost_message;
    }

    @Override
    public GameCell[][] generateCircles(CellsGenerator cellsGenerator, int width, int height) {
        GameCell[][] gameCells = cellsGenerator.generateEmptyCells(width, height, 4, 4);
        gameCells[1][1].setCircleAliveWithColor(R.drawable.blue_ball);
        gameCells[2][2].setCircleAliveWithColor(R.drawable.blue_ball);
        gameCells[1][2].setCircleAliveWithColor(R.drawable.yellow_ball);
        gameCells[2][1].setCircleAliveWithColor(R.drawable.yellow_ball);
        return gameCells;
    }

    @Override
    public int getMinesAmount() {
        return 0;
    }
}
