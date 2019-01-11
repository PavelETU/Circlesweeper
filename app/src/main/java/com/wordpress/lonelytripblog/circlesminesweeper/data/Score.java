package com.wordpress.lonelytripblog.circlesminesweeper.data;

public class Score {
    private final int level;
    private final String DateAndTime;
    private final int score;

    public Score(int level, String dateAndTime, int score) {
        this.level = level;
        DateAndTime = dateAndTime;
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public String getDateAndTime() {
        return DateAndTime;
    }

    public int getScore() {
        return score;
    }
}
