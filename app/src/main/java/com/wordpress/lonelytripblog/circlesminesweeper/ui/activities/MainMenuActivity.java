package com.wordpress.lonelytripblog.circlesminesweeper.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

public class MainMenuActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setClickListeners();
    }

    private void setClickListeners() {
        findViewById(R.id.play_btn).setOnClickListener(v -> goToChooseLevelActivity());
        findViewById(R.id.score_btn).setOnClickListener(v -> goToBestScoreActivity());
        findViewById(R.id.how_to_play_btn).setOnClickListener(v -> goToHowToHowToPlayActivity());
    }

    public void goToChooseLevelActivity() {
        Intent intent = new Intent(this, ChooseLevelActivity.class);
        startActivity(intent);
    }

    public void goToBestScoreActivity() {
        Intent intent = new Intent(this, BestScoreActivity.class);
        startActivity(intent);
    }

    public void goToHowToHowToPlayActivity() {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }
}
