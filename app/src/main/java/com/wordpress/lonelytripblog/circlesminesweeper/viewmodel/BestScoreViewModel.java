package com.wordpress.lonelytripblog.circlesminesweeper.viewmodel;

import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.Score;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class BestScoreViewModel extends ViewModel {
    private GameRepository gameRepository;

    @Inject
    BestScoreViewModel(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public LiveData<List<Score>> getScores() {
        return gameRepository.getScores();
    }

}
