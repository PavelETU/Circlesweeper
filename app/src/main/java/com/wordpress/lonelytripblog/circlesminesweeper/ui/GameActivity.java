package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.di.CircleSweeperApp;
import com.wordpress.lonelytripblog.circlesminesweeper.di.Singletons;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.DefaultLevelFactory;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.FullWindowUtils;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.LevelFactory;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.Mapper;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModel;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModelFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class GameActivity extends AppCompatActivity implements
        CustomLevelDialogFragment.CustomLevelDialogCallback {

    static String EXTRA_LEVEL = "level";
    static String EXTRA_CUSTOM_FIELD_SIZE = "field_size";
    static String EXTRA_CUSTOM_MINES = "number_of_mines";
    private ImageView gameImage;
    private Mapper mapper;
    private LevelFactory levelFactory = new DefaultLevelFactory();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FullWindowUtils.enterFullScreenMode(getWindow());
        int level = getIntent().getIntExtra(EXTRA_LEVEL, -1);
        int fieldSizeCustom = getIntent().getIntExtra(EXTRA_CUSTOM_FIELD_SIZE, -1);
        int minesCustom = getIntent().getIntExtra(EXTRA_CUSTOM_MINES, -1);
        GameViewModelFactory factory = new GameViewModelFactory((CircleSweeperApp) getApplication(),
                new CellsGeneratorImpl(), new Handler(Looper.getMainLooper()));
        GameViewModel viewModel = ViewModelProviders.of(this, factory).get(GameViewModel.class);
        gameImage = findViewById(R.id.game_image);
        gameImage.post(() -> {
            int width = gameImage.getWidth();
            int height = gameImage.getHeight();
            mapper = Singletons.getMapperWithForSize((CircleSweeperApp) getApplication());
            viewModel.setLevel(levelFactory.makeLevel(level, fieldSizeCustom, minesCustom));
            mapper.getGameImageLiveData(viewModel.getGameCells(width, height), width, height).observe(GameActivity.this,
                    gameImage::setImageBitmap);
        });
        gameImage.setOnTouchListener((v, event) -> {
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
                default:
                    throw new RuntimeException("Unknown game condition");
            }
        });
    }

    @Override
    public void onLevelParamsChosen(int fieldSize, int amountOfMines) {

    }

    @Override
    public void onDismiss() {
        onBackPressed();
    }

}
