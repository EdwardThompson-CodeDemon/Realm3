package com.realm.utils.biometrics.fp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.HZFINGER.HAPI;
import com.HZFINGER.HostUsb;
import com.HZFINGER.LAPI;

public class T801_ extends FingerprintManger{

    private LAPI m_cLAPI = null;
    private HostUsb mHostUSb = null;

    private long m_hDevice = 0;
    private byte[] m_image = new byte[LAPI.WIDTH*LAPI.HEIGHT];
    private byte[] m_ansi_template = new byte[LAPI.FPINFO_STD_MAX_SIZE];
    private byte[] m_iso_template = new byte[LAPI.FPINFO_STD_MAX_SIZE];
    private byte[] bfwsq = new byte[512*512];
    private int[] RGBbits = new int[256 * 360];

    public static final int MESSAGE_SET_ID = 100;
    public static final int MESSAGE_SHOW_TEXT = 101;
    public static final int MESSAGE_VIEW_ANSI_TEMPLATE = 103;
    public static final int MESSAGE_VIEW_ISO_TEMPLATE = 104;
    public static final int MESSAGE_SHOW_IMAGE = 200;
    public static final int MESSAGE_ENABLE_BTN = 300;
    public static final int MESSAGE_SHOW_BITMAP = 303;
    public static final int MESSAGE_LIST_START = 400;
    public static final int MESSAGE_LIST_NEXT = 401;
    public static final int MESSAGE_LIST_END = 402;
    public static final int MESSAGE_ID_ENABLED = 403;
    public static final int MESSAGE_ID_SETTEXT= 404;
    public static final int MESSAGE_CHKLIVE_DISABLE= 500;

    private static final int TRANSPARENT_GRAY_THRESHOLD = 150;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private HAPI m_cHAPI = null;

    private boolean DEBUG = true;
    private volatile boolean bContinue = false;
    Activity myThis;


    private ScreenBroadcastReceiver mScreenReceiver;

    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action))
            {
                stop();
            }
            else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
            {
                UsbDevice newDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (newDevice != null && isFingerDevice(newDevice)) {
                    m_cLAPI.setHostUsb(mHostUSb);
                    if(!mHostUSb.AuthorizeDevice(newDevice)){
                        Toast.makeText(context,"FingerDevice attached",Toast.LENGTH_LONG).show();
                    }
                }
            }
            else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
            {
                UsbDevice oldDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (oldDevice != null && isFingerDevice(oldDevice)) {
                    m_cLAPI.setHostUsb(null);
                    Toast.makeText(context,"FingerDevice detached",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private boolean isFingerDevice(UsbDevice device){
        int vid = device.getVendorId();
        int pid = device.getProductId();
        if(vid == LAPI.VID && pid == LAPI.PID){
            return true;
        }

        return false;
    }

    public T801_(Activity activity) {
        super(activity);
        m_cLAPI = new LAPI(activity);
        m_cHAPI = new HAPI(activity,m_fpsdkHandle);

        mHostUSb = new HostUsb(activity);


        mScreenReceiver = new ScreenBroadcastReceiver();
        registerListener();
        Runnable r = new Runnable() {
            public void run() {
                OPEN_DEVICE();
            }
        };
        Thread s = new Thread(r);
        s.start();

    }
    private void registerListener() {
        if (activity != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            //filter.addAction(HostUsb.ACTION_USB_PERMISSION);
            activity.registerReceiver(mScreenReceiver, filter);
        }
    }
    private final Handler m_fpsdkHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str = "";
            Resources res;
            switch (msg.what) {
                case 0xff:
                    break;
                case HAPI.MSG_SHOW_TEXT:
                   // tvHAPImsg.setText((String)msg.obj);
                    break;
                case HAPI.MSG_PUT_FINGER:
//                    res = getResources();
//                    str = res.getString(R.string.Put_your_finger);
//                    if (msg.arg1>0) {
//                        str += (" ("+String.valueOf(msg.arg1)+"/"+String.valueOf(msg.arg2)+")");
//                    }
//                    str += " ! ";
//                    str += (String)msg.obj;
//                    tvHAPImsg.setText(str);
                    break;
                case HAPI.MSG_RETRY_FINGER:
//                    res = getResources();
//                    str = res.getString(R.string.Retry_your_finger);
//                    str += " !";
//                    tvHAPImsg.setText(str);
                    break;
                case HAPI.MSG_TAKEOFF_FINGER:
//                    res = getResources();
//                    str = res.getString(R.string.Takeoff_your_finger);
//                    str += " !";
//                    tvHAPImsg.setText(str);
                    break;
                case HAPI.MSG_ON_SEARCHING:
//                    res = getResources();
//                    str = res.getString(R.string.TEXT_ON_SEARCHING);
//                    if (msg.arg1>0) {
//                        str += (" (quality="+String.valueOf(msg.arg1)+")");
//                    }
//                    str += "  ...  ";
//                    tvHAPImsg.setText(str);
                    break;
                case HAPI.MSG_FINGER_CAPTURED:
                    ShowFingerBitmap ((byte[])msg.obj,msg.arg1,msg.arg2);
                    break;
                case HAPI.MSG_DBRECORD_START:
//                    mListString = new String[msg.arg1];
//                    mFiletString = new String[msg.arg1];
                    break;
                case HAPI.MSG_DBRECORD_NEXT:
//                    mListString[msg.arg2] = String.format("No = %d : ID = %s",msg.arg2,(String)msg.obj);
//                    mFiletString[msg.arg2] = (String)msg.obj;
                    break;
                case HAPI.MSG_DBRECORD_END:
//                    UpdateListView();
//                    String txt = String.format("Record Count = %d", msg.arg1);
//                    tvHAPImsg.setText(txt);
                    break;
            }
        }
    };
    @Override
    public void start() {
        super.start();
        startVideo();
    }

    void startVideo()
    {
        if (bContinue) {
            bContinue = false;
            //btnOnVideo.setText("Video");
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,"Canceled"));
            return;
        }
