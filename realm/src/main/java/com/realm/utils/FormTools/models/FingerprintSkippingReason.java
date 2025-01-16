package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;


@DynamicClass(table_name = "fingerprint_skipping_reason")
//@SyncDescription(service_name = "Gender",download_link = "/Configurations/Gender/GetGender",is_ok_position = "JO:isOkay",download_array_position = "JO:result;JO:result",service_type = SyncDescription.service_type.Download,chunk_size = 100,storage_mode_check = true)
public class FingerprintSkippingReason extends RealmModel implements Serializable {

@DynamicProperty(json_key = "reason")
public String name;

@DynamicProperty(json_key = "member_transaction_no")
public String member_transaction_no;

@DynamicProperty(json_key = "finger_index")
public String finger_index;


@DynamicProperty(json_key = "reason_id")
public String reason_id;





    public FingerprintSkippingReason(String sid, String name)
    {
        this.sid=sid;
        this.name=name;

    }

    @DynamicProperty(json_key = "identifier")
    public String identifier;

    public FingerprintSkippingReason(String finger_index, String reason_id, String identifier)
    {
        this.finger_index=finger_index;
        this.reason_id=reason_id;
        this.identifier=identifier;

    }




public FingerprintSkippingReason()
    {


    }

}
