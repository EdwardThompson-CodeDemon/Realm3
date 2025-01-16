package com.realm.utils.FormTools;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;

import java.io.File;

import com.realm.activities.SpartaAppCompactFingerPrintActivity;
import com.realm.R;
import com.realm.utils.svars;
import com.realm.utils.FormTools.models.AppData;
import com.realm.utils.FormTools.models.InputField;

public class SignatureCapture extends ConstraintLayout {

    TextView title, instructions,spacer;
    CardView cardView;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;
    ImageView imageView, deleteButton, lockButton;
    InputField inputField = new InputField();
    Signature signature;
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

    public SignatureCapture(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public SignatureCapture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public SignatureCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public SignatureCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI();

    }

    void setupUI() {
        title = new TextView(getContext(), null, R.style.Theme_Realm_SubTitle);
        title.setText("Registrant signature Capture");
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

        linearLayout = new LinearLayout(getContext());
        int linearLayoutId = View.generateViewId();
        linearLayout.setId(linearLayoutId);

        signature=new Signature(getContext());
        int signatureId = View.generateViewId();
        signature.setId(signatureId);

        instructions = new TextView(getContext());
        instructions.setText("This document certifies that you have been enrolled as a health insurance card applicant. It reproduces the information that concerns you as recorded by the system operators. It is your responsibility to check them and have any corrections to the encoding errors you have made immediately corrected");
        int subTitleId = View.generateViewId();
        instructions.setId(subTitleId);

        spacer = new TextView(getContext());
        spacer.setText(" ");
        int spacereId = View.generateViewId();
        spacer.setId(spacereId);

        imageView = new ImageView(getContext());
        imageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_add_photo));
        int imageViewId = View.generateViewId();
        imageView.setId(imageViewId);

        deleteButton = new ImageView(getContext());
//        deleteButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.delete);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.GRAY);
        deleteButton.setImageDrawable(wrappedDrawable);
//        deleteButton.setImageDrawable(getContext().getDrawable(R.drawable.delete));

        int deleteButtonId = View.generateViewId();
        deleteButton.setId(deleteButtonId);

        lockButton = new ImageView(getContext());
        lockButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_lock_open_24);
        wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.GRAY);
//        lockButton.setImageDrawable(wrappedDrawable);
        lockButton.setImageDrawable(getContext().getDrawable(R.drawable.ic_lock_open_24));
        int configButtonId = View.generateViewId();
        lockButton.setId(configButtonId);


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
        constraintLayout.addView(instructions, subTitleParams);



        LayoutParams linearLayoutParams = new LayoutParams(0, dpToPx(250));
        linearLayoutParams.topToBottom = subTitleId;
        linearLayoutParams.startToStart = PARENT_ID;
        linearLayoutParams.endToEnd = PARENT_ID;

  LayoutParams signatureParams = new LayoutParams(0, dpToPx(250));
        signatureParams.topToBottom = subTitleId;
        signatureParams.startToStart = PARENT_ID;
        signatureParams.endToEnd = PARENT_ID;

        constraintLayout.addView(signature, signatureParams);



        LayoutParams deleteButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteButtonParams.topToBottom = subTitleId;
        deleteButtonParams.endToEnd = PARENT_ID;

        constraintLayout.addView(deleteButton, deleteButtonParams);

   LayoutParams configButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        configButtonParams.topToTop = deleteButtonId;
        configButtonParams.endToStart = deleteButtonId;

        constraintLayout.addView(lockButton, configButtonParams);
        int padding=dpToPx(10);
        constraintLayout.setPadding(padding,padding,padding,padding);
        padding=dpToPx(20);
        imageView.setPadding(padding,padding,padding,padding);
          signature.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                signature.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
          signature.setInputListener(new Signature.InputListener() {
              @Override
              public void onInputAvailable(boolean valid) {
                  inputField.inputValid=valid;
                  inputField.input=null;
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                      cardView.setOutlineAmbientShadowColor(ContextCompat.getColor(getContext(), valid?R.color.gray: R.color.red));
                      cardView.setOutlineSpotShadowColor(ContextCompat.getColor(getContext(),valid?R.color.gray: R.color.red));
                  }
                  AppData memberPhoto = new AppData(signature.load_img());
                  inputField.imageInput=valid?memberPhoto:null;
              }
          });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signature.clear();
            }
        });
   lockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signature.locked=!signature.locked;
                lockButton.setImageDrawable(getContext().getDrawable(signature.locked?R.drawable.ic_lock:R.drawable.ic_lock_open_24));
            }
        });

        signature.setBackgroundColor(Color.WHITE);
//        linearLayout.addView(signature, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View view) {


    }
});





    }
    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        setTitle(inputField.title);
        setInstructions(inputField.instructions);
        setImage(inputField.imageInput);
//        setValidationRules(inputField.validationRules);

    }
    public void setActivity(SpartaAppCompactFingerPrintActivity activity) {
        this.activity = activity;
    }
 public void setTitle(String title_) {
        this.title.setText(title_);

    }

    public void setInstructions(String instrustions) {
        this.instructions.setText(instrustions);

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
