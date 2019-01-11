package com.wordpress.lonelytripblog.circlesminesweeper.ui.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

public class HowToPlayActivity extends FullScreenActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        TextView textView = findViewById(R.id.about_game_txt);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }
}
