package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepository;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameRepositoryImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.savegame.database.GameDatabase;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProvider;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProviderImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.DefaultLevelFactory;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsToBitmap;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LevelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class GameModule {

    private Resources resources;
    private SharedPreferences sharedPreferences;
    private Context context;

    public GameModule(Context appContext) {
        context = appContext;
        resources = appContext.getResources();
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
    BitmapProvider getBitmapProvider() {
        return new BitmapProviderImpl(resources);
    }

    @Provides
    @Singleton
    Paint getPaintToUseInDrawing() {
        Paint paintToUse = new Paint();
        paintToUse.setColor(Color.BLACK);
        paintToUse.setTextAlign(Paint.Align.LEFT);
        paintToUse.setStrokeWidth(20);
        return paintToUse;
    }

    @Provides
    @Singleton
    GameCellsToBitmap getGameCellsToBitmap(BitmapProvider bitmapProvider, Paint paint) {
        return new GameCellsToBitmap(bitmapProvider, paint, new Rect());
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
