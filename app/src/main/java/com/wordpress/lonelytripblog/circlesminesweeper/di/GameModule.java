package com.wordpress.lonelytripblog.circlesminesweeper.di;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;

import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGenerator;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProvider;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.BitmapProviderImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.DefaultLevelFactory;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.GameCellsToBitmap;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LevelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GameModule {

    private Resources resources;

    public GameModule(Resources resources) {
        this.resources = resources;
    }

    @Provides
    @Singleton
    LevelFactory getLevelFactory() {
        return new DefaultLevelFactory();
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
        paintToUse.setTextSize(100);
        paintToUse.setTextAlign(Paint.Align.CENTER);
        paintToUse.setStrokeWidth(20);
        return paintToUse;
    }

    @Provides
    @Singleton
    GameCellsToBitmap getGameCellsToBitmap(BitmapProvider bitmapProvider, Paint paint) {
        return new GameCellsToBitmap(bitmapProvider, paint);
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

}
