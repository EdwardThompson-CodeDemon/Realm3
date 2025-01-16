package com.realm.utils.FormTools;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;
import static androidx.constraintlayout.widget.ConstraintSet.VERTICAL_GUIDELINE;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Stopwatch;
import com.shuhart.stepview.StepView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

import com.realm.activities.SpartaAppCompactFingerPrintActivity;
import com.realm.Models.Query;
import com.realm.R;
import com.realm.Realm;
import com.realm.utils.svars;
import com.realm.utils.FormTools.adapters.FormAdapter;
import com.realm.utils.FormTools.models.AppData;
import com.realm.utils.FormTools.models.AppImages;
import com.realm.utils.FormTools.models.Form;
import com.realm.utils.FormTools.models.IndependentInputFieldVariable;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.InputFieldInputConstraint;
import com.realm.utils.FormTools.models.InputFieldInputConstraintProcessingResult;
import com.realm.utils.FormTools.models.InputGroup;
import com.realm.utils.FormTools.models.MemberFingerprint;
import com.realm.utils.FormTools.models.MemberFingerprints;

public class FormPlayer extends ConstraintLayout {

    TextView title, instructions, pageTitle, spacer;
    StepView stepView;
    RecyclerView recyclerView;
    Button previous, next;
    Boolean includePageNumberOnPageTitle;

    Form form;
    InputGroup currentPage = new InputGroup();
    SpartaAppCompactFingerPrintActivity activity;
    InputListener inputListener = new InputListener() {
        @Override
        public void onInputRequested(InputField inputField) {

        }

    };

    public Object registeringObject;


    public interface InputListener {
        void onInputRequested(InputField inputField);

        default void onInputAvailable(InputField inputField) {

        }

 default void onPageChanged(InputGroup inputField,int index) {

        }


    }

    public FormPlayer(@NonNull Context context) {
        super(context);
        Stopwatch stopwatch= Stopwatch.createUnstarted();
        stopwatch.start();
        long before=   System.currentTimeMillis();
        setupUI();
        long after=   System.currentTimeMillis();
        Log.e("FormPlayer","Setup time man:"+(after-before));
        Log.e("FormPlayer","Setup time:"+(stopwatch.elapsed(TimeUnit.MILLISECONDS))+"ms");
        stopwatch.stop();    }

    public FormPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Stopwatch stopwatch= Stopwatch.createUnstarted();
        stopwatch.start();
        long before=   System.currentTimeMillis();
        setupUI();
        long after=   System.currentTimeMillis();
        Log.e("FormPlayer","Setup time man:"+(after-before));
        Log.e("FormPlayer","Setup time:"+(stopwatch.elapsed(TimeUnit.MILLISECONDS))+"ms");
        stopwatch.stop();    }

    public FormPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Stopwatch stopwatch= Stopwatch.createUnstarted();
        stopwatch.start();
        long before=   System.currentTimeMillis();
        setupUI();
        long after=   System.currentTimeMillis();
        Log.e("FormPlayer","Setup time man:"+(after-before));
        Log.e("FormPlayer","Setup time:"+(stopwatch.elapsed(TimeUnit.MILLISECONDS))+"ms");
        stopwatch.stop();    }

    public FormPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Stopwatch stopwatch= Stopwatch.createUnstarted();
        stopwatch.start();
     long before=   System.currentTimeMillis();
        setupUI();
        long after=   System.currentTimeMillis();
        Log.e("FormPlayer","Setup time man:"+(after-before));
        Log.e("FormPlayer","Setup time:"+(stopwatch.elapsed(TimeUnit.MILLISECONDS))+"ms");
        stopwatch.stop();

    }

    void setupUI() {
//        title = new TextView(getContext(), null, com.google.android.material.R.style.TextAppearance_AppCompat_SearchResult_Title);
        title = new TextView(getContext(), null, androidx.appcompat.R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
        title.setText("Input form");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setTypeface(null, Typeface.BOLD);
        int title_id = View.generateViewId();
        title.setId(title_id);

        instructions = new TextView(getContext(), null, R.style.Theme_Realm_Title);
        instructions.setText("Fill in the details below to fill the form");
        instructions.setTextColor(getContext().getColor(com.realm.R.color.gray));
        instructions.setTypeface(null, Typeface.BOLD);
        int instructionsId = View.generateViewId();
        instructions.setId(instructionsId);

        /*
          app:sv_animationType="All"
        app:sv_doneCircleColor="?attr/colorSecondary"
        app:sv_doneCircleRadius="12dp"
        app:sv_doneStepLineColor="?attr/colorSecondary"
        app:sv_doneStepMarkColor="@color/colorPrimary"
        app:sv_nextStepLineColor="@color/grey"
        app:sv_nextTextColor="@color/grey"
        app:sv_selectedCircleColor="?attr/colorSecondary"
        app:sv_selectedCircleRadius="12dp"
        app:sv_selectedStepNumberColor="?attr/colorOnSecondary"
        app:sv_selectedTextColor="?attr/colorOnSecondary"
        app:sv_stepLineWidth="1dp"

        app:sv_stepNumberTextSize="12sp"
        app:sv_stepPadding="4dp"
        app:sv_stepViewStyle="@style/StepView" />
         */
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorOnSecondary, typedValue, true);
        int colorSecondary = typedValue.data;
        typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorOnSecondary, typedValue, true);
        int colorOnSecondary = typedValue.data;
        typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        stepView = new StepView(getContext());
        int stepViewId = View.generateViewId();
        stepView.setId(stepViewId);
