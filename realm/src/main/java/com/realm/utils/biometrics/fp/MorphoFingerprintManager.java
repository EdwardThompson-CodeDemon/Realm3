package com.realm.utils.biometrics.fp;

import static com.morpho.morphosmart.sdk.CompressionAlgorithm.MORPHO_NO_COMPRESS;
import static com.morpho.morphosmart.sdk.TemplateFVPType.MORPHO_NO_PK_FVP;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.CallbackMessage;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.CustomInteger;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoImage;
import com.morpho.morphosmart.sdk.MorphoWakeUpMode;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import com.realm.R;
import com.realm.utils.biometrics.fp.sdks.morpho_v2.MorphoUtils;
import com.realm.utils.biometrics.fp.sdks.morpho_v2.utils.Constants;
import com.realm.utils.biometrics.fp.sdks.morpho_v2.utils.Utils;
import com.realm.utils.biometrics.fp.sdks.morpho_v2.utils.morpho.DeviceDetectionMode;
import com.realm.utils.biometrics.fp.sdks.morpho_v2.utils.morpho.MorphoInfo;
import com.realm.utils.biometrics.fp.sdks.morpho_v2.utils.morpho.ProcessInfo;


public class MorphoFingerprintManager extends FingerprintManger implements Observer {


    private String sensorName;
    private MorphoDevice morphoDevice;
    public MorphoFingerprintManager(Activity activity) {
        super(activity);
        USBManager.getInstance().initialize(activity, activity.getResources().getString(R.string.ACTION_USB_PERMISSION));
        mHandler = new Handler();
        this.morphoDevice = new MorphoDevice();
        ProcessInfo.getInstance().setMorphoDevice(this.morphoDevice);
       // initiateMorphoDevice();
    }
    @Override
    public void start() {
        super.start();
        closed=false;
        initiateMorphoDevice();
    }
        @Override
    public void stop() {
        super.stop();
        closeConnection();
    }
    public void initiateMorphoDevice() {
        // {Morpho SDK method} to check the USB permission
        // (call to USBManager.getInstance().initialize(...) required
        if (!Utils.isFP200()) {
            if (USBManager.getInstance().isDevicesHasPermission()) {
                Log.e(TAG, "\t --> Start enumeration of devices");
//                view.informUserOfCurrentProgress(MSG_USER_PROGRESS_ENUMERATION, SECOND_STEP);
                if (enumerate() == ErrorCodes.MORPHO_OK) {
                    Log.e(TAG, "\t --> Start connection");
//                    view.informUserOfCurrentProgress(MSG_USER_PROGRESS_CONNECTION, FOURTH_STEP);
                    connection();
                    openConnection();
                    morphoDeviceGetImage(this);

                }
            }
        } else {
            Log.e(TAG, "\t --> Start connection");
//            view.informUserOfCurrentProgress(MSG_USER_PROGRESS_CONNECTION, FOURTH_STEP);
            connection();
            openConnection();
            morphoDeviceGetImage(this);

        }
    }


    private int enumerate() {

        // The creation of an Integer is required here.
        // It is used as a pointer for memory uses in the call of C native functions in
        // Morpho SDK
        CustomInteger nbUsbDevice = new CustomInteger();
        Log.e(TAG, "\t --> Start initUSBDevicesNameEnum");
        int ret = this.morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);
        Log.e(TAG, "\t --> End initUSBDevicesNameEnum");

