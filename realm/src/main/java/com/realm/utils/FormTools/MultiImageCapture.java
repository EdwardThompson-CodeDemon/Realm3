package com.realm.utils.FormTools;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.io.File;

import com.realm.activities.SpartaAppCompactActivity;
import com.realm.R;
import com.realm.utils.svars;
import com.realm.utils.FormTools.adapters.MultiPhotoInputAdapter;
import com.realm.utils.FormTools.models.AppData;
import com.realm.utils.FormTools.models.AppImages;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.ValidationRules;
import com.realm.utils.InputValidation;


public class MultiImageCapture extends ConstraintLayout {

    TextView title, subTitle;
    ImageView clearAllButton;

    InputField inputField = new InputField();
    RecyclerView recyclerView;
    Barrier barrier1;
    //    AppData memberImage;
    SpartaAppCompactActivity activity;
    InputListener inputListener = new InputListener() {
        @Override
        public void onInputUpdated(boolean valid, AppData input) {

        }

        @Override
        public void onInputRequested(InputField inputField) {

        }
    };


    public interface InputListener {
        void onInputRequested(InputField inputField);

        default void onInputUpdated(boolean valid, AppData input) {

        }
        default void onInputUpdated(boolean valid, InputField inputField) {

        }


    }

    public MultiImageCapture(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public MultiImageCapture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public MultiImageCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public MultiImageCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI();

    }

    MultiPhotoInputAdapter multiPhotoInputAdapter;

    void setupUI() {
        title = new TextView(getContext(), null, R.style.Theme_Realm_SubTitle);
        title.setText("Multi photo capture");
        title.setTypeface(null, Typeface.BOLD);
        int title_id = View.generateViewId();
        title.setId(title_id);

        recyclerView = new RecyclerView(getContext());
        int recyclerViewId = View.generateViewId();
        recyclerView.setId(recyclerViewId);


        subTitle = new TextView(getContext());
        subTitle.setText("Click on the image to add a photo");
        int subTitleId = View.generateViewId();
        subTitle.setId(subTitleId);

        clearAllButton = new ImageView(getContext());
        clearAllButton.setImageDrawable(getContext().getDrawable(android.R.drawable.ic_menu_delete));
        int clearAllButonId = View.generateViewId();
        clearAllButton.setId(clearAllButonId);

        barrier1 = new Barrier(getContext());
        int barrier1Id = View.generateViewId();
        barrier1.setId(barrier1Id);


        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topToTop = PARENT_ID;
        titleParams.startToStart = PARENT_ID;
        titleParams.bottomMargin = 0;
        addView(title, titleParams);

        LayoutParams subTitleParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        subTitleParams.topToBottom = title_id;
        subTitleParams.startToStart = PARENT_ID;
        subTitleParams.endToStart = clearAllButonId;
        subTitleParams.setMargins(dpToPx(4), dpToPx(8), dpToPx(0), dpToPx(0));
        subTitleParams.bottomMargin = 0;
        addView(subTitle, subTitleParams);

        LayoutParams clearAllButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clearAllButtonParams.topToBottom = title_id;
        clearAllButtonParams.endToEnd = PARENT_ID;
        addView(clearAllButton, clearAllButtonParams);


        LayoutParams recyclerViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerViewParams.topToBottom = barrier1Id;
        recyclerViewParams.setMargins(dpToPx(4), dpToPx(0), dpToPx(4), dpToPx(0));
        addView(recyclerView, recyclerViewParams);
//        recyclerView.setBackgroundColor(getContext().getColor(R.color.antiquewhite));

 LayoutParams barrier1Params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        barrier1Params.topToBottom = clearAllButonId;
        addView(barrier1, barrier1Params);

        barrier1.setType(Barrier.BOTTOM);
        int[] referenceIds = new int[]{clearAllButonId,subTitleId};
        barrier1.setReferencedIds(referenceIds);
        clearAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                multiPhotoInputAdapter.clearItems();
            }
        });

        multiPhotoInputAdapter = new MultiPhotoInputAdapter(new MultiPhotoInputAdapter.InputListener() {
            @Override
            public void onPhotoTaken() {

            }

            @Override
            public void onPhotoRequested() {
                inputListener.onInputRequested(inputField);
                activity.takePhoto(inputField.sid);
            }

            @Override
            public void onMaxItemsReached() {
                MultiPhotoInputAdapter.InputListener.super.onMaxItemsReached();

            }

            @Override
            public void onImageDeleted(AppData appData) {
                MultiPhotoInputAdapter.InputListener.super.onImageDeleted(appData);
                inputField.imagesInput.imagesInput=multiPhotoInputAdapter.getItems();
                inputField.inputValid=isInputValid();
                inputListener.onInputUpdated(inputField.inputValid, inputField);

            }

            @Override
            public void onImagesDeleted() {
                MultiPhotoInputAdapter.InputListener.super.onImagesDeleted();
                inputField.imagesInput.imagesInput=multiPhotoInputAdapter.getItems();
                inputField.inputValid=isInputValid();
                inputListener.onInputUpdated(inputField.inputValid, inputField);
            }
        });
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(flowLayoutManager);
        recyclerView.setAdapter(multiPhotoInputAdapter);
        populate();

    }
