package com.realm.utils.FormTools;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Calendar;

import com.realm.R;
import com.realm.utils.Conversions;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.ValidationRules;

public class FormEdittext extends ConstraintLayout {
    TextView title, mandatoryIndicator;
    AutoCompleteTextView inputText;
    ImageView icon;
    OnClickListener onIconClickCallback;
    InputField inputField = new InputField();
    Calendar inputCalendar = null;

    public FormEdittext(Context context) {
        super(context);
        setupUI();
    }

    public FormEdittext(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FormEdittext,
                0, 0);

        setTitle(a.getString(R.styleable.FormEdittext_title));
        setPlaceholder(a.getString(R.styleable.FormEdittext_placeholder));
        setMandatory(a.getBoolean(R.styleable.FormEdittext_mandatory, false));

        a.recycle();
    }

    public FormEdittext(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public FormEdittext(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI();
    }

    void setupUI() {
        title = new TextView(getContext(), null, R.style.Theme_Realm_SubTitle);
        title.setText("Date of birth");

//        title.setBackgroundColor(Color.RED);
        title.setTypeface(null, Typeface.BOLD);
        int title_id = View.generateViewId();
        title.setId(title_id);
        mandatoryIndicator = new TextView(getContext());
        mandatoryIndicator.setText("*");
        mandatoryIndicator.setTextColor(getContext().getColor(R.color.gold));
        mandatoryIndicator.setTypeface(null, Typeface.BOLD_ITALIC);
        inputText = new AutoCompleteTextView(getContext());
        inputText.setHint("dd-mm-yyyy");

        //inputText.setBackgroundColor(Color.BLUE);
        int text_id = View.generateViewId();
        inputText.setId(text_id);
        icon = new ImageView(getContext() );
        icon.setImageDrawable(getContext().getDrawable(R.drawable.calendar_vector_icon));
        int icon_id = View.generateViewId();
        icon.setId(icon_id);
        icon.setVisibility(GONE);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topToTop = PARENT_ID;
        params.startToStart = PARENT_ID;
        params.bottomMargin = 0;
        addView(title, params);

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topToTop = PARENT_ID;
        params.endToEnd = PARENT_ID;
        addView(mandatoryIndicator, params);

        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 0;

        params.topToBottom = title_id;
        params.endToEnd = PARENT_ID;
        params.startToEnd = icon_id;
        addView(inputText, params);
        int padding_in_dp = 6;  // 6 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        inputText.setPadding(inputText.getPaddingLeft(), 0, inputText.getPaddingRight(), inputText.getPaddingBottom());

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        params.topToTop = text_id;
        params.bottomToBottom = text_id;
        params.startToStart = PARENT_ID;

        addView(icon, params);
        inputText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    inputField.inputValid=isInputValid();
                    inputField.input=inputText.getText().toString();
                    inputListener.onInputAvailable(isInputValid(), inputText.getText().toString());
                    if(inputField.input!=null&&!inputField.input.equals(inputText.getText().toString())){

                    }
//                    inputValid();
                }
            }
        });
