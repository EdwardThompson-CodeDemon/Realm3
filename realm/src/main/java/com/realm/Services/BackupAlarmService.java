package com.realm.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.realm.Models.BackupEntry;
import com.realm.Models.BackupUploadEntry;
import com.realm.Models.Query;
import com.realm.Realm;
import com.realm.annotations.sync_status;
import com.realm.utils.Conversions;
import com.realm.utils.backup.BackupManager;

import java.util.Calendar;




public class BackupAlarmService extends Service {
    String logTag = "BackupAlarmService";
    public BackupAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(logTag, "in onCreateCommand");
//        setNextAlarm(getApplicationContext(), 10000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(logTag, "in onStartCommand");
        BackupEntry backupEntry =  Realm.databaseManager.loadObject(BackupEntry.class,new Query().addOrderFilters("reg_time",false));
        if(backupEntry==null){
            Log.e(logTag, " Never backed up, backing up now");
            startBackup();
        }else{
            if(!backupEntry.reg_time.split(" ")[0].equals(Conversions.sdf_db_time.format(Calendar.getInstance().getTime()).split(" ")[0])){
                Log.e(logTag, " Never backed up today, backing up now");
                startBackup();

            }else{
                Log.e(logTag, " Already backed up today not backing up");

            }
        }


        return START_STICKY;
    }
    void startBackup(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BackupManager.backupAppData(BackupAlarmService.this, new BackupManager.BackupListener() {
                    public void onBackupArchiveCreated(BackupEntry backupEntry) {
                        Realm.databaseManager.insertObject(backupEntry);
                    }

                    @Override
                    public void onStatusChanged(String status) {
                        Log.e(logTag, "Backup status: " + status);

                    }

                    @Override
                    public void onBackupComplete() {
              stopSelf();
                    }

                    @Override
                    public void onBackupBegun(BackupManager.BackupType backupType, BackupEntry backupEntry) {
                        Realm.databaseManager.insertObject(new BackupUploadEntry(backupEntry.transaction_no,backupType.ordinal()+"",""+ sync_status.pending.ordinal()));
                    }

                    @Override
                    public void onBackupComplete(BackupManager.BackupType backupType, BackupEntry backupEntry) {
//                        DatabaseManager.database.execSQL("update backup_upload_entry set sync_status ='"+ sync_status.pending.ordinal()+"' where backup_transaction_no='"+backupEntry.transaction_no+"' and backup_type='"+backupType.ordinal()+"'");
//                        DatabaseManager.database.execSQL("update backup_upload_entry set upload_status ='"+ sync_status.syned.ordinal()+"' where backup_transaction_no='"+backupEntry.transaction_no+"' and backup_type='"+backupType.ordinal()+"'");
                    }



                    @Override
                    public void onBackupFailed(BackupManager.BackupType backupType, BackupEntry backupEntry) {

                    }
                });

            }
        }).start();


    }
    }