package com.realm.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;


import com.realm.utils.svars;


public class ApplicationErrorHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler defaultUEH;
        private Context context = null;
String logTag="SpartaApplicationErrorHandler";
        public ApplicationErrorHandler(Context context) {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
            this.context = context;
        }

        public void uncaughtException(Thread t, Throwable e) {
            StackTraceElement[] arr = e.getStackTrace();
            String report = e.toString()+"\n\n";
            report += "--------- Occurred at "+svars.gett_time()+" ---------\n";
            report += "Error:  "+e.getMessage()+" ---------\n";
            report += "Localized:  "+e.getLocalizedMessage()+" ---------\n";
            report += "--------- Stack trace ---------\n\n";
            for (int i=0; i<arr.length; i++) {
                report += "    "+arr[i].toString()+"\n";
            }
            report += "-------------------------------\n\n";

            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause

            report += "--------- Cause ---------\n\n";
            Throwable cause = e.getCause();
            report += cause.getMessage() + "\n";
            report += cause.getLocalizedMessage() + "\n\n";

            if(cause != null) {
                report += cause.toString() + "\n\n";
                arr = cause.getStackTrace();
                for (int i=0; i<arr.length; i++) {
                    report += "    "+arr[i].toString()+"\n";
                }
            }
            report += "-------------------------------\n\n";
try{
//    String root = cntx.getExternalFilesDir(null).getAbsolutePath() + "/traces/"+ svars.getCurrentDateOfMonth();
    String root = svars.current_app_config(context).crashReportsFolder;
    Log.e(logTag, "PATH: " + root);



    File file = new File(root);
    file.mkdirs();

        File trace_file = new File(file, "trc_"+ System.currentTimeMillis()+".trc");
        try(FileWriter writer = new FileWriter(trace_file,true)){
            writer.append(report);
            writer.flush();

        }



}catch (Exception ex){}

            EventLogger.logEvent("Crash Report:\n"+report);

            defaultUEH.uncaughtException(t, e);
        }
    }

