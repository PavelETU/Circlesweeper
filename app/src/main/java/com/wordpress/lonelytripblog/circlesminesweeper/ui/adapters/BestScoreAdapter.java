package com.wordpress.lonelytripblog.circlesminesweeper.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wordpress.lonelytripblog.circlesminesweeper.R;
import com.wordpress.lonelytripblog.circlesminesweeper.data.Score;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BestScoreAdapter extends RecyclerView.Adapter<BestScoreAdapter.BestScoreViewHolder> {
    private static final int AMOUNT_OF_TITLES = 1;
    private final List<Score> scores;

    public BestScoreAdapter(List<Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public BestScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_score, parent, false);
        return new BestScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestScoreViewHolder holder, int position) {
        if (position < AMOUNT_OF_TITLES) {
            holder.firstRow.setText(R.string.level);
            holder.secondRow.setText(R.string.date_and_time);
            holder.thirdRow.setText(R.string.score);
        } else {
            int positionInLevelsList = position - AMOUNT_OF_TITLES;
            holder.firstRow.setText(String.valueOf(scores.get(positionInLevelsList).getLevel()));
            holder.secondRow.setText(scores.get(positionInLevelsList).getDateAndTime());
            holder.thirdRow.setText(String.valueOf(scores.get(positionInLevelsList).getScore()));
        }
    }

    @Override
    public int getItemCount() {
        return scores.size() + AMOUNT_OF_TITLES;
    }

    static class BestScoreViewHolder extends RecyclerView.ViewHolder {

        final TextView firstRow;
        final TextView secondRow;
        final TextView thirdRow;

        BestScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            firstRow = itemView.findViewById(R.id.first);
            secondRow = itemView.findViewById(R.id.second);
            thirdRow = itemView.findViewById(R.id.third);
        }
    }
}
