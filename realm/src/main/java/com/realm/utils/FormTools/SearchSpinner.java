package com.realm.utils.FormTools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import com.realm.Models.Query;
import com.realm.R;
import com.realm.Realm;
import com.realm.utils.FormTools.adapters.SelectionDataRecyclerViewAdapter;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.SelectionData;
import com.realm.utils.FormTools.models.ValidationRules;


public class SearchSpinner extends LinearLayout {
    TextView title, topSelectInstructions, selectInstructions;
    ImageView dropArrow;
    View underline;
    TextView selectedItemTitle, selectedItemInfo;
    AutoCompleteTextView searchText;
    RecyclerView searchList;
    View selectedItemView;
    SelectionData selectedItem;
    ArrayList<? extends SelectionData> selectionData = new ArrayList<>();
    private PopupWindow mPopupWindow;

    public SearchSpinner(Context context) {
        super(context);
        setupUI();
    }

    public SearchSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SearchSpinner,
                0, 0);

        setTitle(a.getString(R.styleable.SearchSpinner_title));
        setPlaceholder(a.getString(R.styleable.SearchSpinner_placeholder));
        setSearchTitle(a.getString(R.styleable.SearchSpinner_searchListTitle));
        setMandatory(a.getBoolean(R.styleable.SearchSpinner_mandatory, false));
        a.recycle();
    }

    public SearchSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //     setupUI();
    }

    public SearchSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //    setupUI();
    }

    void setupUI() {
        setOrientation(VERTICAL);
        selectedItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_selection_data, null);
        selectedItemTitle = selectedItemView.findViewById(R.id.title);
        selectedItemInfo = selectedItemView.findViewById(R.id.info1);

        title = new TextView(getContext(), null, R.style.Theme_Realm_SubTitle);
        title.setText("Gender");
        title.setTypeface(null, Typeface.BOLD);

        selectInstructions = new TextView(getContext());
        selectInstructions.setText("  --Select gender--  ");

        dropArrow = new ImageView(getContext());
        dropArrow.setImageDrawable(getContext().getDrawable(android.R.drawable.arrow_down_float));

        underline = new View(getContext());
        underline.setBackgroundColor(Color.DKGRAY);


        topSelectInstructions = new TextView(getContext());
        topSelectInstructions.setText("Select gender  ");
        topSelectInstructions.setCompoundDrawables(null, null, getContext().getDrawable(android.R.drawable.arrow_down_float), null);
//        topSelectInstructions.setBackgroundTintList();

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), android.R.drawable.arrow_down_float);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.DKGRAY);
        topSelectInstructions.setCompoundDrawables(null, null, wrappedDrawable, null);

        searchText = new AutoCompleteTextView(getContext());
        searchText.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(android.R.drawable.ic_menu_search), null, null, null);
        searchList = new RecyclerView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 1;
        addView(title);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(selectedItemView, params);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(searchText, params);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.gravity = 1;
        addView(topSelectInstructions, params);
//        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(getContext(),1.2f));
//        addView(underline, params);

        selectedItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               if(isEnabled()){
                   setState(state.searching);
               }
            }
        });
//        searchText.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setState(state.searching);
//            }
//        });
        searchText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP&&isEnabled()) {
                    setState(state.searching);
//                    return true;
                }
                return false;
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setState(state.searching);
                selectionDataRecyclerViewAdapter.getFilter().filter(searchText.getText().toString());

            }
        });
        searchText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    setState(b ? state.searching : state.idle);
                    inputField.inputValid = isInputValid();
                }

            }
        });
        topSelectInstructions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
              if(isEnabled()){
                  setState(state.searching);
              }

            }
        });
        searchList.setLayoutManager(new LinearLayoutManager(getContext()));
        searchList.setAdapter(selectionDataRecyclerViewAdapter);
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(getContext().getColor(R.color.ghostwhite));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 1;
        layout.addView(selectInstructions, params);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(searchList, params);

        mPopupWindow = new PopupWindow(getContext());
        mPopupWindow.setContentView(layout);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //hideEdit();
            }
        });

        mPopupWindow.setFocusable(false);
        mPopupWindow.setElevation(16);
//        mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.spinner_drawable));
        setState(state.idle);
      //  title.setTextSize(dpToPx(20));