        if (ret == ErrorCodes.MORPHO_OK) {
            Log.e(TAG, "\t --> MORPHO OK");
            if (nbUsbDevice.getValueOf() > 0) {
                this.sensorName = morphoDevice.getUsbDeviceName(0);
//                view.informUserOfCurrentProgress(MSG_USER_PROGRESS_DEVICE_FOUND, THIRD_STEP);
                Log.e(TAG, "\t --> Enumerate : SensorName : " + sensorName);
            } else {
                ret = -1;
                Log.e(TAG, "\t --> NO DEVICE FOUND");
//                view.deviceNotFound();
            }
        } else {
            Log.d(TAG, "\t --> MORPHO NOT OK");
            ret = -1;
//            view.displayDialogWithMessage(ErrorCodes.getError(ret, morphoDevice.getInternalError()));
        }
        return ret;
    }

    private void connection() {
        int ret;
        if (!Utils.isFP200()) {
            // Open USB connection with Morpho device
            ret = morphoDevice.openUsbDevice(sensorName, 2000);
        } else {
            ret = morphoDevice.openDeviceWithUart(Constants.UART_PORT, Constants.UART_SPEED);
        }
        Log.d(TAG, "\t --> Open Device returned : " + ret);

        if (ret == ErrorCodes.MORPHO_OK) {
            // Set Morpho device data
            initMorphoDeviceData();

//            view.informUserOfCurrentProgress(MSG_USER_PROGRESS_CONNECTION_ESTABLISHED, LAST_STEP);

            String productDescriptor = morphoDevice.getProductDescriptor();
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(productDescriptor, "\n");
            if (tokenizer.hasMoreTokens())
            {
                String l_s_current = tokenizer.nextToken();
                if (l_s_current.contains("FINGER VP") || l_s_current.contains("FVP"))
                    MorphoInfo.setM_b_fvp(true);
            }

            // Close USB connection with Morpho device
            morphoDevice.closeDevice();

            // Start next HomeActivity
//            view.startNextActivity();

        } else {
            // Close USB connection with Morpho device
            morphoDevice.closeDevice();
//            view.displayDialogWithMessage(ErrorCodes.getError(ret, morphoDevice.getInternalError()));
        }
    }
    public void openConnection() {
        this.morphoDevice = ProcessInfo.getInstance().getMorphoDevice();
        if (!Utils.isFP200()) {
            if (this.morphoDevice.openUsbDevice(
                    ProcessInfo.getInstance().getMSOSerialNumber(), 0) != ErrorCodes.MORPHO_OK) {
                closeConnection();
                Log.e(TAG, "\t--> Error opening device in DeviceDetectionMode.SdkDetection");
            }
        } else {
            if (this.morphoDevice.openDeviceWithUart(
                    Constants.UART_PORT, Constants.UART_SPEED) != ErrorCodes.MORPHO_OK) {
            }
        }
        Log.e(TAG, "\t--> Opening device in DeviceDetectionMode.SdkDetection");
        closed=false;
    }
boolean closed=false;
//    @Override
    public void closeConnection() {
        closed=true;
        this.morphoDevice.cancelLiveAcquisition();
        this.morphoDevice.closeDevice();
    }
    private DeviceDetectionMode detectionMode = DeviceDetectionMode.SdkDetection;

    private void initMorphoDeviceData() {
        // Default configuration of the Morpho device
        int sensorBus = -1, sensorAddress = -1, sensorFileDescriptor = -1;

        ProcessInfo.getInstance().setMSOSerialNumber(sensorName);
        ProcessInfo.getInstance().setMSOBus(sensorBus);
        ProcessInfo.getInstance().setMSOAddress(sensorAddress);
        ProcessInfo.getInstance().setMSOFD(sensorFileDescriptor);
        ProcessInfo.getInstance().setMsoDetectionMode(detectionMode);

        if (Utils.isFP200()) {
            int ret = this.morphoDevice.setConfigParam(MorphoDevice.CONFIG_RS232_PREVIEW_BPP, new byte[] { 4 });
            if (ret != ErrorCodes.MORPHO_OK) {
                Log.d(TAG, "\t--> (B) RS232 set preview BPP returned error " + ret);
            }

            byte[] r = this.morphoDevice.getConfigParam(MorphoDevice.CONFIG_RS232_PREVIEW_BPP);
            if (r == null) {
                Log.d(TAG, "\t--> (B) RS232 get preview BPP returned null");
            } else {
                Log.d(TAG, "\t--> (B) RS232 preview BPP set to " + r[0]);
            }

            ret = this.morphoDevice.setConfigParam(MorphoDevice.CONFIG_RS232_PREVIEW_DR, new byte[] { 2 });
            if (ret != ErrorCodes.MORPHO_OK) {
                Log.d(TAG, "\t--> (B) RS232 set preview DR returned error " + ret);
            }
            r = this.morphoDevice.getConfigParam(MorphoDevice.CONFIG_RS232_PREVIEW_DR);
            if (r == null) {
                Log.d(TAG, "\t--> (B) RS232 get preview DR returned null");
            } else {
                Log.d(TAG, "\t--> (B) RS232 preview DR set to " + r[0]);
            }
        }
    }


    private final TemplateType TEMPLATE_TYPE = TemplateType.MORPHO_PK_ISO_FMC_CS;
    private final TemplateFVPType TEMPLATE_FVP_TYPE = MORPHO_NO_PK_FVP;
    private final EnrollmentType ENROLL_TYPE = EnrollmentType.ONE_ACQUISITIONS;
    private final int MAX_SIZE_TEMPLATE = 255;
    private final LatentDetection LATENT_DETECTION = LatentDetection.LATENT_DETECT_ENABLE;
    private final int NB_FINGER = 1;

    private void morphoDeviceGetImage(final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ProcessInfo processInfo = ProcessInfo.getInstance();

                MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
                int timeOut = processInfo.getTimeout();
                int acquisitionThreshold = 0;
                final CompressionAlgorithm compressAlgo = CompressionAlgorithm.MORPHO_COMPRESS_WSQ;
                int compressRate = 10;
                int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();
                LatentDetection latentDetection = LatentDetection.LATENT_DETECT_ENABLE;
                final MorphoImage[] morphoImage = new MorphoImage[] {new MorphoImage()};
                int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();

                callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

                if(ProcessInfo.getInstance().isFingerprintQualityThreshold()) {
                    acquisitionThreshold = ProcessInfo.getInstance().getFingerprintQualityThresholdvalue();
                }

                final int ret = morphoDevice.getImage(timeOut, acquisitionThreshold,
                        compressAlgo,
                        compressRate,
                        detectModeChoice,
                        latentDetection,
                        morphoImage[0],
                        callbackCmd,
                        observer);

                ProcessInfo.getInstance().setCommandBioStart(false);

                MorphoUtils.storeFFDLogs(morphoDevice);

                if (ret == ErrorCodes.MORPHO_OK) {
//                    exportNoCompressedImage(morphoImage[0]);
//                    exportWSQCompressedImage(morphoImage[0]);
//                    exportWSQCompressedImageWithHeader(morphoImage[0]);
//                    exportWSQCompressedImageWithNewHeader(morphoImage[0]);
//                    saveImage(morphoImage);
                    byte[] bitmapdata=latest_img;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
//                    interf.on_result_image_obtained(wsqToIso(bitmapdata));





                    interf.on_result_image_obtained(bitmap);
                    interf.on_result_wsq_obtained(morphoImage[0].getCompressedImage());
                    interf.on_result_obtained(wsqToIso(morphoImage[0].getCompressedImage()));

                }

                final int internalError = morphoDevice.getInternalError();
                final int retValue = ret;

                mHandler.post(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (retValue != ErrorCodes.MORPHOERR_CMDE_ABORTED) {
//                            view.alert(retValue, internalError);
//                            view.onImageCaptureCompleted();

if(!closed){
    morphoDeviceGetImage(observer);
}
                        }
                    }
                });
            }
        }).start();
    }
