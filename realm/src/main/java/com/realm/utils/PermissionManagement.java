package com.realm.utils;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import com.realm.Realm;

public class PermissionManagement {

    public static final int MULTIPLE_PERMISSIONS = 10;


    public static void requestAllPermissions(Activity context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                checkAndRequestPermissions(context, info.requestedPermissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static boolean hasPermission(String specialPermission) {
        Context context= Realm.context;
        ApplicationInfo info = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            info = packageManager.getApplicationInfo(context.getPackageName(),0);
//            Log.d("TAG", "onCreate: "+hasPermission(this,info));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        String op = appOps.permissionToOp(specialPermission);
        if(op==null)return true;
        int mode = appOps.unsafeCheckOpNoThrow(op, info.uid, info.packageName);
        if (mode == AppOpsManager.MODE_DEFAULT)
            granted = (context.checkCallingOrSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        else
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void openOverlaySettings(Activity activity) {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.getPackageName()));
        try {
            activity.startActivityForResult(intent, 6);
        } catch (ActivityNotFoundException e) {
            Log.e("Drawers permission :", e.getMessage());
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean checkAndRequestAllPermissions(Activity context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                return checkAndRequestPermissions(context, info.requestedPermissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public static boolean checkAllPermissions(Activity context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                return checkPermissions(context, info.requestedPermissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }
    public static boolean checkAndRequestPermissions(Activity context, String[] permissions) {
        boolean ok = true;
        List<String> listPermissionsNeeded = new ArrayList();
        String[] var5 = permissions;
        int var6 = permissions.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String p = var5[var7];
            int result = ContextCompat.checkSelfPermission(context, p);
            if (result != 0) {
                listPermissionsNeeded.add(p);
            }
        }

        if (listPermissionsNeeded.contains("android.permission.MANAGE_EXTERNAL_STORAGE")) {
            if (Build.VERSION.SDK_INT >= 30) {
                if (Environment.isExternalStorageManager()) {
                    listPermissionsNeeded.remove("android.permission.MANAGE_EXTERNAL_STORAGE");
                } else {
                    Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                    Uri uri = Uri.fromParts("package", context.getPackageName(), (String)null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            } else {
                listPermissionsNeeded.remove("android.permission.MANAGE_EXTERNAL_STORAGE");
            }
        }

        if (listPermissionsNeeded.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (!Settings.canDrawOverlays(context)) {
                openOverlaySettings(context);
                ok = false;
            } else {
                listPermissionsNeeded.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.PACKAGE_USAGE_STATS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(hasPermission(Manifest.permission.PACKAGE_USAGE_STATS)){
                    listPermissionsNeeded.remove(Manifest.permission.PACKAGE_USAGE_STATS);

                }else{
                    context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            }else{
                listPermissionsNeeded.remove(Manifest.permission.PACKAGE_USAGE_STATS);

            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.BLUETOOTH_CONNECT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            }else{
                listPermissionsNeeded.remove(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        if (listPermissionsNeeded.contains(Manifest.permission.BLUETOOTH_SCAN)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            }else{
                listPermissionsNeeded.remove(Manifest.permission.BLUETOOTH_SCAN);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.BLUETOOTH_PRIVILEGED)) {
            listPermissionsNeeded.remove(Manifest.permission.BLUETOOTH_PRIVILEGED);
        }
        if (listPermissionsNeeded.contains(Manifest.permission.READ_PRECISE_PHONE_STATE)) {
            listPermissionsNeeded.remove(Manifest.permission.READ_PRECISE_PHONE_STATE);
        }

        if (listPermissionsNeeded.contains("android.permission.READ_PRIVILEGED_PHONE_STATE")) {
            listPermissionsNeeded.remove("android.permission.READ_PRIVILEGED_PHONE_STATE");
        }

        if (listPermissionsNeeded.contains("android.permission.REQUEST_INSTALL_PACKAGES")) {
            listPermissionsNeeded.remove("android.permission.REQUEST_INSTALL_PACKAGES");
        }

        if (listPermissionsNeeded.contains("com.mediatek.permission.CTA_ENABLE_BT")) {
            listPermissionsNeeded.remove("com.mediatek.permission.CTA_ENABLE_BT");
        }

        if (!listPermissionsNeeded.isEmpty() && !(listPermissionsNeeded.size() == 1 & ((String)listPermissionsNeeded.get(0)).equalsIgnoreCase("android.permission.ACCESS_FINE_LOCATION"))) {
            ActivityCompat.requestPermissions(context, (String[])listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 10);
            ok = false;
        }

        if (ContextCompat.checkSelfPermission(context, "android.permission.SYSTEM_ALERT_WINDOW") != 0) {
        }

        return ok;
    }

    public static boolean checkAndRequestPermissions_(Activity context, String[] permissions) {
        int result;
        boolean ok = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {

            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    listPermissionsNeeded.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            } else {
                //below android 11=======
                listPermissionsNeeded.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (!Settings.canDrawOverlays(context)) {
                openOverlaySettings(context);
                ok = false;
            } else {
                listPermissionsNeeded.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);


            }

        }
        if (listPermissionsNeeded.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            listPermissionsNeeded.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES);

        }
  if (listPermissionsNeeded.contains(Manifest.permission.BLUETOOTH_PRIVILEGED)) {
            listPermissionsNeeded.remove(Manifest.permission.BLUETOOTH_PRIVILEGED);

        }
 if (listPermissionsNeeded.contains("com.mediatek.permission.CTA_ENABLE_BT")) {
            listPermissionsNeeded.remove("com.mediatek.permission.CTA_ENABLE_BT");

        }

        if (listPermissionsNeeded.isEmpty() || (listPermissionsNeeded.size() == 1 & listPermissionsNeeded.get(0).equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION))) {

        } else {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            ok = false;
        }


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {


            //  openOverlaySettings();
        }


        return ok;
    }

    public static boolean checkPermissions(Activity context, String[] permissions) {
        boolean ok = true;
        List<String> listPermissionsNeeded = new ArrayList();
        String[] var5 = permissions;
        int var6 = permissions.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String p = var5[var7];
            int result = ContextCompat.checkSelfPermission(context, p);
            if (result != 0) {
                listPermissionsNeeded.add(p);
            }
        }

        if (listPermissionsNeeded.contains("android.permission.MANAGE_EXTERNAL_STORAGE")) {
            if (Build.VERSION.SDK_INT >= 30) {
                if (Environment.isExternalStorageManager()) {
                    listPermissionsNeeded.remove("android.permission.MANAGE_EXTERNAL_STORAGE");
                } else {
                    ok = false;
//                    Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
//                    Uri uri = Uri.fromParts("package", context.getPackageName(), (String)null);
//                    intent.setData(uri);
//                    context.startActivity(intent);
                }
            } else {
                listPermissionsNeeded.remove("android.permission.MANAGE_EXTERNAL_STORAGE");
            }
        }

        if (listPermissionsNeeded.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (!Settings.canDrawOverlays(context)) {
//                openOverlaySettings(context);
                ok = false;
            } else {
                listPermissionsNeeded.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.PACKAGE_USAGE_STATS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(hasPermission(Manifest.permission.PACKAGE_USAGE_STATS)){
                    listPermissionsNeeded.remove(Manifest.permission.PACKAGE_USAGE_STATS);

                }else{
//                    context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    ok = false;

                }
            }else{
                listPermissionsNeeded.remove(Manifest.permission.PACKAGE_USAGE_STATS);

            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.BLUETOOTH_CONNECT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ok = false;

            }else{
                listPermissionsNeeded.remove(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        if (listPermissionsNeeded.contains(Manifest.permission.BLUETOOTH_SCAN)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ok = false;

            }else{
                listPermissionsNeeded.remove(Manifest.permission.BLUETOOTH_SCAN);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.BLUETOOTH_PRIVILEGED)) {
            listPermissionsNeeded.remove(Manifest.permission.BLUETOOTH_PRIVILEGED);
        }
        if (listPermissionsNeeded.contains(Manifest.permission.READ_PRECISE_PHONE_STATE)) {
            listPermissionsNeeded.remove(Manifest.permission.READ_PRECISE_PHONE_STATE);
        }

        if (listPermissionsNeeded.contains("android.permission.READ_PRIVILEGED_PHONE_STATE")) {
            listPermissionsNeeded.remove("android.permission.READ_PRIVILEGED_PHONE_STATE");
        }

        if (listPermissionsNeeded.contains("android.permission.REQUEST_INSTALL_PACKAGES")) {
            listPermissionsNeeded.remove("android.permission.REQUEST_INSTALL_PACKAGES");
        }

        if (listPermissionsNeeded.contains("com.mediatek.permission.CTA_ENABLE_BT")) {
            listPermissionsNeeded.remove("com.mediatek.permission.CTA_ENABLE_BT");
        }

        if (!listPermissionsNeeded.isEmpty() && !(listPermissionsNeeded.size() == 1 & ((String)listPermissionsNeeded.get(0)).equalsIgnoreCase("android.permission.ACCESS_FINE_LOCATION"))) {
            ok = false;
        }

        if (ContextCompat.checkSelfPermission(context, "android.permission.SYSTEM_ALERT_WINDOW") != 0) {
        }




        return ok;
    }


}
