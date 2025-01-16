package com.realm.utils.biometrics.fp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.aratek.trustfinger.sdk.DeviceListener;
import com.aratek.trustfinger.sdk.DeviceOpenListener;
import com.aratek.trustfinger.sdk.FingerPosition;
import com.aratek.trustfinger.sdk.LedIndex;
import com.aratek.trustfinger.sdk.LedStatus;
import com.aratek.trustfinger.sdk.LfdLevel;
import com.aratek.trustfinger.sdk.MultiFingerCallback;
import com.aratek.trustfinger.sdk.MultiFingerParam;
import com.aratek.trustfinger.sdk.MultiFingerProcessResult;
import com.aratek.trustfinger.sdk.SegmentImageDesc;
import com.aratek.trustfinger.sdk.TrustFinger;
import com.aratek.trustfinger.sdk.TrustFingerDevice;
import com.aratek.trustfinger.sdk.TrustFingerException;

import java.util.List;


public class AratekFPManagerV2 extends FingerprintManger {
    static String logTag = "AratekFPManager";


    private TrustFinger mTrustFinger;
    protected TrustFingerDevice mTrustFingerDevice;
    private boolean isDeviceOpened = false;
    private int mDeviceId = 0;
    private boolean isCaturing = false;

    public AratekFPManagerV2(Activity activity) {
        super(activity);
        initTrustFinger();
    }

    private void initTrustFinger() {
        try {
            mTrustFinger = TrustFinger.getInstance(activity);

            //            mTextView_version.setText("v" + mTrustFinger.getSdkVersion());
            mTrustFinger.initialize();
            mTrustFinger.getSdkVersion();
            mTrustFinger.setDeviceListener(new DeviceListener() {

                @Override
                public void deviceAttached(List<String> deviceList) {

                    for (int i = 0; i < deviceList.size(); i++) {
                        Log.e(logTag, i + "-" + deviceList.get(i));
                    }


                }

                @Override
                public void deviceDetached(List<String> deviceList) {
                    //Log.i("Sanny", "Demo deviceDetached.");
                    if (mTrustFingerDevice != null) {
                        mTrustFingerDevice = null;
                    }
                    for (int i = 0; i < deviceList.size(); i++) {
                        Log.e(logTag, i + "-" + deviceList.get(i));
                    }
                    isDeviceOpened = false;

                }
            });
            if (mTrustFinger.getDeviceCount() <= 0) {
                Log.e(logTag, "No fingerprint device detected!");
                interf.on_device_error("No fingerprint device detected!");
            }
        } catch (TrustFingerException e) {
            e.printStackTrace();
            Log.e(logTag, "Fingerprint detection failed!");
            interf.on_device_error("Fingerprint detection failed!");

        } catch (ArrayIndexOutOfBoundsException e) {

            Log.e(logTag, "The system does not support simultaneous access to two devices!");
            interf.on_device_error("The system does not support simultaneous access to two devices!");
        }

    }

    void openDevice() {
        if (!isDeviceOpened) {
            if (mDeviceId == -1) {

                return;
            }
            mDeviceId = 0;


            try {
                mTrustFinger.openDevice(mDeviceId, new DeviceOpenListener() {
                    @Override
                    public void openSuccess(TrustFingerDevice trustFingerDevice) {
                        mTrustFingerDevice = trustFingerDevice;
                        String model = mTrustFingerDevice.getDeviceDescription().getProductModel();
                        mTrustFingerDevice.setDryFingerLevel(4);
//                        mTrustFingerDevice.getDryFingerLevel();
                        if (model.equals("A600")) {
                            int firmwareVersion = (int) (Float.valueOf(trustFingerDevice.getDeviceDescription().getFwVersion()) * 1000);
                            if (firmwareVersion < 4200) {
                                Log.e(logTag, "The current firmware version is " + firmwareVersion + ",this software only supports version 4.2 or above!");
                                interf.on_device_error("The current firmware version is " + firmwareVersion + ",this software only supports version 4.2 or above!");
                            }
                        }
                        updateLFDLevel(LfdLevel.OFF);
                        Log.e(logTag, "Device model" + model);
                        interf.on_device_status_changed("Device model" + model);

                        isDeviceOpened = true;

                    }

                    @Override
                    public void openFail(String s) {
                        isDeviceOpened = false;

                    }
                });

            } catch (TrustFingerException e) {
                Log.e(logTag, "Open device failed!\n" + e.getType().toString());
                interf.on_device_error("Open device failed!\n" + e.getType().toString());
                e.printStackTrace();
            }
        }

    }

