package com.wordpress.lonelytripblog.circlesminesweeper.data.levels;

public interface TutorialLevel extends GameLevel {
    int EMPTY_MESSAGE = 0;
    int getMessageResToDisplay();
    int getMinScoreToWin();
    int getMessageResToDisplayIfLost();
}
