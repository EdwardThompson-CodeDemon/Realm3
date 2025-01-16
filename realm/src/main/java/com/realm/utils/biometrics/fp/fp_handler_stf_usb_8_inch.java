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
import com.digitalpersona.uareu.Reader.Priority;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.digitalpersona.uareu.dpfj.CompressionImpl;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost;

import java.io.File;
import java.io.FileOutputStream;

import com.realm.R;
import com.realm.utils.s_bitmap_handler;
import com.realm.utils.svars;


public class fp_handler_stf_usb_8_inch {
    private Engine m_engine = null;
    private Fmd m_fmd = null;
    private int m_score = -1;
    private String m_deviceName = "";

    private String m_enginError;

    private Reader m_reader = null;
    private int m_DPI = 0;
    private Bitmap m_bitmap = null;
    public static Reader.CaptureResult cap_result = null;
   // PTGrab pt;
    Activity act;
    public static   Fid.Format main_fid_format= Fid.Format.ISO_19794_4_2005;
    public static Fmd.Format main_fmd_format= Fmd.Format.ISO_19794_2_2005;
//   public static   Fid.Format main_fid_format= Fid.Format.ANSI_381_2004;
//    public static Fmd.Format main_fmd_format= Fmd.Format.ANSI_378_2004;
  //  public static Fmd.Format main_fmd_format2= Fmd.Format.ISO_19794_2_2005;
    public static boolean include_image=false;


    public static sfp_i interf;
    private static final String ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION";
    ReaderCollection readers;


