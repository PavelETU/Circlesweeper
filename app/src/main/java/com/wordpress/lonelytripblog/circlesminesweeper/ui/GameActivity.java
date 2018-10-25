package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

public class GameActivity extends AppCompatActivity implements
        CustomLevelDialogFragment.CustomLevelDialogCallback {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        /* Under active development. Check out previous git commits */
    }

    @Override
    public void onLevelParamsChosen(int fieldSize, int amountOfMines) {

    }

    @Override
    public void onDismiss() {
        onBackPressed();
    }

}
