package com.realm.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BackupAlarmReceiver extends BroadcastReceiver {
    String logTag="BackupAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(logTag, "onReceive: " );
    }
}
