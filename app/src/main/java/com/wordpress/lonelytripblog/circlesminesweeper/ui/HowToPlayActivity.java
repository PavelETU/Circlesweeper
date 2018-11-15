package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

public class HowToPlayActivity extends AppCompatActivity {
    private ImageView back_img;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        textView = (TextView) findViewById(R.id.about_game_txt);
        back_img = (ImageView) findViewById(R.id.Background_image);
        if (getResources().getConfiguration().orientation ==  2) {
            back_img.setImageResource(R.drawable.background_level_land);
        }
        textView.setMovementMethod(new ScrollingMovementMethod());
    }
    public void exit_clicked(View view) {
        onBackPressed();
    }
}
