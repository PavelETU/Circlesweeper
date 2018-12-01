package com.wordpress.lonelytripblog.circlesminesweeper.di;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {GameModule.class,
        AndroidSupportInjectionModule.class,
        ActivityContributorModule.class})
public interface GameComponent {
    void injectApp(CircleSweeperApp app);
}