//        stepView.setStepsNumber(4);
        stepView.getState()
                .doneCircleColor(colorSecondary)
                .doneStepLineColor(colorSecondary)
                .doneStepMarkColor(colorPrimary)
                .nextStepLineColor(Color.GRAY)
                .nextTextColor(Color.GRAY)
                .selectedCircleColor(colorSecondary)
                .selectedStepNumberColor(colorOnSecondary)
                .animationType(StepView.ANIMATION_ALL)
                .selectedTextColor(colorOnSecondary)
//                .steps(new ArrayList<String>() {{
//                    add("First step");
//                    add("Second step");
//                    add("Third step");
//                }})
                .commit();


        pageTitle = new TextView(getContext(), null, androidx.appcompat.R.style.TextAppearance_AppCompat_Widget_ActionBar_Subtitle);
        pageTitle.setText("Page 1 of x Id registration");
        int pageTitleId = View.generateViewId();
        pageTitle.setId(pageTitleId);

        recyclerView = new RecyclerView(getContext());
        int recyclerVieweId = View.generateViewId();
        recyclerView.setId(recyclerVieweId);

        previous = new Button(getContext());
        previous.setText("< Previous");
        previous.setBackground(getContext().getDrawable(R.drawable.button_negative));
        int previousId = View.generateViewId();
        previous.setId(previousId);

        next = new Button(getContext());
        next.setText("Next >");
        next.setTextColor(Color.WHITE);
        next.setBackground(getContext().getDrawable(R.drawable.button_positive));
        int nextId = View.generateViewId();
        next.setId(nextId);

        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topToTop = PARENT_ID;
        titleParams.startToStart = PARENT_ID;
        titleParams.bottomMargin = 0;
        addView(title, titleParams);

        LayoutParams instructionsParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instructionsParams.topToBottom = title_id;
        instructionsParams.startToStart = PARENT_ID;
        addView(instructions, instructionsParams);

        LayoutParams stepViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stepViewParams.topToBottom = instructionsId;
        stepViewParams.startToStart = PARENT_ID;
        stepViewParams.endToEnd = PARENT_ID;
        addView(stepView, stepViewParams);

        LayoutParams pageTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pageTitleParams.topToBottom = stepViewId;
        pageTitleParams.startToStart = PARENT_ID;
        pageTitleParams.endToEnd = PARENT_ID;
        addView(pageTitle, pageTitleParams);

        LayoutParams recyclerViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerViewParams.topToBottom = pageTitleId;
        recyclerViewParams.startToStart = PARENT_ID;
        recyclerViewParams.endToEnd = PARENT_ID;
//        recyclerViewParams.bottomToTop = previousId;
        recyclerViewParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        addView(recyclerView, recyclerViewParams);

        Guideline guideline = new Guideline(getContext());
        int guidelineId = View.generateViewId();
        guideline.setId(guidelineId);
        LayoutParams guidelineParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        guidelineParams.topToBottom = recyclerVieweId;
        guidelineParams.startToStart = PARENT_ID;
        guidelineParams.endToEnd = PARENT_ID;
        guidelineParams.orientation = VERTICAL_GUIDELINE;
        guidelineParams.guidePercent = 0.5f;
        addView(guideline, guidelineParams);


        LayoutParams previousButtonParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        previousButtonParams.topToBottom = recyclerVieweId;
//        previousButtonParams.bottomToBottom = PARENT_ID;
        previousButtonParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        previousButtonParams.startToStart = PARENT_ID;
        previousButtonParams.endToStart = guidelineId;
        addView(previous, previousButtonParams);


        LayoutParams nextButtonParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        nextButtonParams.topToBottom = recyclerVieweId;
//        nextButtonParams.bottomToBottom = PARENT_ID;
        nextButtonParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        nextButtonParams.startToEnd = guidelineId;
        nextButtonParams.endToEnd = PARENT_ID;
        addView(next, nextButtonParams);
