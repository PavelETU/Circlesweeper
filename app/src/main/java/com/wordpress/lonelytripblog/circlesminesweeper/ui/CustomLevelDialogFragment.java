package com.wordpress.lonelytripblog.circlesminesweeper.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wordpress.lonelytripblog.circlesminesweeper.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomLevelDialogFragment extends DialogFragment {

    final static String TAG_IN_BACKSTACK = "choose";
    public final static int FIELD_3_4 = 1;
    public final static int FIELD_4_6 = 2;
    public final static int FIELD_6_10 = 3;
    private CustomLevelDialogCallback eventCallback;
    private EditText numberOfMinesInput;
    private RadioGroup fieldSizeGroup;
    private Button backBtn;
    private Button playBtn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            eventCallback = (CustomLevelDialogCallback) context;
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException("To show customLevelDialogFragment " +
                    "class should implement CustomLevelDialogCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_custom_level, container, false);
        numberOfMinesInput = dialogView.findViewById(R.id.number_of_mines);
        fieldSizeGroup = dialogView.findViewById(R.id.radioGroup);
        backBtn = dialogView.findViewById(R.id.back_to_menu_btn);
        playBtn = dialogView.findViewById(R.id.play_btn);

        trackChangeOfFieldSizeToCorrectInput();
        trackChangeOfMinesAmountToCorrectInput();

        whenTypingIsDoneHideSoftInput();

        setListenersToPlayBackButtons();

        return dialogView;
    }

    private void trackChangeOfFieldSizeToCorrectInput() {
        fieldSizeGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            validateMinesInputForNewFieldSize(checkedId);
        });
    }

    private void trackChangeOfMinesAmountToCorrectInput() {
        final TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    final int minesAmount = Integer.parseInt(editable.toString());
                    verifyInputAndCorrectItAsNeededDisplayingToast(numberOfMinesInput,
                            fieldSizeGroup.getCheckedRadioButtonId(), minesAmount);
                }
            }
        };
        numberOfMinesInput.addTextChangedListener(tw);
    }

    private void whenTypingIsDoneHideSoftInput() {
        numberOfMinesInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                hideSoftInput();
                return true;
            }
            return false;
        });
    }

    private void setListenersToPlayBackButtons() {
        playBtn.setOnClickListener(view -> {
            if (inputNotValid()) {
                displayToast(R.string.specify_mines_amount);
                return;
            }
            eventCallback.onLevelParamsChosen(extractFieldSize(), extractNumberOfMines());
            dismiss();
        });
        backBtn.setOnClickListener(view -> {
            eventCallback.onDismiss();
            dismiss();
        });
    }

    private void validateMinesInputForNewFieldSize(int newFieldSize) {
        if (!numberOfMinesInput.getText().toString().isEmpty()) {
            final int minesAmount = Integer.parseInt(numberOfMinesInput.getText().toString());
            verifyInputAndCorrectItAsNeededDisplayingToast(numberOfMinesInput, newFieldSize, minesAmount);
        }
    }

    private void verifyInputAndCorrectItAsNeededDisplayingToast(@NonNull EditText number,
                                                                int checkedId,
                                                                int amountOfMines) {
        SizeOfTheFieldHelper helper = new SizeOfTheFieldHelper(checkedId);
        if (amountOfMines > helper.minesLimit) {
            displayToast(helper.stringResourceToBeDisplayed);
            number.setText(String.format(Locale.US, "%d", helper.minesLimit));
        }
    }

    private void hideSoftInput() {
        InputMethodManager mgr = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(numberOfMinesInput.getWindowToken(), 0);
    }

    private boolean inputNotValid() {
        return numberOfMinesInput.getText().toString().isEmpty();
    }

    private int extractFieldSize() {
        int field_size = FIELD_3_4;
        if (fieldSizeGroup.getCheckedRadioButtonId() == R.id.radioButton2) {
            field_size = FIELD_4_6;
        } else if (fieldSizeGroup.getCheckedRadioButtonId() == R.id.radioButton3) {
            field_size = FIELD_6_10;
        }
        return field_size;
    }

    private int extractNumberOfMines() {
        int number_of_mines;
        if (numberOfMinesInput.getText().toString().isEmpty()) {
            number_of_mines = 4;
        } else {
            number_of_mines = Integer.parseInt(numberOfMinesInput.getText().toString());
        }
        return number_of_mines;
    }

    private void displayToast(final int messageResId) {
        Toast.makeText(getContext(), messageResId, Toast.LENGTH_SHORT).show();
    }

    private static class SizeOfTheFieldHelper {
        private int stringResourceToBeDisplayed;
        private int minesLimit;

        SizeOfTheFieldHelper(int radioButtonId) {
            switch (radioButtonId) {
                case R.id.radioButton:
                    minesLimit = 4;
                    stringResourceToBeDisplayed = R.string.valid_value_for_mines_1;
                    break;
                case R.id.radioButton2:
                    minesLimit = 8;
                    stringResourceToBeDisplayed = R.string.valid_value_for_mines_2;
                    break;
                case R.id.radioButton3:
                    minesLimit = 20;
                    stringResourceToBeDisplayed = R.string.valid_value_for_mines_3;
                    break;
                default:
                    throw new UnsupportedOperationException("Add level description");
            }
        }
    }

    interface CustomLevelDialogCallback {
        void onLevelParamsChosen(int fieldSize, int amountOfMines);

        void onDismiss();
    }

}
