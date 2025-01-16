package com.realm.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class EventLogger {
    public static Gpsprobe_r gps;
    public static String logTag = "EventLogger";
    public static Context context;
    public static void logEvent(String data) {
        gps = gps == null ? new Gpsprobe_r(context) : gps;
        String prefix = svars.sparta_EA_calendar().getTime()+ "   :   " + gps.getLatitude() + "," + gps.getLongitude() + "     =>";
        String root = svars.current_app_config(context).logsFolder;
        Log.e(logTag, "Log path: " + root);
        File file = new File(root);
        file.mkdirs();
        try {
//            File gpxfile = new File(file, svars.getCurrentDateOfMonth() + "" + svars.Log_file_name);
            File gpxfile = new File(file, svars.current_app_config(context).encryptedLogFile + svars.getCurrentDateOfMonth());
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(svars.APP_OPERATION_MODE == svars.OPERATION_MODE.DEV ? prefix + data + "\n" : s_cryptor.encrypt(prefix + data + "\n"));
            writer.flush();
            writer.close();
        } catch (Exception ex) {

        }

    }

      public static void logString(String data) {
        gps = gps == null ? new Gpsprobe_r(context) : gps;
        String prefix = svars.sparta_EA_calendar().getTime().toString() + "   :   " + gps.getLatitude() + "," + gps.getLongitude() + "     =>";
        String root = svars.current_app_config(context).logsFolder;
        Log.e(logTag, "PATH: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.current_app_config(context).verboseLogFile + svars.getCurrentDateOfMonth());
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(svars.APP_OPERATION_MODE != svars.OPERATION_MODE.DEV ? prefix + data + "\n" : prefix + data + "\n");
            writer.flush();
            writer.close();
        } catch (Exception ex) {

        }

    }
}
