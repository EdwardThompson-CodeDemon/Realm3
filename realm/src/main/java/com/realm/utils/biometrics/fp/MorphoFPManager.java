package com.realm.utils.biometrics.fp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
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
import com.morpho.morphosmart.sdk.Template;
import com.morpho.morphosmart.sdk.TemplateFVP;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


import com.realm.R;
import com.realm.utils.biometrics.fp.sdks.morpho.MorphoUtils;
import com.realm.utils.biometrics.fp.sdks.morpho.utils.DeviceDetectionMode;
import com.realm.utils.biometrics.fp.sdks.morpho.utils.MorphoInfo;
import com.realm.utils.biometrics.fp.sdks.morpho.utils.ProcessInfo;

import static com.morpho.morphosmart.sdk.CompressionAlgorithm.MORPHO_NO_COMPRESS;
import static com.morpho.morphosmart.sdk.TemplateFVPType.MORPHO_NO_PK_FVP;

public class MorphoFPManager extends FingerprintManger{
    public static String logTag="MorphoFPManager";
    private MorphoDevice morphoDevice;
    private String sensorName;
    private DeviceDetectionMode detectionMode = DeviceDetectionMode.SdkDetection;
    private BroadcastReceiver usbPermissionBroadcastReceiver;
    private String ACTION_USB_PERMISSION;

