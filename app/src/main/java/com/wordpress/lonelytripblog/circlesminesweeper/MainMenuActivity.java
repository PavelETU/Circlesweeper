package com.wordpress.lonelytripblog.circlesminesweeper;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        enterFullScreenMode();
        setClickListeners();
    }

    private void enterFullScreenMode() {
        if (sdkOlderWhenKitKat()) {
            setFullscreenFlag();
        } else {
            enterImmersiveSticky();
        }
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

    private boolean sdkOlderWhenKitKat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    private void setFullscreenFlag() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private void enterImmersiveSticky() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
