package com.realm.utils.FormTools.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.realm.activities.SpartaAppCompactFingerPrintActivity;
import com.realm.R;
import com.realm.utils.FormTools.FingerprintCapture;
import com.realm.utils.FormTools.FormEdittext;
import com.realm.utils.FormTools.ImageCapture;
import com.realm.utils.FormTools.SearchSpinner;
import com.realm.utils.FormTools.SignatureCapture;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.InputGroup;
import com.realm.utils.FormTools.models.MemberFingerprint;


public class FormAdapter_ extends RecyclerView.Adapter<FormAdapter_.view> {

    Activity activity;
    public InputGroup page;
    public FingerprintCapture activeFingerprintCapture;
    public ImageCapture activeImageCapture;


    public interface InputListener {
        void onInputAvailable(InputField inputField);

        boolean onInputActivityStatusRequired(InputField inputField);
    }

    InputListener inputListener;

    public FormAdapter_(Activity activity, InputGroup page, InputListener inputListener) {
        this.activity = activity;
        this.page = page;


        this.inputListener = inputListener;
    }

    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        this.cntxt = parent.getContext();
        View view = LayoutInflater.from(activity).inflate(R.layout.item_form_input, parent, false);

        return new view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view holder, int position) {
        holder.populate(position);

    }


    @Override
    public int getItemCount() {
        return page.inputFields.size();
    }

    public class view extends RecyclerView.ViewHolder implements View.OnClickListener {
        public FingerprintCapture fingerprintCapture;
        public ImageCapture imageCapture;
        public SignatureCapture signatureCapture;
        public SearchSpinner selectionInput;
        public FormEdittext textInput;
        public int position;
        public ImageView icon;


        view(View itemView) {
            super(itemView);

            selectionInput = itemView.findViewById(R.id.selection_input);
            textInput = itemView.findViewById(R.id.text_input);
            fingerprintCapture = itemView.findViewById(R.id.fingerprint_capture);
            imageCapture = itemView.findViewById(R.id.image_capture);
            signatureCapture = itemView.findViewById(R.id.signature_capture);

        }
        boolean isInputFieldInputAffectingOtherFieldsInThisPage(    InputField inputField){
            for(InputField inputField1:page.inputFields){
                if(inputField1.inputFieldInputConstraint!=null&&inputField1.inputFieldInputConstraint.independent_input_field.equals(inputField.sid)){
                    return true;
                }
            }
            return false;
        }
        void populate(int position) {
            this.position = position;
            InputField inputField = page.inputFields.get(position);
            if (inputField.inputFieldInputConstraint != null) {
                if (!inputListener.onInputActivityStatusRequired(inputField)) {
                    inputField.input = null;
                    inputField.inputValid = true;
//    itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = 0;
                    itemView.setLayoutParams(params);
                    return;
                } else {
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    itemView.setLayoutParams(params);
                    inputField.inputValid = false;

                }
            } else {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                itemView.setLayoutParams(params);

            }
//            formReview.setVisibility(inputField.input_type.equalsIgnoreCase(InputField.InputType.FormReview.ordinal() + "") ? View.VISIBLE : View.GONE);
            signatureCapture.setVisibility(inputField.input_type.equalsIgnoreCase(InputField.InputType.Signature.ordinal() + "") ? View.VISIBLE : View.GONE);
            imageCapture.setVisibility(inputField.input_type.equalsIgnoreCase(InputField.InputType.Image.ordinal() + "") ? View.VISIBLE : View.GONE);
            fingerprintCapture.setVisibility(inputField.input_type.equalsIgnoreCase(InputField.InputType.Fingerprint.ordinal() + "") ? View.VISIBLE : View.GONE);
            selectionInput.setVisibility(inputField.input_type.equalsIgnoreCase(InputField.InputType.Selection.ordinal() + "") ? View.VISIBLE : View.GONE);
            textInput.setVisibility(inputField.input_type.equalsIgnoreCase(InputField.InputType.Text.ordinal() + "") ? View.VISIBLE :
                    inputField.input_type.equalsIgnoreCase(InputField.InputType.Date.ordinal() + "") ? View.VISIBLE :
                            inputField.input_type.equalsIgnoreCase(InputField.InputType.DateTime.ordinal() + "") ? View.VISIBLE :
                                    inputField.input_type.equalsIgnoreCase(InputField.InputType.Number.ordinal() + "") ? View.VISIBLE :
                                            View.GONE);

            if (textInput.getVisibility() == View.VISIBLE) {
//                textInput.setInput(inputField.input);
                textInput.setInputField(inputField, new FormEdittext.InputListener() {
                    @Override
                    public void onInputAvailable(boolean valid, String input) {
                        inputField.input = input;
                        inputField.inputValid = textInput.isInputValid();
                        if(isInputFieldInputAffectingOtherFieldsInThisPage(inputField)){
                            try {
                                notifyDataSetChanged();
                            } catch (Exception ex) {
                            }
                        }
                        inputListener.onInputAvailable(inputField);

                    }
                });
                inputField.inputValid = textInput.isInputValid();


            } else if (selectionInput.getVisibility() == View.VISIBLE) {
                selectionInput.setInputField(inputField, new SearchSpinner.InputListener() {
                    @Override
                    public void onInputAvailable(boolean valid, String input) {
                        inputField.input = input;
//                        inputField.inputValid = selectionInput.isInputValid();
                        if(isInputFieldInputAffectingOtherFieldsInThisPage(inputField)){
                            try {
                                notifyDataSetChanged();
                            } catch (Exception ex) {
                            }
                        }

                        inputListener.onInputAvailable(inputField);
                    }
                });
//                selectionInput.setInput(inputField.input);
//                inputField.inputValid = selectionInput.isInputValid();

            } else if (fingerprintCapture.getVisibility() == View.VISIBLE) {
                activeFingerprintCapture=fingerprintCapture;
                fingerprintCapture.initFingerprint(activity);
                fingerprintCapture.setInputField(inputField, new FingerprintCapture.InputListener() {
                    @Override
                    public void onInputAvailable(boolean valid, ArrayList<MemberFingerprint> input) {
                        FingerprintCapture.InputListener.super.onInputAvailable(valid, input);
                        inputListener.onInputAvailable(inputField);

                    }
                });

            }else if (imageCapture.getVisibility() == View.VISIBLE) {
                imageCapture.setActivity((SpartaAppCompactFingerPrintActivity) activity);
                imageCapture.setInputField(inputField, new ImageCapture.InputListener() {
                    @Override
                    public void onInputRequested(InputField inputField) {
                        activeImageCapture=imageCapture;
                    }
                });


            }else if (signatureCapture.getVisibility() == View.VISIBLE) {

                signatureCapture.setInputField(inputField, new SignatureCapture.InputListener() {
                    @Override
                    public void onInputRequested(InputField inputField) {

                    }
                });


            }


        }

        @Override
        public void onClick(View view) {

        }
    }
}
