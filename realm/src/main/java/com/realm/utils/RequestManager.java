package com.realm.utils;

import static com.realm.Services.SynchronizationManager.getJsonValue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.realm.Realm;
import com.realm.utils.svars;

public class RequestManager {
    public String logTag = "RequestManager";

    public interface RequestCallback {

        void onApiConnectionFailed();

        default void onApiConnectionStatusUpdated(int status) {


        }

        default void onServerConnected() {


        }

        default void OnRequestSuccessfully(String token, JSONObject response) {


        }

        void OnRequestSuccessfully(JSONObject response);

        void OnRequestFailed();

        default void  OnAuthenticatedSuccessfully(String token, JSONObject response) {


        }

        default void  OnAuthenticationFailed() {

        }

        default void OnAuthenticated(JSONObject response) {


        }
    }

    public interface DownloadCallback {


        default void onDownloadStarted() {


        }

        default void onDownloadComplete(File file) {


        }

        default void onDownloadProgress(long total, long downloaded) {


        }

        default void onApiConnectionStatusUpdated(int status) {


        }
    }

    public void authenticate(String is_ok_position, String username, String password, RequestCallback requestCallback) {
        Context context = Realm.context;
        AppConfig appConfig = svars.current_app_config(context);

        final JSONObject JO = new JSONObject();

        JSONObject user = new JSONObject();
        try {

            user.put("PassWord", password);
            user.put("UserName", username);
            user.put("Branch", appConfig.ACCOUNT_BRANCH);
            user.put("AccountName", appConfig.ACCOUNT);
            user.put("Language", "English");


            JO.put("IsRenewalPasswordRequest", "false");
            JO.put("CurrentUser", user);
        } catch (JSONException ex) {
        }
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = {new JSONObject()};

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (svars.isInternetAvailable()) {
                            String response = "";
                            String error_data = "";
                            HttpURLConnection httpURLConnection = null;

                            try {
                                Log.e(logTag, "URL: " + appConfig.APP_MAINLINK + appConfig.AUTHENTICATION_URL);
                                Log.e(logTag, "TX: " + JO);
                                httpURLConnection = (HttpURLConnection) new URL(appConfig.APP_MAINLINK + appConfig.AUTHENTICATION_URL).openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                                httpURLConnection.setDoOutput(true);

                                requestCallback.onServerConnected();
                                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                                wr.write(JO.toString().getBytes());
                                wr.flush();
                                wr.close();
                                int status = httpURLConnection.getResponseCode();
                                requestCallback.onApiConnectionStatusUpdated(status);

                                try {
                                    InputStream in = httpURLConnection.getInputStream();
                                    response = new String(ByteStreams.toByteArray(in));
                                    Log.d(logTag, "Response:" + response);

                                    maindata[0] = new JSONObject(response);
                                    requestCallback.OnAuthenticated(maindata[0]);

                                    if (is_ok_position == null || (boolean) getJsonValue(is_ok_position, maindata[0])) {
                                        requestCallback.OnAuthenticatedSuccessfully(httpURLConnection.getHeaderField("authorization"), maindata[0]);

//                                       svars.set_Service_token(context, httpURLConnection.getHeaderField("authorization"));

                                    } else {
                                        requestCallback.OnAuthenticationFailed();

                                    }

                                } catch (Exception ex) {
                                    InputStream error = httpURLConnection.getErrorStream();
                                    error_data = new String(ByteStreams.toByteArray(error));
                                    requestCallback.OnAuthenticationFailed();
                                    requestCallback.OnRequestFailed();

                                    Log.e(logTag, "Authentication Error: " + error_data);

                                }

                            } catch (Exception e) {

                                requestCallback.OnRequestFailed();
                            }
                        } else {


                            requestCallback.onApiConnectionFailed();
                            requestCallback.OnRequestFailed();
                        }
                    }
                }, 10);

                Looper.loop();
            }
        };
        thread.start();


    }


    public void requestGet(String url, String is_ok_position, RequestCallback requestCallback) {
        Context context = Realm.context;
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = {new JSONObject()};
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (svars.isInternetAvailable()) {
                            String response = "";
                            String error_data = "";
                            HttpURLConnection httpURLConnection = null;

                            try {
                                Log.e(logTag, "URL:" + url);
                                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                                httpURLConnection.setRequestMethod("GET");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                                httpURLConnection.setDoOutput(false);
                                requestCallback.onServerConnected();

                                int status = httpURLConnection.getResponseCode();
                                requestCallback.onApiConnectionStatusUpdated(status);

                                try {
                                    InputStream in = httpURLConnection.getInputStream();
                                    response = new String(ByteStreams.toByteArray(in));
                                    Log.e(logTag, "Response:" + response);
                                    maindata[0] = new JSONObject(response);

                                    if (is_ok_position == null || (boolean) getJsonValue(is_ok_position, maindata[0])) {
                                        requestCallback.OnRequestSuccessfully("", maindata[0]);

//                                       svars.set_Service_token(context, httpURLConnection.getHeaderField("authorization"));

                                    } else {
                                        requestCallback.OnRequestFailed();

                                    }

                                } catch (Exception ex) {
                                    InputStream error = httpURLConnection.getErrorStream();
                                    error_data = new String(ByteStreams.toByteArray(error));
                                    requestCallback.OnRequestFailed();
                                    Log.e(logTag, "Request Error: " + error_data);

                                }

                            } catch (Exception e) {

                                requestCallback.OnRequestFailed();
                            }
                        } else {

                            requestCallback.onApiConnectionFailed();
                            requestCallback.OnRequestFailed();

                        }
                    }
                }, 10);

                Looper.loop();
            }
        };
        thread.start();


    }

    public void requestPost(String url, String is_ok_position, JSONObject output, RequestCallback requestCallback) {
        Context context = Realm.context;

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = {new JSONObject()};

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (svars.isInternetAvailable()) {
                            String response = "";
                            String error_data = "";
                            HttpURLConnection httpURLConnection = null;

                            try {
                                Log.e(logTag, "URL:" + url);
                                Log.e(logTag, "TX:" + output);
                                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                                httpURLConnection.setDoOutput(true);

                                requestCallback.onServerConnected();
                                if(output!=null){
                                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                                    wr.write(output.toString().getBytes());
                                    wr.flush();
                                    wr.close();
                                }

                                int status = httpURLConnection.getResponseCode();
                                requestCallback.onApiConnectionStatusUpdated(status);
                                Log.e(logTag, "Status:" + status);

                                try {
                                    InputStream in = httpURLConnection.getInputStream();
                                    response = new String(ByteStreams.toByteArray(in));
                                    Log.e(logTag, "Response:" + response);

                                    maindata[0] = new JSONObject(response);
                                    Boolean isok = is_ok_position == null || (boolean) getJsonValue(is_ok_position, maindata[0]);
                                    if (isok) {
                                        requestCallback.OnRequestSuccessfully(maindata[0]);
//                                       svars.set_Service_token(context, httpURLConnection.getHeaderField("authorization"));

                                    } else {
                                        requestCallback.OnRequestFailed();

                                    }

                                } catch (Exception ex) {
                                    InputStream error = httpURLConnection.getErrorStream();
                                    error_data = new String(ByteStreams.toByteArray(error));
                                    requestCallback.OnRequestFailed();

                                    Log.e(logTag, "Request Error: " + error_data);
                                }


                            } catch (Exception e) {

                                requestCallback.OnRequestFailed();
                            }
                        } else {
                            requestCallback.onApiConnectionFailed();
                            requestCallback.OnRequestFailed();

                        }
                    }
                }, 10);

                Looper.loop();
            }
        };
        thread.start();


    }


    public void download_(String url, String destination, DownloadCallback downloadCallback) {

        Context context = Realm.context;
        AppConfig appConfig = svars.current_app_config(context);

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = {new JSONObject()};

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (svars.isInternetAvailable()) {
                            String error_data = "";
                            HttpURLConnection httpURLConnection = null;

                            try {
                                Log.e(logTag, "URL:" + url);

                                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                                int status = httpURLConnection.getResponseCode();
                                int contenet_len = httpURLConnection.getContentLength();
                                downloadCallback.onApiConnectionStatusUpdated(status);
                                Log.e(logTag, "Download stt:" + status);

                                Log.e(logTag, "Download content:" + httpURLConnection.getContentType() + " len: " + contenet_len);
//                                long existingFileSize = new File(destination).length();
//                                if (existingFileSize < fileLength) {
//                                    httpFileConnection.setRequestProperty(
//                                            "Range",
//                                            "bytes=" + existingFileSize + "-" + fileLength
//                                    );
//                                }

                                try {
                                    File outFile = new File(destination);
                                    try (FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
                                        try (InputStream inputStream = httpURLConnection.getInputStream()) {
//                                            ByteStreams.copy(inputStream, fileOutputStream);
                                            byte[] buf = new byte[16384000 * 2];
                                            long total = 0;
                                            while (true) {
                                                int r = inputStream.read(buf);
                                                if (r == -1) {
                                                    break;
                                                }
                                                fileOutputStream.write(buf, 0, r);
                                                total += r;
                                                downloadCallback.onDownloadProgress(contenet_len, total);
                                            }
                                        }
                                    }
                                    downloadCallback.onDownloadComplete(outFile);

                                    Log.e(logTag, "File size :" + outFile.length());


                                } catch (Exception ex) {
                                    Log.e(logTag, "Request Error: ", ex);
                                    downloadCallback.onApiConnectionStatusUpdated(0);
                                    try (InputStream error = httpURLConnection.getErrorStream()) {
                                        error_data = new String(ByteStreams.toByteArray(error));
                                    }

                                    Log.e(logTag, "Request Error: " + error_data);
                                }


                            } catch (Exception e) {

                                Log.e(logTag, "Request exception: ", e);
                                downloadCallback.onApiConnectionStatusUpdated(0);
                            }
                        } else {

                            downloadCallback.onApiConnectionStatusUpdated(0);

                        }
                    }
                }, 10);

                Looper.loop();
            }
        };
        thread.start();


    }

    public void download(String url, String destination, DownloadCallback downloadCallback) {
        destination = destination.replace(":", "_");
        String finalDestination = destination;
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = {new JSONObject()};

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (svars.isInternetAvailable()) {
                            try {
                                downloadUsingNIO(new URL(url), new File(finalDestination), downloadCallback);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                downloadCallback.onApiConnectionStatusUpdated(0);
                            }
                        } else {


                            downloadCallback.onApiConnectionStatusUpdated(0);

                        }
                    }
                }, 10);

                Looper.loop();
            }
        };
        thread.start();


    }

    private static void downloadUsingStream(String urlStr, String file, DownloadCallback downloadCallback) {
        try {
            URL url = new URL(urlStr);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            FileOutputStream fis = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fis.write(buffer, 0, count);
            }
            fis.close();
            bis.close();
        } catch (Exception exception) {
            downloadCallback.onApiConnectionStatusUpdated(0);
        }
    }

    public void downloadUsingNIO(URL url, File outFile, DownloadCallback downloadCallback) {
        Stopwatch stopwatch =  Stopwatch.createUnstarted();
        stopwatch.start();
        downloadCallback.onApiConnectionStatusUpdated(200);
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream()); FileOutputStream fos = new FileOutputStream(outFile)) {
            downloadCallback.onDownloadProgress(100, 1);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            downloadCallback.onDownloadProgress(100, 100);
            stopwatch.stop();
            downloadCallback.onDownloadComplete(outFile);
            Log.e(logTag, "File size: " + outFile.length() + " time: " + stopwatch.elapsed(TimeUnit.SECONDS) + "seconds");
        } catch (Exception exception) {
            Log.e(logTag, "Request exception: ", exception);
            downloadCallback.onApiConnectionStatusUpdated(0);
        }
    }


    class DownloadStatus {
        public int downloadPercent = 0;
        public boolean reportedComplete = false;
        public File file;
        public long expectedFileSize;
    }

    Timer downloadProgressTimer;
    DownloadStatus downloadStatus;

    void setTimer() {

        downloadProgressTimer = new Timer();
        downloadProgressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (downloadStatus == null || downloadStatus.file == null) {
                    cancel();
                    return;
                } else {

                }

            }
        }, 0, 5000);
    }

}

