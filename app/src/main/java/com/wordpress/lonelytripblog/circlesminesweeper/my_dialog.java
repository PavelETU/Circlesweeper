package com.wordpress.lonelytripblog.circlesminesweeper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Павел on 08.11.2016.
 */
public class my_dialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String out, positive_btn;
        if (getTag().equals("game_over")) {
            out = "Unfortunately there was a mine. Game over!";
            positive_btn = "Try again";
        } else {
            out = "Congratulation! You win!";
            positive_btn = "Continue";
        }
        builder.setMessage(out)
                .setPositiveButton(positive_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((game)getActivity()).set_circles(24);
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        getActivity().onBackPressed();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
