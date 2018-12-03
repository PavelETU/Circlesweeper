package com.wordpress.lonelytripblog.circlesminesweeper.di;

import com.wordpress.lonelytripblog.circlesminesweeper.ui.ChooseLevelActivity;
import com.wordpress.lonelytripblog.circlesminesweeper.ui.GameActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityContributorModule {
    @ContributesAndroidInjector
    abstract GameActivity getGameActivity();

    @ContributesAndroidInjector
    abstract ChooseLevelActivity getChooseLevelActivity();
}
