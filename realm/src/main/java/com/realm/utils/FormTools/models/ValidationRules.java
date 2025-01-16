package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;


public class ValidationRules extends RealmModel implements Serializable {


    @DynamicProperty(json_key = "input_field")
    public String input_field;
    @DynamicProperty(json_key = "mandatory")
    public String mandatory = "1";

    @DynamicProperty(json_key = "text_input_type")
    public String text_input_type = "4";
    @DynamicProperty(json_key = "max_text_input_length")
    public String max_text_input_length;
    @DynamicProperty(json_key = "min_text_input_length")
    public String min_text_input_length;
    @DynamicProperty(json_key = "min_text_input_length_error")
    public String min_text_input_length_error = "Invalid input";


    ////////////number input type////
    @DynamicProperty(json_key = "numeric_input_error")
    public String numeric_input_error;
    @DynamicProperty(json_key = "max_input_value")
    public String max_input_value;
    @DynamicProperty(json_key = "max_input_value_error")
    public String max_input_value_error;
    @DynamicProperty(json_key = "min_input_value")
    public String min_input_value;
    @DynamicProperty(json_key = "min_input_value_error")
    public String min_input_value_error;


    ////////////date time input type////
    @DynamicProperty(json_key = "allow_manual_date_input")
    public String allow_manual_input = "1";
    @DynamicProperty(json_key = "date_time_input_format")
    public String date_time_input_format;


    ////////////selection input type////
    @DynamicProperty(json_key = "validation_function")
    public String selectionR;
    @DynamicProperty(json_key = "value_not_selected_error")
    public String value_not_selected_error;


    ////////////image input type////
    @DynamicProperty(json_key = "size_profile")
    public String size_profile;
    @DynamicProperty(json_key = "image_source")
    public String image_source;
    @DynamicProperty(json_key = "compression_format")
    public String compression_format;
    @DynamicProperty(json_key = "compression_percent")
    public String compression_percent;

    @DynamicProperty(json_key = "min_images")
    public String min_images;
    @DynamicProperty(json_key = "max_images")
    public String max_images;


    ////////////selection input type////
    @DynamicProperty(json_key = "validation_function")
    public String validation_function;

    public enum MandatoryStatus {
        None,
        NonMandatory,
        Mandatory,
    }

    public enum SizeProfile {
        None,
        fullPage,
        ImageInputSize,
        formInputSize
    }

    public enum ImageSource {
        None,
        manualSelection,
        fileSystem,
        androidCamera,
        CustomHq,
        ocvIcao,
        luxandIcao,
        docScanner
    }


    public ValidationRules() {


    }

    public ValidationRules(String sid, String name) {
        this.sid = sid;

    }


    public enum TextInputType {
        None,
        alphanumeric,
        email,
        numeric,
        uppercase,
        lowercase,
        capWords,
        Date,
        Time,
        DateTime,
        phone,
        Image,
        Signature

    }
}
