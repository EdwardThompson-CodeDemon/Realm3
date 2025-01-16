package com.realm.Models;

import android.graphics.drawable.Drawable;

import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;

public class AppModule extends RealmModel implements Serializable {


    @DynamicProperty(json_key = "name")
    public String name;

    @DynamicProperty(json_key = "code")
    public String code;




    public boolean active = false;
    public Drawable icon;

    public AppModule() {

    }

    public AppModule(String code, String name, boolean active) {
        this.name = name;
        this.code = code;
        this.active = active;


    }
 public AppModule(Drawable icon, String code, String name, boolean active) {
        this.icon = icon;
        this.name = name;
        this.code = code;
        this.active = active;


    }
}
