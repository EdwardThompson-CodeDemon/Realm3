package com.realm.utils.FormTools.models;

import com.realm.annotations.DynamicProperty;

public class IndependentInputFieldVariable {
    @DynamicProperty(json_key = "independent_input_field_column")
    public String independent_input_field_column;

 @DynamicProperty(json_key = "independent_input_field")
    public String independent_input_field;

    @DynamicProperty(json_key = "operation_value")
    public String operation_value;
}