//    private void exportWSQCompressedImageWithNewHeader(final MorphoImage morphoImage) {
//        try (FileOutputStream fos = new FileOutputStream("sdcard/TemplateFP_WSQ_newHeader" + CompressionAlgorithm.MORPHO_COMPRESS_WSQ.getExtension())) {
//            byte[] data = morphoImage.getCompressedImage();
//            byte[] result = WSQUtils.setNewHeader(data);
//            Log.d(TAG, "Writing data in file with WSQ format : " + Arrays.toString(data));
//            fos.write(result);
//        } catch (IOException e) {
//            Log.e(TAG, "An error has occurred while manipulating files " + e.getMessage());
//        }
//    }
byte[] latest_img=null;
    private void exportWSQCompressedImage(final MorphoImage morphoImage) {
        try (FileOutputStream fos = new FileOutputStream("sdcard/TemplateFP_WSQ" + CompressionAlgorithm.MORPHO_COMPRESS_WSQ.getExtension())) {
            byte[] data = morphoImage.getCompressedImage();
            Log.d(TAG, "Writing data in file with WSQ format : " + Arrays.toString(data));
            fos.write(data);
        } catch (IOException e) {
            Log.e(TAG, "An error has occurred while manipulating files " + e.getMessage());
        }
    }
    public void update(Observable o, Object arg) {
// Convert the object to a callback message.
        CallbackMessage message = (CallbackMessage) arg;
        int type = message.getMessageType();

        switch (type) {
            case 1: // Message is a command.
                handleCommand((Integer) message.getMessage());
                break;
            case 2: // Message is a low resolution image
                handleImage((byte[]) message.getMessage());
                break;
            case 3: // Message is the coded image quality.
                handleQuality((Integer) message.getMessage());
                break;
            default:
                Log.e(TAG, "Unknown message received from Morpho device : " + arg);
                break;
        }
    }
    private void handleCommand(final Integer command) {
        mHandler.post(new Runnable() {
            @Override
            public synchronized void run() {
                Log.e(TAG, "updateSensorMessage : " + MorphoUtils.createMessage(command));

//                view.updateSensorMessage(MorphoUtils.createMessage(command));
            }
        });
    }
    /**
     * Update UI Thread with quality return by Morpho device
     *
     * @param quality used to set new progress
     */
    private void handleQuality(final Integer quality) {
        mHandler.post(new Runnable() {
            @Override
            public synchronized void run() {
                Log.e(TAG, "FP QUALITY : " + quality);

//                view.updateSensorProgressBar(quality);
            }
        });
    }

    /**
     * Update UI Thread with image return by Morpho device
     *
     * @param image to display
     */
    private void handleImage(final byte[] image) {
        mHandler.post(new Runnable() {
            @Override
            public synchronized void run() {
                latest_img=image;
//                view.updateImage(MorphoUtils.createBitmap(image));
//                interf.on_result_image_obtained(MorphoUtils.createBitmap(image));
            }
        });
    }

    private void morphoDeviceCapture(final Observer observer) {
        //Background thread to capture a new fingerprint
        new Thread(new Runnable() {
            @Override
            public void run() {
                ProcessInfo processInfo = ProcessInfo.getInstance();
                final int timeout = processInfo.getTimeout();
                TemplateList templateList = new TemplateList();

                int acquisitionThreshold = (processInfo.isFingerprintQualityThreshold()) ?
                        processInfo.getFingerprintQualityThresholdvalue() : 0;
                int advancedSecurityLevelsRequired = (processInfo.isAdvancedSecLevCompReq()) ?
                        1 : 0xFF;

                int callbackCmd = processInfo.getCallbackCmd();
                Coder coderChoice = processInfo.getCoder();

                int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();
                if (processInfo.isForceFingerPlacementOnTop())
                    detectModeChoice |= DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
                if (processInfo.isWakeUpWithLedOff())
                    detectModeChoice |= MorphoWakeUpMode.MORPHO_WAKEUP_LED_OFF.getCode();

                int ret = morphoDevice.setStrategyAcquisitionMode(processInfo.getStrategyAcquisitionMode());
                if (ret == ErrorCodes.MORPHO_OK) {
                    ret = morphoDevice.capture(timeout, acquisitionThreshold, advancedSecurityLevelsRequired,
                            NB_FINGER,
                            TEMPLATE_TYPE, TEMPLATE_FVP_TYPE, MAX_SIZE_TEMPLATE, ENROLL_TYPE,
                            LATENT_DETECTION, coderChoice, detectModeChoice,
                            MORPHO_NO_COMPRESS, 0, templateList, callbackCmd, observer);

                    //Test of getImage method from SDK
//                    final MorphoImage[] morphoImage = new MorphoImage[] {new MorphoImage()};
//                    ret = morphoDevice.getImage(timeout, acquisitionThreshold,
//                            MORPHO_COMPRESS_WSQ, 0, detectModeChoice, LATENT_DETECTION, morphoImage[0], callbackCmd, observer);
                }

                processInfo.setCommandBioStart(false);

                MorphoUtils.storeFFDLogs(morphoDevice);

                if (ret == ErrorCodes.MORPHO_OK) {
//                    exportFVP(templateList);
//                    exportFP(templateList);
                }

                final int internalError = morphoDevice.getInternalError();
                final int retValue = ret;

                mHandler.post(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (retValue != ErrorCodes.MORPHOERR_CMDE_ABORTED) {
//                            view.onCaptureCompleted();
                        }
                    }
                });
            }
        }).start();
    }
    private static final String TAG = MorphoFingerprintManager.class.getSimpleName();

