package com.realm.Services;


import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.database.Cursor;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.common.base.Stopwatch;
import com.realm.Models.Query;
import com.realm.annotations.RealmDataClass;
import com.realm.annotations.sync_service_description;
import com.realm.utils.AppConfig;
import com.realm.utils.Gpsprobe_r;
import com.realm.utils.s_cryptor;
import com.realm.utils.svars;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by Thompsons on 01-Feb-17.
 */

public class DatabaseManager {
    private static final String PACKAGE_INSTALLED_ACTION = "com.example.android.apis.content.SESSION_API_PACKAGE_INSTALLED";
    public static SQLiteDatabase database = null;
    public static boolean loaded_db = false;
    public static String logTag = "DatabaseManager";
    public static HashMap<Integer, Integer> pagerEventMap = new HashMap<>();

    static Context context;
    public static RealmDataClass realm;

    public DatabaseManager(Context context, RealmDataClass realm) {
        this.context = context;
        this.realm = realm;
        if (!loaded_db) {
            try {
                setupDbAnn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //region String operations
    public String conccat_sql_filters(String[] str_to_join) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? "WHERE " : " AND ") + str_to_join[i];
        }
        return result;

    }

    public String conccat_sql_string(String[] str_to_join, ArrayList<String> str_to_join2) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? "" : ",") + "'" + str_to_join[i] + "'";
        }
        for (int i = 0; i < str_to_join2.size(); i++) {
            result = result + (result.length() < 0 ? "" : ",") + "'" + str_to_join2.get(i) + "'";
        }
        return result;

    }

    public static String conccat_sql_string(String[] str_to_join) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? "" : ",") + "'" + str_to_join[i] + "'";
        }
        return result;

    }

    public static String concatString(String delimeter, String[] params) {
        String result = "";
        for (int i = 0; i < params.length; i++) {
            result = result + (i == 0 ? "" : delimeter) + "" + params[i] + "";
        }
        return result;

    }

    public static String[] orderStatements(LinkedHashMap<String, Boolean> statements) {
        String[] result = new String[statements.size()];
        int i = 0;
        for (Map.Entry<String, Boolean> entry : statements.entrySet()) {
            result[i] = entry.getKey() + " " + (entry.getValue() ? "ASC" : "DESC");
            i++;
        }
        return result;

    }

    public static String concatRealmClientString(String delimeter, String[] params) {
        String result = "";
        for (int i = 0; i < params.length; i++) {
            result = result + (i == 0 ? "" : delimeter) + "" + params[i] + "";
        }
        return result;

    }

    public static String conccat_sql_string(ArrayList<String> str_to_join2) {
        String result = "";


        for (int i = 0; i < str_to_join2.size(); i++) {
            result = result + (result.length() < 1 ? "" : ",") + "'" + str_to_join2.get(i) + "'";
        }
        return result;

    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        //    @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

//endregion


    //region App installation
    private static void addApkToInstallSession(Context context, Uri uri, PackageInstaller.Session session) {
        Log.i("TAG", "addApkToInstallSession " + uri);
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try {
            OutputStream packageInSession = session.openWrite("package", 0, -1);
            InputStream input;
//            Uri uri = Uri.parse(filename);
            input = context.getContentResolver().openInputStream(uri);

            if (input != null) {
                Log.i("TAG", "input.available: " + input.available());
                byte[] buffer = new byte[16384];
                int n;
                while ((n = input.read(buffer)) >= 0) {
                    packageInSession.write(buffer, 0, n);
                }
            } else {
                Log.i("TAG", "addApkToInstallSession failed");
                throw new IOException("addApkToInstallSession");
            }
            packageInSession.close();  //need to close this stream
            input.close();             //need to close this stream
        } catch (Exception e) {
            Log.i("TAG", "addApkToInstallSession failed2 " + e.toString());
        }
    }

    public static void install(Uri uri, Class<?> cls) {
        PackageInstaller.Session session = null;
        try {
            PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            int sessionId = packageInstaller.createSession(params);
            session = packageInstaller.openSession(sessionId);
            addApkToInstallSession(context, uri, session);
            // Create an install status receiver.
            Context context = DatabaseManager.context;
            Intent intent = new Intent(context, cls);
            intent.setAction(PACKAGE_INSTALLED_ACTION);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            IntentSender statusReceiver = pendingIntent.getIntentSender();
            // Commit the session (this will start the installation workflow).
            session.commit(statusReceiver);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't install package", e);
        } catch (RuntimeException e) {
            if (session != null) {
                session.abandon();
            }
            throw e;
        }

    }
    private void addApkToInstallSession(String assetName, PackageInstaller.Session session)
            throws IOException {
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try (OutputStream packageInSession = session.openWrite("package", 0, -1);
             InputStream is = context.getAssets().open(assetName)) {
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
        }
    }
    //endregion


    //region System realm

    /**
     * Sets up database by creating and adding missing tables and missing columns and indices in their respective tables from mappery of annotated data which is pre-reflected at pre build
     */
    void setupDbAnn() {


        AppConfig appconfig = svars.current_app_config(context);


        SQLiteDatabase.loadLibs(context);

        File par = new File(new File(appconfig.file_path_db()).getParent());
        if (!par.exists()) {
            par.mkdirs();
        }
        Log.e(logTag, "DB Path:" + appconfig.file_path_db());

        database = SQLiteDatabase.openOrCreateDatabase(new File(appconfig.file_path_db()), appconfig.DB_PASS, null);

        // if(svars.version_action_done(act, svars.version_action.DB_CHECK)){  loaded_db=true;return;}


        Stopwatch sw = Stopwatch.createUnstarted();
        sw.start();


        for (String s : realm.getDynamicClassPaths()) {


            Log.e("Classes reflected =>", "Ann :" + s);

            String table_name = realm.getPackageTable(s);
            try {
                Cursor cursor1 = database.rawQuery("SELECT * FROM " + table_name + " LIMIT 1", null);
//                cursor1.moveToFirst();
//                if (!cursor1.isAfterLast()) {
//                    do {
//                        cursor1.getString(0);
//                    } while (cursor1.moveToNext());
//                }
                cursor1.close();
            } catch (Exception e) {
                database.execSQL(realm.getTableCreateSttment(table_name, false));
                database.execSQL(realm.getTableCreateSttment(table_name, true));
                String crt_stt = realm.getTableCreateIndexSttment(table_name);
                if (crt_stt.length() > 1 & crt_stt.contains(";")) {

                    for (String st : crt_stt.split(";")) {
                        try {
                            Log.e("DB :", "Index statement creating =>" + st);
                            database.execSQL(st);
                            Log.e("DB :", "Index statement created =>" + st);
                        } catch (Exception ex1) {
                        }

                    }


                }
                continue;
            }

            for (Map.Entry<String, String> col : realm.getTableColumns(table_name).entrySet()) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.getKey() + ") FROM " + table_name, null);
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE " + table_name + " ADD COLUMN " + col.getKey());//why did i change to this??
//                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.getKey() + " "+col.data_type+" "+col.default_value);
                }
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.getKey() + ") FROM CP_" + table_name, null);
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE CP_" + table_name + " ADD COLUMN " + col.getKey());
//                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.getKey() + " "+col.data_type+" "+col.default_value);
                }
            }


        }

        Log.e("Classes reflected :", "Ann :" + sw.elapsed(TimeUnit.MICROSECONDS));

        svars.set_version_action_done(context, svars.version_action.DB_CHECK);
        Log.e("DB", "Finished DB Verification");
        loaded_db = true;

    }

    /**
     * @deprecated As of release 1.0.187, replaced by {@link #loadObjectArray(Class, String, String[], String[], String[], int, int, String[])} ()}
     * <p> Use {@link #loadObjectArray(Class, String, String[], String[], String[], int, int, String[])} instead.
     */
    @Deprecated
    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, String[] columns, String[] table_filters, String[] order_filters, boolean order_asc, int limit, int offset) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null ? "" : " ORDER BY " + concatString(",", order_filters) + " " + (order_asc ? "ASC" : "DESC")) + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }


    /**
     * @deprecated As of release 1.0.187, replaced by {@link #loadObjectArray(Class, String, String[], String[], String[], int, int, String[])} ()}
     * <p> Use {@link #loadObjectArray(Class, String, String[], String[], String[], int, int, String[])} instead.
     */
    @Deprecated
    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, String[] columns, String[] table_filters, String[] order_filters, int limit, int offset, String[] queryParameters) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null || order_filters.length < 1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, queryParameters);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    @Deprecated
    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, int pagerEventId, int searchIndex, String[] columns, String[] table_filters, String[] order_filters, boolean order_asc, int limit, int offset) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null ? "" : " ORDER BY " + concatString(",", order_filters) + " " + (order_asc ? "ASC" : "DESC")) + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, null);


        if (c.moveToFirst()) {
            do {
                objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
            } while (c.moveToNext() && pagerEventMap.get(pagerEventId) == searchIndex);
        }
        c.close();


        return pagerEventMap.get(pagerEventId) == searchIndex ? objs : null;
