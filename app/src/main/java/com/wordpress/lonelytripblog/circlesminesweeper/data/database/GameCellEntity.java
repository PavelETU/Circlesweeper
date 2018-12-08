package com.wordpress.lonelytripblog.circlesminesweeper.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GameCellEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int row;
    private final int col;
    @ColumnInfo(name = "with_mine")
    private final boolean withMine;
    private final boolean marked;
    @ColumnInfo(name = "mines_near")
    private final int minesNear;
    @ColumnInfo(name = "top_left_x")
    private final int topLeftX;
    @ColumnInfo(name = "top_left_y")
    private final int topLeftY;
    @ColumnInfo(name = "side_length")
    private final int sideLength;
    @ColumnInfo(name = "circle_color_drawable_src")
    private final int circleColorDrawableSrc;
    @ColumnInfo(name = "is_circle_alive")
    private final boolean isCircleAlive;

    public GameCellEntity(int row, int col, boolean withMine, boolean marked,
                          int minesNear, int topLeftX, int topLeftY, int sideLength,
                          int circleColorDrawableSrc, boolean isCircleAlive) {
        this.row = row;
        this.col = col;
        this.withMine = withMine;
        this.marked = marked;
        this.minesNear = minesNear;
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.sideLength = sideLength;
        this.circleColorDrawableSrc = circleColorDrawableSrc;
        this.isCircleAlive = isCircleAlive;
    }

    public int getId() {
        return id;
    }

    public boolean isWithMine() {
        return withMine;
    }

    public boolean isMarked() {
        return marked;
    }

    public int getMinesNear() {
        return minesNear;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public int getSideLength() {
        return sideLength;
    }

    public int getCircleColorDrawableSrc() {
        return circleColorDrawableSrc;
    }

    public boolean isCircleAlive() {
        return isCircleAlive;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
