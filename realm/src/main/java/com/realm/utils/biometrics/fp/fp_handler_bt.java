package com.realm.utils.biometrics.fp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import com.realm.utils.biometrics.fp.reader.BluetoothReaderService;
import com.realm.utils.bluetooth.bt_device_connector;


/**
 * Created by Thompsons on 08-Feb-17.
 */

public class fp_handler_bt {

        private static final String TAG = "BluetoothReader";
        private static final boolean D = true;

        private final static byte CMD_PASSWORD=0x01;	//Password
        private final static byte CMD_ENROLID=0x02;		//Enroll in Device
        private final static byte CMD_VERIFY=0x03;		//Verify in Device
        private final static byte CMD_IDENTIFY=0x04;	//Identify in Device
        private final static byte CMD_DELETEID=0x05;	//Delete in Device
        private final static byte CMD_CLEARID=0x06;		//Clear in Device
        private final static byte CMD_ENROLHOST=0x07;	//Enroll to Host
        private final static byte CMD_CAPTUREHOST=0x08;	//Caputre to Host
        private final static byte CMD_MATCH=0x09;		//Match
        private final static byte CMD_WRITECARD=0x0A;	//Write Card Data
        private final static byte CMD_READCARD=0x0B;	//Read Card Data
        private final static byte CMD_CARDID=0x0C;		//Card Sn Match
        private final static byte CMD_CARDFINGER=0x0D;	//Fingerprint Card Match
        private final static byte CMD_CARDSN=0x0E;		//Read Card Sn
        private final static byte CMD_GETSN=0x10;
        private final static byte CMD_PRINTCMD=0x20;		//Printer Print
        private final static byte CMD_GETBAT=0x21;
        private final static byte CMD_GETIMAGE=0x30;
        private final static byte CMD_GETCHAR=0x31;
        private final static byte CMD_UPCARDSN=0x43;

        private byte mDeviceCmd=0x00;
        private boolean mIsWork=false;
        private byte  mCmdData[]=new byte[10240];
        private int	  mCmdSize=0;

        private Timer mTimerTimeout=null;
        private TimerTask mTaskTimeout=null;
        private Handler mHandlerTimeout;

        // Message types sent from the BluetoothChatService Handler
        public static final int MESSAGE_STATE_CHANGE = 1;
        public static final int MESSAGE_READ = 2;
        public static final int MESSAGE_WRITE = 3;
        public static final int MESSAGE_DEVICE_NAME = 4;
        public static final int MESSAGE_TOAST = 5;

        // Key names received from the BluetoothChatService Handler
        public static final String DEVICE_NAME = "device_name";
        public static final String TOAST = "toast";

        // Intent request codes
        public static final int REQUEST_CONNECT_DEVICE = 1;
        public static final int REQUEST_ENABLE_BT = 2;

        // Layout Views
        private TextView mTitle;
        private ListView mConversationView;
        private ImageView fingerprintImage;

        // Name of the connected device
        private String mConnectedDeviceName = null;
        // Array adapter for the conversation thread
        private ArrayAdapter<String> mConversationArrayAdapter;
        // String buffer for outgoing messages
        private StringBuffer mOutStringBuffer;
        // Local Bluetooth adapter
        public static BluetoothAdapter mBluetoothAdapter = null;
        // Member object for the chat services
        public static BluetoothReaderService mChatService = null;

        public byte mRefData[]=new byte[512];
        public int mRefSize=0;
        public byte mMatData[]=new byte[512];
        public int mMatSize=0;

        public byte mCardSn[]=new byte[4];
        public byte mCardData[]=new byte[4096];
        public int mCardSize=0;

        public byte mBat[]=new byte[2];
        public int mUpImageSize=0;
    public byte mUpImage[] = new byte[73728]; // image data

        //
        public byte mRefCoord[]=new byte[512];
        public byte mMatCoord[]=new byte[512];

