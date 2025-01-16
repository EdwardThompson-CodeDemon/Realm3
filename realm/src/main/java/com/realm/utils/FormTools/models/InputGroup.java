package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;
import java.util.ArrayList;


@DynamicClass(table_name = "input_group")
public class InputGroup extends RealmModel implements Serializable {



    @DynamicProperty(json_key = "title")
    public String title;
    @DynamicProperty(json_key = "sub_title")
    public String sub_title;
    @DynamicProperty(json_key = "parent")
    public String parent;
     @DynamicProperty(json_key = "form")
    public String form;
    @DynamicProperty(json_key = "order_index")
    public String order_index;

  //  @DynamicProperty(json_key = "inputFields"/*,parent_column="",child_column=""*/)
    public ArrayList<InputField> inputFields=new ArrayList<>();
    public ArrayList<InputGroup> inputGroups=new ArrayList<>();
    public InputFieldInputConstraint inputFieldInputConstraint;
    public ArrayList<InputFieldInputConstraint> inputFieldInputConstraints=new ArrayList<>();
    public ValidationRules validationRules = new ValidationRules();

    public InputGroup( String sid,String title,String form,String parent,String order_index) {
        this.form = form;
        this.sid = sid;
        this.title = title;
        this.parent = parent;
        this.order_index = order_index;
    }

    public InputGroup() {

    }



    public enum InputType{
        None,
        Text,
        Number,
        Selection,
        Date,
        Time,
        DateTime,
        Image,
        Signature

    }
}
