package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.os.Bundle;
import android.widget.ImageView;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.CellsGeneratorImpl;
import com.wordpress.lonelytripblog.circlesminesweeper.data.levels.FirstLevel;
import com.wordpress.lonelytripblog.circlesminesweeper.di.CircleSweeperApp;
import com.wordpress.lonelytripblog.circlesminesweeper.di.Singletons;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.FullWindowUtils;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.Mapper;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModel;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.GameViewModelFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class GameActivity extends AppCompatActivity implements
        CustomLevelDialogFragment.CustomLevelDialogCallback {

    private ImageView gameImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FullWindowUtils.enterFullScreenMode(getWindow());
        GameViewModelFactory factory = new GameViewModelFactory((CircleSweeperApp) getApplication(),
                new CellsGeneratorImpl());
        GameViewModel viewModel = ViewModelProviders.of(this, factory).get(GameViewModel.class);
        gameImage = findViewById(R.id.game_image);
        gameImage.post(() -> {
            int width = gameImage.getWidth();
            int height = gameImage.getHeight();
            Mapper mapper = Singletons.getMapperWithForSize((CircleSweeperApp) getApplication(),
                    width, height);
            viewModel.setSizeOfGameWindow((int) mapper.getInitialGameWindowWidth(),
                    (int) mapper.getInitialGameWindowHeight());
            viewModel.setLevel(new FirstLevel());
            mapper.getGameImageLiveData(viewModel.getGameCells()).observe(GameActivity.this,
                    gameImage::setImageBitmap);
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
