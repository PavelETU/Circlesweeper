package com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface GameCellsDao {
    @Query("SELECT * FROM GameCellEntity ORDER by id")
    LiveData<List<GameCellEntity>> getCells();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCells(List<GameCellEntity> cells);
}
