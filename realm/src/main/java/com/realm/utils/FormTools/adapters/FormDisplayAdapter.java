package com.realm.utils.FormTools.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.realm.Models.Query;
import com.realm.R;
import com.realm.Realm;
import com.realm.utils.FormTools.models.InputField;
import com.realm.utils.FormTools.models.InputFieldInputConstraintProcessingResult;
import com.realm.utils.FormTools.models.InputGroup;
import com.realm.utils.FormTools.models.SelectionData;


public class FormDisplayAdapter extends RecyclerView.Adapter<FormDisplayAdapter.view> {

    Activity activity;
    Context context;
    public InputGroup page=new InputGroup();



    public interface InputListener {
        void onInputAvailable(InputField inputField);

        boolean onInputActivityStatusRequired(InputField inputField);
        InputFieldInputConstraintProcessingResult onInputFieldInputConstraintProcessingResultRequired(InputField inputField);
    }

    InputListener inputListener;

    public FormDisplayAdapter(Activity activity, InputGroup page, InputListener inputListener) {
        this.activity = activity;
        this.page = page;

        this.inputListener = inputListener;
    }

    public FormDisplayAdapter(InputGroup page, InputListener inputListener) {
        this.page = page;
        this.inputListener = inputListener;
    }

    public FormDisplayAdapter(InputListener inputListener) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_form_input_display, parent, false);

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
        public TextView title;
        public TextView value;
;
        public int position;


        view(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            value = itemView.findViewById(R.id.value);


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
                    return;
                } else {
                    if (inputField.input_type.equals(InputField.InputType.ValueOnly.ordinal()+"")) {
                        inputField.inputValid = true;
                        ViewGroup.LayoutParams params = itemView.getLayoutParams();
                        params.height = 0;
                        itemView.setLayoutParams(params);
                        return;
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
                    return;
                }else{
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
                    throw new RuntimeException(e);
                }

            }else {
                value.setText(inputField.input);

            }



        }

        @Override
        public void onClick(View view) {

        }
    }
}