//        btnOnVideo.setText("Stop");
        bContinue = true;
        Runnable r = new Runnable() {
            public void run() {
                ON_VIDEO ();
            }
        };
        Thread s = new Thread(r);
        s.start();
    }
    protected void ON_VIDEO() {

//        m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_ID_ENABLED, R.id.btnOnVideo, 1));
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,"Put your finger"));
        int secLevel = Integer.parseInt("3");
        if (secLevel < 1) secLevel = 1; if (secLevel > 5) secLevel = 5;
        while (bContinue) {
            int startTime = (int)System.currentTimeMillis();
            int ret = m_cLAPI.GetImage(m_hDevice, m_image);
            if (ret == LAPI.NOTCALIBRATED) {
                m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "not Calibrated !").sendToTarget();
                break;
            }
            else if (ret != LAPI.TRUE) {
                m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "Can't get image !").sendToTarget();
                break;
            }
            ret = m_cLAPI.IsPressFingerEx(m_hDevice, m_image, false, LAPI.LIVECHECK_THESHOLD[secLevel - 1]);
            m_fEvent.obtainMessage(MESSAGE_SHOW_IMAGE, LAPI.WIDTH, LAPI.HEIGHT, m_image).sendToTarget();
            if (ret == LAPI.FAKEFINGER) {
                m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, "warning : Fake Finger !").sendToTarget();
                SLEEP(500);
                continue;
                //break;
            }
            String msg = String.format("GetImage(%d) = OK : %dms", ret, (int)(System.currentTimeMillis() - startTime));
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0,msg));
        }
        bContinue = false;
//        m_appHandle.obtainMessage(MESSAGE_ID_SETTEXT, R.id.btnOnVideo, R.string.TEXT_VIDEO).sendToTarget();
//        EnableAllButtons(false,true);
    }
    protected void SLEEP (int waittime)
    {
        int startTime, passTime = 0;
        startTime = (int)System.currentTimeMillis();
        while (passTime < waittime) {
            passTime = (int)System.currentTimeMillis();
            passTime = passTime - startTime;
        }
    }
    private final Handler m_fEvent = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_TEXT:
//                    tvLAPImsg.setText((String)msg.obj);
                    break;
                case MESSAGE_VIEW_ANSI_TEMPLATE:
//                    tvANSITemp.setText((String)msg.obj);
                    break;
                case MESSAGE_VIEW_ISO_TEMPLATE:
//                    tvISOTemp.setText((String)msg.obj);
                    break;
                case MESSAGE_ID_ENABLED:
//                    Button btn = (Button) findViewById(msg.arg1);
//                    if (msg.arg2 != 0) btn.setEnabled(true);
//                    else btn.setEnabled(false);
                    break;
                case MESSAGE_SHOW_IMAGE:
                    ShowFingerBitmap ((byte[])msg.obj,msg.arg1,msg.arg2);
                    break;
                case MESSAGE_CHKLIVE_DISABLE:
