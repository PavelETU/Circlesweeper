package com.wordpress.lonelytripblog.circlesminesweeper.ui.activities;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.GameCell;
import com.wordpress.lonelytripblog.circlesminesweeper.di.InjectMe;
import com.wordpress.lonelytripblog.circlesminesweeper.ui.CustomLevelDialogFragment;
import com.wordpress.lonelytripblog.circlesminesweeper.ui.GameView;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.FullWindowUtils;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModel;

import javax.inject.Inject;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import static com.wordpress.lonelytripblog.circlesminesweeper.ui.CustomLevelDialogFragment.TAG_IN_BACKSTACK;

public class GameActivity extends FullScreenActivity
        implements CustomLevelDialogFragment.CustomLevelDialogCallback, InjectMe {

    private GameView gameView;
    private GameViewModel viewModel;
    private Observer<GameCell[][]> observer;
    private LiveData<GameCell[][]> gameCellsLiveData;
    @Inject
    ViewModelProvider.Factory factory;
    private Snackbar snackbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        viewModel = ViewModelProviders.of(this, factory).get(GameViewModel.class);
        gameView = findViewById(R.id.game_image);
        observer = gameView::setGameCells;
        gameView.setCallbackForSizeChange(this::reobserveGameCellsForSize);
        gameView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    viewModel.actionDown((int) event.getX(), (int) event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    viewModel.actionMove((int) event.getX(), (int) event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    viewModel.actionUp();
                    break;
            }
            return true;
        });
        ImageButton checkButton = findViewById(R.id.checking_btn);
        checkButton.setOnClickListener(v -> viewModel.markClicked());
        viewModel.getCheckButtonSrc().observe(this, checkButton::setImageResource);
        TextView minesText = findViewById(R.id.mine_out);
        viewModel.getMinesToDisplay().observe(this, mines -> minesText.setText(mines.toString()));
        TextView scoreText = findViewById(R.id.score_out);
        viewModel.getScore().observe(this, score -> scoreText.setText(score.toString()));
        TextView textAboutMines = findViewById(R.id.text_about_mines);
        ImageView smileOut = findViewById(R.id.smile_out);
        ImageButton nextRepeatBtn = findViewById(R.id.next_repeat_btn);
        viewModel.getGameCondition().observe(this, gameCondition -> {
            switch (gameCondition) {
                case GameViewModel.GAME_IN_PROCESS:
                    textAboutMines.setVisibility(View.VISIBLE);
                    minesText.setVisibility(View.VISIBLE);
                    checkButton.setVisibility(View.VISIBLE);
                    smileOut.setVisibility(View.INVISIBLE);
                    nextRepeatBtn.setVisibility(View.INVISIBLE);
                    break;
                case GameViewModel.GAME_WON:
                    textAboutMines.setVisibility(View.INVISIBLE);
                    minesText.setVisibility(View.INVISIBLE);
                    checkButton.setVisibility(View.INVISIBLE);
                    smileOut.setVisibility(View.VISIBLE);
                    nextRepeatBtn.setVisibility(View.VISIBLE);
                    smileOut.setImageResource(R.drawable.smile_win);
                    break;
                case GameViewModel.GAME_LOST:
                    textAboutMines.setVisibility(View.INVISIBLE);
                    minesText.setVisibility(View.INVISIBLE);
                    checkButton.setVisibility(View.INVISIBLE);
                    smileOut.setVisibility(View.VISIBLE);
                    nextRepeatBtn.setVisibility(View.VISIBLE);
                    smileOut.setImageResource(R.drawable.smile_lost);
                    break;
                case GameViewModel.SHOW_CUSTOM_LEVEL_DIALOG:
                    showDialogIfNeeded();
                    break;
                default:
                    throw new RuntimeException("Unknown game condition");
            }
        });
        nextRepeatBtn.setOnClickListener(v -> viewModel.nextRepeatClicked());
        viewModel.getScoreToDisplayInGameView().observe(this, gameView::setScoreToDisplay);
        viewModel.getToastEvent().observe(this, integerLiveEvent -> {
            Integer stringResourceForToast = integerLiveEvent.getValueOrNull();
            if (stringResourceForToast != null) {
                showToastWithResource(stringResourceForToast);
            }
        });
        viewModel.getSnackbarMessage().observe(this, stringSrc -> {
            if (stringSrc != null) {
                showSnackBar(stringSrc);
            } else {
                hideSnackBar();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullWindowUtils.enterFullScreenMode(getWindow());
    }

    @Override
    protected void onPause() {
        viewModel.beforeGameGoAway();
        super.onPause();
    }

    private void showDialogIfNeeded() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_IN_BACKSTACK) == null) {
            DialogFragment chooseDialog = new CustomLevelDialogFragment();
            chooseDialog.show(getSupportFragmentManager(), TAG_IN_BACKSTACK);
            chooseDialog.setCancelable(false);
        }
    }

    private void showToastWithResource(int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLevelParamsChosen(int fieldSize, int amountOfMines) {
        viewModel.onCustomLevelParamsChosen(fieldSize, amountOfMines);
    }

    @Override
    public void onDismiss() {
        onBackPressed();
    }

    private void showSnackBar(int resId) {
        snackbar = Snackbar.make(gameView, resId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.got_it, v -> viewModel.onSnackbarMessageClicked());
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(10);
        snackbar.show();
    }

    private void hideSnackBar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
        snackbar = null;
    }

    private void reobserveGameCellsForSize(int newWidth, int newHeight) {
        if (gameCellsLiveData != null && gameCellsLiveData.hasActiveObservers()) {
            gameCellsLiveData.removeObserver(observer);
        }
        gameCellsLiveData = viewModel.getGameCells(newWidth, newHeight);
        gameCellsLiveData.observe(GameActivity.this, observer);
    }

}