//        recyclerView.setPadding(0, 0, 0, dpToPx(70));
        recyclerView.setClipToPadding(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        formAdapter = new FormAdapter(new FormAdapter.InputListener() {
            @Override
            public void onInputAvailable(InputField inputField) {
                saveTempRegistrationField(inputField);
                inputListener.onInputAvailable(inputField);

            }

            @Override
            public boolean onInputActivityStatusRequired(InputField inputField) {
//                return shouldDisplay(inputField.inputFieldInputConstraint);
                return false;
            }

            @Override
            public InputFieldInputConstraintProcessingResult onInputFieldInputConstraintProcessingResultRequired(InputField inputField) {
                return getInputFieldInputConstraintProcessingResult_(inputField.inputFieldInputConstraints,form.inputGroups);
            }
        });
        formAdapter.setPage(currentPage);
        formAdapter.setActivity(activity);
        recyclerView.setAdapter(formAdapter);

        next.setOnClickListener(view -> {
            next.requestFocus();
            recyclerView.scrollToPosition(formAdapter.getItemCount() - 1);
            prooceed();

        });
        next.setOnFocusChangeListener((view, b) -> {
            if (b) {
                recyclerView.scrollToPosition(formAdapter.getItemCount() - 1);
                next.requestFocus();
                prooceed();
            }
        });
        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                previousPage();
            }
        });
        next.setFocusable(true);
        next.setFocusableInTouchMode(true);

    }

    void run(int n, IntConsumer consumer) {
        for (int i = 0; i < n; i++) {
            consumer.accept(i);
        }
    }

    //    run(10, x -> System.out.println(x+1));
    boolean shouldDisplay(InputFieldInputConstraint inputFieldInputConstraint) {
        switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
            case EqualTo:
//                InputField independentInputField=getInputField(inputFieldInputConstraint.independent_input_field);
//                String independentInput=getField(Globals.registeringMember,independentInputField.object_field_name);
//                return independentInput.equals(inputFieldInputConstraint.operation_value);
                String independent_value = getField(registeringObject, getInputField(inputFieldInputConstraint.independent_input_field).object_field_name);
                return independent_value == null ? false : independent_value.equals(inputFieldInputConstraint.operation_value);

            case NotEqualTo:
                independent_value = getField(registeringObject, getInputField(inputFieldInputConstraint.independent_input_field).object_field_name);

                return independent_value == null ? true : !independent_value.equals(inputFieldInputConstraint.operation_value);

            case ParentChild:
                InputField independentInputField = getInputField(inputFieldInputConstraint.independent_input_field);
                String independentInput = getField(registeringObject, independentInputField.object_field_name);
                break;
        }
        return true;

    }

    public class InputFieldInputConstraintProcessingResult_ {

        public boolean field_active = true;
        public String datasetQuery = "true";
        ArrayList<String> tableFilters = new ArrayList<>();

    }

    InputFieldInputConstraintProcessingResult getInputFieldInputConstraintProcessingResult_(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints_,ArrayList<InputGroup> inputGroups) {
        ArrayList<InputFieldInputConstraint> inputFieldInputConstraints = new ArrayList<>(inputFieldInputConstraints_);
        ArrayList<InputFieldInputConstraint> remainingInputFieldInputConstraints = new ArrayList<>();
        InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult = new InputFieldInputConstraintProcessingResult();
        Boolean equalToFilterCanDisplay = null;
        Boolean notEqualToFilterCanDisplay = null;
        Boolean includeOnlyFilterCanDisplay = null;

        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case EqualTo:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        InputField independentInputField=getInputField_(independentInputFieldVariable.independent_input_field,inputGroups);
                        String independentValue = independentInputField.input;

                        if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
                            try {
                                independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
                            } catch (Exception e) {
                                independentValue=null;
//                            throw new RuntimeException(e);
                            }

                        }
                        if (independentValue.equals(independentInputFieldVariable.operation_value)) {
                            inputFieldInputConstraintProcessingResult.field_active = true;
                            return inputFieldInputConstraintProcessingResult;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;


                        }
                    }
                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
//                        String independentValue = getInputField_(independentInputFieldVariable.independent_input_field,inputGroups).input;
                        InputField independentInputField=getInputField_(independentInputFieldVariable.independent_input_field,inputGroups);
                        String independentValue = independentInputField.input;

                        if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
                            try {
                                independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
                            } catch (Exception e) {
                                independentValue=null;
//                            throw new RuntimeException(e);
                            }

                        }

                        if (independentValue == null) {
                            andFilterOk = false;
                            break;
                        } else if (!independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = false;
                            break;
                        } else if (andFilterOk == null) {
                            andFilterOk = true;

                        }
                    }
                    if (andFilterOk != null && andFilterOk) {
                        inputFieldInputConstraintProcessingResult.field_active = true;
                        return inputFieldInputConstraintProcessingResult;
                    }
                    if (!(orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk)) {
                        equalToFilterCanDisplay = false;
                    } else if (equalToFilterCanDisplay == null) {
                        equalToFilterCanDisplay = true;

                    }

                    break;
                default:
                    remainingInputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        if(equalToFilterCanDisplay!=null){
            inputFieldInputConstraintProcessingResult.field_active = false;
            return inputFieldInputConstraintProcessingResult;
        }
        inputFieldInputConstraints.clear();
        for (InputFieldInputConstraint inputFieldInputConstraint : remainingInputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case NotEqualTo:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field,inputGroups).object_field_name);
//                        String independentValue = getInputField_(independentInputFieldVariable.independent_input_field,inputGroups).input;
                        InputField independentInputField=getInputField_(independentInputFieldVariable.independent_input_field,inputGroups);
                        String independentValue = independentInputField.input;

                        if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
                            try {
                                independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
                            } catch (Exception e) {
                                independentValue=null;
//                            throw new RuntimeException(e);
                            }

                        }
                        if (!independentValue.equals(independentInputFieldVariable.operation_value)) {
                            orFilterOk = true;
                            break;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;


                        }
                    }
                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field,inputGroups).object_field_name);
//                        String independentValue = getInputField_(independentInputFieldVariable.independent_input_field,inputGroups).input;
                        InputField independentInputField=getInputField_(independentInputFieldVariable.independent_input_field,inputGroups);
                        String independentValue = independentInputField.input;

                        if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
                            try {
                                independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
                            } catch (Exception e) {
                                independentValue=null;
//                            throw new RuntimeException(e);
                            }

                        }
                        if (independentValue == null) {
                            inputFieldInputConstraintProcessingResult.field_active = false;
                            return inputFieldInputConstraintProcessingResult;
                        } else {
                            if (independentValue.equals(independentInputFieldVariable.operation_value)) {
                                andFilterOk = false;
                                break;
                            } else if (andFilterOk == null) {
                                andFilterOk = true;

                            }
                        }
                    }

                    if (!((orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk))) {
                        notEqualToFilterCanDisplay = false;
                    } else if (notEqualToFilterCanDisplay == null) {
                        notEqualToFilterCanDisplay = true;

                    }
                    break;
                default:
                    inputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        inputFieldInputConstraintProcessingResult.field_active = ((equalToFilterCanDisplay == null || equalToFilterCanDisplay) && (notEqualToFilterCanDisplay == null || notEqualToFilterCanDisplay));
        if (!inputFieldInputConstraintProcessingResult.field_active) {

            return inputFieldInputConstraintProcessingResult;

        }
