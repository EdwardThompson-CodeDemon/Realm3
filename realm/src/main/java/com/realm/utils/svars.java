package com.realm.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.realm.Models.EmailConfiguration;
import com.realm.utils.backup.BackupManager;
import com.realm.utils.bluetooth.bt_device_connector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;






/**
 * Created by THOMPSONS on 07-Feb-17.
 */

public class svars {
static Context context;
    public static final class data_type_indexes {
        public static final int
                fingerprints = 1,
                photo = 2,
                fingerprint_images_wsq = 3,
                fingerprint_images_jpg = 4,
                fingerprint_skipping_reason = 5;


    }
      public static String backupEmail() {
        SharedPreferences prefs = context.getSharedPreferences(svars.sharedprefsname, context.MODE_PRIVATE);
        return prefs.getString("backupEmail", null);

    }

    public static void setBackupEmail(String backupEmail) {

        SharedPreferences.Editor saver = context.getSharedPreferences(svars.sharedprefsname, context.MODE_PRIVATE).edit();

        saver.putString("backupEmail", backupEmail);
        saver.commit();

    }
    public static boolean isBackupTypeActive(BackupManager.BackupType backupType) {
        SharedPreferences prefs = context.getSharedPreferences(svars.sharedprefsname, context.MODE_PRIVATE);
        return prefs.getBoolean("isBackupTypeActive_"+backupType.name(), true);

    }

    public static void setIsBackupTypeActive(BackupManager.BackupType backupType, boolean active) {

        SharedPreferences.Editor saver = context.getSharedPreferences(svars.sharedprefsname, context.MODE_PRIVATE).edit();

        saver.putBoolean("isBackupTypeActive_"+backupType.name(), active);
        saver.commit();

    }
    public static void setBackupEmailConfiguration(EmailConfiguration config) {
        svars.setWorkingObject(config, "backup_email");
    }

    public static EmailConfiguration backupEmailConfiguration() {
        return svars.workingObject(EmailConfiguration.class, "backup_email");
    }


    public static void setWorkingObject( Object working_object, String id) {
        Context act=context;
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(working_object);

        saver.putString("wo::" + id + working_object.getClass().getName(), json);
        saver.commit();


    }

    public static <t> t workingObject(Class<t> s, String id) {
        Context act=context;
        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("wo::" + id + s.getName(), "");
        if (!json.equals("")) {
            try {
                return gson.fromJson(json, s);
            }catch (JsonSyntaxException e){
                return null;
            }
        } else {
            return null;
        }

    }
    public static String device_code(Activity act) {
        String deviceUniqueIdentifier = "";
        try {
            TelephonyManager tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                if (ActivityCompat.checkSelfPermission(act, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    deviceUniqueIdentifier = "0";
                }
                deviceUniqueIdentifier = tm.getDeviceId();
            }
        } catch (Exception ex) {
        }

        try{
            deviceUniqueIdentifier = deviceUniqueIdentifier + "|" + Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
        }catch (Exception EX){}
        return deviceUniqueIdentifier;

    }


    public static String device_code(Context act) {
        String deviceUniqueIdentifier = "";
        try {
            TelephonyManager tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                if (ActivityCompat.checkSelfPermission(act, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                  //  ActivityCompat.requestPermissions(act,new String[]{Manifest.permission.READ_PHONE_STATE},1);
                    deviceUniqueIdentifier= "0";
                }
                deviceUniqueIdentifier = tm.getDeviceId();
            }
        } catch (Exception ex) {
        }

     try{
         deviceUniqueIdentifier = deviceUniqueIdentifier + "|" + Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
     }catch (Exception EX){}
//        Log.e("Dev code =>", "" + deviceUniqueIdentifier);
        return deviceUniqueIdentifier;

    }




