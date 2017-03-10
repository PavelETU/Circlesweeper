package com.wordpress.lonelytripblog.circlesminesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class about_the_game extends AppCompatActivity {
    private ImageView back_img;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_the_game_view);
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
