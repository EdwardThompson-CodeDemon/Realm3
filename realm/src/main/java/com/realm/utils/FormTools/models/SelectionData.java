package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;




public class SelectionData extends RealmModel implements Serializable {



    @DynamicProperty(json_key = "name")
    public String name;

    @DynamicProperty(json_key = "parent")
    public String parent;

    @DynamicProperty(json_key = "code")
    public String code;

    public boolean selected=false;

    public SelectionData() {


    }

    public SelectionData(String sid, String name) {
        this.sid = sid;
this.name=name;

    }



}