        public byte mIsoData[]=new byte[378];
Activity act;
    Timer get_batt, get_print;
    sfp_i interf;
       public fp_handler_bt(final Activity act)
       {
           this.act=act;
           get_batt = new Timer();
           get_print = new Timer();

           mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
interf=(sfp_i) act;
            if (mBluetoothAdapter == null) {
               // Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
               // finish();
                return;
            }
           if (!mBluetoothAdapter.isEnabled()) {
               Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
             act.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
               // Otherwise, setup the chat session
           } else {
               if (mChatService == null) {
                   setupChat();
               }
               else {

               }
           }
          /* Intent serverIntent = new Intent(act, DeviceListActivity.class);
           act.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);*/


       }
public void start_auto()
{
    get_print.schedule(new TimerTask() {
        @Override
        public void run() {
            capture();

        }
    },100,5000);

}
    private int imgSize;
    public static final int IMG200 = 200;
    public static final int IMG288 = 288;
    public static final int IMG360 = 360;

public void capture()
{
    act.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            try {
                if (mChatService != null & mChatService.getState() == BluetoothReaderService.STATE_CONNECTED) {
                    imgSize = 288;
                    mUpImageSize = 0;

                    SendCommand(CMD_GETIMAGE,null,0);
//                    SendCommand(CMD_CAPTUREHOST, null, 0);
                }
            }catch (Exception ex){

            }
        }
    });
}


public void stop_auto()
{
    get_print.cancel();
    get_batt.cancel();

}


    public void enrol()
    {
        int id=2;
        byte buf[]=new byte[2];
        buf[0]=(byte)(id);
        buf[1]=(byte)(id>>8);
        SendCommand(CMD_ENROLID,buf,2);
    }
    public void veryfyenrole()
    {
        int id=2;
        byte buf[]=new byte[2];
        buf[0]=(byte)(id);
        buf[1]=(byte)(id>>8);
        SendCommand(CMD_VERIFY,buf,2);
    }
    public void search()
    {
        SendCommand(CMD_IDENTIFY,null,0);
    }
    public void delete()
    {
        int id=2;
        byte buf[]=new byte[2];
        buf[0]=(byte)(id);
        buf[1]=(byte)(id>>8);
        SendCommand(CMD_DELETEID,buf,2);
    }
    public void clearall()
    {
        SendCommand(CMD_CLEARID,null,0);
    }
    public void enrolltemplate()
    {
        SendCommand(CMD_ENROLHOST,null,0);

    }
    public void capturetemplate()
    {
        SendCommand(CMD_CAPTUREHOST,null,0);
    }
    public void matchtemplate()
    {
        byte buf[]=new byte[1024];
        memcpy(buf,0,mRefData,0,512);
        memcpy(buf,512,mMatData,0,512);
        //System.arraycopy(mRefData, 0, buf, 0, 512);
        //System.arraycopy(mMatData, 0, buf, 512, 256);
        SendCommand(CMD_MATCH,buf,1024);
    }
    public void writecard()
    {
        SendCommand(CMD_WRITECARD,mCardData,1024);
    }
    public void readcard()
    {
        SendCommand(CMD_READCARD,null,0);
    }

    public void cardsnmatch()
    {

        SendCommand(CMD_CARDID,null,0);
    }

    public void fingerprintcardmatch()
    {
        SendCommand(CMD_CARDFINGER,null,0);

    }
    public void getimage()
    {
        SendCommand(CMD_GETIMAGE,null,0);
    }
    public void getdata()
    {
        SendCommand(CMD_GETCHAR,null,0);

    }