//    public String getInput() {
//        return inputText.getText().toString();
//
//    }
//

    int requiredImages = 1;

    public void setRequiredImages(int requiredImages) {
        this.requiredImages = requiredImages;
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        setTitle(inputField.title);
        setSubTitle(inputField.sub_title);
        addImage(inputField.imageInput);
        svars.setImageCameraType(getContext(), inputField.sid, Integer.parseInt(inputField.default_image_source));

        setValidationRules(inputField.validationRules);
        setImagesInput(inputField.imagesInput);
        inputField.inputValid=isInputValid();
    }
    public void setImagesInput(AppImages appImages) {
        for(AppData appData:appImages.imagesInput){
            multiPhotoInputAdapter.addImage(appData);
        }
        inputField.inputValid=isInputValid();

        inputListener.onInputUpdated(inputField.inputValid, inputField);

    }
    public void setValidationRules(ValidationRules validationRules) {
        multiPhotoInputAdapter.setMaxImages(InputValidation.isNumeric(validationRules.max_images) ? Integer.parseInt(validationRules.max_images) : -1);

    }

    public boolean isInputValid() {
        if (inputField == null || inputField.validationRules.mandatory == null || !inputField.validationRules.mandatory.equals(ValidationRules.MandatoryStatus.Mandatory.ordinal() + "")) {
            return true;
        }
        int imageCount = multiPhotoInputAdapter.getImageCount();
        int minImages = InputValidation.isNumeric(inputField.validationRules.min_images) ? Integer.parseInt(inputField.validationRules.min_images) : 0;
        int maxImages = InputValidation.isNumeric(inputField.validationRules.max_images) ? Integer.parseInt(inputField.validationRules.max_images) : -1;
        if (imageCount < minImages) {
            ObjectAnimator.ofFloat(this, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);

            return false;
        }
        if (maxImages != -1 && imageCount > maxImages) {
            ObjectAnimator.ofFloat(this, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);

            return false;
        }

        return true;
    }

    public void setActivity(SpartaAppCompactActivity activity) {
        this.activity = activity;
        multiPhotoInputAdapter.setActivity(activity);
    }

    public void setTitle(String title_) {
        this.title.setText(title_);

    }

    public void setSubTitle(String subTitle) {
        this.subTitle.setText(subTitle);

    }

    public void clearImages() {

        multiPhotoInputAdapter.clearItems();
        inputField.inputValid = isInputValid();

    }

    public void addImage(AppData appData) {

        if (appData == null || appData.data == null) {
//            inputField.inputValid = false;
            return;
        } else {
            File file = new File(svars.current_app_config(getContext()).appDataFolder, appData.data);
            if (!file.exists() || file.length() < 500) {
//                inputField.inputValid = false;
                return;
            }

        }


        inputField.imagesInput.imagesInput.add(appData);
        multiPhotoInputAdapter.addImage(appData);
        inputField.inputValid = isInputValid();
        inputListener.onInputUpdated(inputField.inputValid, inputField);

    }

    public void populate(){

    }

    int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}
