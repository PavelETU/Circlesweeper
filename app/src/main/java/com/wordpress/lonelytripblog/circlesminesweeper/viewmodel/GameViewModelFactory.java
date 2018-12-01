package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsToBitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameViewModelFactory implements ViewModelProvider.Factory {

    private final CellsGenerator cellsGenerator;
    private final Handler mainHandler;
    private final GameCellsToBitmap gameCellsToBitmap;

    public GameViewModelFactory(CellsGenerator cellsGenerator, Handler mainHandler,
                                GameCellsToBitmap gameCellsToBitmap) {
        this.cellsGenerator = cellsGenerator;
        this.mainHandler = mainHandler;
        this.gameCellsToBitmap = gameCellsToBitmap;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameViewModel(cellsGenerator, mainHandler, gameCellsToBitmap);
    }
}
