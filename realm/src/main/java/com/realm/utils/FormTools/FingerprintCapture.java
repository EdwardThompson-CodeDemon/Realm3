package com.realm.utils.FormTools;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.realm.activities.SpartaAppCompactFingerPrintActivity;
import com.realm.R;
import com.realm.utils.biometrics.fp.BTV2;
import com.realm.utils.biometrics.fp.FP08_UAREU;
import com.realm.utils.biometrics.fp.FingerprintManger;
import com.realm.utils.biometrics.fp.MorphoFingerprintManager;
import com.realm.utils.biometrics.fp.T801;
import com.realm.utils.FormTools.adapters.FingerprintSkippingReasonAdapter;
import com.realm.utils.FormTools.adapters.FingerprintToCaptureAdapter;
import com.realm.utils.FormTools.models.Fingerprint;
import com.realm.utils.FormTools.models.FingerprintSkippingReason;
import com.realm.utils.FormTools.models.FingerprintToCapture;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.MemberFingerprint;

public class FingerprintCapture extends ConstraintLayout {
    TextView title, instructions;
    Button skipFinger;
    ImageView visualDisplay;
    RecyclerView fpGrid;
    ArrayList<FingerprintToCapture> fingerprintsToCapture = new ArrayList<>();
    ArrayList<FingerprintSkippingReason> reasons_to_skip = new ArrayList<>();
    ArrayList<FingerprintSkippingReason> selectedReasonsToSkip = new ArrayList<>();
    ArrayList<MemberFingerprint> memberFingerprints = new ArrayList<>();
    int activeFingerprintPosition = 0;

    FingerprintManger fingerprintManger;
    InputField inputField=new InputField();

   InputListener inputListener=new InputListener() {
        @Override
        public void onInputAvailable(boolean valid, ArrayList<MemberFingerprint> input) {

        }
    };




    public interface InputListener
    {
        default void onInputAvailable(boolean valid,ArrayList<MemberFingerprint> input){

        }
  default void onRequestedToSkip(FingerprintToCapture fingerprintToCapture){

        }



    }
    public FingerprintCapture(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public FingerprintCapture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public FingerprintCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public FingerprintCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI();
    }


