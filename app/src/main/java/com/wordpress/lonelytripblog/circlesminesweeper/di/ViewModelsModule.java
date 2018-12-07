package com.wordpress.lonelytripblog.circlesminesweeper.di;

import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.BestScoreViewModel;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.ChooseLevelViewModel;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModel;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModelFactory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(GameViewModel.class)
    abstract ViewModel bindGameViewModel(GameViewModel gameViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChooseLevelViewModel.class)
    abstract ViewModel bindChooseLevelViewModel(ChooseLevelViewModel chooseLevelViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BestScoreViewModel.class)
    abstract ViewModel bindBestScoreViewModel(BestScoreViewModel bestScoreViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(GameViewModelFactory viewModelFactory);

}
