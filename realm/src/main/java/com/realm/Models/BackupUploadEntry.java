package com.realm.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.utils.svars;

import java.io.Serializable;




@DynamicClass(table_name = "backup_upload_entry")
public class BackupUploadEntry extends RealmModel implements Serializable {


    @DynamicProperty(json_key = "backup_transaction_no")
    public String backup_transaction_no;

    @DynamicProperty(json_key = "backup_type")
    public String backup_type;

 @DynamicProperty(json_key = "upload_status")
    public String upload_status;



    public BackupUploadEntry() {


    }

    public BackupUploadEntry(String backup_transaction_no, String backup_type, String upload_status) {
        this.backup_transaction_no = backup_transaction_no;
        this.backup_type = backup_type;
        this.upload_status = upload_status;


        this.transaction_no = svars.getTransactionNo();
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
