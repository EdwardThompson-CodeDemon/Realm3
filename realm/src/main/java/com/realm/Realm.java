package com.realm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.luxand.FSDK;
import com.realm.Services.DatabaseManager;
import com.realm.annotations.RealmDataClass;
import com.realm.utils.AppConfig;
import com.realm.utils.ApplicationErrorHandler;
import com.realm.utils.svars;





public class Realm {

    public static Context context;
    public static RealmDataClass realm;
    public static DatabaseManager databaseManager;
    static String logTag = "Realm";

    public static void Initialize(Context cont, RealmDataClass realm_, String version_code, String app_name, AppConfig app_config) {
        context = cont;
        realm = realm_;
        svars.set_current_version(version_code);
        svars.set_current_app_name(app_name);
        svars.set_current_app_config(cont, app_config);
        Thread.setDefaultUncaughtExceptionHandler(new ApplicationErrorHandler(Realm.context));
        String[] supportedABIS = Build.SUPPORTED_ABIS;
        for (String abi : supportedABIS) {
            Log.e(logTag, "Supported device architecture " + abi);
        }
        if (app_config.APP_CONTROLL_MAIN_LINK != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Intent serviceIntent = new Intent(cont, App_updates.class);
//
//                cont.startService(serviceIntent);
//                cont.bindService(serviceIntent, new ServiceConnection() {
//                        @Override
//                        public void onServiceConnected(ComponentName name, IBinder service) {
//                            //retrieve an instance of the service here from the IBinder returned
//                            //from the onBind method to communicate with
//                        }
//
//                        @Override
//                        public void onServiceDisconnected(ComponentName name) {
//                        }
//                    }, Context.BIND_AUTO_CREATE);
//            }else{
//                context.startService(new Intent(  context, App_updates.class));
//
//            }
        }
        try {
            databaseManager = new DatabaseManager(Realm.context, realm);

        } catch (Exception ex) {
        }

    }

    public static void Initialize(Context cont, RealmDataClass realm_, String version_code, String app_name, AppConfig app_config, String FSDK_KEY) {
        Initialize(cont, realm_, version_code, app_name, app_config);

        if (FSDK_KEY != null) {
            try {
                int res = FSDK.ActivateLibrary(FSDK_KEY);
                FSDK.Initialize();
                FSDK.SetFaceDetectionParameters(false, false, 100);
                FSDK.SetFaceDetectionThreshold(5);

                if (res == FSDK.FSDKE_OK) {
                    Log.d("FSDK : ", "Initialization OK");

                } else {
                    Log.e("FSDK Error : ", "Initialization failed");

                }
            } catch (Exception e) {
                Log.e("exception ", "" + e.getMessage());
            }
        }


    }
}
