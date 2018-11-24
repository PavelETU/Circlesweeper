package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.di.CircleSweeperApp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameViewModelFactory implements ViewModelProvider.Factory {

    private CircleSweeperApp app;
    private CellsGenerator cellsGenerator;

    public GameViewModelFactory(CircleSweeperApp app, CellsGenerator cellsGenerator) {
        this.app = app;
        this.cellsGenerator = cellsGenerator;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameViewModel(app, cellsGenerator);
    }
}
