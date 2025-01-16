package com.realm.utils.FormTools;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;

import java.io.File;

import com.realm.activities.SpartaAppCompactFingerPrintActivity;
import com.realm.R;
import com.realm.utils.svars;
import com.realm.utils.FormTools.models.AppData;
import com.realm.utils.FormTools.models.InputField;

public class ImageCapture extends ConstraintLayout {

    TextView title, subTitle,spacer;
    CardView cardView;
    ConstraintLayout constraintLayout;
    ImageView imageView, deleteButton, configButton;
    InputField inputField = new InputField();
//    AppData memberImage;
SpartaAppCompactFingerPrintActivity activity;
    InputListener inputListener = new InputListener() {
        @Override
        public void onInputAvailable(boolean valid, AppData input) {

        }

        @Override
        public void onInputRequested(InputField inputField) {

        }
    };


    public interface InputListener {
        void onInputRequested(InputField inputField) ;
        default void onInputAvailable(boolean valid, AppData input) {

        }


    }

    public ImageCapture(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public ImageCapture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public ImageCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public ImageCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI();

    }

    void setupUI() {
        title = new TextView(getContext(), null, R.style.Theme_Realm_SubTitle);
        title.setText("Photo Capture");
        title.setTypeface(null, Typeface.BOLD);
        int title_id = View.generateViewId();
        title.setId(title_id);

        cardView = new CardView(getContext());
        cardView.setCardElevation(12);
        cardView.setRadius(dpToPx(5));
        int cardViewId = View.generateViewId();
        cardView.setId(cardViewId);

        constraintLayout = new ConstraintLayout(getContext());
        int constraintLayoutId = View.generateViewId();
        constraintLayout.setId(constraintLayoutId);

        subTitle = new TextView(getContext());
        subTitle.setText("front side");
        int subTitleId = View.generateViewId();
        subTitle.setId(subTitleId);

        spacer = new TextView(getContext());
        spacer.setText(" ");
        int spacereId = View.generateViewId();
        spacer.setId(spacereId);

        imageView = new ImageView(getContext());
        imageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_add_photo));
        int imageViewId = View.generateViewId();
        imageView.setId(imageViewId);

        deleteButton = new ImageView(getContext());
        deleteButton.setImageDrawable(getContext().getDrawable(android.R.drawable.ic_menu_delete));
        int deleteButtonId = View.generateViewId();
        deleteButton.setId(deleteButtonId);

        configButton = new ImageView(getContext());
        configButton.setImageDrawable(getContext().getDrawable(android.R.drawable.ic_menu_manage));
        int configButtonId = View.generateViewId();
        configButton.setId(configButtonId);


        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topToTop = PARENT_ID;
        titleParams.startToStart = PARENT_ID;
        titleParams.bottomMargin = 0;
        addView(title, titleParams);

        LayoutParams cardViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardViewParams.topToBottom = title_id;
        cardViewParams.leftMargin=dpToPx(10);
        cardViewParams.rightMargin=dpToPx(10);
        cardViewParams.topMargin=dpToPx(10);
        cardViewParams.bottomMargin=dpToPx(20);
        cardViewParams.startToStart = PARENT_ID;
        cardViewParams.endToEnd = PARENT_ID;

        addView(cardView, cardViewParams);

        LayoutParams spacerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        spacerParams.topToBottom = cardViewId;
        spacerParams.startToStart = PARENT_ID;
        spacerParams.endToEnd = PARENT_ID;

        addView(spacer, spacerParams);

        LayoutParams constraintLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardView.addView(constraintLayout, constraintLayoutParams);

        LayoutParams subTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subTitleParams.topToTop = PARENT_ID;
        subTitleParams.startToStart = PARENT_ID;
        subTitleParams.bottomMargin = 0;
        constraintLayout.addView(subTitle, subTitleParams);



        LayoutParams imageViewParams = new LayoutParams(0, dpToPx(200));
        imageViewParams.topToBottom = subTitleId;
        imageViewParams.startToStart = PARENT_ID;
        imageViewParams.endToEnd = PARENT_ID;
        imageViewParams.bottomMargin = 0;
        constraintLayout.addView(imageView, imageViewParams);



        LayoutParams deleteButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteButtonParams.topToTop = PARENT_ID;
        deleteButtonParams.endToEnd = PARENT_ID;

        constraintLayout.addView(deleteButton, deleteButtonParams);

   LayoutParams configButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        configButtonParams.topToBottom = deleteButtonId;
        configButtonParams.endToEnd = PARENT_ID;

        constraintLayout.addView(configButton, configButtonParams);
        int padding=dpToPx(10);
        constraintLayout.setPadding(padding,padding,padding,padding);
        padding=dpToPx(20);
        imageView.setPadding(padding,padding,padding,padding);
