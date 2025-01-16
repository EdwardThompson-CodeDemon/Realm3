package com.realm.utils.biometrics.fp;

import android.graphics.Bitmap;

public interface sfp_i {

    void onRegistrationComplete();
    void onRegistrationSegmentChanged(int segment_type);

    void on_result_obtained(int fp_index, String capt_result);

    void onGlobalImageObtained(Bitmap image);

    void on_result_image_obtained(int fp_index, Bitmap capt_result_img);

    void on_result_wsq_obtained(int fp_index, byte[] wsq);





    void onDeviceStarted();

    void onDeviceClosed();

    void on_result_obtained(String capt_result);

    void on_result_image_obtained(Bitmap capt_result_img);

    void on_result_wsq_obtained(byte[] wsq);

    void on_result_error(String capt_error);

    void on_device_error(String device_error);

    void on_device_status_changed(String status);

    void on_result_image_error(String s);
}