//        return currentActiveIndex(pagerEventId)==searchIndex?objs:null;
    }

    /**
     * @deprecated As of release 1.0.187, replaced by {@link #loadObjectArray(Class, int, int, String, String[], String[], String[], int, int, String[])} ()}
     * <p> Use {@link #loadObjectArray(Class, int, int, String, String[], String[], String[], int, int, String[])} instead.
     */
    @Deprecated
    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, int pagerEventId, int searchIndex, String[] columns, String[] table_filters, String[] order_filters, int limit, int offset, String[] queryParameters) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
//        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null ? "" : " ORDER BY " + concatString(",", order_filters) + " " + (order_asc ? "ASC" : "DESC")) + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, queryParameters);


        if (c.moveToFirst()) {
            do {
                objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
            } while (c.moveToNext() && pagerEventMap.get(pagerEventId) == searchIndex);
        }
        c.close();


        return pagerEventMap.get(pagerEventId) == searchIndex ? objs : null;
//        return currentActiveIndex(pagerEventId)==searchIndex?objs:null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public <RM> int getRecordCount(Class<RM> realm_model, @Nullable String... params) {
        String table_name = realm.getPackageTable(realm_model.getName());
        return Integer.parseInt(get_record_count(table_name, params));
    }

    public <RM> int getRecordCount(Class<RM> realm_model, Query query) {
        return loadObjectCount(realm_model, query.customQuery, query.columns, query.tableFilters, orderStatements(query.orderFilters), query.limit, query.offset, query.queryParameters);

//        return loadObjectArray(realm_model, query.setColumns("rowid")).size();
    }

    public <RM> int loadObjectCount(Class<RM> realm_model, String customQuery, String[] columns, String[] table_filters, String[] order_filters, int limit, int offset, String[] queryParameters) {
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + (customQuery == null ? table_name : "( " + customQuery + ")") + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null || order_filters.length < 1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        qry = "SELECT COUNT(*) FROM (" + qry + ")";
        Cursor c = database.rawQuery(qry, queryParameters);
        if (c.moveToFirst()) {
            do {
                int cnt = c.getInt(0);
                c.close();
                return cnt;
            } while (c.moveToNext());
        }
        c.close();


        return 0;
    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, String customQuery, String[] columns, String[] table_filters, String[] order_filters, int limit, int offset, String[] queryParameters) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
