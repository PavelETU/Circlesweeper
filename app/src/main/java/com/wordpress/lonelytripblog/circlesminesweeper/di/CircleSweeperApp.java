package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class CircleSweeperApp extends Application implements HasAndroidInjector {

    private GameComponent gameComponent;

    @Inject
    DispatchingAndroidInjector<Object> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (gameComponent == null) {
            gameComponent = DaggerGameComponent.builder()
                    .gameModule(new GameModule(this)).build();
        }
        gameComponent.injectApp(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (activity instanceof InjectMe) {
                    AndroidInjection.inject(activity);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingActivityInjector;
    }
}