//        ArrayList<String> table_filters = new ArrayList<>();
        remainingInputFieldInputConstraints.clear();
        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case ParentChild:
                    InputField independentInputField = getInputField_(inputFieldInputConstraint.independent_input_field,inputGroups);
//                    String independentInput = getField(registeringObject, independentInputField.object_field_name);
                    String independentInput =independentInputField.input;
                    if(inputFieldInputConstraint.independent_column!=null&&independentInput!=null){
                        try {
                            independentInput =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),inputFieldInputConstraint.independent_column);
                        } catch (Exception e) {
                            independentInput=null;
//                            throw new RuntimeException(e);
                        }
                    }
                    if (independentInput == null) {
                        inputFieldInputConstraintProcessingResult.field_active = false;
                        return inputFieldInputConstraintProcessingResult;
                    } else {
                        inputFieldInputConstraintProcessingResult.tableFilters.add(inputFieldInputConstraint.dependent_column + "='" + independentInput + "'");
                    }
                    break;
                default:
                    remainingInputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        inputFieldInputConstraints.clear();
        include_only_loop:
        for (InputFieldInputConstraint inputFieldInputConstraint : remainingInputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case IncludeOnly:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field,inputGroups).object_field_name);
                        String independentValue = getInputField_(independentInputFieldVariable.independent_input_field,inputGroups).input;
                        if (independentValue != null && independentValue.equals(independentInputFieldVariable.operation_value)) {
                            orFilterOk = true;
                            inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                            includeOnlyFilterCanDisplay = true;
                            break include_only_loop;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;
                        }
                    }

                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        String independentValue = getInputField_(independentInputFieldVariable.independent_input_field,inputGroups).input;
                        if (independentValue != null && !independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = false;
                            break;
                        } else if (independentValue == null) {
                            andFilterOk = false;
                            break;
                        } else if (independentValue != null && independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = true;
                        } else if (andFilterOk == null) {
                            andFilterOk = false;

                        }
                    }

                    if (andFilterOk != null && andFilterOk) {
                        inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                        inputFieldInputConstraintProcessingResult.field_active = true;
                        return inputFieldInputConstraintProcessingResult;
                    } else if (includeOnlyFilterCanDisplay == null) {
                        includeOnlyFilterCanDisplay = false;
//                        inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");

                    }

                    break;
                default:
                    inputFieldInputConstraints.add(inputFieldInputConstraint);
            }


        }
        if (includeOnlyFilterCanDisplay != null) {
            inputFieldInputConstraintProcessingResult.field_active = false;
            return inputFieldInputConstraintProcessingResult;
        }
        remainingInputFieldInputConstraints.clear();
        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case Exclude:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        String independentValue = getInputField_(independentInputFieldVariable.independent_input_field,inputGroups).input;
                        if (!independentValue.equals(independentInputFieldVariable.operation_value)) {
                            orFilterOk = true;
                            break;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;


                        }
                    }
                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        String independentValue = getInputField_(independentInputFieldVariable.independent_input_field,inputGroups).input;
                        if (independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = false;
                            break;
                        } else if (andFilterOk == null) {
                            andFilterOk = true;

                        }
                    }

                    if (!(orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk)) {
                        includeOnlyFilterCanDisplay = false;
                    } else if (equalToFilterCanDisplay == null) {
                        includeOnlyFilterCanDisplay = true;

                    }

                    if (!(orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk)) {
                        includeOnlyFilterCanDisplay = false;
                        inputFieldInputConstraintProcessingResult.field_active = false;
                        return inputFieldInputConstraintProcessingResult;
                    } else if (equalToFilterCanDisplay == null) {
                        includeOnlyFilterCanDisplay = true;
                        inputFieldInputConstraintProcessingResult.tableFilters.add("sid NOT IN (" + inputFieldInputConstraint.dataset_values + ")");

                    }

                    break;
                default:
//                    remainingInputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        return inputFieldInputConstraintProcessingResult;

    }
    public static InputFieldInputConstraintProcessingResult getInputFieldInputConstraintProcessingResult(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints_,ArrayList<InputField> inputFields) {
        ArrayList<InputFieldInputConstraint> inputFieldInputConstraints = new ArrayList<>(inputFieldInputConstraints_);
        ArrayList<InputFieldInputConstraint> remainingInputFieldInputConstraints = new ArrayList<>();
        InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult = new InputFieldInputConstraintProcessingResult();
        Boolean equalToFilterCanDisplay = null;
        Boolean notEqualToFilterCanDisplay = null;
        Boolean includeOnlyFilterCanDisplay = null;

        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case EqualTo:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        InputField independentInputField=getInputField(independentInputFieldVariable.independent_input_field,inputFields);
                        String independentValue = independentInputField.input;
if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
    try {
        independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
    } catch (Exception e) {
        independentValue=null;

//                            throw new RuntimeException(e);
    }

}

                        if (independentValue.equals(independentInputFieldVariable.operation_value)) {
                            inputFieldInputConstraintProcessingResult.field_active = true;
                            return inputFieldInputConstraintProcessingResult;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;


                        }
                    }
                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
