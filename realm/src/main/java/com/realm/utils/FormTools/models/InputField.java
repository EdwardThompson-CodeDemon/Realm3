package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;

import java.io.Serializable;
import java.util.ArrayList;


@DynamicClass(table_name = "input_field")
public class InputField extends InputGroup implements Serializable {


    @DynamicProperty(json_key = "object_field_name")
    public String object_field_name;
    @DynamicProperty(json_key = "dataset")
    public String dataset;
    @DynamicProperty(json_key = "dataset_table_filter")
    public String dataset_table_filter;
    //region Image input
    @DynamicProperty(json_key = "default_image_source")
    public String default_image_source;
    //endregion
    @DynamicProperty(json_key = "placeholder")
    public String placeholder;
    //region selection input
    @DynamicProperty(json_key = "search_placeholder")
    public String search_placeholder;
    @DynamicProperty(json_key = "search_placeholder")
    public String search_title;

    //endregion
    @DynamicProperty(json_key = "instructions")
    public String instructions;

    @DynamicProperty(json_key = "input_type")
    public String input_type;

    @DynamicProperty(json_key = "input_enabled")
    public String input_enabled;

    //region Input section
    @DynamicProperty(json_key = "input")
    public String input;
    public AppData imageInput = null;
    public AppImages imagesInput = new AppImages();
    public MemberFingerprints fingerprintsInput = new MemberFingerprints();
    public MemberFingerprint fingerprintInput = new MemberFingerprint();
    @DynamicProperty(json_key = "input_format")
    public String input_format;

    //endregion

    @DynamicProperty(json_key = "search_filter_status")
    public String search_filter_status;

    //    @DynamicProperty(json_key = "validationRules"/*,parent_column="",child_column=""*/)

    public InputField() {


    }

    public boolean inputValid = false;

//    public boolean isInputValid() {
//        return true;
//    }


    //Review constructor
    public InputField(String sid, String title, String instructions) {

        this.sid = sid;
        this.title = title;
        this.instructions = instructions;

        this.input_type = InputType.FormReview.ordinal() + "";
    }

    //Value only constructor
    public InputField(String sid) {

        this.sid = sid;
        this.input_type = InputType.ValueOnly.ordinal() + "";
    }

    //Value only constructor
    public InputField(String sid, String input) {
        this.input = input;

        this.sid = sid;
        this.input_type = InputType.ValueOnly.ordinal() + "";
    }

    //Image constructor
    public InputField(String sid, String title, String subTitle, String dataset, String defaultImageSource, String object_field_name, ValidationRules validationRules) {

        this.sid = sid;
        this.title = title;
        this.sub_title = subTitle;
        this.dataset = dataset;
        this.default_image_source = defaultImageSource;
        this.object_field_name = object_field_name;
        this.input_type = InputType.Image.ordinal() + "";
        this.validationRules = validationRules;
    }

    //Image constructor
    public InputField(String sid, String title, String subTitle, String dataset, String defaultImageSource, String object_field_name, ValidationRules validationRules, ArrayList<InputFieldInputConstraint> inputFieldInputConstraints) {
        this.inputFieldInputConstraints = inputFieldInputConstraints;

        this.sid = sid;
        this.title = title;
        this.sub_title = subTitle;
        this.dataset = dataset;
        this.default_image_source = defaultImageSource;
        this.object_field_name = object_field_name;
        this.input_type = InputType.Image.ordinal() + "";
        this.validationRules = validationRules;
    }

    //Selection constractor

    /**
     * This is the selection constructor
     *
     * @param sid
     * @param title
     * @param search_placeholder
     * @param search_title
     * @param dataset
     * @param parent
     * @param order_index
     * @param object_field_name
     * @param validationRules
     * @param inputFieldInputConstraints
     */
    public InputField(String sid, String title, String search_placeholder, String search_title, String dataset, String parent, String order_index, String object_field_name, ValidationRules validationRules, ArrayList<InputFieldInputConstraint> inputFieldInputConstraints) {
        super(sid, title, null, parent, order_index);
        this.search_placeholder = search_placeholder;
        this.search_title = search_title;
        this.title = title;
        this.input_type = InputType.Selection.ordinal() + "";
        this.dataset = dataset;
        this.parent = parent;
        this.order_index = order_index;
        this.object_field_name = object_field_name;
        this.validationRules = validationRules;
        this.inputFieldInputConstraints = inputFieldInputConstraints;
    }

    public InputField(String sid, String title, String placeholder, String dataset, String input_type, String parent, String order_index, ValidationRules validationRules, String object_field_name) {
        super(sid, title, null, parent, order_index);
        this.object_field_name = object_field_name;
        this.title = title;
        this.placeholder = placeholder;
        this.dataset = dataset;
        this.input_type = input_type;
        this.parent = parent;
        this.order_index = order_index;
        this.validationRules = validationRules;
        this.sid = sid;

    }

    public InputField(String sid, String title, String placeholder, String dataset, String input_type, String parent, String order_index, ValidationRules validationRules, String object_field_name, ArrayList<InputFieldInputConstraint> inputFieldInputConstraints) {
        super(sid, title, null, parent, order_index);
        this.object_field_name = object_field_name;
        this.inputFieldInputConstraints = inputFieldInputConstraints;
        this.title = title;
        this.placeholder = placeholder;
        this.dataset = dataset;
        this.input_type = input_type;
        this.parent = parent;
        this.order_index = order_index;
        this.validationRules = validationRules;
        this.sid = sid;

    }

    public InputField(String sid, String title, String placeholder, String dataset, String input_type, String parent, String order_index, ValidationRules validationRules, String object_field_name, String input_format) {
        super(sid, title, null, parent, order_index);
        this.object_field_name = object_field_name;
        this.input_format = input_format;
        this.title = title;
        this.placeholder = placeholder;
        this.dataset = dataset;
        this.input_type = input_type;
        this.parent = parent;
        this.order_index = order_index;
        this.validationRules = validationRules;
        this.sid = sid;

    }


    public enum InputType {
        None,
        Text,
        Number,
        Selection,
        MultiSelection,
        RadioSelection,
        Date,
        Time,
        DateTime,
        BarcodeScanText,
        Image,
        Signature,
        Fingerprint,
        FormReview,
        ValueOnly,
        MultiImage


    }
}
