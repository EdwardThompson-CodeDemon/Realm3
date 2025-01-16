package com.realm.utils.biometrics.fp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Base64;
import android.util.Log;

import com.digitalpersona.uareu.Compression;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.digitalpersona.uareu.dpfj.CompressionImpl;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost;

import com.realm.R;

public class FP08_UAREU extends FingerprintManger {
    private Engine m_engine = null;
    //private Fmd m_fmd = null;
    //private int m_score = -1;
    private String m_deviceName = "";
    private String m_enginError;
    private Reader m_reader = null;
    private int m_DPI = 0;
    private Bitmap m_bitmap = null;
    public static Reader.CaptureResult cap_result = null;
    public static Fid.Format main_fid_format = Fid.Format.ISO_19794_4_2005;
    public static Fmd.Format main_fmd_format = Fmd.Format.ISO_19794_2_2005;
    public static boolean include_image = false;
    int session = 0;
    private boolean m_reset = false;
    private boolean m_resultAvailableToDisplay = false;
    public static String logTag = "FP08_UAREU";
    private static final String ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION";
    ReaderCollection readers;

    public FP08_UAREU(Activity activity) {
        super(activity);
    }

    @Override
    public void start() {
        super.start();
        initializeDevice();
    }

    @Override
    public void stop() {
        super.stop();
        close();
    }

    public void initializeDevice() {
        if (readers == null || readers.size() < 1) {
            try {
                readers = Globals.getInstance().getReaders(activity);
            } catch (UareUException e) {
                close();
                interf.on_device_error(e.getMessage());
                return;
            }
        }
        try {
            m_deviceName = readers.get(0).GetDescription().name;
        } catch (Exception ex) {
            interf.on_device_error(activity.getString(R.string.usb_cable_connected_error));
            return;
        }
        try {
            Globals.ClearLastBitmap();
            m_reader = Globals.getInstance().getReader(m_deviceName, activity);
            PendingIntent mPermissionIntent;
            mPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            activity.registerReceiver(mUsbReceiver, filter);

            if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(activity, mPermissionIntent, m_deviceName)) {
                checkDevice();
            }
            openDevice();
        } catch (UareUException e1) {
            Log.e(logTag, "Error opening device:" + e1.getMessage());
        } catch (DPFPDDUsbException e) {
            Log.e(logTag, "Error finding device:" + e.getMessage());
        }

    }

    void openDevice() {
        m_bitmap = Globals.GetLastBitmap();
        if (m_bitmap == null) {
            m_bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_settings);
        }
        Globals.DefaultImageProcessing = Reader.ImageProcessing.IMG_PROC_DEFAULT;
        try {
            if (m_reader == null) {
                m_reader = Globals.getInstance().getReader(m_deviceName, activity);
            }
            m_reader.Open(Reader.Priority.EXCLUSIVE);
            m_DPI = Globals.GetFirstDPI(m_reader);
            m_engine = UareUGlobal.GetEngine();
        } catch (UareUException e) {
            Log.e(logTag, "Error opening device:" + e.getMessage());
            interf.on_device_error(e.getMessage());
            return;
        }
        beginCapture();
    }

    public void beginCapture() {
        session++;
        new Thread(() -> {
            m_reset = false;
            int i = 0;
            while (!m_reset) {
                try {
                    Log.e(logTag, "Session: " + session + " loop: " + i);
                    Log.e(logTag, "DPI: " + m_DPI);
                    i++;
                    cap_result = m_reader.Capture(main_fid_format, Reader.ImageProcessing.IMG_PROC_DEFAULT, m_DPI, -1);
                    Log.e(logTag, "Capture quality: " + cap_result.quality.toString());
                    if (cap_result == null || cap_result.image == null) continue;
                    if (captureTemplate) {
                        interf.on_result_obtained(Base64.encodeToString(m_engine.CreateFmd(cap_result.image, main_fmd_format).getData(), 0));
                    }

                    if (captureWsq) {
                        compressImage(cap_result.image);
                    }



                } catch (Exception e) {
                    if (!m_reset) {
                        Log.e(logTag, "Capture error: " + e.toString());
                        m_deviceName = "";
                        //  close();
                    }
                }

                m_resultAvailableToDisplay = false;

                // an error occurred
                if (cap_result == null || cap_result.image == null) continue;

                try {
                    m_enginError = "";
                    m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());
                    if (captureImage) {   interf.on_result_image_obtained(m_bitmap); }


                } catch (Throwable e) {
                    m_enginError = e.toString();
                    Log.e(logTag, "Captured image extraction error: " + e.toString());
                }
            }
        }).start();
    }

    public static Compression cmp = new CompressionImpl();

    void compressImage(Fid ISOFid) {
        try {
            cmp = new CompressionImpl();
            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
            byte[] rawCompress = cmp.CompressRaw(ISOFid.getViews()[0].getImageData(), ISOFid.getViews()[0].getWidth(), ISOFid.getViews()[0].getHeight(), 500, 8, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            cmp.Finish();
            interf.on_result_wsq_obtained(rawCompress);
        } catch (UareUException ex) {
            Log.e(logTag, "Compression error: " + ex.getMessage());
        } catch (Exception ex) {
            Log.e(logTag, "Compression error: " + ex.getMessage());
        }
    }

    public void close() {
        Log.e(logTag, "Closing device");
        try {
            m_reset = true;
            try {
                m_reader.CancelCapture();
            } catch (Exception e) {
            }
            m_reader.Close();
        } catch (Exception e) {
            Log.e(logTag, "Error closing device");
        }
    }

    protected void checkDevice() {
        try {
            m_reader.Open(Reader.Priority.EXCLUSIVE);
            Reader.Capabilities cap = m_reader.GetCapabilities();
            m_reader.Close();
        } catch (UareUException e1) {
            Log.e(logTag, "Error finding device:" + e1.getMessage());
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            checkDevice();
                            openDevice();
                        }
                    }
                }
            }
        }
    };
}