//        inputText.setTextSize(18);
    }

    public enum state {
        idle, searching

    }

    public void setMandatory(boolean mandatory) {
//        this.mandatoryIndicator.setVisibility(mandatory ? VISIBLE : GONE);
        inputField.validationRules.mandatory = mandatory ? ValidationRules.MandatoryStatus.Mandatory.ordinal() + "" : ValidationRules.MandatoryStatus.NonMandatory.ordinal() + "";
    }

    public void setTitle(String title_) {
        this.title.setText(title_);
    }

    public void setPlaceholder(String placeholder) {
        this.searchText.setHint(placeholder);
    }

    public void setSearchTitle(String searchTitle) {
        this.selectInstructions.setText(searchTitle);
    }

    public void setDataset(String dataset, InputListener inputListener) {
        this.inputListener = inputListener;
        try {
            selectionData.clear();
            selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(dataset), new Query()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        selectionDataRecyclerViewAdapter.setupLists();
    }

    public void setDataset(ArrayList dataset, InputListener inputListener) {
        this.inputListener = inputListener;
        selectionData.clear();
//        selectionDataRecyclerViewAdapter.setupLists();
//        selectionData.clear();
//        selectionData.addAll(dataset);
//        selectedItem = null;
//        selectionDataRecyclerViewAdapter.setupLists();
        Handler handler=new Handler(getContext().getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {

                    selectionData.addAll(dataset);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        selectionDataRecyclerViewAdapter.setupLists();
                        setInput(inputField.input);
                        inputField.inputValid=isInputValid();
                    }
                });
            }
        }).start();
        setState(state.idle);

    }

    String input = null;

    public String getInput() {
        return selectedItem == null ? null : selectedItem.sid;

    }

    public void setInput(String input) {
        int tot = selectionData.size();
        selectedItem = null;
        for (int i = 0; i < tot; i++) {
            if (selectionData.get(i).sid.equals(input)) {
                selectedItem = selectionData.get(i);
                selectedItemTitle.setText(selectedItem.name);
                selectedItemInfo.setText(selectedItem.code);
//              inputListener.onInputAvailable(isInputValid(),selectedItem.sid);
                break;
            }
        }
        if(selectedItem==null){
            inputField.input=null;
        }
        setState(state.idle);


    }

    public boolean isInputValid() {
        if (inputField == null) {
            return true;
        }
        if (inputField.validationRules.mandatory != null && inputField.validationRules.mandatory.equals(ValidationRules.MandatoryStatus.Mandatory.ordinal() + "") && selectedItem == null) {
//          searchText.setError("Input not selected");
            searchText.setError(inputField.validationRules.value_not_selected_error != null ? inputField.validationRules.value_not_selected_error : "Input not selected");
            searchText.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getContext(), searchText.getError(), Toast.LENGTH_LONG).show();
            return false;
        } else {
            searchText.setError(null);
//          searchText.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);

        }
        return selectedItem != null;

    }

    public interface InputListener {
        default void onInputAvailable(boolean valid, String input) {

        }


    }

    InputListener inputListener = new InputListener() {
        @Override
        public void onInputAvailable(boolean valid, String input) {

        }
    };


    void setState(state state) {
//        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), android.R.drawable.arrow_down_float);
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_baseline_arrow_drop_down_24);
//        unwrappedDrawable.setBounds();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.DKGRAY);
//        searchText.setCompoundDrawables(null,null, wrappedDrawable,null);
//        searchText.setCompoundDrawablesWithIntrinsicBounds(null,null, wrappedDrawable,null);
        searchText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
        switch (state) {
            case idle:
                if (selectedItem == null) {
//                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), android.R.drawable.arrow_down_float);
//                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
//                    DrawableCompat.setTint(wrappedDrawable, Color.DKGRAY);
//                    searchText.setCompoundDrawables(getContext().getDrawable(android.R.drawable.ic_menu_search),null, wrappedDrawable,null);

                    topSelectInstructions.setVisibility(GONE);
                    topSelectInstructions.setVisibility(GONE);
                    selectedItemView.setVisibility(GONE);
                    searchText.setVisibility(VISIBLE);
                    searchList.setVisibility(GONE);

                } else {
                    topSelectInstructions.setVisibility(GONE);
                    selectedItemView.setVisibility(VISIBLE);
                    searchText.setVisibility(GONE);
                    searchList.setVisibility(GONE);
                }
                mPopupWindow.dismiss();

                break;
            case searching:
                topSelectInstructions.setVisibility(GONE);
//                selectInstructions.setVisibility(VISIBLE);
                selectedItemView.setVisibility(GONE);
                searchText.setVisibility(VISIBLE);
                searchList.setVisibility(VISIBLE);
                searchText.requestFocus();
                mPopupWindow.showAsDropDown(searchText);
                break;
        }


    }

    void setState_(state state) {
        switch (state) {
            case idle:
                if (selectedItem == null) {
                    topSelectInstructions.setVisibility(VISIBLE);
                    selectedItemView.setVisibility(GONE);
                    searchText.setVisibility(GONE);
                    searchList.setVisibility(GONE);

                } else {
                    topSelectInstructions.setVisibility(GONE);
                    selectedItemView.setVisibility(VISIBLE);
                    searchText.setVisibility(GONE);
                    searchList.setVisibility(GONE);
                }
                mPopupWindow.dismiss();
                break;
            case searching:
                topSelectInstructions.setVisibility(GONE);
//                selectInstructions.setVisibility(VISIBLE);
                selectedItemView.setVisibility(GONE);
                searchText.setVisibility(VISIBLE);
                searchList.setVisibility(VISIBLE);
                searchText.requestFocus();
                mPopupWindow.showAsDropDown(searchText);
                break;
        }


    }

    private int mScreenHeightPixels;
    private int mScreenWidthPixels;

    private void getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenHeightPixels = metrics.heightPixels;
        mScreenWidthPixels = metrics.widthPixels;
    }

    private boolean mShowBorders = false;
    private @Px int mBordersSize = 4;
    private @Px int mExpandSize = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getScreenSize();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mShowBorders) {     // + 4 because of card layout_margin in the view_searchable_spinner.xml
            width -= dpToPx( (mBordersSize + 4));
        } else {
            width -= dpToPx( 8);
        }
        mPopupWindow.setWidth(width);
        if (mExpandSize <= 0) {
            mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            mPopupWindow.setHeight(heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public  int dpToPx( float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public void reset() {


    }

    InputField inputField = new InputField();

    public void setInputField_OtherThreadLoad(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        reset();
        setTitle(inputField.title);
//        searchText.setHint(inputField.placeholder);
        searchText.setHint(getContext().getString(R.string.search)+" " +inputField.title.toLowerCase());
        setSearchTitle((inputField.title.toLowerCase().startsWith("a")
                ||inputField.title.toLowerCase().startsWith("e")
                ||inputField.title.toLowerCase().startsWith("i")
                ||inputField.title.toLowerCase().startsWith("o")
                ||inputField.title.toLowerCase().startsWith("u")?getContext().getString(R.string.select_an) :getContext().getString(R.string.select_a))+"" +inputField.title.toLowerCase());
            selectionData.clear();
            Handler handler=new Handler(getContext().getMainLooper());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), new Query().setTableFilters(inputField.dataset_table_filter)));
                        selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), inputField.dataset_table_filter==null?new Query():new Query().setTableFilters(inputField.dataset_table_filter)));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            selectionDataRecyclerViewAdapter.setupLists();
                            setInput(inputField.input);
                            inputField.inputValid=isInputValid();
                        }
                    });
                }
            }).start();