    public MorphoFPManager(Activity activity) {
        super(activity);
        morphoDevice = new MorphoDevice();
        ACTION_USB_PERMISSION = activity.getResources().getString(R.string.ACTION_USB_PERMISSION);
        ProcessInfo.getInstance().setMorphoDevice(this.morphoDevice);
        this.usbPermissionBroadcastReceiver = createUSBPermissionBroadcastReceiver();
        // Register a BroadcastReceiver for USB permission (Notified when Dialog of USB permission
        // is dismissed)
        activity.registerReceiver(this.usbPermissionBroadcastReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        USBManager.getInstance().initialize(activity, ACTION_USB_PERMISSION);
        initiateMorphoDevice();



    }

    @Override
    public void stop() {
        super.stop();
        closeConnection();
    }

    byte[] image;



    @Override
    public void start() {
        super.start();
        run=true;

        morphoDeviceGetImage((observable, arg) -> {
            CallbackMessage message = (CallbackMessage) arg;
            int type = message.getMessageType();
            Log.e(logTag,"type:"+type);
            switch (type) {
                case 1: // Message is a command.
                    handleCommand((Integer) message.getMessage());
                    break;
                case 2: // Message is a low resolution image
                    image=(byte[]) message.getMessage();
                    // handleImage(image);
                    break;
                case 3: // Message is the coded image quality.
                    handleQuality((Integer) message.getMessage());
                    break;
                default:
                    Log.e(logTag, "Unknown message received from Morpho device : " + arg);
                    break;
            }
        });
    }


    public int rebootSoft(Observer callback) {
        return morphoDevice.rebootSoft(30, callback);
    }
    public Map.Entry getMorphoDeviceInfo() {

        return new AbstractMap.SimpleEntry(morphoDevice.getProductDescriptor(), morphoDevice.getSoftwareDescriptor());
    }

    // FP device still not coming on red light

    //yeap.....still same

    public void openConnection() {
        this.morphoDevice = ProcessInfo.getInstance().getMorphoDevice();
        if(this.morphoDevice.openUsbDevice(
                ProcessInfo.getInstance().getMSOSerialNumber(),0)!= ErrorCodes.MORPHO_OK) {
            closeConnection();
            Log.e(logTag, "\t--> Error opening device in DeviceDetectionMode.SdkDetection");
        }
        Log.e(logTag, "\t--> Opening device in DeviceDetectionMode.SdkDetection");
        start();
    }

    // kiasi kinushow

    // nimenotice ni kama kuna listener ya screen timout.... after that inadisconnect FP
    //chill kiasi

    public void closeConnection() {
        run=false;
        this.morphoDevice.cancelLiveAcquisition();
        this.morphoDevice.closeDevice();

    }
    boolean run=false;
    boolean running=false;

    private final TemplateType TEMPLATE_TYPE = TemplateType.MORPHO_PK_ISO_FMC_CS;
    private final TemplateFVPType TEMPLATE_FVP_TYPE = MORPHO_NO_PK_FVP;
    private final EnrollmentType ENROLL_TYPE = EnrollmentType.ONE_ACQUISITIONS;
    private final int MAX_SIZE_TEMPLATE = 255;
    private final LatentDetection LATENT_DETECTION = LatentDetection.LATENT_DETECT_ENABLE;
    private final int NB_FINGER = 1;
    private void morphoDeviceCapture(final Observer observer) {
        //Background thread to capture a new fingerprint
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                if(!running){
                    running=true;
                    while(run){
                        if(morphoDevice==null){
                            Log.e(logTag,"Missing device");
                            break;
                        }
//                        Log.e(logTag,"Running loop");
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
                            exportFVP(templateList);
                            exportFP(templateList);
                        }

                        final int internalError = morphoDevice.getInternalError();
                        final int retValue = ret;

                    }
                    running=false;
                }



            }
        }).start();
    }


    private void morphoDeviceGetImage(final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run()

            {
                if(!running) {
                    running = true;
                    while (run) {
                        if (morphoDevice == null) {
                            Log.e(logTag, "Missing device");
                            break;
                        }
                        ProcessInfo processInfo = ProcessInfo.getInstance();

                        MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
                        int timeOut = processInfo.getTimeout();
                        int acquisitionThreshold = 0;
                        final CompressionAlgorithm compressAlgo = CompressionAlgorithm.MORPHO_COMPRESS_WSQ;
                        int compressRate = 10;
                        int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();
                        LatentDetection latentDetection = LatentDetection.LATENT_DETECT_ENABLE;
                        final MorphoImage[] morphoImage = new MorphoImage[]{new MorphoImage()};
                        int callbackCmd = ProcessInfo.getInstance().getCallbackCmd();

                        callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

                        if (ProcessInfo.getInstance().isFingerprintQualityThreshold()) {
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

                            interf.on_result_image_obtained(MorphoUtils.createBitmap(image));
//                            interf.on_result_image_obtained(MorphoUtils.createBitmap(morphoImage[0].getImage()));
                            interf.on_result_wsq_obtained(morphoImage[0].getCompressedImage());

                            interf.on_result_obtained(wsqToIso(morphoImage[0].getCompressedImage()));
                        }

                        final int internalError = morphoDevice.getInternalError();
                        final int retValue = ret;


                    }
                    running=false;
                }
            }
        }).start();
    }




    /**
     * Update UI Thread with quality return by Morpho device
     *
     * @param quality used to set new progress
     */
    private void handleQuality(final Integer quality) {
        Log.e(logTag,"Image quality:"+quality);
    }

    /**
     * Update UI Thread with image return by Morpho device
     *
     * @param image to display
     */
    private void handleImage(final byte[] image) {
        interf.on_result_image_obtained(MorphoUtils.createBitmap(image));
        interf.on_result_wsq_obtained(imageToWsq(MorphoUtils.createBitmap(image)));
        interf.on_result_obtained(wsqToIso(imageToWsq(MorphoUtils.createBitmap(image))));


    }

    /**
     * Update UI Thread with message return by Morpho device
     *
     * @param command corresponding to a String to display
     */
    private void handleCommand(final Integer command) {
        Log.e(logTag,"Sensor message"+MorphoUtils.createMessage(command));


    }
    /**
     * Write data in a file with FP format
     *
     * @param templateList containing data
     */
    private void exportFP(TemplateList templateList) {
        int nbTemplate = templateList.getNbTemplate();
        for (int i = 0; i < nbTemplate; i++) {
            try (FileOutputStream fos = new FileOutputStream("sdcard/TemplateFP_" + System.currentTimeMillis() + "_f" + (i + 1) + TEMPLATE_TYPE.getExtension())) {
                Template t = templateList.getTemplate(i);
                byte[] data = t.getData();
                Log.d(logTag, "Writing data in file with FP format : " + Arrays.toString(data));
                fos.write(data);
            } catch (IOException e) {
                Log.e(logTag, "An error has occurred while manipulating files " + e.getMessage());
            }
        }
    }

    /**
     * Write data in a file with FVP format
     *
     * @param templateList containing data
     */
    private void exportFVP(TemplateList templateList) {
        int nbTemplateFVP = templateList.getNbFVPTemplate();
        for (int i = 0; i < nbTemplateFVP; i++) {
            try (FileOutputStream fos = new FileOutputStream("sdcard/TemplateFVP_" + System.currentTimeMillis() + "_f" + (i + 1) + TEMPLATE_FVP_TYPE.getExtension())) {
                TemplateFVP t = templateList.getFVPTemplate(i);
                byte[] data = t.getData();
                Log.d(logTag, "Writing data in file with FVP format : " + Arrays.toString(data));
                fos.write(data);
            } catch (IOException e) {
                Log.e(logTag, "An error has occurred while manipulating files " + e.getMessage());
            }
        }
    }

    private BroadcastReceiver createOnAttachUSBBroadcastReceiver() {
        return new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        Log.i(logTag, "\t--> onReceive: USB Attached " + device.toString());
                        if (device.getVendorId() == 8797) {
                            rebootSoft(new Observer() {
                                @Override
                                public void update(Observable o, Object rebootOK) {
                                    boolean isRebootOK = (Boolean) rebootOK;
                                    if (isRebootOK) {
                                        Log.d(logTag, "onReceive: update : device reconnected");
                                        // displayValidReconnectingToast();
                                    } else {
                                        // displayErrorReconnectingToast();
                                    }
                                }
                            });
                        } else {
                            Log.e(logTag, "onReceive: Device attached : Not a Morpho");
                        }
                    }
                }
            }
        };
    }
    private BroadcastReceiver createOnDetachedUSBBroadcastReceiver() {
        return new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        Log.i(logTag, "\t--> onReceive: USB Detached " + device.toString());
                        if (device.getVendorId() == 8797) {
                            closeConnection();
//                            Fragment f = getFragmentManager().findFragmentById(R.id.fragment_container);
//                            if ((f instanceof EnrollFragment) || (f instanceof VerifyFragment) || (f instanceof ImageFragment)) {
//                                onBackPressed();
//                            }
//                            displayDetachToast();
                        } else {
                            Log.e(logTag, "onReceive: Device Detached : Not a Morpho");
                        }
                    }
                }
            }
        };
    }

    private BroadcastReceiver createUSBPermissionBroadcastReceiver() {
        return new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Log.e(logTag, "\t --> Permission granted for device" + device);
                        if(device != null){
                            Log.e(logTag, "\t --> Device USB found ");
                            // Initiate if permission granted
                            initiateMorphoDevice();
                        }
                    } else {
                        Log.e(logTag, "\t --> Permission denied for device " + device);
                        //   permissionDenied();
                    }
                }
            }
        };
    }

    //cool
    // yeah bro...very cool...
