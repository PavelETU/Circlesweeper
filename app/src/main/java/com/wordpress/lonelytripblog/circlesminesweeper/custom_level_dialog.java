package com.wordpress.lonelytripblog.circlesminesweeper;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Павел on 13.11.2016.
 */
public class custom_level_dialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialog_view =  layoutInflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialog_view);
        final EditText number = (EditText) dialog_view.findViewById(R.id.number_of_mines);
        final RadioGroup rg = (RadioGroup) dialog_view.findViewById(R.id.radioGroup);
        final InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        final Context context = getActivity().getApplicationContext();
        final int durations = Toast.LENGTH_SHORT;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!number.getText().toString().isEmpty()) {
                    final int number_of_mines = Integer.parseInt(number.getText().toString());
                    if (rg.getCheckedRadioButtonId() == R.id.radioButton) {
                        if (number_of_mines > 4) {
                            Toast.makeText(context, getString(R.string.valid_value_for_mines_1), durations).show();
                            number.setText(String.format(Locale.US,"%d",4));
                        }
                    }
                    if (rg.getCheckedRadioButtonId() == R.id.radioButton2) {
                        if (number_of_mines > 8) {
                            Toast.makeText(context, getString(R.string.valid_value_for_mines_2), durations).show();
                            number.setText(String.format(Locale.US,"%d",8));
                        }
                    }
                    if (rg.getCheckedRadioButtonId() == R.id.radioButton3) {
                        if (number_of_mines > 20) {
                            Toast.makeText(context, getString(R.string.valid_value_for_mines_3), durations).show();
                            number.setText(String.format(Locale.US,"%d",20));
                        }
                    }
                }
            }
        });
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    final int number_of_mines = Integer.parseInt(editable.toString());
                    if (rg.getCheckedRadioButtonId() == R.id.radioButton) {
                        if (number_of_mines > 4) {
                            Toast.makeText(context, getString(R.string.valid_value_for_mines_1), durations).show();
                            number.setText(String.format(Locale.US,"%d",4));
                        }
                    }
                    if (rg.getCheckedRadioButtonId() == R.id.radioButton2) {
                        if (number_of_mines > 8) {
                            Toast.makeText(context, getString(R.string.valid_value_for_mines_2), durations).show();
                            number.setText(String.format(Locale.US,"%d",8));
                        }
                    }
                    if (rg.getCheckedRadioButtonId() == R.id.radioButton3) {
                        if (number_of_mines > 20) {
                            Toast.makeText(context, getString(R.string.valid_value_for_mines_3), durations).show();
                            number.setText(String.format(Locale.US,"%d",20));
                        }
                    }
                }
            }
        };
        number.addTextChangedListener(tw);

        number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ) {
                    mgr.hideSoftInputFromWindow(number.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        builder//.setMessage(out)
                //.setTitle(title)
                .setPositiveButton("Play", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int field_size = 1;
                        int number_of_mines;
                        if (number.getText().toString().isEmpty()) {
                            number_of_mines = 4;
                        } else {
                            number_of_mines = Integer.parseInt(number.getText().toString());
                        }
                        if (rg.getCheckedRadioButtonId() == R.id.radioButton) {
                            field_size = 1;
                        } else {
                            if (rg.getCheckedRadioButtonId() == R.id.radioButton2) {
                                field_size = 2;
                            } else {
                                if (rg.getCheckedRadioButtonId() == R.id.radioButton3) {
                                    field_size = 3;
                                }
                            }
                        }
                        if (getTag().equals("choose")) {
                            ((choose_level) getActivity()).set_custom(field_size, number_of_mines);
                        } else {
                            ((game) getActivity()).set_custom(field_size, number_of_mines);
                        }
                    }
                })
                .setNegativeButton("Back to menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (getTag().equals("game")) {
                            getActivity().onBackPressed();
                        }
                        // User cancelled the dialog
                        //getActivity().onBackPressed();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