    void setupUI() {
        title = new TextView(getContext(), null, R.style.Theme_Realm_SubTitle);
        title.setText("Fingerprint Capture");
        title.setTypeface(null, Typeface.BOLD);
        int title_id = View.generateViewId();
        title.setId(title_id);

        visualDisplay = new ImageView(getContext());
        visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.fp_reg_thumb));
        int visualDisplayId = View.generateViewId();
        visualDisplay.setId(visualDisplayId);

        instructions = new TextView(getContext());
        instructions.setText("Click to capture any finger then place the active finger on the scanner");
        int instructionsId = View.generateViewId();
        instructions.setId(instructionsId);

        fpGrid = new RecyclerView(getContext());
        int fpGridId = View.generateViewId();
        fpGrid.setId(fpGridId);

        skipFinger = new Button(getContext());
        skipFinger.setText("Skip finger");
        int skipFingerId = View.generateViewId();
        skipFinger.setId(skipFingerId);


        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topToTop = PARENT_ID;
        titleParams.startToStart = PARENT_ID;
        titleParams.endToEnd = PARENT_ID;
        titleParams.bottomMargin = 0;
        addView(title, titleParams);


        LayoutParams visualDisplayParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(300));
        visualDisplayParams.topToBottom = title_id;
        visualDisplayParams.startToStart = PARENT_ID;
        visualDisplayParams.endToEnd = PARENT_ID;
        visualDisplayParams.bottomMargin = 0;
        addView(visualDisplay, visualDisplayParams);


        LayoutParams instructionsParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instructionsParams.topToBottom = visualDisplayId;
        instructionsParams.startToStart = PARENT_ID;
        instructionsParams.endToEnd = PARENT_ID;
        instructionsParams.rightMargin = dpToPx(32);
        instructionsParams.leftMargin = dpToPx(32);
        addView(instructions, instructionsParams);

        LayoutParams fpGridParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fpGridParams.topToBottom = instructionsId;
        fpGridParams.startToStart = PARENT_ID;
        fpGridParams.endToEnd = PARENT_ID;
        fpGridParams.bottomMargin = 0;
        addView(fpGrid, fpGridParams);

        LayoutParams skipFingerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        skipFingerParams.topToTop = PARENT_ID;
        skipFingerParams.endToEnd = PARENT_ID;
        skipFingerParams.rightMargin = dpToPx(16);
        addView(skipFinger, skipFingerParams);
        setFingerprintsToCapture(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        skipFinger.setBackground(getContext().getDrawable(R.drawable.button_positive));
        skipFinger.setTextColor(getContext().getColor(R.color.white));
        int padding=dpToPx(10);
        skipFinger.setPadding(padding,dpToPx(5),padding,dpToPx(5));
        fpGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fpGrid.setAdapter(new FingerprintToCaptureAdapter(fingerprintsToCapture, (fingerprint, view, position) -> {
            for (FingerprintToCapture fp : fingerprintsToCapture) {
                fp.capturing = false;
            }
            fingerprintsToCapture.get(position).wsq=null;
            fingerprintsToCapture.get(position).jpeg=null;
            fingerprintsToCapture.get(position).iso=null;
            setActiveFingerprint(position);
            fingerprintManger.capture();
        }));
        skipFinger.setOnClickListener(view -> skipCurrentActiveFingerprint());
        setActiveFingerprint(0);
    }
    public void setTitle(String title_) {
        this.title.setText(title_);
    }
   public void setInstructions(String instructions) {
        this.instructions.setText(instructions);
    }

    public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;

        setTitle(inputField.title);
        setInstructions(inputField.instructions);
        populateFingerprintToCapture(inputField.fingerprintsInput.fingerprintsInput);
        inputField.inputValid=validated();
        reasons_to_skip.clear();
        reasons_to_skip.add(0, new FingerprintSkippingReason("1", "Finger is missing"));
        reasons_to_skip.add(0, new FingerprintSkippingReason("2", "Finger is unreadable"));
        reasons_to_skip.add(0, new FingerprintSkippingReason("0", getContext().getString(R.string.select_reason_to_skip)));
    }
    boolean validated() {
        boolean valid = true;
        // if(1==1){return valid;}
        for (FingerprintToCapture fingerprint : fingerprintsToCapture) {
            if ((fingerprint.wsq == null || fingerprint.jpeg == null || fingerprint.iso == null || fingerprint.wsq.length() < 5) && checkFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index) == false) {
                Toast.makeText(getContext(), getContext().getString(R.string.finerprint_missing) + fingerprint.name, Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return valid;
    }
    void  populateFingerprintToCapture(ArrayList<MemberFingerprint> fingerprintsInput){
        for(Fingerprint fingerprint:fingerprintsInput){
            for(FingerprintToCapture fingerprintToCapture:fingerprintsToCapture){
                if(fingerprint.template!=null&&fingerprint.fingerprint_index.equals((fingerprintToCapture.index+1)+"")){
                    switch (fingerprint.template_format){
                        case "1":
                            fingerprintToCapture.wsq=fingerprint.template;

                            break;
                    case "2":
                        fingerprintToCapture.jpeg =fingerprint.template;

                        break;
                    case "3":
                        fingerprintToCapture.iso=fingerprint.template;

                        break;


                    }

                }

            }
        }
        memberFingerprints.clear();
            for(FingerprintToCapture fingerprintToCapture:fingerprintsToCapture){
                MemberFingerprint memberFingerprintWsq = new MemberFingerprint(FingerprintDataType.WSQ.ordinal()+"",fingerprintToCapture.wsq, (fingerprintToCapture.index+1) + "", null);
                MemberFingerprint memberFingerprintImg = new MemberFingerprint(FingerprintDataType.JPEG.ordinal()+"",fingerprintToCapture.jpeg, (fingerprintToCapture.index+1) + "", null);
                MemberFingerprint memberFingerprintIso = new MemberFingerprint(FingerprintDataType.ISO.ordinal()+"",fingerprintToCapture.iso, (fingerprintToCapture.index+1) + "", null);


                for(MemberFingerprint fingerprint:fingerprintsInput){
                    if(fingerprint.template!=null&&fingerprint.fingerprint_index.equals(fingerprintToCapture.index+"")){
                        switch (fingerprint.template_format){
                            case "1":
                                memberFingerprintWsq.template=fingerprint.template;
                                memberFingerprintWsq.member_transaction_no=fingerprint.member_transaction_no;
                                break;
                            case "2":
                                memberFingerprintImg.template =fingerprint.template;
                                memberFingerprintWsq.member_transaction_no=fingerprint.member_transaction_no;

                                break;
                            case "3":
                                memberFingerprintIso.template=fingerprint.template;
                                memberFingerprintWsq.member_transaction_no=fingerprint.member_transaction_no;

                                break;


                        }
                    }
                }
                if(memberFingerprintWsq.template!=null) {
                    memberFingerprints.add(memberFingerprintWsq);
                }
                if(fingerprintToCapture.jpeg!=null) {
                    memberFingerprints.add(memberFingerprintImg);
                }
                if(fingerprintToCapture.iso!=null) {
                    memberFingerprints.add(memberFingerprintIso);
                }
                }

            }



        public void initFingerprint(Activity activity) {
        fingerprintManger = getFingerprintManger(activity);
        ((SpartaAppCompactFingerPrintActivity)activity).startFPModule(fingerprintManger);
    }


    FingerprintManger getFingerprintManger(Activity activity)
    {
        ArrayList<String>uareu_devices=new ArrayList<>();
        ArrayList<String>t801_devices=new ArrayList<>();
        ArrayList<String>famoco_devices=new ArrayList<>();
        uareu_devices.add("SF807N");
        uareu_devices.add("F807");
        uareu_devices.add("FP-08");
        uareu_devices.add("SF-08");
        uareu_devices.add("SF-807");
        uareu_devices.add("S807");
        uareu_devices.add("ax6737_65_n");
        uareu_devices.add("SF-807N");
        t801_devices.add("SEEA900");
        famoco_devices.add("FX205");
        if(uareu_devices.contains(Build.MODEL))return new FP08_UAREU(activity);
        if(t801_devices.contains(Build.MODEL))return new T801(activity);
        if(famoco_devices.contains(Build.MODEL))return new MorphoFingerprintManager(activity);

        return new BTV2(activity);
    }
   public static FingerprintManger getDeviceFingerprintManger(Activity activity)
    {
        ArrayList<String>uareu_devices=new ArrayList<>();
        ArrayList<String>t801_devices=new ArrayList<>();
        ArrayList<String>famoco_devices=new ArrayList<>();
        uareu_devices.add("SF807N");
        uareu_devices.add("F807");
        uareu_devices.add("FP-08");
        uareu_devices.add("SF-08");
        uareu_devices.add("SF-807");
        uareu_devices.add("S807");
        uareu_devices.add("ax6737_65_n");
        uareu_devices.add("SF-807N");
        t801_devices.add("SEEA900");
        famoco_devices.add("FX205");
        if(uareu_devices.contains(Build.MODEL))return new FP08_UAREU(activity);
        if(t801_devices.contains(Build.MODEL))return new T801(activity);
        if(famoco_devices.contains(Build.MODEL))return new MorphoFingerprintManager(activity);

        return new BTV2(activity);
    }
    public enum FingerprintDataType {
        None,

        WSQ,
        JPEG,
        ISO


    }

    public void setData(FingerprintDataType fingerprintDataType, String data) {

        FingerprintToCapture fingerprint = fingerprintsToCapture.get(activeFingerprintPosition);
        switch (fingerprintDataType) {
            case WSQ:
                fingerprint.wsq = data;
                break;
            case JPEG:
                fingerprint.jpeg = data;

                break;
            case ISO:
                fingerprint.iso = data;

                break;

        }
        removeFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index);
        fingerprint.skipped = false;
        if ((fingerprint.wsq == null || fingerprint.jpeg == null || fingerprint.iso == null || fingerprint.wsq.length() < 10) && checkFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index) == false) {

        } else {
            if (activeFingerprintPosition < (fingerprintsToCapture.size() - 1)&&((System.currentTimeMillis()-lastTimeAutoMovedToNext)>=minTimeAutoMovedToNext)) {
                lastTimeAutoMovedToNext=System.currentTimeMillis();
            setActiveFingerprint(activeFingerprintPosition + 1);
            } else if (activeFingerprintPosition == (fingerprintsToCapture.size() - 1)) {
//                procceed();

            }
            memberFingerprints.clear();
            for(FingerprintToCapture fingerprintToCapture:fingerprintsToCapture){
                MemberFingerprint memberFingerprintWsq = new MemberFingerprint(FingerprintDataType.WSQ.ordinal()+"",fingerprintToCapture.wsq, (fingerprintToCapture.index+1) + "", null);
                MemberFingerprint memberFingerprintImg = new MemberFingerprint(FingerprintDataType.JPEG.ordinal()+"",fingerprintToCapture.jpeg, (fingerprintToCapture.index+1) + "", null);
                MemberFingerprint memberFingerprintIso = new MemberFingerprint(FingerprintDataType.ISO.ordinal()+"",fingerprintToCapture.iso, (fingerprintToCapture.index+1) + "", null);


                if(memberFingerprintWsq.template!=null) {
                    memberFingerprints.add(memberFingerprintWsq);
                }
                if(fingerprintToCapture.jpeg!=null) {
                    memberFingerprints.add(memberFingerprintImg);
                }
                if(fingerprintToCapture.iso!=null) {
                    memberFingerprints.add(memberFingerprintIso);
                }
            }
            inputField.inputValid=validated();
            inputField.fingerprintsInput.fingerprintsInput=memberFingerprints;
            inputListener.onInputAvailable(inputField.inputValid,memberFingerprints);
            fpGrid.getAdapter().notifyDataSetChanged();

        }


    }
