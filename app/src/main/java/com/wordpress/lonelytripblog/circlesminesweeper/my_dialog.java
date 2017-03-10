package com.wordpress.lonelytripblog.circlesminesweeper;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
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
        final String out, positive_btn, title;
        if (getTag().equals("game_over")) {
            title = getString(R.string.game_over);
            out = getString(R.string.bad_outcome_out);
            positive_btn = getString(R.string.try_again);
        } else {
            title = getString(R.string.winner);
            out = getString(R.string.congratulation);
            positive_btn = getString(R.string.continue_str);
        }
        builder.setMessage(out)
                .setTitle(title)
                .setPositiveButton(positive_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!getTag().equals("game_over")) {
                            ((game) getActivity()).set_new_level();
                        } else {
                            while (!((game) getActivity()).set_circles());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.back_to_menu), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        getActivity().onBackPressed();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
