package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.graphics.Bitmap;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class Mapper {

    private GameCellsToBitmap gameCellsToBitmap;

    public Mapper(GameCellsToBitmap gameCellsToBitmap) {
        this.gameCellsToBitmap = gameCellsToBitmap;
    }

    public LiveData<Bitmap> getGameImageLiveData(LiveData<GameCell[][]> gameCellsLiveData, float width, float height) {
        return Transformations.map(gameCellsLiveData,
                gameCells -> gameCellsToBitmap.gameCellsToBitmap(gameCells, width, height));
    }


}