//    step 1 of 13 done
    public void initiateMorphoDevice() {
        // {Morpho SDK method} to check the USB permission
        // (call to USBManager.getInstance().initialize(...) required
        if(USBManager.getInstance().isDevicesHasPermission()) {
            Log.e(logTag,"\t --> Start enumeration of devices");
            // view.informUserOfCurrentProgress(MSG_USER_PROGRESS_ENUMERATION, SECOND_STEP);
            if(enumerate() == ErrorCodes.MORPHO_OK) {
                Log.e(logTag,"\t --> Start connection");
//                view.informUserOfCurrentProgress(MSG_USER_PROGRESS_CONNECTION, FOURTH_STEP);
                connection();
                openConnection();
            }
        }
    }
    /**
     * Enumerate the devices connected
     * Suppression of Warning on purpose to avoid suppression of new Integer(0) line
     * which is useful to store object in memory
     */
    @SuppressWarnings("all")
    private int enumerate() {

        // The creation of an Integer is required here.
        // It is used as a pointer for memory uses in the call of C native functions in
        // Morpho SDK
        //Integer nbUsbDevice = new Integer(0);
        CustomInteger cUsbDevice = new CustomInteger();

        Log.e(logTag, "\t --> Start initUSBDevicesNameEnum");
        int ret = this.morphoDevice.initUsbDevicesNameEnum(cUsbDevice);
        Integer nbUsbDevice = new Integer(cUsbDevice.getValueOf());
        Log.e(logTag, "\t --> End initUSBDevicesNameEnum");

        if (ret == ErrorCodes.MORPHO_OK) {
            Log.d(logTag, "\t --> MORPHO OK");
            if (nbUsbDevice > 0) {
                this.sensorName = morphoDevice.getUsbDeviceName(0);
                //view.informUserOfCurrentProgress(MSG_USER_PROGRESS_DEVICE_FOUND, THIRD_STEP);
                Log.i(logTag, "\t --> Enumerate : SensorName : " + sensorName);
            } else {
                ret = -1;
                Log.e(logTag, "\t --> NO DEVICE FOUND");
                //    view.deviceNotFound();
            }
        } else {
            Log.e(logTag, "\t --> MORPHO NOT OK");
            ret = -1;
            //  view.displayDialogWithMessage(ErrorCodes.getError(ret, morphoDevice.getInternalError()));
        }
        return ret;
    }

    /**
     * Connection with Morpho device and set data
     * If all initialization succeed, starts next HomeActivity
     */
    private void connection() {

        // Open USB connection with Morpho device
        int ret = morphoDevice.openUsbDevice(sensorName, 2000);
        Log.e(logTag, "\t --> Open USB Device returned : " + ret);

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
            //    view.startNextActivity();

        } else {
            // Close USB connection with Morpho device
            morphoDevice.closeDevice();
//            view.displayDialogWithMessage(ErrorCodes.getError(ret, morphoDevice.getInternalError()));
            Log.e(logTag,"Failed to open device");
        }
    }


    /**
     * Initiate the Morpho device data
     */
    private void initMorphoDeviceData() {
        // Default configuration of the Morpho device
        int sensorBus = -1, sensorAddress = -1, sensorFileDescriptor = -1;

        ProcessInfo.getInstance().setMSOSerialNumber(sensorName);
        ProcessInfo.getInstance().setMSOBus(sensorBus);
        ProcessInfo.getInstance().setMSOAddress(sensorAddress);
        ProcessInfo.getInstance().setMSOFD(sensorFileDescriptor);
        ProcessInfo.getInstance().setMsoDetectionMode(detectionMode);
    }
}