    public static int getCpuCores() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            // Use saurabh64's answer
            return getNumCoresOldPhones();
        }
    }


    public static int getNumCoresOldPhones() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    public static String getTransactionNo() {
        return (System.currentTimeMillis() + "_" + new Random().nextDouble() + "_" + new Random().nextDouble()).replace(".","_");
    }

    public static String device_specific_transaction_no(Context act) {
        return device_code(act) + "_" + System.currentTimeMillis() + "_" + svars.user_id(act);
    }

    public static String get_ip_forcefully() {
        String ipAddress = "";
        try {
            while (!ipAddress.startsWith("192.168.")) {
                overal:
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ipAddress = inetAddress.getHostAddress().toString();
                            if (ipAddress.startsWith("192.168.")) {
                                Log.e("Seraching IP :", "OK :" + inetAddress.getHostAddress());
                                Log.e("Seraching IP :", "OK :" + ipAddress);
                                break overal;
                            }
                        } else {

                            Log.e("Seraching IP :", "Loopback" + inetAddress.getHostAddress());

                        }
                    }
                }

            }
        } catch (SocketException ex) {
        }

        Log.e("Current IP :", "" + ipAddress);
        return ipAddress;
    }

    public static String get_ip() {
        String ipAddress = "";
        try {


            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress().toString();
                        if (ipAddress.startsWith("192.168.")) {
                            Log.e("Seraching IP :", "OK :" + inetAddress.getHostAddress());
                            Log.e("Seraching IP :", "OK :" + ipAddress);


                        }
                    } else {

                        Log.e("Seraching IP :", "Loopback" + inetAddress.getHostAddress());

                    }
                }
            }


        } catch (SocketException ex) {
        }

        Log.e("Current IP :", "" + ipAddress);
        return ipAddress;
    }




    public enum OPERATION_MODE {

        DEV,
        TRAINING,
        LIVE


    }




    public static OPERATION_MODE APP_OPERATION_MODE = OPERATION_MODE.LIVE;


       public static String GUIDE_NAME = "guide_.pdf";

    public static String sharedprefsname = "realm_SPARTASHAREDPREFS";




    public static String fromEmail = "wallmountedfpdevice@gmail.com";
    public static String fromPassword = "@wallmountedfp";
