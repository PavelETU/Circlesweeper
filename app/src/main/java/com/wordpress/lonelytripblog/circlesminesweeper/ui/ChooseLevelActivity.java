package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.FullWindowUtils;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.ChooseLevelViewModel;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

public class ChooseLevelActivity extends DaggerAppCompatActivity {

    private static final int AMOUNT_OF_ALWAYS_OPENED_LEVELS = 1;
    private final Button[] levelButtons = new Button[6];
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ChooseLevelViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        levelButtons[0] = findViewById(R.id.first);
        levelButtons[1] = findViewById(R.id.second);
        levelButtons[2] = findViewById(R.id.third);
        levelButtons[3] = findViewById(R.id.fourth);
        levelButtons[4] = findViewById(R.id.fifth);
        levelButtons[5] = findViewById(R.id.sixth);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ChooseLevelViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullWindowUtils.enterFullScreenMode(getWindow());
        syncContinueButtonVisibility();
        syncLevelButtonsWithLevelsAmount();
        syncLevelButtonsWithPastLevels();
        setListenersForLevelButtons();
    }

    private void setListenersForLevelButtons() {
        for (int i = 0; i < levelButtons.length; i++) {
            final int level = i + 1;
            levelButtons[i].setOnClickListener(view -> openGameActivityWithLevel(level));
        }
    }

    private void openGameActivityWithLevel(int level) {
        viewModel.setLevel(level);
        goToGameActivity();
    }

    public void continueLastGame(View view) {
        viewModel.openSavedLevel();
        goToGameActivity();
    }

    private void goToGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void syncContinueButtonVisibility() {
        final Button continueButton = findViewById(R.id.continue_btn);
        if (gameWasSaved()) {
            continueButton.setVisibility(View.VISIBLE);
        } else {
            continueButton.setVisibility(View.INVISIBLE);
        }
    }

    private boolean gameWasSaved() {
        return viewModel.gameWasSaved();
    }

    private void syncLevelButtonsWithLevelsAmount() {
        int levelsAmountZeroBased = viewModel.getLevelsAmount() - 1;
        for (int i = 0; i < levelButtons.length; i++) {
            int visibilityToSet;
            if (i <= levelsAmountZeroBased) {
                visibilityToSet = View.VISIBLE;
            } else {
                visibilityToSet = View.INVISIBLE;
            }
            levelButtons[i].setVisibility(visibilityToSet);
        }
    }

    private void syncLevelButtonsWithPastLevels() {
        int openedLevels = getAmountOfOpenedLevels();
        for (int i = AMOUNT_OF_ALWAYS_OPENED_LEVELS; i < levelButtons.length; i++) {
            if (i >= openedLevels) {
                levelButtons[i].setEnabled(false);
                levelButtons[i].setBackgroundResource(getBackgroundWithLockDependingOnPosition(i));
            } else {
                final int level = i + 1;
                levelButtons[i].setEnabled(true);
                levelButtons[i].setText(String.valueOf(level));
                levelButtons[i].setBackgroundResource(getBackgroundDependingOnPosition(i));
            }
        }
    }

    private int getAmountOfOpenedLevels() {
        return viewModel.getLastLevelNumber();
    }

    private int getBackgroundWithLockDependingOnPosition(final int i) {
        switch (i) {
            case 1:
                return R.drawable.purple_ball_with_lock;
            case 2:
                return R.drawable.red_ball_with_lock;
            case 3:
                return R.drawable.orange_ball_with_lock;
            case 4:
                return R.drawable.yellow_ball_with_lock;
            case 5:
                return R.drawable.green_ball_with_lock;
            default:
                throw new UnsupportedOperationException("Not defined level");

        }
    }

    private int getBackgroundDependingOnPosition(int i) {
        switch (i) {
            case 1:
                return R.drawable.purple_ball;
            case 2:
                return R.drawable.red_ball;
            case 3:
                return R.drawable.orange_ball;
            case 4:
                return R.drawable.yellow_ball;
            case 5:
                return R.drawable.green_ball;
            default:
                throw new UnsupportedOperationException("Not defined level");

        }
    }

}