    public fp_handler_stf_usb_8_inch(Activity act)
    {
        this.act=act;
        interf=(sfp_i)act;
if(readers==null||readers.size()<1) {

    try {


        readers = Globals.getInstance().getReaders(act);
    } catch (UareUException e) {
        close();
        interf.on_device_error(e.getMessage());
        return;
    }
}
try{        m_deviceName= readers.get(0).GetDescription().name;}catch (Exception ex){interf.on_device_error(act.getString(R.string.usb_cable_connected_error));return;}
        try {
            Globals.ClearLastBitmap();


            m_reader = Globals.getInstance().getReader(m_deviceName, act);

            {
                PendingIntent mPermissionIntent;
                mPermissionIntent = PendingIntent.getBroadcast(act, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                act.registerReceiver(mUsbReceiver, filter);

                if(DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(act, mPermissionIntent, m_deviceName))
                {
                    CheckDevice();
                    open_device();
                }else{
                    open_device();
                }
            }
        } catch (UareUException e1)
        {

            Log.e("SParta =>","Reader open error =>"+e1.getMessage());

        }
        catch (DPFPDDUsbException e)
        {
            // displayReaderNotFound();
            Log.e("SParta =>","Reader not found =>"+e.getMessage());
        }

    }
    public fp_handler_stf_usb_8_inch(Activity act, String m_deviceName)
    {
        this.act=act;
        interf=(sfp_i)act;

        open_device(m_deviceName);

    }


    void open_device()
    {



       // Toast.makeText(act,"SFP =>device name =>"+m_deviceName ,Toast.LENGTH_SHORT);

        m_bitmap = Globals.GetLastBitmap();
        if (m_bitmap == null) m_bitmap = BitmapFactory.decodeResource(act.getResources(), R.drawable.ic_settings);
        Globals.DefaultImageProcessing = Reader.ImageProcessing.IMG_PROC_DEFAULT;

        try
        {

if(m_reader==null){
    m_reader = Globals.getInstance().getReader(m_deviceName, act);

}


            m_reader.Open(Priority.EXCLUSIVE);
            m_DPI = Globals.GetFirstDPI(m_reader);
            m_engine = UareUGlobal.GetEngine();
          //  m_engine.SelectEngine(Engine.EngineType.ENGINE_DPFJ7);

          //  Toast.makeText(act,"SFP =>device opened =>" ,Toast.LENGTH_SHORT).show();
            Log.e("SFP =>","device opened =>");
            Log.e("SFP =>","device opened : DPI :"+m_reader.GetCapabilities().resolutions.length);
    Log.e("SFP =>","device opened : DPI 0 :"+m_reader.GetCapabilities().resolutions[0]);
            Log.e("SFP =>","device opened : DPI 1 :"+m_reader.GetCapabilities().resolutions[1]);

        }
        catch (UareUException e)
        {
            Log.e("SFP =>","Error opening device =>"+e.getMessage());
            interf.on_device_error(e.getMessage());

            return;
        }


        begin_capture();
    }
    void open_device(String deviceName)
    {

        m_enginError = "";

        m_deviceName= deviceName;


        //Toast.makeText(act,"SFP =>device name =>"+m_deviceName ,Toast.LENGTH_SHORT);

        m_bitmap = Globals.GetLastBitmap();
       if (m_bitmap == null) m_bitmap = BitmapFactory.decodeResource(act.getResources(), R.drawable.ic_settings);
        Globals.DefaultImageProcessing = Reader.ImageProcessing.IMG_PROC_DEFAULT;

        try
        {


            m_reader = Globals.getInstance().getReader(m_deviceName, act);
            m_reader.Open(Priority.EXCLUSIVE);
            m_DPI = Globals.GetFirstDPI(m_reader);
            m_engine = UareUGlobal.GetEngine();
          //  m_engine.SelectEngine(Engine.EngineType.ENGINE_DPFJ7);

        //    Toast.makeText(act,"SFP =>device opened =>" ,Toast.LENGTH_SHORT).show();
            Log.e("SFP =>","device opened : DPI :"+m_reader.GetCapabilities().resolutions.length);
            Log.e("SFP =>","device opened : DPI 0 :"+m_reader.GetCapabilities().resolutions[0]);
    Log.e("SFP =>","device opened : DPI 1 :"+m_reader.GetCapabilities().resolutions[1]);

        }
        catch (Exception e)
        {
            Log.e("SFP =>","Error opening device =>"+e.getMessage());
            interf.on_device_error(e.getMessage());

            return;
        }


        begin_capture();
    }
    private boolean m_resultAvailableToDisplay = false;
    int session =0;

    public void begin_capture()
    {
        session ++;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                m_reset = false;

                int i=00;
                while (!m_reset)
                {
                    try
                    {
                        Log.e("Looping through image :",session+" ::: "+i);
                        Log.e("Looping through image :"," ::: "+m_DPI);
                        i++;
                       // cap_result = m_reader.Capture(main_fid_format, Globals.DefaultImageProcessing, m_DPI, -1);
                        cap_result = m_reader.Capture(main_fid_format, Reader.ImageProcessing.IMG_PROC_DEFAULT, m_DPI, -1);
                        Log.e("Looping through image :"," inside"+session+" ::: "+cap_result.quality.toString());
                        if (cap_result == null || cap_result.image == null) continue;
                       compress_img(cap_result.image);
                        interf.on_result_obtained(Base64.encodeToString(m_engine.CreateFmd(cap_result.image, main_fmd_format).getData(),0));
/*                        Fmd my_fmd=m_engine.CreateFmd(cap_result.image, main_fmd_format);

                        Log.e("8_INCH_HANDLER"," Width =>"+my_fmd.getWidth());
                        Log.e("8_INCH_HANDLER"," Height =>"+my_fmd.getHeight());
                        Log.e("8_INCH_HANDLER"," RESOLUTION =>"+my_fmd.getResolution());
                        Log.e("8_INCH_HANDLER"," CbeffId =>"+my_fmd.getCbeffId());


                        interf.on_result_obtained(Base64.encodeToString(my_fmd.getData(),0));*/

                   }
                    catch (Exception e)
                    {
                        if(!m_reset)
                        {
                            Log.e("sparta =>", "error during capture: " + e.toString());
                           // interf.on_device_error("error during capture: " + e.toString());
                            m_deviceName = "";
                          //  close();
                        }
                    }

                    m_resultAvailableToDisplay = false;

                    // an error occurred
                    if (cap_result == null || cap_result.image == null) continue;

                    try
                    {
                        m_enginError="";
                        Log.e("Looping through image :"," Bitamp creating"+session+" ::: "+cap_result.quality.toString());

                        // save bitmap image locally
                        m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());
                        interf.on_result_image_obtained(m_bitmap);
                        Log.e("Looping through image :"," Bitamp created"+session+" ::: "+cap_result.quality.toString());
                        //    extract_template(m_bitmap,Base64.encodeToString(m_engine.CreateFmd(cap_result.image, main_fmd_format).getData(),0));
                    }
                    catch (Throwable e)
                    {
                        m_enginError = e.toString();
                        Log.e("sparta =>", "Engine error: " + e.toString());
                    }


                    Log.e("Looping through image :"," Finished"+session+" ::: "+cap_result.quality.toString());



                }
            }
        }).start();
    }

    public static Compression cmp = new CompressionImpl();
    void compress_img(Fid ISOFid){
try {
   // com.digitalpersona.uareu.Compression.RawImage raw = new Compression.RawImage(0, 0, 0, 0, new byte[]);

 /*   Importer importer = new ImporterImpl();
  //fid = importer.ImportFid(fidDataArray, mFidFmt);



    Compression compression = null;

    compression = new CompressionImpl();



    compression.Start();



    compression.SetWsqBitrate(90, 0);



    Fid compressFid = null;

    compressFid = compression.CompressFid(ISOFid, Compression.CompressionAlgorithm.COMPRESSION_WSQ_AWARE);



    compression.Finish();
*/



    cmp = new CompressionImpl();
     cmp.Start();
    cmp.SetWsqBitrate(90, 0);



    //captureResult is a parameter passed into the Async capture listener\callback and contains the captured fingerprint image data (Fid).
    //    byte[] rawCompress = cmp.CompressRaw(ISOFid.getViews()[0].getWidth(), ISOFid.getViews()[0].getHeight(), 500, 8, ISOFid.getViews()[0].getImageData(), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */ File.WriteAllBytes("WSQfromRaw.wsq",rawCompress);
    byte[] rawCompress = cmp.CompressRaw(ISOFid.getViews()[0].getImageData(), ISOFid.getViews()[0].getWidth(), ISOFid.getViews()[0].getHeight(), 500, 8, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
    cmp.Finish();

    interf.on_result_wsq_obtained(rawCompress);
if(1==1){return;}
  String prefix = svars.sparta_EA_calendar().getTime().toString() + "     =>";
    String root = act.getExternalFilesDir(null).getAbsolutePath() + "/WSQz";
    Log.e("LOG_TAG", "PATH: " + root);

    File filei = new File(root);
    filei.mkdirs();
    File file=new File(filei,"SP_WSQ_" + System.currentTimeMillis() + "TA");
    try {
        FileOutputStream fOutputStream = new FileOutputStream(file);
        fOutputStream.write(rawCompress);
        fOutputStream.flush();
        fOutputStream.close();


//        File gpxfile = new File(file, "SP_WSQ_" + System.currentTimeMillis() + "TA");
//        FileWriter writer = new FileWriter(gpxfile, true);
//        writer.write(svars.APP_OPERATION_MODE != svars.OPERATION_MODE.DEV ?rawCompress: s_cryptor.encrypt(rawCompress));
//        writer.flush();
//        writer.close();

    } catch (Exception ex) {
        Log.e("Writing error =>"," "+ex.getMessage());

    } /* */
}catch (UareUException ex){
Log.e("Compression error =>","uareu "+ex.getMessage());

}catch (Exception ex){
Log.e("Compression error =>"," "+ex.getMessage());

}

    }




    private boolean m_reset = false;






    public void close()
    {
        Log.e("fph =>", "shutting down");

        try
        {
            m_reset = true;
            try {m_reader.CancelCapture(); } catch (Exception e) {}
            m_reader.Close();

        }
        catch (Exception e)
        {
            Log.e("fph =>", "error during reader shutdown");
        }
//        Intent i = new Intent();
//        i.putExtra("device_name", m_deviceName);
        // setResult(Activity.RESULT_OK, i);


    }


    protected void CheckDevice()
    {
        try
        {
            m_reader.Open(Priority.EXCLUSIVE);
            Reader.Capabilities cap = m_reader.GetCapabilities();

            m_reader.Close();
            //open_device();
        }
        catch (UareUException e1)
        {
            Log.e("Reader Error =>","Reader not found "+e1.getMessage());

        }
    }
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this)
                {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        if(device != null)
                        {
                            //call method to set up device communication
                            CheckDevice();
                            open_device();
                        }
                    }
                    else
                    {


                    }
                }
            }
        }
    };
    public int match2(Reader.CaptureResult cap_result, String fp1, String fp2)
    {

        try {
            String b64 = Base64.encodeToString(m_engine.CreateFmd(cap_result.image,main_fmd_format).getData(),0);
            Log.e("BASE64 =>",""+b64);
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(Base64.decode(b64,0), main_fmd_format,main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(b64,0), main_fmd_format,main_fmd_format);


            //  Log.e("FFM =>","");

            int mm_score = m_engine.Compare(m_engine.CreateFmd(cap_result.image, main_fmd_format), 0,ffm, 0);
            Log.e("Disimilarity =>",""+mm_score);
            return mm_score;

        } catch (UareUException e) {
            Log.e("BASE64 Error=>",""+e.toString());
            e.printStackTrace();
            return -1;
        }
    }
    public static int match(String fp1, String fp2)
    {

        try {
            //String b64 =Base64.encodeToString(m_engine.CreateFmd(cap_result.image,main_fmd_format).getData(),0);
            //Log.e("BASE64 =>",""+b64);
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp1,0), main_fmd_format,main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp2,0), main_fmd_format,main_fmd_format);


            //  Log.e("FFM =>","");

            int mm_score = UareUGlobal.GetEngine().Compare(ffm, 0,ffm2, 0);
            Log.e("BASE64 Result=>",""+mm_score);

            return mm_score;

        } catch (UareUException e) {
            Log.e("BASE64 Error=>",""+e.toString());
            e.printStackTrace();
            return -1;
        }
    }
    public static boolean match_true(byte[] fp1, byte[] fp2)
    {
        boolean retv=false;
        try {
            //String b64 =Base64.encodeToString(m_engine.CreateFmd(cap_result.image,main_fmd_format).getData(),0);
            //Log.e("BASE64 =>",""+b64);
            // Log.e("BASE64 matching =>","");
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(fp1, main_fmd_format,main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(fp2, main_fmd_format,main_fmd_format);


            //  Log.e("FFM =>","");

//            int mm_score = ;
            //   Log.e("BASE64 Result=>",""+mm_score);

            retv= UareUGlobal.GetEngine().Compare(ffm, 0,ffm2, 0)<101;

        } finally {
            //  Log.e("BASE64 Error=>","False XXXXXX");
            return retv;

//            e.printStackTrace();
        }





    }
    public static boolean match_true(String fp1, String fp2)
    {
        boolean retv=false;
        try {
            //String b64 =Base64.encodeToString(m_engine.CreateFmd(cap_result.image,main_fmd_format).getData(),0);
            //Log.e("BASE64 =>",""+b64);
            // Log.e("BASE64 matching =>","");
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp1,0), main_fmd_format,main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp2,0), main_fmd_format,main_fmd_format);


            //  Log.e("FFM =>","");