//        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null||order_filters.length<1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + (customQuery == null ? table_name : "( " + customQuery + ")") + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null || order_filters.length < 1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, queryParameters);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public ArrayList<JSONObject> loadJSONArray(Class<?> realm_model, String customQuery, String[] columns, String[] table_filters, String[] order_filters, int limit, int offset, String[] queryParameters) {
        ArrayList<JSONObject> objs = new ArrayList<JSONObject>();
        String table_name = realm.getPackageTable(realm_model.getName());
//        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null||order_filters.length<1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + (customQuery == null ? table_name : "( " + customQuery + ")") + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null || order_filters.length < 1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, queryParameters);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(realm.getJsonFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, int pagerEventId, int searchIndex, String customQuery, String[] columns, String[] table_filters, String[] order_filters, int limit, int offset, String[] queryParameters) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + (customQuery == null ? table_name : "( " + customQuery + ")") + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null || order_filters.length < 1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));

        Cursor c = database.rawQuery(qry, queryParameters);


        if (c.moveToFirst()) {
            do {
                objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
            } while (c.moveToNext() && pagerEventMap.get(pagerEventId) == searchIndex);
        }
        c.close();


        return pagerEventMap.get(pagerEventId) == searchIndex ? objs : null;
//        return currentActiveIndex(pagerEventId)==searchIndex?objs:null;
    }

    public ArrayList<JSONObject> loadJSONArray(Class<?> realm_model, int pagerEventId, int searchIndex, String customQuery, String[] columns, String[] table_filters, String[] order_filters, int limit, int offset, String[] queryParameters) {
        ArrayList<JSONObject> objs = new ArrayList<>();
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + (customQuery == null ? table_name : "( " + customQuery + ")") + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null || order_filters.length < 1 ? "" : " ORDER BY " + concatString(",", order_filters)) + " " + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));

        Cursor c = database.rawQuery(qry, queryParameters);


        if (c.moveToFirst()) {
            do {

                try {
                    objs.add(realm.getJsonFromCursor(c, realm_model.getName()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext() && pagerEventMap.get(pagerEventId) == searchIndex);
        }
        c.close();


        return pagerEventMap.get(pagerEventId) == searchIndex ? objs : null;
//        return currentActiveIndex(pagerEventId)==searchIndex?objs:null;
    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, String rawquery) {
        ArrayList<RM> objs = new ArrayList<RM>();
//        String table_name=realm.getPackageTable(realm_model.getName());
        String qry = rawquery;//"SELECT "+(columns==null?"*":concatString(",",columns))+" FROM "+table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+(order_filters==null?"":" ORDER BY "+concatString(",",order_filters)+" "+(order_asc?"ASC":"DESC"))+(limit<=0?"":" LIMIT "+limit+(offset<=0?"": " OFFSET "+offset));
        Cursor c = database.rawQuery(qry, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public ArrayList<JSONObject> loadJSONArray(Class<?> realm_model, String rawquery) {
        ArrayList<JSONObject> objs = new ArrayList<JSONObject>();
//        String table_name=realm.getPackageTable(realm_model.getName());
        String qry = rawquery;//"SELECT "+(columns==null?"*":concatString(",",columns))+" FROM "+table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+(order_filters==null?"":" ORDER BY "+concatString(",",order_filters)+" "+(order_asc?"ASC":"DESC"))+(limit<=0?"":" LIMIT "+limit+(offset<=0?"": " OFFSET "+offset));
        Cursor c = database.rawQuery(qry, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(realm.getJsonFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, Query query) {
//        return loadObjectArray(realm_model, query.columns, query.table_filters, query.order_filters, query.order_asc, query.limit, query.offset);
        return loadObjectArray(realm_model, query.customQuery, query.columns, query.tableFilters, orderStatements(query.orderFilters), query.limit, query.offset, query.queryParameters);

    }

    public ArrayList<JSONObject> loadJSONArray(Class<?> realm_model, Query query) {
        return loadJSONArray(realm_model, query.customQuery, query.columns, query.tableFilters, orderStatements(query.orderFilters), query.limit, query.offset, query.queryParameters);

    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, int pagerEventId, int searchIndex, Query query) {
        return loadObjectArray(realm_model, pagerEventId, searchIndex, query.customQuery, query.columns, query.tableFilters, orderStatements(query.orderFilters), query.limit, query.offset, query.queryParameters);

    }

    public ArrayList<JSONObject> loadJSONArray(Class<?> realm_model, int pagerEventId, int searchIndex, Query query) {
        return loadJSONArray(realm_model, pagerEventId, searchIndex, query.customQuery, query.columns, query.tableFilters, orderStatements(query.orderFilters), query.limit, query.offset, query.queryParameters);

    }

    public <RM> RM loadObject(Class<RM> realm_model, Query query) {
//        ArrayList<RM> res = loadObjectArray(realm_model, query.columns, query.table_filters, query.order_filters, query.order_asc, 1, 0);
        ArrayList<RM> res = loadObjectArray(realm_model, query.customQuery, query.columns, query.tableFilters, orderStatements(query.orderFilters), 1, 0, query.queryParameters);
        return res.size() > 0 ? res.get(0) : null;

    }

    public JSONObject loadJSONObject(Class<?> realm_model, Query query) {
        ArrayList<JSONObject> res = loadJSONArray(realm_model, query.customQuery, query.columns, query.tableFilters, orderStatements(query.orderFilters), 1, 0, query.queryParameters);
        return res.size() > 0 ? res.get(0) : null;

    }

    public <RM> RM loadObject(Class<RM> realm_model, String rawquery) {
        ArrayList<RM> res = loadObjectArray(realm_model, rawquery);
        if (res.size() > 0) {
            Log.e(logTag, "Array size :" + res.size());
        }
        return res.size() > 0 ? res.get(0) : null;

    }

    public <RM> boolean insertObject(RM realm_model) {
        String table_name = realm.getPackageTable(realm_model.getClass().getName());

        return database.insert(table_name, null, (ContentValues) realm.getContentValuesFromObject(realm_model)) > 0;

    }

    public void executeQuery(String query, String... queryParams) {

        database.execSQL(query, queryParams);

    }

    public void beginTransaction() {
        database.beginTransaction();

    }


    public void finishSuccessTransaction() {
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void finishErrorTransaction() {
        database.endTransaction();
    }

    public Object getJsonValue(String pos, JSONObject jo) {
        Object json = jo;
        if (!pos.contains(":")) {
            return null;
        }
        if (!pos.contains(";")) {
            pos += ";";
        }
        for (String s : pos.split(";")) {
            if (s.length() > 0) {
                try {
//            if(json instanceof JSONObject){
                    if (s.split(":")[0].equalsIgnoreCase("JO")) {
                        json = new JSONTokener(((JSONObject) json).opt(s.split(":")[1]).toString()).nextValue();

                    } else if (s.split(":")[0].equalsIgnoreCase("JA")) {

//                if (json instanceof JSONArray){

                        json = ((JSONArray) json).get(Integer.parseInt(s.split(":")[1]));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return json;
    }

    public String get_record_count(String table_name, @Nullable String... filters) {

        String qry = "SELECT COUNT(*) FROM " + table_name + (filters == null ? "" : " " + conccat_sql_filters(filters));
        //  Log.e("QRY :",""+qry);
        Cursor c = database.rawQuery(qry, null);

        if (c.moveToFirst()) {
            do {
                String res = c.getString(0);
                c.close();
                return res;

            } while (c.moveToNext());
        }
        c.close();
        return "0";
    }

    public String greatest_sync_var(String table_name, @Nullable String... filters) {
        try {
            String[] flts = new String[filters.length + 1];
            flts[0] = "sync_var NOT NULL";
            for (int i = 0; i < filters.length; i++) {
                flts[i + 1] = filters[i];
            }
            String qry = "SELECT CAST(sync_var AS INTEGER) FROM " + table_name + (filters == null ? "" : " " + conccat_sql_filters(flts)) + " ORDER BY CAST(sync_var AS INTEGER) DESC LIMIT 1";
            Cursor c = database.rawQuery(qry, null);

            if (c.moveToFirst()) {
                do {

                    String res = c.getString(0);
                    c.close();
                    return res;
                } while (c.moveToNext());
            }
            c.close();
            return "0";
        } catch (Exception e) {
            Log.e("DatabaseManager", "" + e.getMessage());
            return null;
        }
    }

//endregion

    public ArrayList<JSONObject> load_dynamic_json_records_ann(sync_service_description ssd, String[] table_filters) {
        ArrayList<JSONObject> objs = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ssd.table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + " ORDER BY data_status DESC LIMIT " + ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {
                try {

                    objs.add(realm.getJsonFromCursor(c, ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }


}