//                        String independentValue = getInputField(independentInputFieldVariable.independent_input_field,inputFields).input;
                        InputField independentInputField=getInputField(independentInputFieldVariable.independent_input_field,inputFields);
                        String independentValue = independentInputField.input;

                        if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
                            try {
                                independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
                            } catch (Exception e) {
                                independentValue=null;

//                            throw new RuntimeException(e);
                            }

                        }
                        if (independentValue == null) {
                            andFilterOk = false;
                            break;
                        } else if (!independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = false;
                            break;
                        } else if (andFilterOk == null) {
                            andFilterOk = true;

                        }
                    }
                    if (andFilterOk != null && andFilterOk) {
                        inputFieldInputConstraintProcessingResult.field_active = true;
                        return inputFieldInputConstraintProcessingResult;
                    }
                    if (!(orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk)) {
                        equalToFilterCanDisplay = false;
                    } else if (equalToFilterCanDisplay == null) {
                        equalToFilterCanDisplay = true;

                    }

                    break;
                default:
                    remainingInputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        if(equalToFilterCanDisplay!=null){
            inputFieldInputConstraintProcessingResult.field_active = false;
            return inputFieldInputConstraintProcessingResult;
        }
        inputFieldInputConstraints.clear();
        for (InputFieldInputConstraint inputFieldInputConstraint : remainingInputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case NotEqualTo:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field,inputGroups).object_field_name);
//                        String independentValue = getInputField(independentInputFieldVariable.independent_input_field,inputFields).input;
                        InputField independentInputField=getInputField(independentInputFieldVariable.independent_input_field,inputFields);
                        String independentValue = independentInputField.input;

                        if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
                            try {
                                independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
                            } catch (Exception e) {
                                independentValue=null;

//                            throw new RuntimeException(e);
                            }

                        }
                        if (!independentValue.equals(independentInputFieldVariable.operation_value)) {
                            orFilterOk = true;
                            break;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;


                        }
                    }
                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field,inputGroups).object_field_name);
//                        String independentValue = getInputField(independentInputFieldVariable.independent_input_field,inputFields).input;
                        InputField independentInputField=getInputField(independentInputFieldVariable.independent_input_field,inputFields);
                        String independentValue = independentInputField.input;

                        if(independentInputFieldVariable.independent_input_field_column!=null&&independentValue!=null){
                            try {
                                independentValue =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),independentInputFieldVariable.independent_input_field_column);
                            } catch (Exception e) {
                                independentValue=null;
//                            throw new RuntimeException(e);
                            }

                        }
                        if (independentValue == null) {
                            inputFieldInputConstraintProcessingResult.field_active = false;
                            return inputFieldInputConstraintProcessingResult;
                        } else {
                            if (independentValue.equals(independentInputFieldVariable.operation_value)) {
                                andFilterOk = false;
                                break;
                            } else if (andFilterOk == null) {
                                andFilterOk = true;

                            }
                        }
                    }

                    if (!((orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk))) {
                        notEqualToFilterCanDisplay = false;
                    } else if (notEqualToFilterCanDisplay == null) {
                        notEqualToFilterCanDisplay = true;

                    }
                    break;
                default:
                    inputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        inputFieldInputConstraintProcessingResult.field_active = ((equalToFilterCanDisplay == null || equalToFilterCanDisplay) && (notEqualToFilterCanDisplay == null || notEqualToFilterCanDisplay));
        if (!inputFieldInputConstraintProcessingResult.field_active) {

            return inputFieldInputConstraintProcessingResult;

        }
//        ArrayList<String> table_filters = new ArrayList<>();
        remainingInputFieldInputConstraints.clear();
        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case ParentChild:
                    InputField independentInputField = getInputField(inputFieldInputConstraint.independent_input_field,inputFields);
//                    String independentInput = getField(registeringObject, independentInputField.object_field_name);
                    String independentInput =independentInputField.input;
                    if(inputFieldInputConstraint.independent_column!=null){
                        try {
                            independentInput =getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset),new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)),inputFieldInputConstraint.independent_column);
                        } catch (Exception e) {
                            independentInput=null;
//                            throw new RuntimeException(e);
                        }
                    }
                    if (independentInput == null) {
                        inputFieldInputConstraintProcessingResult.field_active = false;
                        return inputFieldInputConstraintProcessingResult;
                    } else {
                        inputFieldInputConstraintProcessingResult.tableFilters.add(inputFieldInputConstraint.dependent_column + "='" + independentInput + "'");
                    }
                    break;
                default:
                    remainingInputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        inputFieldInputConstraints.clear();
        include_only_loop:
        for (InputFieldInputConstraint inputFieldInputConstraint : remainingInputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case IncludeOnly:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field,inputGroups).object_field_name);
                        String independentValue = getInputField(independentInputFieldVariable.independent_input_field,inputFields).input;
                        if (independentValue != null && independentValue.equals(independentInputFieldVariable.operation_value)) {
                            orFilterOk = true;
                            inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                            includeOnlyFilterCanDisplay = true;
                            break include_only_loop;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;
                        }
                    }

                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        String independentValue = getInputField(independentInputFieldVariable.independent_input_field,inputFields).input;
                        if (independentValue != null && !independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = false;
                            break;
                        } else if (independentValue == null) {
                            andFilterOk = false;
                            break;
                        } else if (independentValue != null && independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = true;
                        } else if (andFilterOk == null) {
                            andFilterOk = false;

                        }
                    }

                    if (andFilterOk != null && andFilterOk) {
                        inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                        inputFieldInputConstraintProcessingResult.field_active = true;
                        return inputFieldInputConstraintProcessingResult;
                    } else if (includeOnlyFilterCanDisplay == null) {
                        includeOnlyFilterCanDisplay = false;
//                        inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");

                    }

                    break;
                default:
                    inputFieldInputConstraints.add(inputFieldInputConstraint);
            }


        }
        if (includeOnlyFilterCanDisplay != null) {
            inputFieldInputConstraintProcessingResult.field_active = false;
            return inputFieldInputConstraintProcessingResult;
        }
        remainingInputFieldInputConstraints.clear();
        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            switch (InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) {
                case Exclude:
                    Boolean orFilterOk = null;
                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        String independentValue = getInputField(independentInputFieldVariable.independent_input_field,inputFields).input;
                        if (!independentValue.equals(independentInputFieldVariable.operation_value)) {
                            orFilterOk = true;
                            break;
                        } else if (orFilterOk == null) {
                            orFilterOk = false;


                        }
                    }
                    Boolean andFilterOk = null;

                    for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
