package com.wordpress.lonelytripblog.circlesminesweeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

public class main_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_view);
        final ImageButton record_btn = (ImageButton) findViewById(R.id.record_btn);
        final ImageButton play_btn = (ImageButton) findViewById(R.id.play_btn);
        final ImageButton sound_btn = (ImageButton) findViewById(R.id.sound_btn);
        final ImageButton help_btn = (ImageButton) findViewById(R.id.help_btn);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int size_of_side = (metrics.widthPixels > metrics.heightPixels) ?
                    metrics.heightPixels/5: metrics.widthPixels/5;
        //Bitmap button = Bitmap.createBitmap(size_of_side, size_of_side, Bitmap.Config.ARGB_8888);
        Bitmap play_bmp = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.play),
                size_of_side, size_of_side, false);
        Bitmap help_bmp = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.help),
                size_of_side, size_of_side, false);
        Bitmap sound_bmp = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.sound),
                size_of_side, size_of_side, false);
        Bitmap crown = Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(getResources(),R.drawable.crown),
                                            size_of_side, size_of_side, false);
        play_btn.setImageBitmap(play_bmp);
        sound_btn.setImageBitmap(sound_bmp);
        help_btn.setImageBitmap(help_bmp);
        record_btn.setImageBitmap(crown);
        //record_btn.setBackground(null);
    }

    public void to_choose_level(View view) {
        Intent intent = new Intent(this, choose_level.class);
        startActivity(intent);
    }
    public void about_game(View view) {
        Intent intent = new Intent(this, about_the_game.class);
        startActivity(intent);
    }

    public void on_record(View view) {
        Intent intent = new Intent(this, best_score.class);
        startActivity(intent);
    }
}
