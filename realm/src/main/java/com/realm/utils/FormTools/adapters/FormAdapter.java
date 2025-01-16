package com.realm.utils.FormTools.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.realm.activities.SpartaAppCompactActivity;
import com.realm.activities.SpartaAppCompactFingerPrintActivity;
import com.realm.R;
import com.realm.Services.DatabaseManager;
import com.realm.utils.FormTools.FingerprintCapture;
import com.realm.utils.FormTools.FormEdittext;
import com.realm.utils.FormTools.ImageCapture;
import com.realm.utils.FormTools.MultiImageCapture;
import com.realm.utils.FormTools.SearchSpinner;
import com.realm.utils.FormTools.SignatureCapture;
import com.realm.utils.FormTools.models.IndependentInputFieldVariable;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.InputFieldInputConstraint;
import com.realm.utils.FormTools.models.InputFieldInputConstraintProcessingResult;
import com.realm.utils.FormTools.models.InputGroup;
import com.realm.utils.FormTools.models.MemberFingerprint;


public class FormAdapter extends RecyclerView.Adapter<FormAdapter.view> {

    Activity activity;
    Context context;
    public InputGroup page=new InputGroup();
    public FingerprintCapture activeFingerprintCapture;
    public ImageCapture activeImageCapture;
    public MultiImageCapture activeMultiImageCapture;


    public interface InputListener {
        void onInputAvailable(InputField inputField);

        boolean onInputActivityStatusRequired(InputField inputField);
        InputFieldInputConstraintProcessingResult onInputFieldInputConstraintProcessingResultRequired(InputField inputField);
    }

    InputListener inputListener;

    public FormAdapter(Activity activity, InputGroup page, InputListener inputListener) {
        this.activity = activity;
        this.page = page;

        this.inputListener = inputListener;
    }

    public FormAdapter(InputGroup page, InputListener inputListener) {
        this.page = page;
        this.inputListener = inputListener;
    }