configButton.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View view) {

    }
});
imageView.setOnClickListener(view -> {
    inputListener.onInputRequested(inputField);
    activity.takePhoto(inputField.sid);

//        if(inputField.default_image_source.equals("1")){
//            if(!activity.isPackageInstalled("com.realm.iccaoluxand")) {
//                Toast.makeText(getContext(), "Latest ICAO not installed", Toast.LENGTH_LONG).show();
//                return;
//            }
//            Date time1 = null;
//            try {
//                time1 = Conversions.sdf_user_friendly_date.parse(Globals.registeringMember().birth_date);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            Calendar cc = Calendar.getInstance();
//            cc.add(Calendar.YEAR, -5);
//            Intent iccao_int = new Intent("sparta.icaochecker.icao_camera2");
//            iccao_int.putExtra("sid", "memberPhoto.transaction_no");
//            iccao_int.putExtra("camera_index", 0);
//            iccao_int.putExtra("camera_rotation", 90);
//            // iccao_int.putExtra("camera_rotation", 270);
//            iccao_int.putExtra("replaceFromTrackerOnRegistration", true);
//            iccao_int.putExtra("replaceOnRegistration", true);
//            iccao_int.putExtra("perform_checks", time1.before(cc.getTime()));
//            activity.startActivityForResult(iccao_int, 1);
//        }else{
//            svars.set_photo_camera_type(Realm.context,1,Integer.parseInt(inputField.default_image_source));
//            activity.take_photo(1,""+System.currentTimeMillis());
//        }



});

deleteButton.setOnClickListener(view -> clearImage());

//        title.setTextSize(dpToPx(20));


    }
    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        setTitle(inputField.title);
        setSubTitle(inputField.sub_title);
        setImage(inputField.imageInput);
        svars.setImageCameraType(getContext(),inputField.sid,Integer.parseInt(inputField.default_image_source));

//        setValidationRules(inputField.validationRules);

    }
    public void setActivity(SpartaAppCompactFingerPrintActivity activity) {
        this.activity = activity;
    }
 public void setTitle(String title_) {
        this.title.setText(title_);

    }

    public void setSubTitle(String subTitle) {
        this.subTitle.setText(subTitle);

    }
  public void clearImage() {
      inputField.inputValid=false;
      imageView.setImageURI(null);
      imageView.setImageBitmap(null);
      imageView.setImageDrawable(null);
//      imageView.setImageResource(null);
      imageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_add_photo));

//      imageView.getDrawable().setTintList(null);
//      ImageViewCompat.setImageTintList(imageView, null);
      inputField.imageInput=null;
  }
  public void setImage(AppData appData) {
      clearImage();
        if(appData ==null|| appData.data ==null){
            inputField.inputValid=false;
            return;
        }else {
            File file = new File(svars.current_app_config(getContext()).appDataFolder, appData.data);
            if (!file.exists() || file.length() < 500) {
                inputField.inputValid=false;
                return;
            }

        }
      try {

          imageView.setImageURI(null);
          imageView.setImageURI(Uri.parse(Uri.parse(svars.current_app_config(getContext()).appDataFolder) + appData.data));
          imageView.setColorFilter(null);
          imageView.getDrawable().setTintList(null);
          ImageViewCompat.setImageTintList(imageView, null);
          inputField.imageInput= appData;
          inputField.inputValid=true;
      } catch (Exception ex) {

      }

    }
    int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}
