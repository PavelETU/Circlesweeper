package com.wordpress.lonelytripblog.circlesminesweeper.ui.activities;

import com.wordpress.lonelytripblog.circlesminesweeper.utils.FullWindowUtils;

import androidx.appcompat.app.AppCompatActivity;

public abstract class FullScreenActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        FullWindowUtils.enterFullScreenMode(getWindow());
    }
}
