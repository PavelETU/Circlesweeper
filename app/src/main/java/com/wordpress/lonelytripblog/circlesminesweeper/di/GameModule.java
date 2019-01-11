package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepositoryImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database.GameDatabase;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.DefaultLevelFactory;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.LevelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class GameModule {

    private SharedPreferences sharedPreferences;
    private Context context;

    public GameModule(Context appContext) {
        context = appContext;
        sharedPreferences = appContext.getSharedPreferences("gamePreference", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    LevelFactory getLevelFactory() {
        return new DefaultLevelFactory();
    }

    @Provides
    @Singleton
    GameRepository getGameRepo(LevelFactory levelFactory, GameDatabase db, Executor executor) {
        return new GameRepositoryImpl(levelFactory, sharedPreferences, db, executor);
    }

    @Provides
    @Singleton
    CellsGenerator getCellsGenerator() {
        return new CellsGeneratorImpl();
    }

    @Provides
    @Singleton
    Handler getMainHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    @Singleton
    GameDatabase getGameDatabase() {
        return Room.databaseBuilder(context, GameDatabase.class, "GameDatabase").build();
    }

    @Provides
    @Singleton
    Executor getExecutor() {
        return Executors.newSingleThreadExecutor();
    }

}
