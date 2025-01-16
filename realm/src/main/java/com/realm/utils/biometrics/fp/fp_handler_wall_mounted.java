package com.realm.utils.biometrics.fp;//package com.realm.utils.biometrics.fp;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Base64;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.digitalpersona.uareu.Engine;
//import com.digitalpersona.uareu.Fmd;
//import com.digitalpersona.uareu.UareUException;
//import com.digitalpersona.uareu.UareUGlobal;
//
//
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//
//import android_serialport_api.AsyncFingerprint;
//import android_serialport_api.SerialPortManager;
//import com.realm.R;
//import com.realm.utils.biometrics.fp.sdks.fgtit.utils.Conversions;
//import com.realm.utils.s_bitmap_handler;
//
//import static com.realm.utils.biometrics.fp.fp_handler_stf_usb_8_inch.main_fmd_format;
//
//
//public class fp_handler_wall_mounted {
//
//
//
//    Activity act;
//    private boolean			 bIsCancel=false;
//    private boolean			 bfpWork=false;
//
//    private Timer startTimer;
//    private TimerTask startTask;
//    private Handler startHandler;
//    private AsyncFingerprint vFingerprint;
//
//
//
//    public static sfp_i interf;
//
//public boolean display_image=false;
//
//    public fp_handler_wall_mounted(Activity act)
//    {
//        this.act=act;
//        interf=(sfp_i)act;
//
//        begin_capture();
//        setFpIoState(true);
//    }
//
//
//    private void setFpIoState(boolean isOn) {
//        int state = 0;
//        if (isOn) {
//            state = 1;
//        } else {
//            state = 0;
//        }
//        Intent i = new Intent("ismart.intent.action.fingerPrint_control");
//        i.putExtra("state", state);
//        act.sendBroadcast(i);
//    }
//
//
//    public void begin_capture()
//    {
//       vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
//        FPInit();
//        FPProcess();
//
//    }
//
//
//
//
//
//
//    private void FPProcess(){
//        if(!bfpWork){
//            try {
//                Thread.currentThread();
//                Thread.sleep(500);
//            }catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//            log_device_stt(act.getString(R.string.txt_fpplace));
//           vFingerprint.FP_GetImage();
//            //vFingerprint.FP_UpImage();
//            bfpWork=true;
//        }
//    }
//    public boolean IsUpImage = true;
//    private void FPInit(){
//        //指纹处理
//        vFingerprint.setOnGetImageListener(new AsyncFingerprint.OnGetImageListener() {
//            @Override
//            public void onGetImageSuccess() {
//              if(IsUpImage){
//                    vFingerprint.FP_UpImage();
//                    log_device_stt(act.getString(R.string.txt_fpdisplay));
//                }else{  /**/
//                    log_device_stt(act.getString(R.string.txt_fpprocess));
//                    vFingerprint.FP_GenChar(1);
//                }
//            }
//
//            @Override
//            public void onGetImageFail() {
//                if(!bIsCancel){
//                    vFingerprint.FP_GetImage();
//                    //SignLocalActivity.this.AddStatus("Error");
//                }else{
//                    Toast.makeText(act, "Cancel OK", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        vFingerprint.setOnUpImageListener(new AsyncFingerprint.OnUpImageListener() {
//            @Override
//            public void onUpImageSuccess(byte[] data) {
//                if(display_image){
//                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
//            //  image = s_bitmap_handler.getImage(s_bitmap_handler.getBytes_JPG(image));
//                     image=getResizedBitmap(image,256,360,true);
//                    byte[] img_by= s_bitmap_handler.getBytes_JPG(image);
//                   // setDpi(img_by,200);
//                //fpImage.setImageBitmap(image);
//                interf.on_result_image_obtained(image);
//if(1==1)
//{
//    log_device_stt(act.getString(R.string.txt_fpprocess));
//    vFingerprint.FP_GenChar(1);
//    return;
//}
//                  //  Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
//                   // Fmd ffm2 = UareUGlobal.GetEngine().C(data, main_fmd_format, main_fmd_format);
//                    //   Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_wall_mat_device, 0), main_fmd_format, main_fmd_format);
//                    //  Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_8_inch, 0), main_fmd_format, main_fmd_format);
//                try{
//                    Engine eng=UareUGlobal.GetEngine();
//                    eng.SelectEngine(Engine.EngineType.ENGINE_DPFJ7);
//
//                    Fmd fmd=eng.CreateFmd(
//                            img_by,
//                            image.getWidth(),
//                            image.getHeight(),
//                            508, 0, 0, main_fmd_format);
//                    //fmd.g
//                    Log.e("WALL_MOUNTED :","DPI ::"+fmd.getResolution()+" W::"+image.getWidth()+" X "+image.getHeight());
////                    Fmd fmd1=UareUGlobal.GetEngine().CreateFmd(
////                            data,
////                            256,
////                            288,
////                            500,
////                            0, 0,  main_fmd_format);
////                    Fmd fmd3=UareUGlobal.GetEngine().CreateFmd(
////                            s_bitmap_handler.getBytes(image),
////                            image.getWidth(),
////                            image.getHeight(),
////                            200,
////                            0, 1,  main_fmd_format);
////        Fid fid1=UareUGlobal.GetImporter().ImportRaw(
////                            s_bitmap_handler.getBytes(image),
////                            image.getWidth(),
////                            image.getHeight(),
////                            200,
////                            0, 0,  main_fid_format,0,true);
////                    Fid fid1=UareUGlobal.GetImporter().ImportRaw(
////                            s_bitmap_handler.getBytes(image),
////                            s_bitmap_handler.getBytes(image).length,
////                            image.getWidth(),
////                            image.getHeight(),
////                            200,
////                            0,   main_fid_format,0,true);
//
//                  //  Log.e("WALL_MOUNTED :","Fid "+fid1.getFormat()+" X "+fid1.getBpp());
//                 //  interf.on_result_obtained(Base64.encodeToString(fmd.getData(),0));
//                }catch (UareUException ex){
//
//                    Log.e("FMD ERROR ",""+ex.getMessage());
//                }
//
//
//                   // mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
//
//            }
//                log_device_stt(act.getString(R.string.txt_fpprocess));
//                vFingerprint.FP_GenChar(1);
//            }
//
//            @Override
//            public void onUpImageFail() {
//                bfpWork=false;
//                TimerStart();
//            }
//        });
//
//        vFingerprint.setOnGenCharListener(new AsyncFingerprint.OnGenCharListener() {
//            @Override
//            public void onGenCharSuccess(int bufferId) {
//                log_device_stt(act.getString(R.string.txt_fpidentify));
//                vFingerprint.FP_UpChar();
//            }
//
//            @Override
//            public void onGenCharFail() {
//                log_device_stt(act.getString(R.string.txt_fpfail));
//            }
//        });
//
//        vFingerprint.setOnUpCharListener(new AsyncFingerprint.OnUpCharListener() {
//
//            @Override
//            public void onUpCharSuccess(byte[] model) {
//               byte[] fpenrol=new byte[512];
//             interf.on_result_obtained(Base64.encodeToString(model,0));
//if(1==1){
//
//    bfpWork=false;
//    TimerStart();
//    return;
//
//}/**/
//
//
//
//                String bsiso="";
//                System.arraycopy(model, 0, fpenrol, 0, 512);
//                int CoordType=1;
//                byte mMatCoord[]=new byte[512];
//                byte mIsoData[]=new byte[512];
//                Log.d("FingerDemoActivity", "mIsoData:" + mIsoData);
//                byte mTmpData[]=new byte[512];
//                Log.d("FingerDemoActivity", "Conversions.getInstance().GetDataType(fpenrol):" + Conversions.getInstance().GetDataType(fpenrol));
//
//                //TODO:   ISO  国际化标准
//                /**/switch(Conversions.getInstance().GetDataType(fpenrol)){
//                    case 1:{
//                        //STD
//                        Conversions.getInstance().StdChangeCoord(fpenrol, 256, mMatCoord, CoordType);
//                        Conversions.getInstance().StdToIso(2,mMatCoord,mIsoData);
//                        bsiso= Base64.encodeToString(mIsoData,0,378, Base64.DEFAULT);
//                    }
//                    break;
//                    case 2:{
//                        //ISO 1
//                        Conversions.getInstance().IsoToStd(1,fpenrol,mTmpData);
//                        Conversions.getInstance().StdChangeCoord(mTmpData, 256, mMatCoord, CoordType);
//                        Conversions.getInstance().StdToIso(2,mMatCoord,mIsoData);
//                        bsiso= Base64.encodeToString(mIsoData,0,378, Base64.DEFAULT);
//                    }
//                    break;
//                    case 3:{
//                        //19794-2 or Ansi-378
//                        //bsiso=Base64.encodeToString(fpmatch,0,378,Base64.DEFAULT);
//                        bsiso=Conversions.getInstance().IsoChangeCoord(fpenrol, CoordType);
//                    }
//                    break;
//                }
//
//
//                //	bsiso=Conversions.getInstance().IsoChangeCoord(fpenrol, 1);
//              //  AddStatusList(bsiso);
//                //Log.i("xpb", "bsiso=" + bsiso);
//                //	Log.i("xpb", "template=" + DataUtils.toHexString(model));
//                Log.e("FingerDemoActivity", "Conversions.getInstance().GetDataType(fpenrol):" + Conversions.getInstance().GetDataType(Base64.decode(bsiso,0)));
//                interf.on_result_obtained(bsiso);
//
//
//
//                bfpWork=false;
//                TimerStart();
//            }
//
//            @Override
//            public void onUpCharFail() {
//                log_device_stt(act.getString(R.string.txt_fpmatchfail)+":-1");
//                bfpWork=false;
//                TimerStart();
//            }
//        });
//
//    }
//    public void setDpi(byte[] imageData, int dpi) {
//        imageData[13] = 1;
//        imageData[14] = (byte) (dpi >> 8);
//        imageData[15] = (byte) (dpi & 0xff);
//        imageData[16] = (byte) (dpi >> 8);
//        imageData[17] = (byte) (dpi & 0xff);
//    }
//    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean recycleOriginal) {
//        int width = bm.getWidth();
//        int height = bm.getHeight();
//
//        // Determine scale to change size
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//
//        // Create Matrix for maniuplating size
//        Matrix matrix = new Matrix();
//        // Set the Resize Scale for the Matrix
//        matrix.postScale(scaleWidth, scaleHeight);
//
//        //Create a new Bitmap from original using matrix and new width/height
//        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
//
//        //Remove memory leaks if told to recycle, warning, if using original else where do not recycle it here
//        if(recycleOriginal) {
//            bm.recycle();
//
//        }
//
//        //Return the scaled new bitmap
//        return resizedBitmap;
//
//    }
//    public static Bitmap cropImage(Bitmap imgToCrop, int startX, int startY, int width, int height, boolean recycleOriginal){
//        Bitmap croppedImage = Bitmap.createBitmap(imgToCrop, startX, startY , width , height);
//
//        if(recycleOriginal){
//            imgToCrop.recycle();
//
//        }
//
//        return croppedImage;
//    }
//
//    public void TimerStart(){
//        if(bIsCancel)
//            return;
//        if(startTimer==null){
//            startTimer = new Timer();
//            startHandler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//
//                    TimeStop();
//                    FPProcess();
//                }
//            };
//            startTask = new TimerTask() {
//                @Override
//                public void run() {
//                    Message message = new Message();
//                    message.what = 1;
//                    startHandler.sendMessage(message);
//                }
//            };
//            startTimer.schedule(startTask, 500, 200);
//        }
//    }
//
//    public void TimeStop(){
//        if (startTimer!=null)
//        {
//            startTimer.cancel();
//            startTimer = null;
//            startTask.cancel();
//            startTask=null;
//        }
//    }
//    public void close(){
//        if(SerialPortManager.getInstance().isOpen()){
//            bIsCancel=true;
//            SerialPortManager.getInstance().closeSerialPort();
//
//
//
//
//        }
//    }
//    void log_device_stt(String data)
//    {
//        Log.e("FP DATA =>",""+data);
//        interf.on_device_status_changed(data);
//    }
//
//}
