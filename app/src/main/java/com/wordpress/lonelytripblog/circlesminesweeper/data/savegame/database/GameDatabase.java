package com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GameCellEntity.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {
    public abstract GameCellsDao gameCellsDao();
}