public void close()
{
    mChatService.stop();
    mChatService=null;
}
    public void setupChat() {
            Log.d(TAG, "setupChat()");

 mChatService = new BluetoothReaderService(act, mHandler);	// Initialize the BluetoothChatService to perform bluetooth connections
            mOutStringBuffer = new StringBuffer("");
            if(new bt_device_connector(act, bt_device_connector.bt_device_type.fp_device).set_device(bt_device_connector.bt_device_type.fp_device)==null)
            {
                new bt_device_connector(act, bt_device_connector.bt_device_type.fp_device).show(new bt_device_connector.device_selection_handler() {
                    @Override
                    public void on_device_paired_and_selected(BluetoothDevice device) {
                        mChatService.connect(new bt_device_connector(act, bt_device_connector.bt_device_type.fp_device).set_device(bt_device_connector.bt_device_type.fp_device));

                    }

                    @Override
                    public void on_device_slected(BluetoothDevice device) {

                    }

                    @Override
                    public void on_device_paired(BluetoothDevice device) {

                    }
                });
            }else{
                mChatService.connect(new bt_device_connector(act, bt_device_connector.bt_device_type.fp_device).set_device(bt_device_connector.bt_device_type.fp_device));
             //   new bt_device_connector(act, bt_device_connector.bt_device_type.fp_device).set_device(bt_device_connector.bt_device_type.fp_device);
            }
       // Initialize the buffer for outgoing messages
        }



        private void AddStatusList(String text) {
//            mConversationArrayAdapter.add(text);
        }

        private void AddStatusListHex(byte[] data,int size) {
            String text="";
            for(int i=0;i<size;i++) {
                text=text+","+ Integer.toHexString(data[i]&0xFF).toUpperCase();
            }
      //      mConversationArrayAdapter.add(text);
        }

        private void memcpy(byte[] dstbuf,int dstoffset,byte[] srcbuf,int srcoffset,int size) {
            for(int i=0;i<size;i++) {
                dstbuf[dstoffset+i]=srcbuf[srcoffset+i];
            }
        }

        private int calcCheckSum(byte[] buffer,int size) {
            int sum=0;
            for(int i=0;i<size;i++) {
                sum=sum+buffer[i];
            }
            return (sum & 0x00ff);
        }

        public void TimeOutStart() {
            if(mTimerTimeout!=null){
                return;
            }
            mTimerTimeout = new Timer();
            mHandlerTimeout = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    TimeOutStop();
                    if(mIsWork){
                        mIsWork=false;
//                        AddStatusList("Time Out");
                     //   interf.oncommandtimeout();
                    }
                    super.handleMessage(msg);
                }
            };
            mTaskTimeout = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    mHandlerTimeout.sendMessage(message);
                }
            };
            mTimerTimeout.schedule(mTaskTimeout, 10000, 10000);
        }

        public void TimeOutStop() {
            if (mTimerTimeout!=null) {
                mTimerTimeout.cancel();
                mTimerTimeout = null;
                mTaskTimeout.cancel();
                mTaskTimeout=null;
            }
        }

        private void SendCommand(byte cmdid,byte[] data,int size) {
            if(mIsWork)return;

            int sendsize=9+size;
            byte[] sendbuf = new byte[sendsize];
            sendbuf[0]='F';
            sendbuf[1]='T';
            sendbuf[2]=0;
            sendbuf[3]=0;
            sendbuf[4]=cmdid;
            sendbuf[5]=(byte)(size);
            sendbuf[6]=(byte)(size>>8);
            if(size>0) {
                for(int i=0;i<size;i++) {
                    sendbuf[7+i]=data[i];
                }
            }
            int sum=calcCheckSum(sendbuf,(7+size));
            sendbuf[7+size]=(byte)(sum);
            sendbuf[8+size]=(byte)(sum>>8);

            mIsWork=true;
            TimeOutStart();
            mDeviceCmd=cmdid;
            mCmdSize=0;
            mChatService.write(sendbuf);

            switch(sendbuf[4]) {
                case CMD_PASSWORD:
                    break;
                case CMD_ENROLID:
                    AddStatusList("Enrol ID ...");
                    break;
                case CMD_VERIFY:
                    AddStatusList("Verify ID ...");
                    break;
                case CMD_IDENTIFY:
                    AddStatusList("Search ID ...");
                    break;
                case CMD_DELETEID:
                    AddStatusList("Delete ID ...");
                    break;
                case CMD_CLEARID:
                    AddStatusList("Clear ...");
                    break;
                case CMD_ENROLHOST:
                    AddStatusList("Enrol Template ...");
                    break;
                case CMD_CAPTUREHOST:
                    AddStatusList("Capture Template ...");
                    break;
                case CMD_MATCH:	//�ȶ�
                    AddStatusList("Match Template ...");
                    break;
                case CMD_WRITECARD:	//д��
                    AddStatusList("Write Card ...");
                    break;
                case CMD_READCARD:	//����
                    AddStatusList("Read Card ...");
                    break;
                case CMD_CARDID:	//�����кűȶ�
                    AddStatusList("Match Card SN  ...");
                    break;
                case CMD_CARDFINGER:	//ָ�ƿ��ȶ�
                    AddStatusList("FingerprintCard Match ...");
                    break;
                case CMD_CARDSN:		//�������к�
                    AddStatusList("Read Card SN ...");
                    break;
                case CMD_GETSN:
                    AddStatusList("Get Device SN ...");
                    break;
                case CMD_GETBAT:
                    AddStatusList("Get Battery Value ...");
                    break;
                case CMD_GETIMAGE:
                    mUpImageSize=0;

                    break;
                case CMD_GETCHAR:
               //     AddStatusList("Get Fingerprint Data ...");
                    break;
            }
        }

        private byte[] changeByte(int data) {
            byte b4 = (byte) ((data) >> 24);
            byte b3 = (byte) (((data) << 8) >> 24);
            byte b2 = (byte) (((data) << 16) >> 24);
            byte b1 = (byte) (((data) << 24) >> 24);
            byte[] bytes = { b1, b2, b3, b4 };
            return bytes;
        }

        private byte[] toBmpByte(int width, int height, byte[] data) {
            byte[] buffer = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);

                int bfType = 0x424d;
                int bfSize = 54 + 1024 + width * height;
                int bfReserved1 = 0;
                int bfReserved2 = 0;
                int bfOffBits = 54 + 1024;

                dos.writeShort(bfType);
                dos.write(changeByte(bfSize), 0, 4);
                dos.write(changeByte(bfReserved1), 0, 2);
                dos.write(changeByte(bfReserved2), 0, 2);
                dos.write(changeByte(bfOffBits), 0, 4);

                int biSize = 40;
                int biWidth = width;
                int biHeight = height;
                int biPlanes = 1;
                int biBitcount = 8;
                int biCompression = 0;
                int biSizeImage = width * height;
                int biXPelsPerMeter = 0;
                int biYPelsPerMeter = 0;
                int biClrUsed = 256;
                int biClrImportant = 0;

                dos.write(changeByte(biSize), 0, 4);
                dos.write(changeByte(biWidth), 0, 4);
                dos.write(changeByte(biHeight), 0, 4);
                dos.write(changeByte(biPlanes), 0, 2);
                dos.write(changeByte(biBitcount), 0, 2);
                dos.write(changeByte(biCompression), 0, 4);
                dos.write(changeByte(biSizeImage), 0, 4);
                dos.write(changeByte(biXPelsPerMeter), 0, 4);
                dos.write(changeByte(biYPelsPerMeter), 0, 4);
                dos.write(changeByte(biClrUsed), 0, 4);
                dos.write(changeByte(biClrImportant), 0, 4);

                byte[] palatte = new byte[1024];
                for (int i = 0; i < 256; i++) {
                    palatte[i * 4] = (byte) i;
                    palatte[i * 4 + 1] = (byte) i;
                    palatte[i * 4 + 2] = (byte) i;
                    palatte[i * 4 + 3] = 0;
                }
                dos.write(palatte);

                dos.write(data);
                dos.flush();
                buffer = baos.toByteArray();
                dos.close();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return buffer;
        }

        public byte[] getFingerprintImage(byte[] data,int width,int height) {
            if (data == null) {
                return null;
            }
            byte[] imageData = new byte[data.length * 2];
            for (int i = 0; i < data.length; i++) {
                imageData[i * 2] = (byte) (data[i] & 0xf0);
                imageData[i * 2 + 1] = (byte) (data[i] << 4 & 0xf0);
            }
            byte[] bmpData = toBmpByte(width, height, imageData);
            return bmpData;
        }
    public int mUpImageCount = 0;

        private void ReceiveCommand(byte[] databuf,int datasize) {
            if(mDeviceCmd==CMD_GETIMAGE) {


                if (imgSize == IMG200) {   //image size with 152*200
                    memcpy(mUpImage, mUpImageSize, databuf, 0, datasize);
                    mUpImageSize = mUpImageSize + datasize;
                    if (mUpImageSize >= 15200) {
                        File file = new File("/sdcard/test.raw");
                        try {
                            file.createNewFile();
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(mUpImage);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] imageData = checkHaveTis(mUpImage);
                        byte[] bmpdata = getFingerprintImage(imageData, 152, 200, 0/*18*/);
//                        textSize.setText("152 * 200");
                        Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0, bmpdata.length);
                        interf.on_result_image_obtained(image);
                        Log.d(TAG, "bmpdata.length:" + bmpdata.length);
                        fingerprintImage.setImageBitmap(image);
                        mUpImageSize = 0;
                        mUpImageCount = mUpImageCount + 1;
                        mIsWork = false;
                        AddStatusList("Display Image");
                    }
                }
                else if (imgSize == IMG288) {   //image size with 256*288
                    memcpy(mUpImage, mUpImageSize, databuf, 0, datasize);
                    mUpImageSize = mUpImageSize + datasize;
                    if (mUpImageSize >= 36864) {
                        File file = new File("/sdcard/test.raw");
                        try {
                            file.createNewFile();
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(mUpImage);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] imageData = checkHaveTis(mUpImage);
                        byte[] bmpdata = getFingerprintImage(imageData, 256, 288, 0/*18*/);
//                        textSize.setText("256 * 288");
                        Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0, bmpdata.length);
                        interf.on_result_image_obtained(image);

//                        byte[] inpdata = new byte[73728];
//                        int inpsize = 73728;
//                        System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize);
//                        SaveWsqFile(inpdata, inpsize, "fingerprint.wsq");
//
//                        Log.d(TAG, "bmpdata.length:" + bmpdata.length);
//                        fingerprintImage.setImageBitmap(image);
                        mUpImageSize = 0;
                        mUpImageCount = mUpImageCount + 1;
                        mIsWork = false;
                        AddStatusList("Display Image");
                    }
                }
                else if (imgSize == IMG360) {   //image size with 256*360
                    memcpy(mUpImage, mUpImageSize, databuf, 0, datasize);
                    mUpImageSize = mUpImageSize + datasize;
                    //AddStatusList("Image Len="+Integer.toString(mUpImageSize)+"--"+Integer.toString(mUpImageCount));
                    if (mUpImageSize >= 46080) {
                        File file = new File("/sdcard/test.raw");
                        try {
                            file.createNewFile();
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(mUpImage);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] imageData = checkHaveTis(mUpImage);
                        byte[] bmpdata = getFingerprintImage(imageData, 256, 360, 0/*18*/);
//                        textSize.setText("256 * 360");
                        Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0, bmpdata.length);
                        interf.on_result_image_obtained(image);

//                        byte[] inpdata = new byte[92160];
//                        int inpsize = 92160;
//                        System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize);
//                        SaveWsqFile(inpdata, inpsize, "fingerprint.wsq");
//
//                        Log.d(TAG, "bmpdata.length:" + bmpdata.length);
//                        fingerprintImage.setImageBitmap(image);
                        mUpImageSize = 0;
                        mUpImageCount = mUpImageCount + 1;
                        mIsWork = false;
                        AddStatusList("Display Image");

                    }

           /*     File f = new File("/sdcard/fingerprint.png");
                if (f.exists()) {
                    f.delete();
                }
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    image.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] inpdata=new byte[73728];
                int inpsize=73728;
                System.arraycopy(bmpdata,1078, inpdata, 0, inpsize);
                SaveWsqFile(inpdata,inpsize,"fingerprint.wsq");*/
                }


            }else{
                memcpy(mCmdData,mCmdSize,databuf,0,datasize);
                mCmdSize=mCmdSize+datasize;
                int totalsize=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xFF00)+9;
                if(mCmdSize>=totalsize){
                    mCmdSize=0;
                    mIsWork=false;
                    if((mCmdData[0]=='F')&&(mCmdData[1]=='T'))	{
                        switch(mCmdData[4]) {
                            case CMD_PASSWORD: {
                            }
                            break;
                            case CMD_ENROLID: {
                                if(mCmdData[7]==1) {
                                    int id=mCmdData[8]+(mCmdData[9]<<8);
                                    AddStatusList("Enrol Succeed:"+ String.valueOf(id));
                                }
                                else
                                    AddStatusList("Enrol Fail");

                            }
                            break;
                            case CMD_VERIFY: {
                                if(mCmdData[7]==1)
                                    AddStatusList("Verify Succeed");
                                else
                                    AddStatusList("Verify Fail");
                            }
                            break;
                            case CMD_IDENTIFY: {
                                if(mCmdData[7]==1) {
                                    int id=(byte)(mCmdData[8])+(byte)((mCmdData[9]<<8)&0xF0);
                                    AddStatusList("Search Result:"+ String.valueOf(id));
                                } else
                                    AddStatusList("Search Fail");
                            }
                            break;
                            case CMD_DELETEID:
                            {
                                if(mCmdData[7]==1)
                                    AddStatusList("Delete Succeed");
                                else
                                    AddStatusList("Delete Fail");
                            }
                            break;
                            case CMD_CLEARID: {
                                if(mCmdData[7]==1)
                                    AddStatusList("Clear Succeed");
                                else
                                    AddStatusList("Clear Fail");
                            }
                            break;
                            case CMD_ENROLHOST: {
                                int size=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xFF00)-1;
                                if(mCmdData[7]==1) {
                                    memcpy(mRefData,0,mCmdData,8,size);
                                    mRefSize=size;
                                    //ת��
   		    					/*
   		    					Conversions.getInstance().StdChangeCoord(mRefData, 512, mRefCoord, 1);
   		    					Conversions.getInstance().StdToIso(2,mRefCoord,mIsoData);

   		    					String bsiso=Base64.encodeToString(mIsoData,Base64.DEFAULT);
   		    					AddStatusList(bsiso);
   		    					*/
                                    AddStatusList("Len="+ String.valueOf(mRefSize));
                                    AddStatusListHex(mRefData,mRefSize);
                                    AddStatusList("Enrol Succeed");
                                }else
                                    AddStatusList("Enrol Fail");
                            }
                            break;
                            case CMD_CAPTUREHOST: {
                                int size=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xFF00)-1;
                                if(mCmdData[7]==1) {
                                    memcpy(mMatData,0,mCmdData,8,size);
                                    mMatSize=size;
                                    //ת��
   		    					/*
   		    					Conversions.getInstance().StdChangeCoord(mMatData, 256, mMatCoord, 1);
   		    					Conversions.getInstance().StdToIso(2,mMatCoord,mIsoData);

   		    					String bsiso=Base64.encodeToString(mIsoData,Base64.DEFAULT);
   		    					AddStatusList(bsiso);
   		    					*/
                                    AddStatusList("Len="+ String.valueOf(mMatSize));
                                    AddStatusListHex(mMatData,mMatSize);
                                    try {
                                        byte[] model1 = new byte[512];
                                        System.arraycopy(mMatData, 0, model1, 0, 512);
                                        mMatData = model1;
                                    }catch (Exception ex){}
                                    interf.on_result_obtained(Base64.encodeToString(mMatData,0));
                                    act.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getdata();
                                        }
                                    });
                                    AddStatusList("Capture Succeed");
                                }
                                else
                                    AddStatusList("Capture Fail");
                            }
                            break;
                            case CMD_MATCH:	{
                                int score=(byte)(mCmdData[8])+((mCmdData[9]<<8)&0xF0);
                                if(mCmdData[7]==1)
                                    AddStatusList("Match Succeed:"+ String.valueOf(score));
                                else
                                    AddStatusList("Match Fail");
                            }
                            break;
                            case CMD_WRITECARD: {
                                if(mCmdData[7]==1)
                                    AddStatusList("Write Card Succeed");
                                else
                                    AddStatusList("Write Card Fail");
                            }
                            break;
                            case CMD_READCARD: {
                                int size=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xF0);
                                if(size>0)
                                {
                                    memcpy(mCardData,0,mCmdData,7,size);
                                    mCardSize=size;
                                    AddStatusList("Read Card Succeed");
                                }
                                else
                                    AddStatusList("Read Card Fail");
                            }
                            break;
                            case CMD_CARDID: {
                                if(mCmdData[7]==1)
                                    AddStatusList("Match Succeed");
                                else
                                    AddStatusList("Match Fail");
                            }
                            break;
                            case CMD_CARDFINGER: {
                                if(mCmdData[7]==1)
                                    AddStatusList("Match Succeed");
                                else
                                    AddStatusList("Match Fail");
                            }
                            break;
                            case CMD_UPCARDSN:
                            case CMD_CARDSN: {
                                int size=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xF0)-1;
                                if(size>0) {
                                    memcpy(mCardSn,0,mCmdData,8,size);
                                    AddStatusList("Read Card SN Succeed:"+ Integer.toHexString(mCardSn[0]&0xFF)+ Integer.toHexString(mCardSn[1]&0xFF)+ Integer.toHexString(mCardSn[2]&0xFF)+ Integer.toHexString(mCardSn[3]&0xFF));
                                }
                                else
                                    AddStatusList("Read Card SN Fail");
                            }
                            break;
                            case CMD_GETSN:{
                                int size=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xFF00)-1;
                                if(mCmdData[7]==1) {
                                    byte[] snb=new byte[32];
                                    memcpy(snb,0,mCmdData,8,size);
                                    String sn = null;
                                    try {
                                        sn = new String(snb,0,size,"UNICODE");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    AddStatusList("SN:"+sn);
                                }
                                else
                                    AddStatusList("Get SN Fail");
                            }
                            break;
                            case CMD_PRINTCMD:{
                                if(mCmdData[7]==1){
                                    AddStatusList("Print OK");
                                }else{
                                    AddStatusList("Print Fail");
                                }
                            }
                            break;
                            case CMD_GETBAT:{
                                int size=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xFF00)-1;
                                if(size>0)
                                {
                                    memcpy(mBat,0,mCmdData,8,size);
                                    AddStatusList("Battery Value:"+ Integer.toString(mBat[0]/10)+"."+ Integer.toString(mBat[0]%10)+"V");
                                }else
                                    AddStatusList("Get Battery Value Fail");
                            }
                            break;
                            case CMD_GETCHAR:{
                                int size=(byte)(mCmdData[5])+((mCmdData[6]<<8)&0xFF00)-1;
                                if(mCmdData[7]==1) {
                                    memcpy(mMatData,0,mCmdData,8,size);
                                    mMatSize=size;
//                                    AddStatusList("Len="+String.valueOf(mMatSize));
//                                    AddStatusList("Get Data Succeed");
                                  //  AddStatusListHex(mMatData,mMatSize);
                                    interf.on_result_obtained(Base64.encodeToString(mMatData,0));
                                    act.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getdata();
                                        }
                                    });   //getdata();
                                }
                                else
                                    AddStatusList("Get Data Fail");
                            }
                            break;
                        }
                    }
                }
            }
        }
        Timer fp_timer=new Timer();
       Boolean STREAMING=true;
    public byte mUpImage2[] = new byte[73728]; // image data