// public static String fromEmail="capturetandamobile@gmail.com";
//    public static String fromPassword="@Capturet&a123";


    public static List<String> toEmailList() {
        ArrayList<String> toEmailLis = new ArrayList<>();
        toEmailLis.add("edward@cs4africa.com");

        return (List<String>) toEmailLis;
    }



    public static void set_current_version(String current_version) {
        Context act = context;
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("current_version", current_version);
        saver.commit();

    }

    public static void set_current_app_name(String current_app_name) {

        Context act = context;
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("current_app_name", current_app_name);
        saver.commit();

    }

    public static String current_version() {
        SharedPreferences prefs = context.getSharedPreferences(svars.sharedprefsname, context.MODE_PRIVATE);
        return prefs.getString("current_version", "R.E.A.L.M");


    }

    public static String current_app_name() {
        SharedPreferences prefs = context.getSharedPreferences(svars.sharedprefsname, context.MODE_PRIVATE);
        return prefs.getString("current_app_name", "R.E.A.L.M");


    }

    public static void set_current_app_config(Context act, AppConfig app_config) {


        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(app_config);

        saver.putString("current_app_config", json);
        saver.commit();

    }

    public static AppConfig current_app_config(Context act) {

        AppConfig app = null;

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("current_app_config", "");
        app = gson.fromJson(json, AppConfig.class);
        return app;


    }
 public static AppConfig current_app_config() {

        AppConfig app = null;

        SharedPreferences prefs = context.getSharedPreferences(svars.sharedprefsname, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("current_app_config", "");
        app = gson.fromJson(json, AppConfig.class);
        return app;


    }




    public static int default_sync_interval_mins = 20;

    public static int sync_interval_mins(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getInt("sync_interval_mins", default_sync_interval_mins);

    }

    public static void set_sync_interval_mins(Context act, int sync_interval_mins) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putInt("sync_interval_mins", sync_interval_mins);
        saver.commit();

    }

    public static void set_device_authorised(Context act, boolean device_authorised) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("device_authorised", device_authorised);
        saver.commit();

    }

    public static boolean device_authorised(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("device_authorised", APP_OPERATION_MODE != OPERATION_MODE.LIVE);
        //return prefs.getBoolean("device_authorised",false);

    }


    public static void set_Service_token(Context act, String Service_token) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("token", Service_token);
        saver.commit();

    }

    public enum version_action {
        DB_CHECK,
        MODULE_CHECK,
        Accept_terms
    }

    public static boolean version_action_done(Context act, version_action va) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        String pr_str = prefs.getString("v_a_" + va.name(), "");
        Log.e("VERSION CHECK :", "" + pr_str);
        boolean ok = prefs.getString("v_a_" + va.name(), "").equalsIgnoreCase(current_version());
        Log.e("VERSION CHECK :", "" + pr_str + " status " + ok);
        return ok;

    }

    public static void set_version_action_done(Context act, version_action va) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("v_a_" + va.name(), current_version());
        saver.commit();

    }

    public static String apiToken(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("token", null);

    }


    public static void set_sync_time(Context act, String sync_time_var) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("sync_time_var", sync_time_var);
        saver.commit();

    }

    public static String sync_time(Context act) {
        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("sync_time_var", null);

    }




    public static String all_records(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);

        String json = prefs.getString("all_records", "");
        //  Log.e("Downloads ",""+json);

        return json;
    }

    public static void add_used_code(Context act, String url) {
        JSONArray ja = new JSONArray();
        try {
            ja = new JSONArray(all_records(act));
            JSONObject jo = new JSONObject();
            jo.put("url", url);
            ja.put(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();


        saver.putString("all_records", ja.toString());
        saver.commit();


    }

    public static boolean is_used(Context act, String url) {
        JSONArray ja = new JSONArray();
        try {
            ja = new JSONArray(all_records(act));
            for (int i = 0; i < ja.length(); i++) {
                try {
                    Log.e("Check ", "" + ja.getJSONObject(i).getString("url") + " Against " + url);
                    if (ja.getJSONObject(i).getString("url").equalsIgnoreCase(url)) {
                        return true;
                    }
                } catch (Exception ex) {
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return false;
    }

    public static void set_update_check_time(Context act, String update_check_time) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("sync_time_var", update_check_time);
        saver.commit();

    }

    public static String update_check_time(Context act) {
        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("update_check_time", null);

    }


    public static int regsyncinterval_mins = 60;
    public static int update_check_interval_mins = 10;
    public static int matching_acuracy = 50;//%
    public static int matching_error_margin = 200;//%




    public static boolean use_bt_fp_device(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("use_bt_fp_device", false);

    }

    public static void set_use_bt_device(Context act, boolean use) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("use_bt_fp_device", use);
        saver.commit();

    }

    public static boolean background_sync(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("background_sync", true);

    }

    public static void set_background_sync(Context act, boolean use) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("background_sync", use);
        saver.commit();

    }

    public static String backup_time(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("backup_time", "00-00-0000");

    }

    public static void set_backup_time(Context act, String backup_time) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("backup_time", backup_time);
        saver.commit();

    }


    public static void set_global_data_sync(Context act, boolean global_data_sync) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("global_data_sync", global_data_sync);
        saver.commit();

    }

    public static boolean recrusive_backup(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("recrusive_backup", false);

    }

    public static void set_recrusive_backupp(Context act, boolean recrusive_backup) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("recrusive_backup", recrusive_backup);
        saver.commit();

    }

    public static boolean email_backup(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("email_backup", true);

    }

    public static void set_email_backup(Context act, boolean email_backup) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("email_backup", email_backup);
        saver.commit();

    }

    public static boolean ftp_backup(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("ftp_backup", true);

    }

    public static void set_ftp_backup(Context act, boolean ftp_backup) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("ftp_backup", ftp_backup);
        saver.commit();

    }

    public static boolean sd_backup(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("sd_backup", true);

    }

    public static void set_sd_backup(Context act, boolean sd_backup) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("sd_backup", sd_backup);
        saver.commit();

    }

    public static void set_user_id(Context act, String user_id) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("user_id", user_id);
        saver.commit();

    }

    public static String user_id(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("user_id", null);

    }

    public static String user_name(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("user_name", null);

    }

    public static String site_id(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("site_id", null);

    }

    public static String site_name(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("site_name", null);

    }

    public static void set_site_name(Context act, String site_name) {
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("site_name", site_name);
        saver.commit();
    }


    public static String gate_service_characteristic = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static String bt_device_address(Context act, bt_device_connector.bt_device_type dev_type) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString(dev_type.name() + "sbt_address", null);

    }

    public static void set_bt_device_address(Context act, bt_device_connector.bt_device_type dev_type, String macaddress) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString(dev_type.name() + "sbt_address", macaddress);

        saver.commit();

    }


    public static int default_photo_camera_type(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getInt("default_photo_camera_type", 2);

    }

    public static int default_printer_type(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getInt("default_printer_type", 1);

    }

    public static void set_default_printer_type(Context act, int default_printer_type) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putInt("default_printer_type", default_printer_type);

        saver.commit();

    }

    public static void set_default_photo_camera_type(Context act, int photo_camera_type) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putInt("default_photo_camera_type", photo_camera_type);

        saver.commit();

    }




    public static int photo_camera_type(Context act, int image_index) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getInt(image_index + "photo_camera_type", default_photo_camera_type(act));

    }
    public static void set_photo_camera_type(Context act, int image_index, int camera_type) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putInt(image_index + "photo_camera_type", camera_type);

        saver.commit();

    }
    public static void setImageCameraType(Context act, String image_index, int camera_type) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putInt(image_index + "photo_camera_type", camera_type);

        saver.commit();

    }