//            int mm_score = ;
            //   Log.e("BASE64 Result=>",""+mm_score);

            retv= UareUGlobal.GetEngine().Compare(ffm, 0,ffm2, 0)<101;

        } finally {
            //  Log.e("BASE64 Error=>","False XXXXXX");
            return retv;

//            e.printStackTrace();
        }





    }
   public int match_global(String fp1, String fp2)
    {

        try {
            //String b64 =Base64.encodeToString(m_engine.CreateFmd(cap_result.image,main_fmd_format).getData(),0);
            //Log.e("BASE64 =>",""+b64);
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp1,0), main_fmd_format,main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp2,0), main_fmd_format,main_fmd_format);


            //  Log.e("FFM =>","");

            int mm_score = m_engine.Compare(ffm, 0,ffm2, 0);
            Log.e("BASE64 Result=>",""+mm_score);

            return mm_score;

        } catch (UareUException e) {
            Log.e("BASE64 Error=>",""+e.toString());
            e.printStackTrace();
            return -1;
        }
    }
    
    void extract_template(Bitmap bmpp, String fp2) throws UareUException {

      //  Fmd ffm = UareUGlobal.GetImporter().ImportFmd(s_bitmap_handler.getBytes(bmpp), main_fmd_format,main_fmd_format);

fp2= svars.fp_demo;
bmpp= s_bitmap_handler.getImage(Base64.decode(fp2,0));

        Fid fid = UareUGlobal.GetImporter().ImportRaw(s_bitmap_handler.getBytes(bmpp),
                bmpp.getWidth(), bmpp.getHeight() ,m_DPI, 500, 3407615,main_fid_format, m_DPI, true);
        Fmd fmd_raw = UareUGlobal.GetEngine().CreateFmd(fid,
                Fmd.Format.ANSI_378_2004);
        Fmd fmd_raw_data = UareUGlobal.GetImporter().ImportFmd(fmd_raw.getData(), main_fmd_format,main_fmd_format);

        Fmd fmd_raw2 = UareUGlobal.GetEngine().CreateFmd(s_bitmap_handler.getBytes(bmpp), bmpp.getWidth(),  bmpp.getHeight(),500, 0, 3407615,main_fmd_format);
        Fmd fmd_raw2_data = UareUGlobal.GetImporter().ImportFmd(fmd_raw2.getData(), main_fmd_format,main_fmd_format);

        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp2,0), main_fmd_format,main_fmd_format);


        //  Log.e("FFM =>","");

        int mm_score = m_engine.Compare(fmd_raw, 0,ffm2, 0);
        Log.e("Extraction Result 0=>",""+mm_score);
      int mm_score2 = m_engine.Compare(fmd_raw2, 0,ffm2, 0);
        Log.e("Extraction Result 1=>",""+mm_score2);
 int mm_score3 = m_engine.Compare(fmd_raw2, 0,fmd_raw, 0);
        Log.e("Extraction Result 2=>",""+mm_score3);


 int mm_score4 = m_engine.Compare(fmd_raw_data, 0,ffm2, 0);
        Log.e("Extraction Result 4=>",""+mm_score4);
 int mm_score5 = m_engine.Compare(fmd_raw2_data, 0,ffm2, 0);
        Log.e("Extraction Result 5=>",""+mm_score5);

 int mm_score6 = m_engine.Compare(fmd_raw_data, 0,fmd_raw2_data, 0);
        Log.e("Extraction Result 6=>",""+mm_score6);



    }


}
