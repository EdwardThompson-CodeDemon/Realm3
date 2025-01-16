package com.realm.utils.biometrics.fp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.digitalpersona.uareu.Compression;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUGlobal;
import com.digitalpersona.uareu.dpfj.CompressionImpl;
import com.gemalto.wsq.WSQEncoder;

public class FingerprintManger {

    public Activity activity;
    public sfp_i interf;
    public boolean captureTemplate=true;
    public boolean captureImage=true;
    public boolean captureWsq=true;

    public FingerprintManger(Activity activity) {
        this.activity = activity;
        interf = (sfp_i) activity;
    }

    public void start() {

    }

    public void capture() {

    }

    public void stop() {

    }

    public void OnTemplateCaptured(String template, Fid.Format format) {

    }

    public void OnImageCaptured(Bitmap image) {

    }

    public void OnWSQCaptured(byte[] wsq) {

    }


    public static String imageToIso(Bitmap bmp) {
        byte[] wsq = new WSQEncoder(bmp).encode();
        Log.e("Converted wsq1", "" + wsq.length);
        CompressionImpl cmp = new CompressionImpl();
        try {
            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
            Compression.RawImage rawCompress = cmp.ExpandRaw(wsq, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST);
            cmp.Finish();
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);

            return ISOTemplate1;
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);
            try {
                cmp.Finish();
            } catch (Exception exx) {

            }
        }

        return null;
    }


    public static byte[] imageToWsq(Bitmap bmp) {

        try {

            byte[] wsq = new WSQEncoder(bmp).encode();
            Log.e("Converted wsq1", "" + wsq.length);
            return wsq;
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);


        }

        return null;
    }


    public static String wsqToIso(byte[] wsq) {
        CompressionImpl cmp = new CompressionImpl();
        try {
            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
            Compression.RawImage rawCompress = cmp.ExpandRaw(wsq, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST);
            cmp.Finish();
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);

            return ISOTemplate1;
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);
            try {
                cmp.Finish();
            } catch (Exception exx) {

            }
        }

        return null;
    }

}
