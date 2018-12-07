package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;

import javax.inject.Inject;

import androidx.lifecycle.ViewModel;

public class ChooseLevelViewModel extends ViewModel {
    private GameRepository gameRepository;

    @Inject
    ChooseLevelViewModel(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void setLevel(int levelNumber) {
        gameRepository.setLevelNumber(levelNumber);
    }

    public int getLastLevelNumber() {
        return gameRepository.getLastOpenedLevel();
    }

    public boolean gameWasSaved() {
        return false;
    }
}