//                        String independentValue = getField(registeringObject, getInputField(independentInputFieldVariable.independent_input_field).object_field_name);
                        String independentValue = getInputField(independentInputFieldVariable.independent_input_field,inputFields).input;
                        if (independentValue.equals(independentInputFieldVariable.operation_value)) {
                            andFilterOk = false;
                            break;
                        } else if (andFilterOk == null) {
                            andFilterOk = true;

                        }
                    }

                    if (!(orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk)) {
                        includeOnlyFilterCanDisplay = false;
                    } else if (equalToFilterCanDisplay == null) {
                        includeOnlyFilterCanDisplay = true;

                    }

                    if (!(orFilterOk == null || orFilterOk) && (andFilterOk == null || andFilterOk)) {
                        includeOnlyFilterCanDisplay = false;
                        inputFieldInputConstraintProcessingResult.field_active = false;
                        return inputFieldInputConstraintProcessingResult;
                    } else if (equalToFilterCanDisplay == null) {
                        includeOnlyFilterCanDisplay = true;
                        inputFieldInputConstraintProcessingResult.tableFilters.add("sid NOT IN (" + inputFieldInputConstraint.dataset_values + ")");

                    }

                    break;
                default:
//                    remainingInputFieldInputConstraints.add(inputFieldInputConstraint);
            }

        }
        return inputFieldInputConstraintProcessingResult;

    }

    static String getField(Object member, String field_name) {
        Field ff = null;
        try {
            ff = member.getClass().getField(field_name);
            ff.setAccessible(true);
            try {
                String input = (String) ff.get(member);
                return input;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

//      return null;
    }

    public static InputField getInputField_(String sid,ArrayList<InputGroup> inputGroups) {

        for (InputGroup inputGroup : inputGroups) {
            for (InputField inputField : inputGroup.inputFields) {
                if (sid.equals(inputField.sid)) {
                    return inputField;
                }
            }

        }
        return null;
    }
  public static InputField getInputField(String sid,ArrayList<InputField> inputFields) {

            for (InputField inputField : inputFields) {
                if (sid.equals(inputField.sid)) {
                    return inputField;
                }
                for (InputGroup inputGroup : inputField.inputGroups) {
                    for (InputField inputField2 : inputGroup.inputFields) {
                        if (sid.equals(inputField2.sid)) {
                            return inputField2;
                        }
                    }

                }
            }

        return null;
    }
 InputField getInputField(String sid) {

        for (InputGroup inputGroup : form.inputGroups) {
            for (InputField inputField : inputGroup.inputFields) {
                if (sid.equals(inputField.sid)) {
                    return inputField;
                }
            }

        }
        return null;
    }

    public Object getRegisteringObject(){
        return registeringObject;
    }
    void prooceed() {
        saveTempRegistration();
//        if (validated() && !procceeded) {
//            procceeded = true;
//            nextPage();
//
//        }
        if (validated()) {

            nextPage();

        }
    }

    int operationMode = 0;
    int operationModeStatic = 0;
    int operationModeDb = 1;
   public boolean lastPage = false;
    public  boolean firstPage = false;
    public boolean onlyPage = false;

    void nextPage() {
        if (lastPage) {
//            showSaveDialog();
        } else {
            currentPageIndex=currentPageIndex + 1;
            InputGroup nextPage = operationMode == operationModeStatic ? form.inputGroups.get(currentPageIndex ) : Realm.databaseManager.loadObject(InputGroup.class, new Query().setTableFilters("form='" + form.sid + "'", "order_index>'" + currentPage.order_index + "'").setOffset(0).setLimit(1).addOrderFilters("order_index", true));

                        currentPage.sid = nextPage.sid;
            currentPage.title = nextPage.title;
            currentPage.sub_title = nextPage.sub_title;
            currentPage.form = nextPage.form;
            currentPage.order_index = nextPage.order_index;
            currentPage.inputFields.clear();
            currentPage.inputFields.addAll(nextPage.inputFields);
//            currentPage=nextPage;
            lastPage = currentPageIndex == form.inputGroups.size() - 1;
            firstPage = currentPageIndex == 0;
            onlyPage=form.inputGroups.size()==1;
//            setPageTitle(currentPage.title);
                      if (!onlyPage) {
                stepView.setStepsNumber(form.inputGroups.size());
                stepView.go(currentPageIndex, true);
                stepView.done(currentPageIndex == form.inputGroups.size() - 1);
                setPageTitle("Page " + (currentPageIndex + 1) + " of " + form.inputGroups.size() + "  " + currentPage.title);
//                          recyclerView.setPadding(0, 0, 0, dpToPx(70));

                      } else {
                recyclerView.setPadding(0, 0, 0, 0);
                stepView.setVisibility(GONE);
                previous.setVisibility(GONE);
                next.setVisibility(GONE);
            }
                      if(firstPage&&!onlyPage){
                          previous.setVisibility(INVISIBLE);
                          next.setVisibility(VISIBLE);
                      }
                      if(lastPage&&!onlyPage){
                          next.setVisibility(INVISIBLE);
                          previous.setVisibility(VISIBLE);
                      }
            if(!lastPage&&!firstPage&&!onlyPage){
                next.setVisibility(VISIBLE);
                previous.setVisibility(VISIBLE);
            }
            populate(this.registeringObject);
            formAdapter.notifyDataSetChanged();
            recyclerView.invalidate();
            inputListener.onPageChanged(currentPage,currentPageIndex);
        }


    }

    void previousPage() {
        if (firstPage) {


        } else {
            currentPageIndex=currentPageIndex - 1;
            InputGroup prevPage = operationMode == operationModeStatic ? form.inputGroups.get(currentPageIndex) : Realm.databaseManager.loadObject(InputGroup.class, new Query().setTableFilters("form='" + form.sid + "'", "order_index<'" + currentPage.order_index + "'").setOffset(0).setLimit(1).addOrderFilters("order_index",true));

            currentPage.sid = prevPage.sid;
            currentPage.title = prevPage.title;
            currentPage.sub_title = prevPage.sub_title;
            currentPage.form = prevPage.form;
            currentPage.order_index = prevPage.order_index;
            currentPage.inputFields.clear();
            currentPage.inputFields.addAll(prevPage.inputFields);
//            currentPage=prevPage;
            lastPage = currentPageIndex == form.inputGroups.size() - 1;
            firstPage = currentPageIndex == 0;

            //            setPageTitle(currentPage.title);
            onlyPage=form.inputGroups.size()==1;
            if (form.inputGroups.size() > 1) {
                stepView.setStepsNumber(form.inputGroups.size());
                stepView.go(currentPageIndex, true);
                stepView.done(currentPageIndex == form.inputGroups.size() - 1);
                setPageTitle("Page " + (currentPageIndex + 1) + " of " + form.inputGroups.size() + "  " + currentPage.title);
//                recyclerView.setPadding(0, 0, 0, dpToPx(70));
            } else {
                recyclerView.setPadding(0, 0, 0, 0);
                stepView.setVisibility(GONE);
                previous.setVisibility(GONE);
                next.setVisibility(GONE);
            }
            formAdapter.notifyDataSetChanged();
            recyclerView.invalidate();
            inputListener.onPageChanged(currentPage,currentPageIndex);
            if(firstPage&&!onlyPage){
                previous.setVisibility(INVISIBLE);
                next.setVisibility(VISIBLE);
            }
            if(lastPage&&!onlyPage){
                next.setVisibility(INVISIBLE);
                previous.setVisibility(VISIBLE);
            }
      if(!lastPage&&!firstPage&&!onlyPage){
                next.setVisibility(VISIBLE);
                previous.setVisibility(VISIBLE);
            }

            populate(this.registeringObject);
        }

    }

    void populate(Object object) {
        for (InputField inputField : currentPage.inputFields) {
            if (inputField.object_field_name == null) {
                continue;
            }

            Field ff = null;
            try {
                ff = object.getClass().getField(inputField.object_field_name);
                ff.setAccessible(true);
                try {
                    Object fieldData = ff.get(object);
                    if (fieldData instanceof AppImages) {
                        AppImages appImages = (AppImages) fieldData;
                        inputField.imagesInput = appImages;
                    }else if (fieldData instanceof MemberFingerprints) {
                        MemberFingerprints fingerprintsInput = (MemberFingerprints) fieldData;
                        inputField.fingerprintsInput = fingerprintsInput;
                    } else if (fieldData instanceof String) {
                        String input = (String) fieldData;
                        inputField.input = input;
                    } else if (fieldData instanceof AppData) {
                        AppData imageInput = (AppData) fieldData;
                        inputField.imageInput = imageInput;
                    } else if (fieldData instanceof MemberFingerprint) {
                        MemberFingerprint fingerprintInput = (MemberFingerprint) fieldData;
                        inputField.fingerprintInput = fingerprintInput;
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }


        }
        formAdapter.notifyDataSetChanged();
    }

    void saveTempRegistrationField(InputField inputField) {
        if (inputField.object_field_name == null) {
            return;
        }
        Field ff = null;
        try {
            ff = registeringObject.getClass().getField(inputField.object_field_name);
            ff.setAccessible(true);
            try {
                if (ff.getType() == String.class) {
                    ff.set(registeringObject, inputField.input);

                } else if (ff.getType() == AppImages.class) {
                    ff.set(registeringObject, inputField.imagesInput);

                } else if (ff.getType() == MemberFingerprints.class) {
                    ff.set(registeringObject, inputField.fingerprintsInput);

                } else if (ff.getType() == MemberFingerprint.class) {
                    ff.set(registeringObject, inputField.fingerprintInput);

                } else if (ff.getType() == AppData.class) {
                    ff.set(registeringObject, inputField.imageInput);

                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {

        }

        svars.setWorkingObject(registeringObject, "FormItem:" + form.sid);

    }

    public void resetObject(){
        clearTempRegistration();
        try {
            svars.setWorkingObject(registeringObject.getClass().newInstance(), "FormItem:" + form.sid);
        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
        }
        populate(svars.workingObject(registeringObject.getClass(),"FormItem:" + form.sid));

    }

    void saveTempRegistration() {
        for (InputField inputField : currentPage.inputFields) {
            if (inputField.object_field_name == null) {
                continue;
            }
            Field ff = null;
            try {
                ff = registeringObject.getClass().getField(inputField.object_field_name);
                ff.setAccessible(true);
                try {
                    if (ff.getType() == String.class) {
                        ff.set(registeringObject, inputField.input);

                    } else if (ff.getType() == AppImages.class) {
                        ff.set(registeringObject, inputField.imagesInput);

                    }else if (ff.getType() == ArrayList.class) {
                        ff.set(registeringObject, inputField.fingerprintsInput);

                    } else if (ff.getType() == MemberFingerprint.class) {
                        ff.set(registeringObject, inputField.fingerprintInput);

                    } else if (ff.getType() == AppData.class) {
                        ff.set(registeringObject, inputField.imageInput);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {

            }


        }
        svars.setWorkingObject(registeringObject, "FormItem:" + form.sid);

    }
   void clearTempRegistration() {
        for (InputField inputField : currentPage.inputFields) {
            if (inputField.object_field_name == null) {
                continue;
            }
            Field ff = null;
            try {
                ff = registeringObject.getClass().getField(inputField.object_field_name);
                ff.setAccessible(true);
                try {
                    if (ff.getType() == String.class) {
//                        ff.set(registeringObject, inputField.input);
                        inputField.input=null;
                    } else if (ff.getType() == AppImages.class) {
//                        ff.set(registeringObject, inputField.imagesInput);
                        inputField.imagesInput=new AppImages();

                    }else if (ff.getType() == MemberFingerprints.class) {
//                        ff.set(registeringObject, inputField.fingerprintsInput);
                        inputField.fingerprintsInput=new MemberFingerprints();

                    } else if (ff.getType() == MemberFingerprint.class) {
//                        ff.set(registeringObject, inputField.fingerprintInput);
                        inputField.fingerprintInput=new MemberFingerprint();

                    } else if (ff.getType() == AppData.class) {
//                        ff.set(registeringObject, inputField.imageInput);
                        inputField.imageInput=new AppData();

                    }
                } catch (Exception e) {
//                    throw new RuntimeException(e);
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {

            }


        }
        formAdapter.notifyDataSetChanged();

    }

   public boolean validated() {
        return currentPage.inputFields.stream()
                .filter(p -> !p.inputValid)
                .findAny()
                .orElse(null) == null;
    }

    FormAdapter formAdapter;

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }
public int currentPageIndex=0;
    public void setForm(SpartaAppCompactFingerPrintActivity spartaAppCompactFingerPrintActivity,Form form, InputListener inputListener, Object registeringObject) {
        this.activity=spartaAppCompactFingerPrintActivity;
        setForm(form,inputListener,registeringObject);
    }
    public void setForm(Form form, InputListener inputListener, Object registeringObject) {
        this.form = form;
        this.inputListener = inputListener;
        this.registeringObject = registeringObject;
        setTitle(form.title);
        if(form.title==null){
            this.title.setVisibility(GONE);

        }
        setFormInstructions(form.instructions);
 if(form.instructions==null){
            this.instructions.setVisibility(GONE);

        }

        currentPage.sid = form.inputGroups.get(0).sid;
        currentPage.title = form.inputGroups.get(0).title;
        currentPage.sub_title = form.inputGroups.get(0).sub_title;
        currentPage.form = form.inputGroups.get(0).form;
        currentPage.order_index = form.inputGroups.get(0).order_index;
        currentPage.inputFields.clear();
        currentPage.inputFields.addAll(form.inputGroups.get(0).inputFields);
        lastPage =  form.inputGroups.size()==1;
        firstPage = true;
        onlyPage=form.inputGroups.size()==1;
        if (!onlyPage) {
            previous.setVisibility(INVISIBLE);
            pageTitle.setVisibility(GONE);
            stepView.setStepsNumber(form.inputGroups.size());
            stepView.go(form.inputGroups.indexOf(currentPage), true);
            stepView.done(form.inputGroups.indexOf(currentPage) == form.inputGroups.size() - 1);
            setPageTitle("Page " + (form.inputGroups.indexOf(currentPage) + 1) + " of " + form.inputGroups.size() + "  " + currentPage.title);
//            recyclerView.setPadding(0, 0, 0, dpToPx(70));
        } else {
            setPageTitle(currentPage.title);
            if(currentPage.title==null){
                this.pageTitle.setVisibility(GONE);

            }

            recyclerView.setPadding(0, 0, 0, 0);
            stepView.setVisibility(GONE);
            previous.setVisibility(GONE);
            next.setVisibility(GONE);
        }

        this.registeringObject=svars.workingObject(registeringObject.getClass(),"FormItem:" + form.sid)==null?registeringObject:svars.workingObject(registeringObject.getClass(),"FormItem:" + form.sid);
       // formAdapter.notifyDataSetChanged();
        populate(this.registeringObject);

    }
    public void refresh(){
         formAdapter.notifyDataSetChanged();
    }
    public void setActiveCaptureImage(AppData image) {
        formAdapter.activeImageCapture.setImage(image);

    }
    public void addActiveCaptureImage(AppData image) {
        formAdapter.activeMultiImageCapture.addImage(image);

    }
  public void addActiveFingerprintCaptureDataWSQ(String wsq) {
        formAdapter.activeFingerprintCapture.setData(FingerprintCapture.FingerprintDataType.WSQ,wsq);

    }
 public void addActiveFingerprintCaptureDataJPG(String jpg) {
        formAdapter.activeFingerprintCapture.setData(FingerprintCapture.FingerprintDataType.JPEG,jpg);

    }
public void addActiveFingerprintCaptureDataISO(String iso) {
        formAdapter.activeFingerprintCapture.setData(FingerprintCapture.FingerprintDataType.ISO,iso);

    }
    public void setActivity(SpartaAppCompactFingerPrintActivity activity) {
        formAdapter.setActivity(activity);
        this.activity = activity;
    }

    public void setTitle(String title_) {
        this.title.setText(title_);

    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle.setText(pageTitle);

    }

    public void setFormInstructions(String formInstructions) {
        this.instructions.setText(formInstructions);

    }

    public int spToPx(float sp) {
        Context context = getContext();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    float dpToPxN(int dp) {
        float dip = dp;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return px;
    }

    int dpToPx(int dp) {

        return (int) dpToPxN(dp);
    }

    int dpToPx_(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}
