package com.realm.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.utils.svars;

import java.io.Serializable;




@DynamicClass(table_name = "backup_entry")
public class BackupEntry extends RealmModel implements Serializable {


    @DynamicProperty(json_key = "file_name")
    public String file_name;

    @DynamicProperty(json_key = "file_size")
    public String file_size;

    @DynamicProperty(json_key = "file_path")
    public String file_path;


    public BackupEntry() {


    }

    public BackupEntry(String file_name, String file_size, String file_path) {
        this.file_name = file_name;
        this.file_size = file_size;
        this.file_path = file_path;


        this.transaction_no = svars.getTransactionNo();
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";
    }

}
