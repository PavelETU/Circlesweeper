package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.ui.adapters.BestScoreAdapter;
import com.wordpress.lonelytripblog.circlesminesweeper.utils.FullWindowUtils;
import com.wordpress.lonelytripblog.circlesminesweeper.viewmodel.BestScoreViewModel;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.DaggerAppCompatActivity;

public class BestScoreActivity extends DaggerAppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_score);
        FullWindowUtils.enterFullScreenMode(getWindow());
        BestScoreViewModel viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(BestScoreViewModel.class);
        TextView title = findViewById(R.id.level_scores_text);
        RecyclerView scoresList = findViewById(R.id.level_scores_list);
        scoresList.setLayoutManager(new LinearLayoutManager(this));
        viewModel.getScores().observe(this, scores -> {
            if (scores == null || scores.isEmpty()) {
                title.setText(R.string.no_score_for_you);
            } else {
                title.setText(R.string.table_of_best_score);
                scoresList.setAdapter(new BestScoreAdapter(scores));
            }
        });
    }

}
