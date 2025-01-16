package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;
import java.util.ArrayList;



@DynamicClass(table_name = "form")
public class Form extends RealmModel implements Serializable {



    @DynamicProperty(json_key = "title")
    public String title;

    @DynamicProperty(json_key = "code")
    public String code;

    @DynamicProperty(json_key = "instructions")
    public String instructions;


    //  @DynamicProperty(json_key = "inputGroups"/*,parent_column="",child_column=""*/)
    public ArrayList<InputGroup> inputGroups=new ArrayList<>();


    public Form() {


    }

    public Form(String sid, String title) {
        this.sid = sid;
this.title=title;

    }


    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }

//        Check if o is an instance of Complex or not
//          "null instanceof [type]" also returns false

        if (!(o instanceof RealmModel)) {
            return false;
        }


        // Compare the data members and return accordingly
        return sid.equalsIgnoreCase(((RealmModel) o).sid);
    }
}