//        title.setTextSize(dpToPx(20));
//        inputText.setTextSize(dpToPx(18));
    }

    public String getInput() {
        return inputText.getText().toString();

    }

    public void setInput(String input) {
        inputText.setText(input);
        inputListener.onInputAvailable(isInputValid(), inputText.getText().toString());

    }

    public AutoCompleteTextView getInputView() {
        return inputText;

    }
    public void setIconTint(int color){
        icon.setImageTintList(ColorStateList.valueOf(color));
    }
    public void setTitle(String title_) {
        this.title.setText(title_);

    }

    public void setPlaceholder(String placeholder) {
        this.inputText.setHint(placeholder);

    }

    public void setClickableIcon(Drawable drawable, OnClickListener onIconClickCallback) {
        icon.setVisibility(drawable == null ? GONE : VISIBLE);

        this.icon.setImageDrawable(drawable);
        this.icon.setOnClickListener(onIconClickCallback);
        this.onIconClickCallback = onIconClickCallback;
        if (!manualInputEnabled) {
            this.inputText.setOnClickListener(onIconClickCallback);

        }

    }



    public void setInputType(int input_type) {
        switch (InputField.InputType.values()[input_type]) {
            case Date:
                inputCalendar = Calendar.getInstance();
                setClickableIcon(getContext().getDrawable(R.drawable.calendar_vector_icon), view -> pickDate());
                break;
            case Time:
                inputCalendar = Calendar.getInstance();
                setClickableIcon(getContext().getDrawable(R.drawable.calendar_vector_icon), view -> pickTime());
                break;
            case DateTime:
                inputCalendar = Calendar.getInstance();
                setClickableIcon(getContext().getDrawable(R.drawable.calendar_vector_icon), view -> pickDateTime());
                break;
            case BarcodeScanText:
                setClickableIcon(getContext().getDrawable(R.drawable.ic_qr_scan), new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                break;
            case Number:
                setClickableIcon(getContext().getDrawable(R.drawable.ic_baseline_format_list_numbered_24), new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;

        }


    }

    private void setMandatory(boolean mandatory) {
        this.mandatoryIndicator.setVisibility(mandatory ? VISIBLE : GONE);
        inputField.validationRules.mandatory = mandatory ? ValidationRules.MandatoryStatus.Mandatory.ordinal() + "" : ValidationRules.MandatoryStatus.NonMandatory.ordinal() + "";

    }

    boolean manualInputEnabled = true;

    private void enableManualInput(boolean enable) {
        manualInputEnabled = enable;
        //this.inputText.setEnabled(enable);
        this.inputText.setFocusable(enable);
        this.inputText.setFocusableInTouchMode(enable);
        this.inputText.setLongClickable(enable);
        if (!enable) {
            this.inputText.setOnClickListener(onIconClickCallback);

        }else{
            this.inputText.setOnClickListener(null);

        }
    }

    public void enableCopyPasting(boolean enable) {
        this.inputText.setLongClickable(enable);
    }

    public void reset() {
        setClickableIcon(null, null);
        inputText.setInputType(0);
        inputText.setKeyListener(null);
        inputText.setFocusable(true);
        inputText.setFocusableInTouchMode(true);
        this.inputText.setOnClickListener(null);
        this.inputText.setLongClickable(true);
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setInputField(InputField inputField, InputListener inputListener) {
        reset();
        this.inputField = inputField;
        this.inputListener = inputListener;
        switch (InputField.InputType.values()[Integer.parseInt(inputField.input_type)]) {
            case Date:
                inputCalendar = Calendar.getInstance();
                setClickableIcon(getContext().getDrawable(R.drawable.calendar_vector_icon), view -> pickDate());
                break;
            case Time:
                inputCalendar = Calendar.getInstance();
                setClickableIcon(getContext().getDrawable(R.drawable.calendar_vector_icon), view -> pickTime());
                break;
            case DateTime:
                inputCalendar = Calendar.getInstance();
                setClickableIcon(getContext().getDrawable(R.drawable.calendar_vector_icon), view -> pickDateTime());
                break;
            case BarcodeScanText:
                setClickableIcon(getContext().getDrawable(R.drawable.ic_qr_scan), new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                break;
            case Number:
                setClickableIcon(getContext().getDrawable(R.drawable.ic_baseline_format_list_numbered_24), new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;

        }
        setTitle(inputField.title);
        setPlaceholder(inputField.placeholder);
        setValidationRules(inputField.validationRules);
//        setInput(inputField.input);
        inputText.setText(inputField.input);
    }

    public void setInputField(InputField inputField) {
        setInputField(inputField, inputListener);
    }

    public void setValidationRules(ValidationRules validationRules) {
//        this.validationRules = validationRules;
        enableManualInput(validationRules.allow_manual_input.equals("1"));
        setMandatory(validationRules.mandatory.equals("" + ValidationRules.MandatoryStatus.Mandatory.ordinal()));
        if (validationRules.max_text_input_length != null) {
            inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(validationRules.max_text_input_length))});
        }
        switch (ValidationRules.TextInputType.values()[Integer.parseInt(validationRules.text_input_type)]) {
            case None:
                break;
            case alphanumeric:
                if (validationRules.max_text_input_length != null) {
                    inputText.setFilters(new InputFilter[]{alphanumericInputFilter,new InputFilter.LengthFilter(Integer.parseInt(validationRules.max_text_input_length))});
                }else{
                    inputText.setFilters(new InputFilter[]{alphanumericInputFilter});

                }

//                inputText.setKeyListener(DigitsKeyListener.getInstance("~+()-_/.çéâêîôûàèìòùëïüqwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890 "));
//            inputText.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(50)});
//            inputText.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(50)});
                inputText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case email:
                inputText.setKeyListener(DigitsKeyListener.getInstance("~+-_.çéâêîôûàèìòùëïüqwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"));
                inputText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case numeric:

                if (validationRules.max_text_input_length != null) {
                    inputText.setFilters(new InputFilter[]{numericInputFilter,new InputFilter.LengthFilter(Integer.parseInt(validationRules.max_text_input_length))});
                }else{
                    inputText.setFilters(new InputFilter[]{numericInputFilter});
                }
//                inputText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                inputText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case uppercase:
                if (validationRules.max_text_input_length != null) {
                    inputText.setFilters(new InputFilter[]{upperCaseInputFilter,new InputFilter.LengthFilter(Integer.parseInt(validationRules.max_text_input_length))});
                }else{
                    inputText.setFilters(new InputFilter[]{upperCaseInputFilter});
                }
//                 inputFilters.add(new InputFilter.AllCaps());
//                      inputText.setKeyListener(DigitsKeyListener.getInstance("~+()-_/.QWERTYUIOPASDFGHJKLZXCVBNM1234567890 "));
                inputText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                break;
            case lowercase:
                if (validationRules.max_text_input_length != null) {
                    inputText.setFilters(new InputFilter[]{lowerCaseInputFilter,new InputFilter.LengthFilter(Integer.parseInt(validationRules.max_text_input_length))});
                }else{
                    inputText.setFilters(new InputFilter[]{lowerCaseInputFilter});
                }

//                inputText.setKeyListener(DigitsKeyListener.getInstance("~+()-_/.çéâêîôûàèìòùëïüqwertyuiopasdfghjklzxcvbnm1234567890 "));
                inputText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case capWords:
                if (validationRules.max_text_input_length != null) {
                    inputText.setFilters(new InputFilter[]{alphanumericInputFilter,new InputFilter.LengthFilter(Integer.parseInt(validationRules.max_text_input_length))});
                }else{
                    inputText.setFilters(new InputFilter[]{alphanumericInputFilter});
                }
//                inputText.setKeyListener(DigitsKeyListener.getInstance("~+()-_/.çéâêîôûàèìòùëïüqwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890 "));
                inputText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                break;
            case Date:
            case Time:
            case DateTime:
                if (validationRules.max_text_input_length != null) {
                    inputText.setFilters(new InputFilter[]{dateInputFilter,new InputFilter.LengthFilter(Integer.parseInt(validationRules.max_text_input_length))});
                }else{
                    inputText.setFilters(new InputFilter[]{dateInputFilter});
                }
                inputText.setInputType(InputType.TYPE_CLASS_DATETIME);
//                inputText.setKeyListener(DigitsKeyListener.getInstance("-/:1234567890 "));
//                new DateInputMask(inputText, validationRules.date_time_input_format);
                break;

        }
    }

    InputFilter upperCaseInputFilter = new ContainsInputFilter("~+()-_/.QWERTYUIOPASDFGHJKLZXCVBNM1234567890 ");
    InputFilter lowerCaseInputFilter = new ContainsInputFilter("~+()-_/.çéâêîôûàèìòùëïüqwertyuiopasdfghjklzxcvbnm1234567890 ");
    InputFilter dateInputFilter = new ContainsInputFilter("-/:1234567890 ");
    InputFilter numericInputFilter = new ContainsInputFilter("0123456789.");
    InputFilter alphanumericInputFilter = new ContainsInputFilter("@~+()-_/.çéâêîôûàèìòùëïüqwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890 ");

    public interface InputListener {
        default void onInputAvailable(boolean valid, String input) {

        }


    }

    InputListener inputListener = new InputListener() {
        @Override
        public void onInputAvailable(boolean valid, String input) {

        }
    };

    public boolean isInputValid() {
        boolean valid = true;
        if (inputField == null || inputField.validationRules.mandatory == null || !inputField.validationRules.mandatory.equals(ValidationRules.MandatoryStatus.Mandatory.ordinal() + "")) {
            return true;
        }
        if (inputField.validationRules.mandatory.equals(ValidationRules.MandatoryStatus.Mandatory.ordinal() + "")) {
            if (inputText.getText().toString().trim().length() < 1) {
                return setError(inputField.validationRules.min_text_input_length_error);
            }
        }
        if (inputField.validationRules.min_text_input_length != null) {
            if (inputText.getText().toString().trim().length() < Integer.parseInt(inputField.validationRules.min_text_input_length)) {
                return setError(inputField.validationRules.min_text_input_length_error);
            }
        }
        if (ValidationRules.TextInputType.values()[Integer.parseInt(inputField.validationRules.text_input_type)] == ValidationRules.TextInputType.numeric) {
            long input;

            try {
                input = Long.parseLong(inputText.getText().toString());
            } catch (NumberFormatException numberFormatException) {
                return setError(inputField.validationRules.numeric_input_error);
            }
            if (inputField.validationRules.min_input_value != null) {
                long min_input = Long.parseLong(inputField.validationRules.min_input_value);
                valid = input < min_input ? setError(inputField.validationRules.min_input_value_error) : valid;
            }
            if (inputField.validationRules.max_input_value != null) {
                long max_input = Long.parseLong(inputText.getText().toString());
                valid = input > max_input ? setError(inputField.validationRules.max_input_value_error) : valid;
            }
        }
        if (valid) {
            inputText.setError(null);
            inputText.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }

        return valid;
    }

    boolean setError(String error) {
        inputText.setError(error);
        inputText.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
//        inputText.requestFocus();
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        return false;
    }


    void pickTime() {
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                //   date.set(Calendar.MINUTE, minute);
                String dom = "" + hourOfDay;
                String moy = "" + minute;
                char[] domch = dom.toCharArray();
                char[] moych = moy.toCharArray();

                if (domch.length < 2) {
                    dom = "0" + dom;
                }
                if (moych.length < 2) {
                    moy = "0" + moy;
                }
                inputText.setText(inputText.getText().toString() + " " + dom + ":" + moy + ":00");
                try {
                    inputCalendar.setTime(Conversions.sdf_user_friendly_time.parse(inputText.getText().toString()));
                } catch (Exception ex) {
                }


            }
        }, inputCalendar.get(Calendar.HOUR_OF_DAY), inputCalendar.get(Calendar.MINUTE), false).show();
    }

    void pickDate() {
        Calendar cc = Calendar.getInstance();
        int mYear = cc.get(Calendar.YEAR);
        int mMonth = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);


        try {


            mYear = inputCalendar.get(Calendar.YEAR);
            mMonth = inputCalendar.get(Calendar.MONTH);
            mDay = inputCalendar.get(Calendar.DAY_OF_MONTH);

        } catch (Exception ex) {
        }

        // Launch Date Picker Dialog..Continuous reg
        DatePickerDialog dpd = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        setInput((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + year);


                        try {
                            inputCalendar.setTime(Conversions.sdf_user_friendly_date.parse(inputText.getText().toString()));
                        } catch (Exception ex) {
                        }


                    }
                }, mYear, mMonth, mDay);
        dpd.show();
        Calendar calendar_min = Calendar.getInstance();
        calendar_min.set(Calendar.YEAR, calendar_min.get(Calendar.YEAR));
    }

    void pickDateTime() {
        Calendar cc = Calendar.getInstance();
        int mYear = cc.get(Calendar.YEAR);
        int mMonth = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);


        try {


            mYear = inputCalendar.get(Calendar.YEAR);
            mMonth = inputCalendar.get(Calendar.MONTH);
            mDay = inputCalendar.get(Calendar.DAY_OF_MONTH);

        } catch (Exception ex) {
        }

        // Launch Date Picker Dialog..Continuous reg
        DatePickerDialog dpd = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        inputText.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + year);


                        try {
                            inputCalendar.setTime(Conversions.sdf_user_friendly_date.parse(inputText.getText().toString()));
                        } catch (Exception ex) {
                        }
                        pickTime();


                    }
                }, mYear, mMonth, mDay);
        dpd.show();
        Calendar calendar_min = Calendar.getInstance();
        calendar_min.set(Calendar.YEAR, calendar_min.get(Calendar.YEAR));
    }

    boolean set_conditional_input_error(boolean valid, EditText edt, String error, String input, int min_length) {
        if (input == null || input.length() < min_length) {
            try {
                edt.setError(error);
                edt.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                edt.requestFocus();
            } catch (Exception ex) {
            }
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();

            }
            valid = false;
            return valid;
        } else {
            (edt).setError(null);
            edt.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);


        }

        return valid;
    }

