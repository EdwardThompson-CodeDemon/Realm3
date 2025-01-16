package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;

import com.realm.utils.svars;


@DynamicClass(table_name = "app_image")
//@SyncDescription(service_name = "App image", service_type = SyncDescription.service_type.Upload, storage_mode_check = true)
public class AppData extends RealmModel implements Serializable {

    //7194276
    @DynamicProperty(json_key = "data_index")
    public String data_index;

    @DynamicProperty(json_key = "data_type")
    public String data_type;

    @DynamicProperty(json_key = "parent_transaction_no")
    public String parent_transaction_no;

    @DynamicProperty(json_key = "parent_id")
    public String parent_id;


    @DynamicProperty(json_key = "data_source")
    public String data_source;


    @DynamicProperty(json_key = "data_format")
    public String data_format;

    @DynamicProperty(json_key = "compression_percent")
    public String compression_percent;


    @DynamicProperty(json_key = "data", storage_mode = DynamicProperty.storage_mode.FilePath)
    public String data;

    @DynamicProperty(json_key = "msid")
    public String msid = "";

    public enum ImageIndecies {
        None,
        idFront,
        birthCert,
        marriageDocument,
        profile_photo,
        student_card,
        signature,
        removed,
        professionDocument,
        parents_doc_pic,
        contributor_receipt_pic,
        payer_id_pic,
        bulleting_doc_pic,
        signe_pic,
        justif_pic,
        receipt_pic,
        idBack,
        payer_id_pic_back,
        otherDocument

    }


    public AppData() {


    }

    public AppData(String data) {
        this.data = data;
        this.transaction_no = svars.getTransactionNo();
        this.data_type = svars.data_type_indexes.photo + "";
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

    public AppData(String data, String data_type, String data_index) {
        this.data = data;
        this.transaction_no = svars.getTransactionNo();
//        this.data_type = svars.data_type_indexes.photo + "";
        this.data_type = data_type;
        this.data_index = data_index;
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