long lastTimeAutoMovedToNext=0;
long minTimeAutoMovedToNext=500;
    void setActiveFingerprint(int position) {
//        fingerprintsToCapture.get(position>0?position-1:fingerprintsToCapture.size()-1).capturing = false;
        for (FingerprintToCapture fp : fingerprintsToCapture) {
                    fp.capturing = false;
                }
        activeFingerprintPosition = position;
        visualDisplay.setImageDrawable(getContext().getDrawable(fingerprintsToCapture.get(position).drawable_resource));
        fingerprintsToCapture.get(position).capturing = true;
        fpGrid.getAdapter().notifyDataSetChanged();
    }

    void setFingerprintsToCapture(int[] fps_to_capture) {
        for (int i = 0; i < fps_to_capture.length; i++) {
            switch (fps_to_capture[i]) {
                case 0:
                    FingerprintToCapture right_thumb = new FingerprintToCapture(0, R.drawable.fp_reg_thumb_right, getContext().getString(R.string.right_thumb));
                    fingerprintsToCapture.add(right_thumb);
                    break;
                case 1:
                    FingerprintToCapture right_index = new FingerprintToCapture(1, R.drawable.fp_reg_index_right, getContext().getString(R.string.right_index));
                    fingerprintsToCapture.add(right_index);
                    break;
                case 2:
                    FingerprintToCapture right_middle = new FingerprintToCapture(2, R.drawable.fp_reg_middle_right, getContext().getString(R.string.right_middle));
                    fingerprintsToCapture.add(right_middle);
                    break;
                case 3:
                    FingerprintToCapture right_ring = new FingerprintToCapture(3, R.drawable.fp_reg_ring_right, getContext().getString(R.string.right_ring));
                    fingerprintsToCapture.add(right_ring);
                    break;
                case 4:
                    FingerprintToCapture right_pinky = new FingerprintToCapture(4, R.drawable.fp_reg_pinky_right, getContext().getString(R.string.right_pinky));
                    fingerprintsToCapture.add(right_pinky);
                    break;
                case 5:
                    FingerprintToCapture left_thumb = new FingerprintToCapture(5, R.drawable.fp_reg_thumb, getContext().getString(R.string.left_thumb));
                    fingerprintsToCapture.add(left_thumb);
                    break;
                case 6:
                    FingerprintToCapture left_index = new FingerprintToCapture(6, R.drawable.fp_reg_index, getContext().getString(R.string.left_index));
                    fingerprintsToCapture.add(left_index);
                    break;
                case 7:
                    FingerprintToCapture left_middle = new FingerprintToCapture(7, R.drawable.fp_reg_middle, getContext().getString(R.string.left_middle));
                    fingerprintsToCapture.add(left_middle);
                    break;
                case 8:
                    FingerprintToCapture left_ring = new FingerprintToCapture(8, R.drawable.fp_reg_ring, getContext().getString(R.string.left_ring));
                    fingerprintsToCapture.add(left_ring);
                    break;
                case 9:
                    FingerprintToCapture left_pinky = new FingerprintToCapture(9, R.drawable.fp_reg_pinky, getContext().getString(R.string.left_pinky));
                    fingerprintsToCapture.add(left_pinky);
                    break;


            }

        }
    }

    void skipCurrentActiveFingerprint() {
        inputListener.onRequestedToSkip(fingerprintsToCapture.get(activeFingerprintPosition));
//        View aldv = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exception_code, null);
//        final AlertDialog ald = new AlertDialog.Builder(getContext())
//                .setView(aldv)
//                .setCancelable(false)
//                .show();
//        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        Button dismiss = (Button) aldv.findViewById(R.id.dismiss);
//        Button procceed_btn = (Button) aldv.findViewById(R.id.proceed);
//        Spinner reason_spn = (Spinner) aldv.findViewById(R.id.reason_selection);
//        reason_spn.setAdapter(new FingerprintSkippingReasonAdapter(getContext(), reasons_to_skip));
//
//        final EditText exception_code = (EditText) aldv.findViewById(R.id.exception_code_edt);
//        dismiss.setOnClickListener((v) -> ald.dismiss());
//
//
//        procceed_btn.setOnClickListener((v) -> {
//
//            if (reason_spn.getSelectedItemPosition() == 0) {
//                ald.findViewById(R.id.reason_spn_lay).setBackground(getContext().getDrawable(R.drawable.textback_error));
//
//            } else {
//                ald.dismiss();
//                for (FingerprintToCapture fp : fingerprintsToCapture) {
//                    fp.capturing = false;
//                }
//                FingerprintSkippingReason fingerprintSkippingReason = new FingerprintSkippingReason(fingerprintsToCapture.get(activeFingerprintPosition).index + "", reasons_to_skip.get(reason_spn.getSelectedItemPosition()).sid, "member_no");
//                removeFingerprintSkippingReason(selectedReasonsToSkip, fingerprintsToCapture.get(activeFingerprintPosition).index);
//                removeFingerprint(memberFingerprints, fingerprintsToCapture.get(activeFingerprintPosition).index);
//
//                selectedReasonsToSkip.add(fingerprintSkippingReason);
//                fingerprintsToCapture.get(activeFingerprintPosition).skipped = true;
//                fingerprintsToCapture.get(activeFingerprintPosition).wsq = null;
//                fingerprintsToCapture.get(activeFingerprintPosition).jpeg = null;
//                fingerprintsToCapture.get(activeFingerprintPosition).iso = null;
//                if (activeFingerprintPosition < (fingerprintsToCapture.size() - 1)) {
//                    setActiveFingerprint(activeFingerprintPosition + 1);
//
//                } else {
//                    setActiveFingerprint(activeFingerprintPosition);
//
////                    procceed();
//                }
//                inputField.inputValid=validated();
//            }
//        });

    }

    public void skipCurrentFinger(FingerprintSkippingReason fingerprintSkippingReason){

        removeFingerprintSkippingReason(selectedReasonsToSkip, fingerprintsToCapture.get(activeFingerprintPosition).index);
                removeFingerprint(memberFingerprints, fingerprintsToCapture.get(activeFingerprintPosition).index);

                selectedReasonsToSkip.add(fingerprintSkippingReason);
                fingerprintsToCapture.get(activeFingerprintPosition).skipped = true;
                fingerprintsToCapture.get(activeFingerprintPosition).wsq = null;
                fingerprintsToCapture.get(activeFingerprintPosition).jpeg = null;
                fingerprintsToCapture.get(activeFingerprintPosition).iso = null;
                if (activeFingerprintPosition < (fingerprintsToCapture.size() - 1)) {
                    setActiveFingerprint(activeFingerprintPosition + 1);

                } else {
                    setActiveFingerprint(activeFingerprintPosition);

//                    procceed();
                }
                inputField.inputValid=validated();
    }

    boolean checkFingerprintSkippingReason(ArrayList<FingerprintSkippingReason> fingerprintSkippingReasons, int fp_index) {
        int len = fingerprintSkippingReasons.size();
        for (int i = 0; i < len; i++) {
            if (fingerprintSkippingReasons.get(i).finger_index.equalsIgnoreCase(fp_index + "")) {
                return true;
            }
        }
        return false;
    }

    void removeFingerprintSkippingReason(ArrayList<FingerprintSkippingReason> fingerprintSkippingReasons, int fp_index) {
        int len = fingerprintSkippingReasons.size();
        for (int i = 0; i < len; i++) {
            if (fingerprintSkippingReasons.get(i).finger_index.equalsIgnoreCase(fp_index + "")) {
                fingerprintSkippingReasons.remove(i);
                return;
            }
        }

    }


    void removeFingerprint(ArrayList<MemberFingerprint> memberMemberFingerprints, int fp_index) {
        int len = memberMemberFingerprints.size();
        for (int i = 0; i < len; i++) {
            if (memberMemberFingerprints.get(i).fingerprint_index.equalsIgnoreCase(fp_index + "")) {
                memberMemberFingerprints.remove(i);
                return;
            }
        }

    }

    int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}