//    public void SaveWsqFile(byte[] rawdata, int rawsize, String filename) {
//        byte[] outdata = new byte[rawsize];
//        int[] outsize = new int[1];
//
//        if (rawsize == (256*288)) {
//            wsq.getInstance().RawToWsq(rawdata, rawsize, 256, 288, outdata, outsize, 2.833755f);
//        } else if (rawsize == 92160) {
//            wsq.getInstance().RawToWsq(rawdata, rawsize, 256, 360, outdata, outsize, 2.833755f);
//        }
//
//        try {
//            File fs = new File("/sdcard/" + filename);
//            if (fs.exists()) {
//                fs.delete();
//            }
//            new File("/sdcard/" + filename);
//            RandomAccessFile randomFile = new RandomAccessFile("/sdcard/" + filename, "rw");
//            long fileLength = randomFile.length();
//            randomFile.seek(fileLength);
//            randomFile.write(outdata, 0, outsize[0]);
//            randomFile.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private byte[] checkHaveTis(byte[] mUpImage) {
        //有提示
        if ((mUpImage[0] == 'F') && (mUpImage[1] == 'T')) {
            if (mUpImage[7] == 2) {
                AddStatusList("Place your finger");
            } else if (mUpImage[7] == 3) {
                AddStatusList("Place your finger");
            } else if (mUpImage[7] == 20) {
                AddStatusList("Timed out");
            }
            memcpy(mUpImage2, 0, mUpImage, 20, mUpImageSize - 20);
        } else {
            memcpy(mUpImage2, 0, mUpImage, 0, mUpImageSize);
        }
        return mUpImage2;
    }
    public byte[] getFingerprintImage(byte[] data, int width, int height, int offset) {
        if (data == null) {
            return null;
        }
        byte[] imageData = new byte[width * height];
        for (int i = 0; i < (width * height / 2); i++) {
            imageData[i * 2] = (byte) (data[i + offset] & 0xf0);
            imageData[i * 2 + 1] = (byte) (data[i + offset] << 4 & 0xf0);
        }
        byte[] bmpData = toBmpByte(width, height, imageData);
        return bmpData;
    }
