package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.app.Activity;
import android.app.Application;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class CircleSweeperApp extends Application implements HasActivityInjector {

    private GameComponent gameComponent;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (gameComponent == null) {
            gameComponent = DaggerGameComponent.builder()
                    .gameModule(new GameModule(this)).build();
        }
        gameComponent.injectApp(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

}