//    private void exportFP(TemplateList templateList) {
//        int nbTemplate = templateList.getNbTemplate();
//        for (int i = 0; i < nbTemplate; i++) {
//            try (FileOutputStream fos = new FileOutputStream("sdcard/TemplateFP_" + ID_USER + "_f" + (i + 1) + TEMPLATE_TYPE.getExtension())) {
//                Template t = templateList.getTemplate(i);
//                byte[] data = t.getData();
//                Log.d(TAG, "Writing data in file with FP format : " + Arrays.toString(data));
//                fos.write(data);
//            } catch (IOException e) {
//                Log.e(TAG, "An error has occurred while manipulating files " + e.getMessage());
//            }
//        }
//    }

    /**
     * Write data in a file with FVP format
     *
     * @param templateList containing data
     */
//    private void exportFVP(TemplateList templateList) {
//        int nbTemplateFVP = templateList.getNbFVPTemplate();
//        for (int i = 0; i < nbTemplateFVP; i++) {
//            try (FileOutputStream fos = new FileOutputStream("sdcard/TemplateFVP_" + ID_USER + "_f" + (i + 1) + TEMPLATE_FVP_TYPE.getExtension())) {
//                TemplateFVP t = templateList.getFVPTemplate(i);
//                byte[] data = t.getData();
//                Log.d(TAG, "Writing data in file with FVP format : " + Arrays.toString(data));
//                fos.write(data);
//            } catch (IOException e) {
//                Log.e(TAG, "An error has occurred while manipulating files " + e.getMessage());
//            }
//        }
//    }
    private Handler mHandler;



    /**
     * Constructor of the Presenter
     *
     * @param view of the MVP pattern
     */
//    EnrollPresenter(EnrollContract.View view) {
//        this.view = checkNotNull(view);
//        mHandler = new Handler();
//        morphoDevice = ProcessInfo.getInstance().getMorphoDevice();
//    }


}
