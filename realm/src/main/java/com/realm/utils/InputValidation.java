package com.realm.utils;


import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.realm.R;


public class InputValidation {

    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean containsNumeric(String str) {
        for (char cct : str.toCharArray()) {

            try {
                Double.parseDouble(cct + "");
                return true;
            } catch (NumberFormatException e) {

            }
        }
        return false;

    }

    protected void set_text_error(EditText edt, String error) {
        edt.setError(error);
        edt.setBackground(edt.getContext().getDrawable(R.drawable.textback_error));
        edt.requestFocus();


        Toast.makeText(edt.getContext(), error, Toast.LENGTH_LONG).show();

    }

    protected boolean set_conditional_input_error(boolean valid, View edt, String error, String input, int min_length) {
        if (input == null || input.length() < min_length) {
            try {
                if (edt.getClass().isInstance(new AppCompatEditText(edt.getContext()))) {
                    ((AppCompatEditText) edt).setError(error);

                }
                edt.setBackground(edt.getContext().getDrawable(R.drawable.textback_error));
                edt.requestFocus();
            } catch (Exception ex) {
            }
            if (error != null) {
                Toast.makeText(edt.getContext(), error, Toast.LENGTH_LONG).show();

            }
            valid = false;
            return valid;
        } else {
            if (edt.getClass().isInstance(new AppCompatEditText(edt.getContext()))) {
                edt.setBackground(edt.getContext().getResources().getDrawable(R.drawable.textback));
                ((AppCompatEditText) edt).setError(null);

            }

        }

        return valid;
    }



}
