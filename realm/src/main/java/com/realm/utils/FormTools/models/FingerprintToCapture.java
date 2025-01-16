package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;


@DynamicClass(table_name = "fingerprint_to_capture")
//@SyncDescription(service_name = "Gender",download_link = "/Configurations/Gender/GetGender",is_ok_position = "JO:isOkay",download_array_position = "JO:result;JO:result",service_type = SyncDescription.service_type.Download,chunk_size = 100,storage_mode_check = true)
public class FingerprintToCapture extends RealmModel implements Serializable {

@DynamicProperty(json_key = "name")
public String name;

    public int index;

@DynamicProperty(json_key = "finger_index")
public String finger_index;

    public boolean capturing;
    public boolean skipped;


    public int drawable_resource;
    public String wsq;
    public String jpeg;
    public String iso;



    public FingerprintToCapture(String sid, String name)
    {
        this.sid=sid;
        this.name=name;

    }

    public FingerprintToCapture(int index,int drawable_resource,String name)
    {
        this.index=index;
        this.drawable_resource=drawable_resource;
        this.name=name;
    }




public FingerprintToCapture()
    {


    }

}