    private void updateLFDLevel(int level) {
        if (mTrustFingerDevice != null) {
            try {
                mTrustFingerDevice.setLfdLevel(level);
            } catch (TrustFingerException e) {
                Log.e(logTag, "enableLFD failed!\n" + e.getType().toString());
                interf.on_device_error("enableLFD failed!\n" + e.getType().toString());
            }
        }
    }

    private int mScannedObject = SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS;
    private int mMissingFingerPos = 0;
    int[] segment_run_count = new int[]{SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS, SegmentImageDesc.ARAFPSCAN_SLAP_4_RIGHT_FINGERS, SegmentImageDesc.ARAFPSCAN_SLAP_2_THUMBS_FINGERS};
    int segment_run_counter = 0;

    public void startFullCapture(int mScannedObject) {
        this.mScannedObject = mScannedObject;
        if (!isCaturing) {
            isCaturing = true;

            segmentFullImage();

        }

    }
   public void startFullCapture_(int mScannedObject) {
        this.mScannedObject = mScannedObject;
        if (!isCaturing) {
            isCaturing = true;

//            if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS
//                    || mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_RIGHT_FINGERS) {
//                mTrustFingerDevice.setMissingFingers(mMissingFingerPos);
//            }
//            segmentFullImage();

        }

    }
//    private int getCurrentPosition() {
//        int count = 0;
//        mMissingFingerPos = 0;
//        for (int i = 9; i >= 6; i--) {
//            if (mFingerCheckBoxSelect[i] == 1) {
//                mMissingFingerPos |= (0x01 << (i - 2));
//                count++;
//            }
//        }
//        for (int i = 1; i < 5; i++) {
//            if (mFingerCheckBoxSelect[i] == 1) {
//                mMissingFingerPos |= (0x01 << i - 1);
//                count++;
//            }
//        }
//        return count;
//    }
//    public void SegmentImage_() {
//        isCaturing = true;
//        mNumberOfSegment = 0;
//        mSegmentImageDesc = null;
//        mMissingFingerPos = 0;
//        mTrustFingerDevice.setMissingFingers(mMissingFingerPos);
//
//        if (mScannedObject >= SegmentImageDesc.ARAFPSCAN_SLAP_RIGHT_THUMB_FINGER && mScannedObject <= SegmentImageDesc.ARAFPSCAN_ROLL_LEFT_LITTLE_FINGER) {
//            mSegmentSupportNum = 1;
//        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_2_THUMBS_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_ANY_TWO_FINGERS) {
//            mSegmentSupportNum = 2;
//        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_RIGHT_FINGERS) {
//            mSegmentSupportNum = 4;
//            int missFingerCount = getCurrentPosition();
//            if (missFingerCount > 0) {
//                mTrustFingerDevice.setMissingFingers(mMissingFingerPos);
//                mSegmentSupportNum -= missFingerCount;
//            }
//        }
//
////        mTrustFingerDevice.setLfdLevel(4);
////        handleMsg("Capturing", Color.BLACK);
//        fpImage_Raw = null;
//        fpImage_bmp = null;
//
//        try {
//
//            MultiFingerParam mParam = new MultiFingerParam(mScannedObject, 0, 2);
//            mTrustFingerDevice.multiFingerCapture(mParam, new MultiFingerCallback() {
//                @Override
//                public void multiFingerCallback(int occurredEventCode, byte[] rawData, SegmentImageDesc[] segmentImageDesc, int numberOfSegment) {
//                    Log.i(TAG, "Multi multiFingerCallback occurredEventCode:" + occurredEventCode + ",number:" + numberOfSegment);
//                    if (occurredEventCode == MultiFingerProcessResult.ON_CAPTURE_IMAGE && rawData != null) {
//
//                        byte[] bmpData = mTrustFingerDevice.rawToBmp(rawData, mTrustFingerDevice.getImageInfo().getWidth(),
//                                mTrustFingerDevice.getImageInfo().getHeight(), mTrustFingerDevice.getImageInfo().getResolution());
//                        if (bmpData == null)
//                            return;
//                        Bitmap bmp = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
//                        if (bmp != null) {
//                            updateFingerprintImage(bmp);
//                        }
//                    }
//                    if (occurredEventCode == MultiFingerProcessResult.ON_SEGMENT_IMAGE && segmentImageDesc != null && numberOfSegment > 0) {
//                        if (numberOfSegment == mSegmentSupportNum) {
//                            fpImage_Raw = rawData;
//                            mSegmentImageDesc = segmentImageDesc;
//                            mNumberOfSegment = numberOfSegment;
//
//                            byte[] bmpD;
//                            for (int i = 0; i < numberOfSegment; i++) {
//                                bmpD = mTrustFingerDevice.rawToBmp(segmentImageDesc[i].pSegmentImagePtr, segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500);
//                                if (bmpD != null) {
//                                    subSegmentBitmap[i] = BitmapFactory.decodeByteArray(bmpD, 0, bmpD.length);
//
//                                } else {
//                                    Log.e(TAG, "rawToBmp null:" + i);
//                                }
//                            }
//                        } else {
//                            handleMsg("Image Quality low, or Finger Number not enough.", Color.RED);
//                        }
//                    }
//                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_PROCESS_SUCCESS && segmentImageDesc != null && numberOfSegment > 0) {
//
//                        fpImage_Raw = rawData;
//                        mSegmentImageDesc = segmentImageDesc;
//                        mNumberOfSegment = numberOfSegment;
//
//                        fpImage_bmp = mTrustFingerDevice.rawToBmp(fpImage_Raw, mTrustFingerDevice
//                                .getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo()
//                                .getHeight(), mTrustFingerDevice.getImageInfo().getResolution());
//
//                        if (fpImage_bmp == null) {
//                            updateFingerprintImage(null);
//                        } else {
//                            fpImage_bitmap = BitmapFactory.decodeByteArray(fpImage_bmp, 0, fpImage_bmp.length);
//                            updateFingerprintImage(fpImage_bitmap);
//                        }
////                                Log.i("Sanny", "mNumberOfSegment:"+mNumberOfSegment);
//                        if (mNumberOfSegment > 0 && mSegmentImageDesc != null) {
//
//                            //updata show subImage
//                            int imageQualitySub = 0;
//                            for (int i = 0; i < mNumberOfSegment; i++) {
//
//                                imageQualitySub = mSegmentImageDesc[i].nQuality;
//                                if (subSegmentBitmap[i] != null) {
//                                    if (i == 0) {
//                                        updateFingerprintImage_1(subSegmentBitmap[i], imageQualitySub);
//                                    } else if (i == 1) {
//                                        updateFingerprintImage_2(subSegmentBitmap[i], imageQualitySub);
//                                    } else if (i == 2) {
//                                        updateFingerprintImage_3(subSegmentBitmap[i], imageQualitySub);
//                                    } else if (i == 3) {
//                                        updateFingerprintImage_4(subSegmentBitmap[i], imageQualitySub);
//                                    }
//                                }
//
//
//                                if (mSegmentImageDesc[i].pFeatureData == null) {
//                                    mHandler.sendMessage(mHandler.obtainMessage(MSG_Segment_FAIL, 0, 0, mFingerPosition));
//                                    handleMsg("Segment fail ! feature " + i + " null !", Color.RED);
//                                    isCaturing = false;
//                                    return;
//                                }
//                            }
//
//                            mHandler.sendMessage(mHandler.obtainMessage(MSG_Segment_SUCCESS, 0, 0, mFingerPosition));
//                            handleMsg("Segment success ! " + mNumberOfSegment + " finger.", Color.BLACK);
//                            mHandler.sendMessage(mHandler.obtainMessage(MSG_SAVE_IMAGE_TO_ENROLL, 0, 0));
//                        } else {
//                            mHandler.sendMessage(mHandler.obtainMessage(MSG_Segment_FAIL, 0, 0, mFingerPosition));
//                            handleMsg("Segment fail", Color.RED);
//                        }
//                        isCaturing = false;
//                    }
//                    if (occurredEventCode == MultiFingerProcessResult.LFDCHECK_FAIL_END && segmentImageDesc != null && numberOfSegment > 0) {
//                        if (mNumberOfSegment > 0 && mSegmentImageDesc != null) {
//                            int imageQualitySub = 0;
//                            for (int i = 0; i < mNumberOfSegment; i++) {
//
//                                imageQualitySub = mSegmentImageDesc[i].nQuality;
//                                if (subSegmentBitmap[i] != null) {
//
//                                    if (i == 0) {
//                                        updateFingerprintImage_1(subSegmentBitmap[i], imageQualitySub);
//                                    } else if (i == 1) {
//                                        updateFingerprintImage_2(subSegmentBitmap[i], imageQualitySub);
//                                    } else if (i == 2) {
//                                        updateFingerprintImage_3(subSegmentBitmap[i], imageQualitySub);
//                                    } else if (i == 3) {
//                                        updateFingerprintImage_4(subSegmentBitmap[i], imageQualitySub);
//                                    }
//                                }
//                            }
//                        }
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_Segment_FAIL, 0, 0, mFingerPosition));
//                        handleMsg("Segment fail!  Fake finger detected! ", Color.RED);
//
//                        isCaturing = false;
//                    }
//                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_TIMEOUT_OR_FORCESTOP_END
//                            || occurredEventCode == MultiFingerProcessResult.SEGMENT_FINGER_NUMBER_NOT_MATCH_FAIL) {
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_Segment_FAIL, 0, 0, mFingerPosition));
//                        if (numberOfSegment == SegmentImageDesc.ALG_SEGMENT_QUALITY_CHECK_FAIL) {
//                            handleMsg("Segment fail: Quality check fail.", Color.RED);
//                        } else if (numberOfSegment == SegmentImageDesc.ALG_SEGMENT_LEFT_RIGHT_FAIL) {
//                            handleMsg("Segment fail: Left or right hands error.", Color.RED);
//                        }
//                        isCaturing = false;
//                    }
//                }
//            });
//
//        } catch (TrustFingerException e) {
//            Log.e(TAG, "device open exception:" + e.getType().toString() + "");
//            e.printStackTrace();
//        }
//    }

    public void segmentFullImage() {
        isCaturing = true;
        mNumberOfSegment = 0;
        mSegmentImageDesc = null;
            mMissingFingerPos = 0;
        mTrustFingerDevice.setMissingFingers(mMissingFingerPos);

        if (mScannedObject >= SegmentImageDesc.ARAFPSCAN_SLAP_RIGHT_THUMB_FINGER && mScannedObject <= SegmentImageDesc.ARAFPSCAN_ROLL_LEFT_LITTLE_FINGER) {
            mSegmentSupportNum = 1;
        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_2_THUMBS_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_ANY_TWO_FINGERS) {
            mSegmentSupportNum = 2;
        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_RIGHT_FINGERS) {
            mSegmentSupportNum = 4;
            if (mMissingFingerPos != 0)
                mSegmentSupportNum--;
        }

        fpImage_Raw = null;
        fpImage_bmp = null;
        try {

            MultiFingerParam mParam = new MultiFingerParam(mScannedObject, 0, 2);
            mTrustFingerDevice.multiFingerCapture(mParam, new MultiFingerCallback() {
                @Override
                public void multiFingerCallback(int occurredEventCode, byte[] rawData, SegmentImageDesc[] segmentImageDesc, int numberOfSegment) {
                    if (occurredEventCode == MultiFingerProcessResult.ON_CAPTURE_IMAGE && rawData != null) {
                        byte[] bmpData = mTrustFingerDevice.rawToBmp(rawData, mTrustFingerDevice
                                .getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo()
                                .getHeight(), mTrustFingerDevice.getImageInfo().getResolution());
                        if (bmpData == null)
                            return;
                        Bitmap bmp = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
                        if (bmp != null) {
                            //          updateFingerprintImage(bmp);
                            //   interf.on_result_image_obtained(bmp);
                            interf.onGlobalImageObtained(bmp);
                        }
                    }
                    if (occurredEventCode == MultiFingerProcessResult.ON_SEGMENT_IMAGE && segmentImageDesc != null && numberOfSegment > 0) {
                        if (numberOfSegment == mSegmentSupportNum) {
                            fpImage_Raw = rawData;
                            mSegmentImageDesc = segmentImageDesc;
                            mNumberOfSegment = numberOfSegment;

                            byte[] bmpD;
                            for (int i = 0; i < numberOfSegment; i++) {
                                bmpD = mTrustFingerDevice.rawToBmp(segmentImageDesc[i].pSegmentImagePtr, segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500);
                                if (bmpD != null) {
                                    subSegmentBitmap[i] = BitmapFactory.decodeByteArray(bmpD, 0, bmpD.length);
                                } else {
//                                    Log.e(TAG, "rawToBmp null:" + i);
                                }
                            }
                        } else {
//                            handleMsg("Image Quality low, or Finger Number not enough.", Color.RED);
                            Log.e(logTag, "Image Quality low, or Finger Number not enough.");
                            interf.on_device_error("Image Quality low, or Finger Number not enough.");

                        }
                    }
                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_PROCESS_SUCCESS && segmentImageDesc != null && numberOfSegment > 0) {
                        if (mNumberOfSegment > 0 && mSegmentImageDesc != null) {
                            int imageQualitySub = 0;
                            for (int i = 0; i < mNumberOfSegment; i++) {

                                imageQualitySub = mSegmentImageDesc[i].nQuality;
                                if (subSegmentBitmap[i] != null && mSegmentImageDesc[i].pFeatureData != null) {
                                    FingerPosition fingerPosition = segment_run_counter == 0 ?
                                            i == 0 ? FingerPosition.LeftLittleFinger : i == 1 ? FingerPosition.LeftRingFinger : i == 2 ? FingerPosition.LeftMiddleFinger : FingerPosition.LeftIndexFinger :
                                            segment_run_counter == 1 ?
                                                    i == 0 ? FingerPosition.RightIndexFinger : i == 1 ? FingerPosition.RightMiddleFinger : i == 2 ? FingerPosition.RightRingFinger : FingerPosition.RightLittleFinger :
                                                    i == 0 ? FingerPosition.LeftThumb : FingerPosition.RightThumb ;

//                                    byte[] wsq = mTrustFingerDevice.rawToWsq(segmentImageDesc[i].pSegmentImagePtr,segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500 );
                                 //   byte[] iso = mTrustFingerDevice.rawToISO(segmentImageDesc[i].pSegmentImagePtr,segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500,fingerPosition , ImgCompressAlg.UNCOMPRESSED_BIT_PACKED );
                                    interf.on_result_image_obtained(i, subSegmentBitmap[i]);
//                                    interf.on_result_wsq_obtained(imageToWsq(subSegmentBitmap[i]));
//                                    interf.on_result_obtained(wsqToIso(imageToWsq(subSegmentBitmap[i])));
                                    interf.on_result_obtained(i, Base64.encodeToString(mSegmentImageDesc[i].pFeatureData, 0));
//                                    interf.on_result_obtained(i, Base64.encodeToString(iso, 0));


                                }


                                if (mSegmentImageDesc[i].pFeatureData == null) {
                                    Log.e(logTag, "FP Image segment failed!");
                                    interf.on_device_error("The system does not support simultaneous access to two devices!");


                                    isCaturing = false;
                                    return;
                                }
                            }
                            segment_run_counter++;
                            if (segment_run_counter >= segment_run_count.length) {
                                segment_run_counter = 0;
                                Log.e(logTag, "FP Image capture finished");
                                interf.on_device_status_changed("FP Image capture finished");
                                interf.onRegistrationComplete();
                            } else {
                                isCaturing = false;

                                new Handler(activity.getMainLooper()).postDelayed(() -> {
                                    interf.onRegistrationSegmentChanged(segment_run_count[segment_run_counter]);
                                    startFullCapture(segment_run_count[segment_run_counter]);

                                }, 2000);

                            }

                            Log.e(logTag, "Segment success ! " + mNumberOfSegment + " finger.");
                            interf.on_device_status_changed("Segment success ! " + mNumberOfSegment + " finger.");

                        } else {
                            Log.e(logTag, "Segment fail! ");
                            interf.on_device_status_changed("Segment fail ! ");
                        }
                        isCaturing = false;
                    }
                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_TIMEOUT_OR_FORCESTOP_END || occurredEventCode == MultiFingerProcessResult.SEGMENT_FINGER_NUMBER_NOT_MATCH_FAIL) {


                        Log.e(logTag, "Segment fail! ");
                        interf.on_device_status_changed("Segment fail ! ");

                    }
                }
            });

        } catch (TrustFingerException e) {

            Log.e(logTag, "Device fp capture exception: " + e.getType().toString());
            interf.on_device_status_changed("Device fp capture exception: " + e.getType().toString());
            e.printStackTrace();
        }
    }
   public void segmentFullImage_() {
        isCaturing = true;
        mNumberOfSegment = 0;
        mSegmentImageDesc = null;

        if (mScannedObject >= SegmentImageDesc.ARAFPSCAN_SLAP_RIGHT_THUMB_FINGER && mScannedObject <= SegmentImageDesc.ARAFPSCAN_ROLL_LEFT_LITTLE_FINGER) {
            mSegmentSupportNum = 1;
        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_2_THUMBS_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_ANY_TWO_FINGERS) {
            mSegmentSupportNum = 2;
        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_RIGHT_FINGERS) {
            mSegmentSupportNum = 4;
            if (mMissingFingerPos != 0)
                mSegmentSupportNum--;
        }

        fpImage_Raw = null;
        fpImage_bmp = null;
        try {

            MultiFingerParam mParam = new MultiFingerParam(mScannedObject, 0, 2);
//            mParam.setFeatureFormat();
            mTrustFingerDevice.multiFingerCapture(mParam, new MultiFingerCallback() {
                @Override
                public void multiFingerCallback(int occurredEventCode, byte[] rawData, SegmentImageDesc[] segmentImageDesc, int numberOfSegment) {
                    if (occurredEventCode == MultiFingerProcessResult.ON_CAPTURE_IMAGE && rawData != null) {
                        byte[] bmpData = mTrustFingerDevice.rawToBmp(rawData, mTrustFingerDevice
                                .getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo()
                                .getHeight(), mTrustFingerDevice.getImageInfo().getResolution());
                        if (bmpData == null)
                            return;
                        Bitmap bmp = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
                        if (bmp != null) {
                            //          updateFingerprintImage(bmp);
                            //   interf.on_result_image_obtained(bmp);
                            interf.onGlobalImageObtained(bmp);
                        }
                    }
                    if (occurredEventCode == MultiFingerProcessResult.ON_SEGMENT_IMAGE && segmentImageDesc != null && numberOfSegment > 0) {
                        if (numberOfSegment == mSegmentSupportNum) {
                            fpImage_Raw = rawData;
                            mSegmentImageDesc = segmentImageDesc;
                            mNumberOfSegment = numberOfSegment;

                            byte[] bmpD;
                            for (int i = 0; i < numberOfSegment; i++) {
                                bmpD = mTrustFingerDevice.rawToBmp(segmentImageDesc[i].pSegmentImagePtr, segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500);
                                if (bmpD != null) {
                                    subSegmentBitmap[i] = BitmapFactory.decodeByteArray(bmpD, 0, bmpD.length);
                                } else {
//                                    Log.e(TAG, "rawToBmp null:" + i);
                                }
                            }
                        } else {
//                            handleMsg("Image Quality low, or Finger Number not enough.", Color.RED);
                            Log.e(logTag, "Image Quality low, or Finger Number not enough.");
                            interf.on_device_error("Image Quality low, or Finger Number not enough.");

                        }
                    }
                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_PROCESS_SUCCESS && segmentImageDesc != null && numberOfSegment > 0) {
                        if (mNumberOfSegment > 0 && mSegmentImageDesc != null) {
                            int imageQualitySub = 0;
                            for (int i = 0; i < mNumberOfSegment; i++) {

                                imageQualitySub = mSegmentImageDesc[i].nQuality;
                                if (subSegmentBitmap[i] != null && mSegmentImageDesc[i].pFeatureData != null) {
                                    FingerPosition fingerPosition = segment_run_counter == 0 ?
                                            i == 0 ? FingerPosition.LeftLittleFinger : i == 1 ? FingerPosition.LeftRingFinger : i == 2 ? FingerPosition.LeftMiddleFinger : FingerPosition.LeftIndexFinger :
                                            segment_run_counter == 1 ?
                                                    i == 0 ? FingerPosition.RightIndexFinger : i == 1 ? FingerPosition.RightMiddleFinger : i == 2 ? FingerPosition.RightRingFinger : FingerPosition.RightLittleFinger :
                                                    i == 0 ? FingerPosition.LeftThumb : FingerPosition.RightThumb ;

                                    byte[] wsq = mTrustFingerDevice.rawToWsq(segmentImageDesc[i].pSegmentImagePtr,segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500 );
                                 //   byte[] iso = mTrustFingerDevice.rawToISO(segmentImageDesc[i].pSegmentImagePtr,segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500,fingerPosition , ImgCompressAlg.UNCOMPRESSED_BIT_PACKED );
                                    interf.on_result_image_obtained(i, subSegmentBitmap[i]);
//                                    interf.on_result_wsq_obtained(imageToWsq(subSegmentBitmap[i]));
//                                    interf.on_result_obtained(wsqToIso(imageToWsq(subSegmentBitmap[i])));
                                    interf.on_result_obtained(i, Base64.encodeToString(mSegmentImageDesc[i].pFeatureData, 0));
//                                    interf.on_result_obtained(i, Base64.encodeToString(iso, 0));


                                }


                                if (mSegmentImageDesc[i].pFeatureData == null) {
                                    Log.e(logTag, "FP Image segment failed!");
                                    interf.on_device_error("The system does not support simultaneous access to two devices!");


                                    isCaturing = false;
                                    return;
                                }
                            }
                            segment_run_counter++;
                            if (segment_run_counter >= segment_run_count.length) {
                                segment_run_counter = 0;
                                Log.e(logTag, "FP Image capture finished");
                                interf.on_device_status_changed("FP Image capture finished");
                                interf.onRegistrationComplete();
                            } else {
                                isCaturing = false;

                                new Handler(activity.getMainLooper()).postDelayed(() -> {
                                    interf.onRegistrationSegmentChanged(segment_run_count[segment_run_counter]);
                                    startFullCapture(segment_run_count[segment_run_counter]);

                                }, 2000);

                            }

                            Log.e(logTag, "Segment success ! " + mNumberOfSegment + " finger.");
                            interf.on_device_status_changed("Segment success ! " + mNumberOfSegment + " finger.");

                        } else {
                            Log.e(logTag, "Segment fail! ");
                            interf.on_device_status_changed("Segment fail ! ");
                        }
                        isCaturing = false;
                    }
                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_TIMEOUT_OR_FORCESTOP_END || occurredEventCode == MultiFingerProcessResult.SEGMENT_FINGER_NUMBER_NOT_MATCH_FAIL) {


                        Log.e(logTag, "Segment fail! ");
                        interf.on_device_status_changed("Segment fail ! ");

                    }
                }
            });

        } catch (TrustFingerException e) {

            Log.e(logTag, "Device fp capture exception: " + e.getType().toString());
            interf.on_device_status_changed("Device fp capture exception: " + e.getType().toString());
            e.printStackTrace();
        }
    }

    public void startCapture(int mScannedObject) {
        this.mScannedObject = mScannedObject;
        if (!isCaturing) {
            isCaturing = true;

//            if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS
//                    || mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_RIGHT_FINGERS) {
//                mTrustFingerDevice.setMissingFingers(mMissingFingerPos);
//            }
            SegmentImage();

        }

    }

    private Bitmap fpImage_bitmap = null;
    private byte[] fpImage_Raw = null;
    private byte[] fpImage_bmp = null;
    private SegmentImageDesc[] mSegmentImageDesc;
    private Bitmap[] subSegmentBitmap = new Bitmap[4];
    private int mNumberOfSegment = 0;
    private int mSegmentSupportNum = 0;

    public void SegmentImage() {
        isCaturing = true;
        mNumberOfSegment = 0;
        mSegmentImageDesc = null;
        mMissingFingerPos=0;
        mTrustFingerDevice.setMissingFingers(mMissingFingerPos);

        if (mScannedObject >= SegmentImageDesc.ARAFPSCAN_SLAP_RIGHT_THUMB_FINGER && mScannedObject <= SegmentImageDesc.ARAFPSCAN_ROLL_LEFT_LITTLE_FINGER) {
            mSegmentSupportNum = 1;
        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_2_THUMBS_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_ANY_TWO_FINGERS) {
            mSegmentSupportNum = 2;
        } else if (mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_LEFT_FINGERS || mScannedObject == SegmentImageDesc.ARAFPSCAN_SLAP_4_RIGHT_FINGERS) {
            mSegmentSupportNum = 4;
            if (mMissingFingerPos != 0)
                mSegmentSupportNum--;
        }

        fpImage_Raw = null;
        fpImage_bmp = null;
        try {

            MultiFingerParam mParam = new MultiFingerParam(mScannedObject, 0, 2);
            mTrustFingerDevice.multiFingerCapture(mParam, new MultiFingerCallback() {
                @Override
                public void multiFingerCallback(int occurredEventCode, byte[] rawData, SegmentImageDesc[] segmentImageDesc, int numberOfSegment) {
                    if (occurredEventCode == MultiFingerProcessResult.ON_CAPTURE_IMAGE && rawData != null) {
                        byte[] bmpData = mTrustFingerDevice.rawToBmp(rawData, mTrustFingerDevice
                                .getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo()
                                .getHeight(), mTrustFingerDevice.getImageInfo().getResolution());
                        if (bmpData == null)
                            return;
                        Bitmap bmp = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
                        if (bmp != null) {
                            //          updateFingerprintImage(bmp);
                            //   interf.on_result_image_obtained(bmp);
                            interf.onGlobalImageObtained(bmp);
                        }
                    }
                    if (occurredEventCode == MultiFingerProcessResult.ON_SEGMENT_IMAGE && segmentImageDesc != null && numberOfSegment > 0) {
                        if (numberOfSegment == mSegmentSupportNum) {
                            fpImage_Raw = rawData;
                            mSegmentImageDesc = segmentImageDesc;
                            mNumberOfSegment = numberOfSegment;

                            byte[] bmpD;
                            for (int i = 0; i < numberOfSegment; i++) {
                                bmpD = mTrustFingerDevice.rawToBmp(segmentImageDesc[i].pSegmentImagePtr, segmentImageDesc[i].nFingerwidth, segmentImageDesc[i].nFingerheight, 500);
                                if (bmpD != null) {
                                    subSegmentBitmap[i] = BitmapFactory.decodeByteArray(bmpD, 0, bmpD.length);
                                } else {
//                                    Log.e(TAG, "rawToBmp null:" + i);
                                }
                            }
                        } else {
//                            handleMsg("Image Quality low, or Finger Number not enough.", Color.RED);
                            Log.e(logTag, "Image Quality low, or Finger Number not enough.");
                            interf.on_device_error("Image Quality low, or Finger Number not enough.");

                        }
                    }
                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_PROCESS_SUCCESS && segmentImageDesc != null && numberOfSegment > 0) {
                        if (mNumberOfSegment > 0 && mSegmentImageDesc != null) {
                            int imageQualitySub = 0;
                            for (int i = 0; i < mNumberOfSegment; i++) {

                                imageQualitySub = mSegmentImageDesc[i].nQuality;
                                if (subSegmentBitmap[i] != null && mSegmentImageDesc[i].pFeatureData != null) {
//                                    Log.i("Sanny",", subSegmentBitmap save  i:"+i);
                                    interf.on_result_image_obtained(i, subSegmentBitmap[i]);
//                                    interf.on_result_wsq_obtained(imageToWsq(subSegmentBitmap[i]));
//                                    interf.on_result_obtained(wsqToIso(imageToWsq(subSegmentBitmap[i])));
                                    interf.on_result_obtained(Base64.encodeToString(mSegmentImageDesc[i].pFeatureData, 0));


                                }


                                if (mSegmentImageDesc[i].pFeatureData == null) {
                                    Log.e(logTag, "FP Image segment failed!");
                                    interf.on_device_error("The system does not support simultaneous access to two devices!");


                                    isCaturing = false;
                                    return;
                                }
                            }

                            Log.e(logTag, "Segment success ! " + mNumberOfSegment + " finger.");
                            interf.on_device_status_changed("Segment success ! " + mNumberOfSegment + " finger.");

                        } else {
                            Log.e(logTag, "Segment fail! ");
                            interf.on_device_status_changed("Segment fail ! ");
                        }
                        isCaturing = false;
                    }
                    if (occurredEventCode == MultiFingerProcessResult.SEGMENT_TIMEOUT_OR_FORCESTOP_END || occurredEventCode == MultiFingerProcessResult.SEGMENT_FINGER_NUMBER_NOT_MATCH_FAIL) {


                        Log.e(logTag, "Segment fail! ");
                        interf.on_device_status_changed("Segment fail ! ");

                    }
                }
            });

        } catch (TrustFingerException e) {

            Log.e(logTag, "Device fp capture exception: " + e.getType().toString());
            interf.on_device_status_changed("Device fp capture exception: " + e.getType().toString());
            e.printStackTrace();
        }
    }


    void close() {
        if (isDeviceOpened) {

            if (isCaturing) {

                if (mTrustFingerDevice != null) {
                    mTrustFingerDevice.forceStopThreadTask();
                }
                isCaturing = false;

            }


            try {
                if (mTrustFingerDevice.getDeviceDescription().getDeviceId() == 600) {
                    if (mTrustFingerDevice.getLedStatus(LedIndex.RED) != LedStatus.CLOSE) {
                        mTrustFingerDevice.setLedStatus(LedIndex.RED, LedStatus.CLOSE);
                    }
                    if (mTrustFingerDevice.getLedStatus(LedIndex.GREEN) != LedStatus
                            .CLOSE) {
                        mTrustFingerDevice.setLedStatus(LedIndex.GREEN, LedStatus.CLOSE);
                    }
                }
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                mTrustFingerDevice.setDryFingerLevel(0);
                mTrustFinger.closeAllDev();
                mTrustFingerDevice = null;
                isDeviceOpened = false;

            } catch (TrustFingerException e) {
                Log.e(logTag, "Close device failed!\n" + e.getType().toString());
                interf.on_device_error("Close device failed!\n" + e.getType().toString());

                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        openDevice();

    }

    @Override
    public void stop() {
        super.stop();
        close();
    }
}