//        searchText.setAdapter(new SelectionDataAdapter(getContext(),selectionData));

        selectionDataRecyclerViewAdapter.setupLists();
        setInput(inputField.input);
    }
    public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        reset();
        setTitle(inputField.title);
//        searchText.setHint(inputField.placeholder);
        searchText.setHint(getContext().getString(R.string.search)+" " +inputField.title.toLowerCase());
        setSearchTitle((inputField.title.toLowerCase().startsWith("a")
                ||inputField.title.toLowerCase().startsWith("e")
                ||inputField.title.toLowerCase().startsWith("i")
                ||inputField.title.toLowerCase().startsWith("o")
                ||inputField.title.toLowerCase().startsWith("u")?getContext().getString(R.string.select_an) :getContext().getString(R.string.select_a))+"" +inputField.title.toLowerCase());
            selectionData.clear();
            Handler handler=new Handler(getContext().getMainLooper());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), new Query().setTableFilters(inputField.dataset_table_filter)));
                        selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), inputField.dataset_table_filter==null?new Query():new Query().setTableFilters(inputField.dataset_table_filter)));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            selectionDataRecyclerViewAdapter.setupLists();
                            setInput(inputField.input);
                            inputField.inputValid=isInputValid();
                        }
                    });
                }
            }).start();


//        searchText.setAdapter(new SelectionDataAdapter(getContext(),selectionData));

        selectionDataRecyclerViewAdapter.setupLists();
        if(inputField.input!=null){
            try {
                SelectionData selectionData1=((SelectionData) Realm.databaseManager.loadObject(Class.forName(inputField.dataset), inputField.dataset_table_filter==null?new Query().setTableFilters("sid=?").setQueryParams(inputField.input):new Query().setTableFilters(inputField.dataset_table_filter,"sid=?").setQueryParams(inputField.input)));
      if(selectionData1==null){
          inputField.input=null;
          inputListener.onInputAvailable(isInputValid(),null);
      }else {
          selectedItemTitle.setText(selectionData1.name);
          selectedItemInfo.setText(selectionData1.code);
      }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

//        setInput(inputField.input);
    }
//    SelectionDataRecyclerViewAdapter selectionDataRecyclerViewAdapter=new SelectionDataRecyclerViewAdapter(selectionData, new SelectionDataRecyclerViewAdapter.onItemClickListener() {
//        @Override
//        public void onItemClick(SelectionData selectionData, View view) {
//
//        }
//    });

    SelectionDataRecyclerViewAdapter selectionDataRecyclerViewAdapter = new SelectionDataRecyclerViewAdapter(selectionData, new SelectionDataRecyclerViewAdapter.onItemClickListener() {
        @Override
        public void onItemClick(SelectionData selectionData, View view) {
            selectedItem = selectionData;
            selectedItemTitle.setText(selectionData.name);
            selectedItemInfo.setText(selectionData.code);

            setState(state.idle);
            inputListener.onInputAvailable(isInputValid(), selectionData.sid);
        }
    });


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled){
            setAlpha(1f);

        }else{

            setAlpha(0.5f);

        }
    }
}
