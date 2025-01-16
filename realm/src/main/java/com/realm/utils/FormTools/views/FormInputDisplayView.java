package com.realm.utils.FormTools.views;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.realm.Adapters.GeneralDataAdapterListener;
import com.realm.Adapters.GeneralDataAdapterView;
import com.realm.Models.Query;
import com.realm.R;
import com.realm.Realm;
import com.realm.utils.FormTools.FormPlayer;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.InputFieldInputConstraintProcessingResult;
import com.realm.utils.FormTools.models.InputGroup;
import com.realm.utils.FormTools.models.SelectionData;

public class FormInputDisplayView extends GeneralDataAdapterView<InputField, GeneralDataAdapterListener<InputField>> {

    TextView title;
    TextView value;

    FormInputDisplayView(View itemView) {
        super(itemView);
    }


    @Override
    public void onBind(InputField inputField) {
        InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult = null;
        if (inputField.inputFieldInputConstraints != null && inputField.inputFieldInputConstraints.size() > 0) {
            inputFieldInputConstraintProcessingResult = FormPlayer.getInputFieldInputConstraintProcessingResult(inputField.inputFieldInputConstraints, items);
            if (!inputFieldInputConstraintProcessingResult.field_active) {
//                inputField.input = null;
//                inputField.inputValid = true;
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = 0;
                itemView.setLayoutParams(params);
                return;
            } else {
                if (inputField.input_type.equals(InputField.InputType.ValueOnly.ordinal() + "")) {
//                    inputField.inputValid = true;
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = 0;
                    itemView.setLayoutParams(params);
                    return;
                } else {
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    itemView.setLayoutParams(params);
//                    inputField.inputValid = false;
                }

            }
        } else {
            if (inputField.input_type.equals(InputField.InputType.ValueOnly.ordinal() + "")) {
//                inputField.inputValid = true;
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = 0;
                itemView.setLayoutParams(params);
                return;
            } else {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                itemView.setLayoutParams(params);
            }


        }

        title.setText(inputField.title);
        if (inputField.input_type.equals(InputGroup.InputType.Selection.ordinal() + "")) {

            try {
                value.setText(((SelectionData) Realm.databaseManager.loadObject(Class.forName(inputField.dataset), new Query().setTableFilters("sid=?").setQueryParams(inputField.input))).name);
            } catch (ClassNotFoundException e) {
                value.setText("!!!");
                throw new RuntimeException(e);
            }catch (NullPointerException e){
                value.setText(null);
            }

        } else {
            value.setText(inputField.input);

        }

    }

    public FormInputDisplayView() {
        super(LayoutInflater.from(Realm.context).inflate(R.layout.item_form_input_display, null, true), null);
        title = itemView.findViewById(R.id.title);
        value = itemView.findViewById(R.id.value);
    }


}
