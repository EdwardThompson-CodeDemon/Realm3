package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;

import com.realm.utils.svars;


public class Fingerprint extends RealmModel implements Serializable {


    @DynamicProperty(json_key = "index_no")
    public String fingerprint_index;


    @DynamicProperty(json_key = "image_name", storage_mode = DynamicProperty.storage_mode.FilePath)
    public String template;

    @DynamicProperty(json_key = "template_format")
    public String template_format;







    public Fingerprint() {
        this.transaction_no = svars.getTransactionNo();


        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