//                    chkCheckLive.setChecked(false);
//                    chkCheckLive.setEnabled(false);
                    break;
            }
        }
    };
    private void ShowFingerBitmap(byte[] image, int width, int height) {
        if(m_cLAPI.GetImageQuality(m_hDevice,m_image)<50){return;}
        if (width==0) return;
        if (height==0) return;
        for (int i = 0; i < width * height; i++ ) {
            int v;
            if (image != null) v = image[i] & 0xff;
            else v = 255;

            if (true) RGBbits[i] = Color.rgb(v, v, v);
            else {
                if (v < TRANSPARENT_GRAY_THRESHOLD) RGBbits[i] = Color.rgb(255, 0, 0);
                else RGBbits[i] = Color.rgb(255, 255, 255);
            }
        }

            Bitmap bmp = Bitmap.createBitmap(RGBbits, width, height, Bitmap.Config.RGB_565);
//        interf.on_result_image_obtained(bmp);
        interf.on_result_obtained(imageToIso(bmp));
        interf.on_result_image_obtained(bmp);
        interf.on_result_wsq_obtained(imageToWsq(bmp));
//        viewFinger.setImageBitmap(bmp);
    }


    private final Handler m_appHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SET_ID:
//                    txtRegID.setText((String)msg.obj);
                    break;
                case MESSAGE_SHOW_TEXT:
//                    tvHAPImsg.setText((String)msg.obj);
                    break;
                case MESSAGE_SHOW_IMAGE:
                    ShowFingerBitmap ((byte[])msg.obj,msg.arg1,msg.arg2);
                    break;
                case MESSAGE_ENABLE_BTN:
                    boolean bEnable = msg.arg1 == 1 ? true : false;
                    boolean bOpen = msg.arg2 == 1 ? true : false;


                    break;
                case MESSAGE_SHOW_BITMAP:
//                    viewFinger.setImageBitmap((Bitmap)msg.obj);
                    break;
                case MESSAGE_LIST_START:
//                    mListString = new String[msg.arg1];
//                    mFiletString = new String[msg.arg1];
                    break;
                case MESSAGE_LIST_NEXT:
//                    mListString[msg.arg2] = String.format("No = %d : ID = %s",msg.arg2,(String)msg.obj);
//                    mFiletString[msg.arg2] = (String)msg.obj;
                    break;
                case MESSAGE_LIST_END:
//                    UpdateListView();
//                    String txt = String.format("Record Count = %d", msg.arg1);
//                    tvHAPImsg.setText(txt);
                    break;
                case MESSAGE_ID_ENABLED:
//                    btn = (Button) findViewById(msg.arg1);
//                    if (msg.arg2 != 0) btn.setEnabled(true);
//                    else btn.setEnabled(false);
                    break;
                case MESSAGE_ID_SETTEXT:
//                    btn = (Button) findViewById(msg.arg1);
//                    btn.setText(msg.arg2);
                    break;
            }
        }
    };

    @Override
    public void stop() {
        super.stop();

        m_cHAPI.DoCancel();
        bContinue = false;
        CLOSE_DEVICE();
    }

    protected void OPEN_DEVICE() {
        String msg = "OPEN ...";
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, msg));
        UsbDevice dev = mHostUSb.hasDeviceOpen();
        if (dev != null) {
            m_cLAPI.setHostUsb(mHostUSb);
            mHostUSb.AuthorizeDevice(dev);
        }
        if (true) m_hDevice = m_cLAPI.OpenDeviceEx(LAPI.SCSI_MODE);
        else m_hDevice = m_cLAPI.OpenDeviceEx(LAPI.SPI_MODE);
        if (m_hDevice == 0) {
            msg = "Can't open device !";
//            EnableAllButtons(true, false);
            //CLOSE_DEVICE();
        } else {
            if (LAPI.bInitNetManager) msg = "OpenDevice() = OK";
            else {
                msg = "OpenDevice() = OK, unable to check live-scan";
                m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_CHKLIVE_DISABLE, 0, 0, 0));
            }
//            EnableAllButtons(false, true);
        }
        m_cHAPI.m_hDev = m_hDevice;
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, msg));
    }

    protected void CLOSE_DEVICE() {

            String msg;
            try {


                m_cHAPI.DoCancel();
                if (m_hDevice != 0) {
                    m_cLAPI.CloseDeviceEx(m_hDevice);
                }
                msg = "CloseDevice() = OK";

                m_hDevice = 0;
                m_cHAPI.m_hDev = 0;

                m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, msg));
            } catch (Exception E) {
                msg = "error:" + E.getMessage();
                m_fEvent.sendMessage(m_fEvent.obtainMessage(MESSAGE_SHOW_TEXT, 0, 0, msg));
                E.printStackTrace();
            }

    }

}