public static int imageCameraType(Context act, String image_index) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getInt(image_index + "photo_camera_type", default_photo_camera_type(act));

    }


    public static int printer_type(Context act, int print_job_index) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getInt(print_job_index + "printer_type", default_printer_type(act));

    }

    public static void set_printer_type(Context act, int print_job_index, int printer_type) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putInt(print_job_index + "printer_type", printer_type);

        saver.commit();

    }


    public static String bt_printer_address(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("bt_printer_address", null);

    }

    public static void set_bt_printer_address(Context act, String macaddress) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("bt_printer_address", macaddress);

        saver.commit();

    }

    public static String bt_weighbridge_address(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("bt_weighbridge_address", null);

    }

    public static void set_bt_weighbridge_address(Context act, String macaddress) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("bt_weighbridge_address", macaddress);

        saver.commit();

    }


    public static String ftp_folder(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("ftp_folder", "SPARTA_IDC_TEMP");

    }

    public static void set_ftp_folder(Context act, String ftp_folder) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("ftp_folder", ftp_folder);

        saver.commit();

    }


    public static String version_check_time(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("version_check_time", "never");

    }

    public static void version_check_time(Context act, String version_check_time) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("version_check_time", version_check_time);

        saver.commit();

    }


    public static Boolean uptodate(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getBoolean("uptodate", true);

    }

    public static String get_zone(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("zone", "Zone Unidentified");

    }

    public static void uptodate(Context act, boolean uptodate) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("uptodate", uptodate);

        saver.commit();

    }

    public static String device_id(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("device_id", null);

    }

    public static void set_device_id(Context act, String device_id) {
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("device_id", device_id);
        saver.commit();
    }

    public static String device_name(Context act) {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("device_name", null);

    }

    public static void set_device_name(Context act, String device_name) {
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("device_name", device_name);
        saver.commit();
    }


    public static int incrementData(Context act) {
        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();
        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);

        saver.putInt("custom_data_index", prefs.getInt("custom_data_index", 0) + 1);
        saver.commit();
        prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);

        return prefs.getInt("custom_data_index", 0);

    }


    public static String receipt(Activity act, String name, String idno, String phoneno, String email) {
        String rr = "";
        String recno = "" + Math.random();
        rr = "TIME AND ATTENDANCE\n\n" +
                "P.O BOX 1273 - 00621, NAIROBI\n" +
                "LIIC:KRA/EFPL22112010/004476\n" +
                "VAT #:  0162551L\n\n" +
                "\nReceipt no :" + recno + "" +
                "\nEmployee name:" + name + "" +
                "\nID number:" + idno + "" +
                "\nEmail:" + email + "" +
                "\nPhone number:" + phoneno + "\n     \n" +
                "Served by :" + svars.user_id(act) + "\n\n\n\n";

        return rr;

    }

    public static Calendar sparta_EA_calendar() {
        TimeZone tmz = TimeZone.getTimeZone("GMT+0300");
        Calendar calendar = Calendar.getInstance(tmz);


        return calendar;
    }

    public static String gett_time() {
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            String datee = sdf_user_friendly_time.format(Calendar.getInstance().getTime());
            //   Log.e("My date =>",""+datee);
            return datee;
        } catch (Exception ex) {
        }
        return null;
    }

    public static String get_time_user() {
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy  HH:mm");
        try {
            String datee = formatter.format(Calendar.getInstance().getTime());
            //   Log.e("My date =>",""+datee);
            return datee;
        } catch (Exception ex) {
        }
        return null;
    }

    public static String gett_date() {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        try {
            String datee = formatter.format(Calendar.getInstance().getTime());
            //   Log.e("My date =>",""+datee);
            return datee;
        } catch (Exception ex) {
        }
        return null;
    }

    public static String getCurrentDateOfMonth() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            String datee = formatter.format(Calendar.getInstance().getTime());
            //   Log.e("My date =>",""+datee);
            return datee;
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * @deprecated Use conversions instead not svars
     * @param db_date
     * @return
     */
    @Deprecated
    public static SimpleDateFormat sdf_user_friendly_date = new SimpleDateFormat("dd-MM-yyyy");//=null;
    /**
     * @deprecated Use conversions instead not svars
     * @param db_date
     * @return
     */
    @Deprecated
    public static SimpleDateFormat sdf_db_date = new SimpleDateFormat("yyyy-MM-dd");//=null;
    /**
     * @deprecated Use conversions instead not svars
     * @param db_date
     * @return
     */
    @Deprecated
    public static SimpleDateFormat sdf_db_date_unseparated = new SimpleDateFormat("yyyyMMdd");//=null;

    /**
     * @deprecated Use conversions instead not svars
     * @param db_date
     * @return
     */
    @Deprecated
    public static SimpleDateFormat sdf_user_friendly_time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//=null;
    @Deprecated
    public static SimpleDateFormat sdf_db_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//=null;

    /**
     * @deprecated Use conversions instead not svars
     * @param db_date
     * @return
     */
    @Deprecated
    public static String get_user_date_from_db_date(String db_date) {


        try {
            Date time1 = sdf_db_date.parse(db_date);

            return sdf_user_friendly_date.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String get_db_date_from_user_date(String db_date) {


        try {
            Date time1 = sdf_user_friendly_date.parse(db_date);

            return sdf_db_date.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String get_db_time_from_user_date(String db_date) {


        try {
            Date time1 = sdf_user_friendly_date.parse(db_date);

            return sdf_db_time.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String get_db_time_from_db_date(String db_date) {


        try {
            Date time1 = sdf_db_date.parse(db_date);

            return sdf_db_time.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String get_user_time_from_db_time(String db_time) {

        try {
            Date time1 = sdf_db_time.parse(db_time);

            return sdf_user_friendly_time.format(time1);
        } catch (Exception ex) {
            return db_time;
        }

    }

    public static String get_db_time_from_user_time(String db_date) {


        try {
            Date time1 = sdf_user_friendly_time.parse(db_date);

            return sdf_db_time.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static boolean gps_enabled(Context act) {

        LocationManager mlocManager = (LocationManager) act.getSystemService(act.LOCATION_SERVICE);

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }


    }

    public static boolean isInternetAvailable() {
        // try {
        InetAddress ipAddr = null; //You can replace it with your name
        try {
            ipAddr = InetAddress.getByName("www.google.com");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e("IP GOOGLE ERROR =>", " " + e.getMessage());
        }
        try {
            Log.e("IP GOOGLE =>", " " + ipAddr.getHostAddress());
        } catch (Exception ex) {
        }
        if (ipAddr == null) {
            return false;
        } else {
            return true;
        }


    }

    public static boolean is_server_online(String server_address) {
        // try {
        InetAddress ipAddr = null; //You can replace it with your name
        try {
            ipAddr = InetAddress.getByName(server_address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e("Error =>", " " + e.getMessage());
        }
        try {
            Log.e("IP =>", " " + ipAddr.getHostAddress());
        } catch (Exception ex) {
        }
        if (ipAddr == null) {
            return false;
        } else {
            return true;
        }


    }


    public static String fp_demo = "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";
    public static String fp_demo1 = "Rk1SACAyMAAAAAGeAAABAAFoAMgAyAEAAABWQEB6AOvVWUBiAIhlWEA6ADRkVUC1AMrfVUA3AIVr\n" +
            "    VEBuAGFhU0DSADJbU0BuAKPdUkAcAPtbUkCyAJHgUUC8AU/VT0DeAPvjTkCFAVW8ToCcANbXTUA2\n" +
            "    APfXTUAoAPjZTUAOASdWTUCpASXYTECsAENdS0BSAFtlSoAiARNgSUAUAQPVSYAcAS5TR4B5ADtb\n" +
            "    RkAYANZxRkAcAOtiRUBYAVw4RECYAEHfRECvAFlkQ0AUAF9qQkB2AUq3QkCYASLMQUCSAUTJQUAC\n" +
            "    AN3tPUAYAPXfO0CiAU7KO0AKATRfOoBVADtfOoCcAR7VOkAfATdJOUBqAU+yOUATAOVqOECwAVbO\n" +
            "    OEBqAUo1OECSAUxDOEBGAT4yN4AYAUpeN0BzAWE5NkAuAQFYNoCeASfSNQAOAPdlMgDsAMdkLwAM\n" +
            "    AQPTLwAEAQ1ALgAZAVUFLgDsAQnlLgDqANTlLADrAP1rLADoAOZdKwDqARPkKwDyAK9gKgDxAKFf\n" +
            "    KQDxALVeKAAlAUplKAAA";

    public static String fp_my_r_thumb_bt_device = "AwFcI40A//7AAoACgAKAAoACgAKAAoACgAKAAoACgAKAAoACgAKAAgAAAAAAAAAAAAAAAAAAAABo\n" +
            "CeP+EBOcNjqUGJ5vFSWeTRaTvhEa2tZhJSj+HiZX3kGolT5YqxO+SrRUXhQ5lp45OxT+Z79pHlm+\n" +
            "Ut8tnBi8JaOXvD2vVRwsOGt8RblqHEg8k5wdm8KdIKAYWiyt1fpBsin6LTSVejoPqxsnHlnYLLDr\n" +
            "+GSx09JNCGWTUwtQ9kMMFrZOi1ERRhCn0wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMBXByOAP/+8B7ADsAGgAKAAoACgAKAAoACgAKA\n" +
            "AoACgAKAAoAGwA4AAAAAAAAAAAAAAAAAAAAASRBYPluS094vHJg+GiCB/i2i195RpNV+aSdTvlqw\n" +
            "1H47NKv+STeVPjFBVd5Fi+x/aLvTXzQgF9xOK1T8QbDV3FS2ahxXuZP8UgiWOjyqFdpRLqo6UQwW\n" +
            "OFqMFTI6mFhyPK0r+FWMqnMVHAJ2H5gbNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n";

}