package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

public class SecondTutorialLevel implements TutorialLevel {
    @Override
    public int getMessageResToDisplay() {
        return R.string.second_tutorial_message;
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
        GameCell[][] gameCells = cellsGenerator.generateEmptyCells(width, height, 5, 5);
        gameCells[2][2].setCircleAliveWithColor(R.drawable.purple_ball);
        gameCells[2][2].setWithMine(true);
        gameCells[1][2].setMinesNear(1);
        gameCells[3][2].setMinesNear(1);
        gameCells[2][1].setMinesNear(1);
        gameCells[2][3].setMinesNear(1);
        gameCells[3][3].setMinesNear(1);
        gameCells[1][1].setMinesNear(1);
        gameCells[1][3].setMinesNear(1);
        gameCells[3][1].setMinesNear(1);
        return gameCells;
    }

    @Override
    public int getMinesAmount() {
        return 1;
    }
}
