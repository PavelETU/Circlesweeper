package com.wordpress.lonelytripblog.circlesminesweeper;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class BestScoreActivity extends AppCompatActivity {
    private ImageView back_img;
    private TextView[] date_time = new TextView[5];
    private TextView[] score = new TextView[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_score);
        back_img = (ImageView) findViewById(R.id.Background_image);
        date_time[0] = (TextView) findViewById(R.id.level1_2);
        date_time[1] = (TextView) findViewById(R.id.level2_2);
        date_time[2] = (TextView) findViewById(R.id.level3_2);
        date_time[3] = (TextView) findViewById(R.id.level4_2);
        date_time[4] = (TextView) findViewById(R.id.level5_2);
        score[0] = (TextView) findViewById(R.id.level1_3);
        score[1] = (TextView) findViewById(R.id.level2_3);
        score[2] = (TextView) findViewById(R.id.level3_3);
        score[3] = (TextView) findViewById(R.id.level4_3);
        score[4] = (TextView) findViewById(R.id.level5_3);
        if (getResources().getConfiguration().orientation ==  2) {
            back_img.setImageResource(R.drawable.background_level_land);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        String[] score_identifier = {"best_first", "best_second", "best_third", "best_fourth",
                "best_fifth"};
        String[] date_identifier = {"best_first_time", "best_second_time",
                "best_third_time", "best_fourth_time", "best_fifth_time"};
        SharedPreferences sharedPreferences = getSharedPreferences("levels", MODE_PRIVATE);
        for (int i = 0; i < 5; i++) {
            int best_score = sharedPreferences.getInt(score_identifier[i], 0);
            if (best_score > 0) {
                date_time[i].setText(sharedPreferences.getString(date_identifier[i], "-"));
                score[i].setText(String.format(Locale.US, "%d", best_score));
            }
        }
    }
    public void back_to_menu(View view) {
        onBackPressed();
    }
}
