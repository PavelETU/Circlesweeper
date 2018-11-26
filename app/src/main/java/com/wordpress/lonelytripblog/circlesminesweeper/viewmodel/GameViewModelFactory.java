package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import android.os.Handler;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.di.CircleSweeperApp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameViewModelFactory implements ViewModelProvider.Factory {

    private final CircleSweeperApp app;
    private final CellsGenerator cellsGenerator;
    private final Handler handler;

    public GameViewModelFactory(CircleSweeperApp app, CellsGenerator cellsGenerator, Handler handler) {
        this.app = app;
        this.cellsGenerator = cellsGenerator;
        this.handler = handler;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameViewModel(app, cellsGenerator, handler);
    }
}
