package com.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.RealmModel;

import java.io.Serializable;
import java.util.ArrayList;

import com.realm.utils.svars;


@DynamicClass(table_name = "member_fingerprints")
public class MemberFingerprints extends RealmModel implements Serializable {

    public ArrayList<MemberFingerprint> fingerprintsInput = new ArrayList<>();




    public MemberFingerprints() {
        this.transaction_no = svars.getTransactionNo();


        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