//        final Set<Character> blockSet = new HashSet<>();
//        blockSet.(Arrays.asList('1', '2', '3', '4','5', '6', '7', '8', '9', '0'));
//
//        InputFilter filter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
//                for (int i = start; i < end; i++) {
//                    if (Character.isDigit(source.charAt(i))) {
//                        char[] v = new char[end - start];
//                        TextUtils.getChars(source, start, end, v, 0);
//                        String s = new String(v);
//
//                        if (source instanceof Spanned) {
//                            CharSequence sp = new SpannableString(s);
//                            TextUtils.copySpansFrom((Spanned) source,
//                                    start, end, null, (Spannable) sp, 0);
//
//                            boolean containsDigit = true;
//                            while (containsDigit) {
//                                containsDigit = false;
//                                for (int j = 0; j < sp.length(); j++) {
//                                    if (blockSet.contains(sp.charAt(j))) {
//                                        sp = TextUtils.concat(sp.subSequence(0, j), sp.subSequence(j + 1, sp.length()));
//                                        containsDigit = true;
//                                        break;
//                                    }
//                                }
//                            }
//
//                            return sp;
//                        } else {
//                            s = s.replaceAll("\\d", "");
//                            return s;
//                        }
//                    }
//                }
//                return null;
//            }
//
//
//        };

    public  int dpToPx( float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }
}
