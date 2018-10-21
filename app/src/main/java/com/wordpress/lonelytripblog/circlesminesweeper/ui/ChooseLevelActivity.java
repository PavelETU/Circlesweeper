package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.FullWindowUtils;

public class ChooseLevelActivity extends AppCompatActivity
        implements CustomLevelDialogFragment.CustomLevelDialogCallback {

    private static final int SAVED_GAME_LEVEL = -1;
    private static final int LAST_LEVEL = 6;
    private static final int AMOUNT_OF_ALWAYS_OPENED_LEVELS = 1;
    private SharedPreferences cachedSharedPref = null;
    private final Button[] levelButtons = new Button[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        FullWindowUtils.enterFullScreenMode(getWindow());
        levelButtons[0] = findViewById(R.id.first);
        levelButtons[1] = findViewById(R.id.second);
        levelButtons[2] = findViewById(R.id.third);
        levelButtons[3] = findViewById(R.id.fourth);
        levelButtons[4] = findViewById(R.id.fifth);
        levelButtons[5] = findViewById(R.id.sixth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncContinueButtonVisibility();
        syncLevelButtonsWithPastLevels();
        setListenersForLevelButtons();
    }

    private void setListenersForLevelButtons() {
        for (int i = 0; i < levelButtons.length; i++) {
            final int level = i + 1;
            if (!isThisLevelOpensDialog(level)) {
                levelButtons[i].setOnClickListener(view -> openGameActivityWithLevel(level));
            } else {
                levelButtons[i].setOnClickListener(view -> openDialogForCustomLevel());
            }
        }
    }

    private void openGameActivityWithLevel(final int level) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }

    private boolean isThisLevelOpensDialog(int level) {
        return level == LAST_LEVEL;
    }

    public void openDialogForCustomLevel() {
        DialogFragment choose_dialog = new CustomLevelDialogFragment();
        choose_dialog.show(getSupportFragmentManager(), "choose");
    }

    public void continueLastGame(View view) {
        openGameActivityWithLevel(SAVED_GAME_LEVEL);
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
        return getSharedPreferences().getInt("game_saved", 0) == 1;
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
        return getSharedPreferences().getInt("opened_levels", 1);
    }

    private SharedPreferences getSharedPreferences() {
        if (cachedSharedPref == null) {
            cachedSharedPref = getSharedPreferences("levels", MODE_PRIVATE);
        }
        return cachedSharedPref;
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

    private int getBackgroundDependingOnPosition(final int i) {
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

    @Override
    public void onLevelParamsChosen(final int fieldSize, final int amountOfMines) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", LAST_LEVEL);
        intent.putExtra("field_size", fieldSize);
        intent.putExtra("number_of_mines", amountOfMines);
        startActivity(intent);
    }

    @Override
    public void onDismiss() {
    }

}
