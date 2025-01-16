package com.realm.Services.HttpClient;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class RealmRequest {
    RealmRequest() {
        headers.put("Content-Type", "application/json");

    }

    public RealmRequest addHeader(String header_name, String header_value) {
        this.headers.put(header_name, header_value);
        return this;

    }

    public RealmRequest setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;

    }

    public RealmRequest setRequestType(RequestType request_type) {
        this.requestType = request_type;
        return this;

    }

    public RealmRequest setRequestContentType(ContentType content_type) {
        this.requestContentType = content_type;
        return this;

    }
     public RealmRequest setResponseContentType(ContentType content_type) {
        this.responseContentType = content_type;
        return this;

    }

//    public void excecute(RequestResponceListener response_listener){
//        String data = "";
//        String error_data = "";
//
//        HttpURLConnection httpURLConnection = null;
//        try {
//
//            httpURLConnection = (HttpURLConnection) new URL( url).openConnection();
//            httpURLConnection.setRequestMethod("POST");
//            for(Map.Entry<String,String> h:headers.entrySet()){
//                httpURLConnection.setRequestProperty(h.getKey(), h.getValue());
//
//            }
////            httpURLConnection.setRequestProperty("Authorization", svars.Service_token(act));
//if(requestType==RequestType.POST){
//    httpURLConnection.setDoOutput(true);
//    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//
//    wr.writeBytes(body);
//    wr.flush();
//    wr.close();  /**/
//    Log.e(" :: TX =>", " " + body);
//}
//
//            if(ssd.use_download_filter) {
//                //  httpURLConnection.setDoOutput(true);
//
//
//            }
//            int status = httpURLConnection.getResponseCode();
//            Log.e(ssd.service_name+" :: RX", " status=> " + status);
//            // ssi.on_status_changed("Synchronizing");
//
//            try {
//                InputStream in = httpURLConnection.getInputStream();
//                // InputStreamReader inputStreamReader = new InputStreamReader(in);
//
//
//                data = new String(ByteStreams.toByteArray(in));
//
//
//                Log.e(ssd.service_name+" :: RX =>", " " + data);
//
//
//
//            } catch (Exception exx) {
//                InputStream error = httpURLConnection.getErrorStream();
//                InputStreamReader inputStreamReader2 = new InputStreamReader(error);
//
//                int inputStreamData2 = inputStreamReader2.read();
//                while (inputStreamData2 != -1) {
//                    char current = (char) inputStreamData2;
//                    inputStreamData2 = inputStreamReader2.read();
//                    error_data += current;
//                }
//                Log.e(ssd.service_name+" :: TX", "error => " + error_data);
//
//            }
////                            maindata[0] = new JSONObject(data);
//            maindata[0] =Main_handler.OnDownloadedObject(ssd,filter_object[0],new JSONObject(data));
//            if(maindata[0]==null){
//
//                sync_complete_counter++;
//                sync_success_counter++;
//                double denm=(double) sync_sum_counter;
//                ssi.on_status_changed("Synchronized "+ssd.service_name);
//
//                double num=(double) sync_complete_counter;
//                double per=(num/denm)*100.0;
//                ssi.on_main_percentage_changed((int)per);
//                if(per==100.0)
//                {
//                    ssi.on_main_percentage_changed(100);
//                    ssi.on_status_changed("Synchronization complete");
//                    ssi.on_secondary_progress_changed(100);
//                    ssi.on_main_percentage_changed(100);
//                    ssi.on_info_updated("Synchronization complete");
//                    ssi.on_status_code_changed(3);
//                    ssi.onSynchronizationCompleted(ssd);
//                    ssi.onSynchronizationCompleted();
//                }else{
//                    ssi.onSynchronizationCompleted(ssd);
//
//                }
//                return;
//            }
//            data=null;
//            //  ssi.on_main_percentage_changed(0);
//
//            if (ssd.is_ok_position==null||(boolean)getJsonValue(ssd.is_ok_position,maindata[0] )) {
////  if (maindata[0].getBoolean(app_config.SYNC_USE_CAPS?"IsOkay":"isOkay")) {
//
//
//                JSONArray temp_ar=(JSONArray)getJsonValue(ssd.download_array_position,maindata[0] );
////                                Object json = new JSONTokener(maindata[0].opt(app_config.SYNC_USE_CAPS?"Result":"result").toString()).nextValue();
////                                temp_ar = json instanceof JSONArray ? (JSONArray) json : ((JSONObject) json).getJSONArray(app_config.SYNC_USE_CAPS?"Result":"result");
//                temp_ar=new JSONArray(temp_ar.toString().replace("'","''"));
//
// double den = (double) temp_ar.length();
//                // sdb.register_object_auto_ann(true,null,ssd);
//                if (!ssd.use_download_filter) {
//                    sdb.getDatabase().execSQL("DELETE FROM " + ssd.table_name + " WHERE sync_status ='" + sync_status.syned.ordinal() + "'");
//                }
//                Log.e(ssd.service_name + " :: RX", "IS OK " + den);
//                if (den>=0){
//
//                }
//
//                Log.e(ssd.service_name+" :: RX", " DONE " );
//
//
//                //   sdb.register_object_auto_ann(false,null,ssd);
//
//
//                sync_complete_counter++;
//                sync_success_counter++;
//                double denm=(double) sync_sum_counter;
//                ssi.on_status_changed("Synchronized "+ssd.service_name);
//
//                double num=(double) sync_complete_counter;
//                double per=(num/denm)*100.0;
//                ssi.on_main_percentage_changed((int)per);
//
//
//
//                if( den>=ssd.chunk_size&&ssd.use_download_filter)
//                {
//                    sync_sum_counter++;
//                    download_(ssd);
//
//
//                }else{
//                    if(per==100.0)
//                    {
//                        ssi.on_main_percentage_changed(100);
//                        ssi.on_status_changed("Synchronization complete");
//                        ssi.on_secondary_progress_changed(100);
//                        ssi.on_main_percentage_changed(100);
//                        ssi.on_info_updated("Synchronization complete");
//                        ssi.on_status_code_changed(3);
//                        ssi.onSynchronizationCompleted(ssd);
//                        ssi.onSynchronizationCompleted();
//                    }else{
//                        ssi.onSynchronizationCompleted(ssd);
//
//                    }
//
//                }
//                maindata[0] =null;
//                temp_ar=null;
//            }
//
//        } catch (Exception e) {
//            Log.e(ssd.service_name+":: TX", " error => " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (httpURLConnection != null) {
//                httpURLConnection.disconnect();
//            }
//        }
//
//    }
//    public void processDownloaded(JSONArray temp_ar){
//
//        synchronized (this) {
//            String[][] ins = realm.getInsertStatementsFromJson(temp_ar,ssd.object_package);
//            String sidz = ins[0][0];
//            String sidz_inactive = ins[0][1];
//            String[] qryz = ins[1];
//            int q_length=qryz.length;
//            temp_ar = null;
//            //  while(dbh.database.inTransaction()){Log.e("Waiting .. ","In transaction ");}
//            ssi.on_status_changed("Synchronizing " + ssd.service_name);
//
//            DatabaseManager.database.beginTransaction();
//            DatabaseManager.database.execSQL("INSERT INTO CP_" + ssd.table_name + " SELECT * FROM " + ssd.table_name + " WHERE sid in " + sidz + " AND sync_status=" + sync_status.pending.ordinal() + "");
//
//            for (int i = 0; i < q_length; i++) {
//                DatabaseManager.database.execSQL(qryz[i]);
//                double num = (double) i + 1;
//                double per = (num / q_length) * 100.0;
//                ssi.on_secondary_progress_changed((int) per);
//                //ssi.on_info_updated("Members :"+num+"/"+den+"    Total local members :"+sdb.employee_count());
//                ssi.on_info_updated(ssd.service_name + " :" + num + "/" + q_length + "    Local data :" + Integer.parseInt(sdb.get_record_count(ssd.table_name, ssd.table_filters)));
//            }
//
//            DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE data_status='false'");
////                                         DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE sid IN("+sidz_inactive+")AND sync_status<>" + sync_status.pending.ordinal());
////                                         DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE sid IN("+DatabaseManager.conccat_sql_string(sidz_inactive)+")AND sync_status<>" + sync_status.pending.ordinal());
//            DatabaseManager.database.execSQL("REPLACE INTO " + ssd.table_name + " SELECT * FROM CP_" + ssd.table_name + "");
//            DatabaseManager.database.execSQL("DELETE FROM CP_" + ssd.table_name + "");
//            DatabaseManager.database.setTransactionSuccessful();
//            DatabaseManager.database.endTransaction();
//        }
//    }
public HTTPResponse excecute(){
return null;
}
    public String body="";
    public String url="";
    public ContentType requestContentType = ContentType.JSON;
    public ContentType responseContentType = ContentType.JSON;
    public RequestType requestType = RequestType.POST;
    public HashMap<String, String> headers = new LinkedHashMap<String, String>();
    public HashMap<String, String> getBody = new LinkedHashMap<String, String>();
public class HTTPResponse{


}
public interface RequestResponceListener{
    void onResponseReceived(HTTPResponse response);


}
    enum ContentType {
           JSON,
           XML

       }

    enum RequestType {
        GET,
        POST

    }
}