void stream_data()
{
    if(STREAMING){
        return;
    }
    STREAMING=true;
    fp_timer=new Timer();

    fp_timer.schedule(new TimerTask() {
        @Override
        public void run() {
act.runOnUiThread(new Runnable() {
    @Override
    public void run() {
        getdata();
    }
});
        }
    },0,10000);


}
        // The Handler that gets information back from the BluetoothChatService
        private final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            case BluetoothReaderService.STATE_CONNECTED:
                              //  mTitle.setText("Connected to ");
                                interf.on_device_status_changed("connected ");
                                Toast.makeText(act,"Connected to => "+mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                                //getimage();
                             //   stream_data();
                                getdata();
                               // mTitle.append(mConnectedDeviceName);
//                                mConversationArrayAdapter.clear();
                                break;
                            case BluetoothReaderService.STATE_CONNECTING:
//                                mTitle.setText("Connecting ...");
                                break;
                            case BluetoothReaderService.STATE_LISTEN:
                            case BluetoothReaderService.STATE_NONE:
//                                mTitle.setText("Not connected ");
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        //String writeMessage = new String(writeBuf);
                        //AddStatusList("Send:  " + writeMessage);
                        //AddStatusListHex(writeBuf,writeBuf.length);
                        break;
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        //AddStatusList("Len="+Integer.toString(msg.arg1));
                        //AddStatusListHex(readBuf,msg.arg1);
                        ReceiveCommand(readBuf,msg.arg1);
                        break;
                    case MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        Toast.makeText(act, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_TOAST:
                        Toast.makeText(act, msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                        if(msg.getData().getString(TOAST).equalsIgnoreCase("Unable to connect device"))
                        {
                            new bt_device_connector(act, bt_device_connector.bt_device_type.fp_device).show(new bt_device_connector.device_selection_handler() {
                                @Override
                                public void on_device_paired_and_selected(BluetoothDevice device) {
                                    mChatService.connect(new bt_device_connector(act, bt_device_connector.bt_device_type.fp_device).set_device(bt_device_connector.bt_device_type.fp_device));

                                }

                                @Override
                                public void on_device_slected(BluetoothDevice device) {

                                }

                                @Override
                                public void on_device_paired(BluetoothDevice device) {

                                }
                            });

                        }
                        break;
                }
            }
        };

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(D) Log.d(TAG, "onActivityResult " + resultCode);

        }

       void startsettings() {
          /* Intent serverIntent = new Intent(act, DeviceListActivity.class);
           act.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
*/
       }

    }