    public FormAdapter(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setPage(InputGroup page) {
        this.page = page;
    }

    public void setActivity(Activity activity) {


        this.activity = activity;
    }

    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_form_input, parent, false);

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
        public MultiImageCapture multiImageCapture;
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
            multiImageCapture = itemView.findViewById(R.id.multi_image_capture);

        }

        boolean isInputFieldInputAffectingOtherFieldsInThisPage_(InputField inputField) {
            for (InputField inputField1 : page.inputFields) {
                if (inputField1.inputFieldInputConstraint != null && inputField1.inputFieldInputConstraint.independent_input_field.equals(inputField.sid)) {
                    return true;
                }
            }
            return false;
        }
 boolean isInputFieldInputAffectingOtherFieldsInThisPage(InputField inputField) {
            for (InputField inputField1 : page.inputFields) {
                if (inputField1.inputFieldInputConstraint != null && inputField1.inputFieldInputConstraint.independent_input_field.equals(inputField.sid)) {

                    return true;
                }else{
                    for (InputFieldInputConstraint inputFieldInputConstraint : inputField1.inputFieldInputConstraints) {
                        if (inputFieldInputConstraint.independent_input_field!=null&&inputFieldInputConstraint.independent_input_field.equals(inputField.sid)) {
                            return true;

                        }
                            for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
      if(independentInputFieldVariable.independent_input_field!=null&&independentInputFieldVariable.independent_input_field.equals(inputField.sid)){
          return true;
      }

  }
for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
      if(independentInputFieldVariable.independent_input_field!=null&&independentInputFieldVariable.independent_input_field.equals(inputField.sid)){
          return true;
      }

  }

                    }
                    }
            }
            return false;
        }

        void populate(int position) {
            this.position = position;
            InputField inputField = page.inputFields.get(position);
            InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult=null;
            if (inputField.inputFieldInputConstraints != null&&inputField.inputFieldInputConstraints.size()>0) {
                inputFieldInputConstraintProcessingResult=inputListener.onInputFieldInputConstraintProcessingResultRequired(inputField);
                if (!inputFieldInputConstraintProcessingResult.field_active) {
                    inputField.input = null;
                    inputField.inputValid = true;
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = 0;
                    itemView.setLayoutParams(params);
                    inputListener.onInputAvailable(inputField);
                    return;
                } else {
                    if (inputField.input_type.equals(InputField.InputType.ValueOnly.ordinal()+"")) {
                        inputField.inputValid = true;
                        ViewGroup.LayoutParams params = itemView.getLayoutParams();
                        params.height = 0;
                        itemView.setLayoutParams(params);
                    }else {
                        ViewGroup.LayoutParams params = itemView.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        itemView.setLayoutParams(params);
                        inputField.inputValid = false;
                    }

                }
            } else {
                if (inputField.input_type.equals(InputField.InputType.ValueOnly.ordinal()+"")) {
                    inputField.inputValid = true;
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = 0;
                    itemView.setLayoutParams(params);
                }else{
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    itemView.setLayoutParams(params);
                }


            }
            multiImageCapture.setVisibility(inputField.input_type.equalsIgnoreCase(InputField.InputType.MultiImage.ordinal() + "") ? View.VISIBLE : View.GONE);
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
                        if (isInputFieldInputAffectingOtherFieldsInThisPage(inputField)) {
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
                SearchSpinner.InputListener inputListener1= new SearchSpinner.InputListener() {
                    @Override
                    public void onInputAvailable(boolean valid, String input) {
                        inputField.input = input;
//                        inputField.inputValid = selectionInput.isInputValid();
                        if (isInputFieldInputAffectingOtherFieldsInThisPage(inputField)) {
                            try {
                                notifyDataSetChanged();
                            } catch (Exception ex) {
                            }
                        }

                        inputListener.onInputAvailable(inputField);
                    }
                };

                if (inputField.inputFieldInputConstraints != null&&inputField.inputFieldInputConstraints.size()>0&&inputFieldInputConstraintProcessingResult!=null&&inputFieldInputConstraintProcessingResult.tableFilters!=null&&inputFieldInputConstraintProcessingResult.tableFilters.size()>0) {
                    try {
                        String[] tblfilters=new String[inputFieldInputConstraintProcessingResult.tableFilters.size()];
                        tblfilters=inputFieldInputConstraintProcessingResult.tableFilters.toArray(tblfilters);

//                        String[] tblfilters=inputFieldInputConstraintProcessingResult.tableFilters.stream().map(s->s).toArray();
                        inputField.dataset_table_filter=DatabaseManager.concatString(" AND ",tblfilters);
//                        selectionInput.setDataset((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), new Query().setTableFilters(DatabaseManager.concatString(" AND ",tblfilters))),inputListener1);
                    } catch (Exception e) {
//                        throw new RuntimeException(e);
                    }
                }
                selectionInput.setInputField(inputField,inputListener1);
//                selectionInput.setInput(inputField.input);
//                inputField.inputValid = selectionInput.isInputValid();

            } else if (fingerprintCapture.getVisibility() == View.VISIBLE) {
                activeFingerprintCapture = fingerprintCapture;
                fingerprintCapture.initFingerprint(activity);
                fingerprintCapture.setInputField(inputField, new FingerprintCapture.InputListener() {
                    @Override
                    public void onInputAvailable(boolean valid, ArrayList<MemberFingerprint> input) {
                        FingerprintCapture.InputListener.super.onInputAvailable(valid, input);
                        inputListener.onInputAvailable(inputField);

                    }
                });

            } else if (imageCapture.getVisibility() == View.VISIBLE) {
                imageCapture.setActivity((SpartaAppCompactFingerPrintActivity) activity);
                imageCapture.setInputField(inputField, new ImageCapture.InputListener() {
                    @Override
                    public void onInputRequested(InputField inputField) {
                        activeImageCapture = imageCapture;
                    }
                });


            } else if (signatureCapture.getVisibility() == View.VISIBLE) {

                signatureCapture.setInputField(inputField, new SignatureCapture.InputListener() {
                    @Override
                    public void onInputRequested(InputField inputField) {

                    }
                });


            }else if (multiImageCapture.getVisibility() == View.VISIBLE) {
                multiImageCapture.setActivity((SpartaAppCompactActivity) activity);

                multiImageCapture.setInputField(inputField, new MultiImageCapture.InputListener() {
                    @Override
                    public void onInputRequested(InputField inputField) {
                        activeMultiImageCapture = multiImageCapture;

                    }

                    @Override
                    public void onInputUpdated(boolean valid, InputField inputField) {
                        MultiImageCapture.InputListener.super.onInputUpdated(valid, inputField);
                        inputListener.onInputAvailable(inputField);
                    }
                });


            }


        }

        @Override
        public void onClick(View view) {

        }
    }
}
