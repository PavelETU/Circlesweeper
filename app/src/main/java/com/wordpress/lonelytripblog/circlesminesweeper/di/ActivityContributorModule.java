package com.wordpress.lonelytripblog.circlesminesweeper.di;

import com.wordpress.lonelytripblog.circlesminesweeper.ui.activities.BestScoreActivity;
import com.wordpress.lonelytripblog.circlesminesweeper.ui.activities.ChooseLevelActivity;
import com.wordpress.lonelytripblog.circlesminesweeper.ui.activities.GameActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityContributorModule {
    @ContributesAndroidInjector
    abstract GameActivity getGameActivity();

    @ContributesAndroidInjector
    abstract ChooseLevelActivity getChooseLevelActivity();

    @ContributesAndroidInjector
    abstract BestScoreActivity getBestScoreActivity();
}
