package com.realm.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.luxand.FSDK;

import com.realm.utils.biometrics.DataMatcher;
import com.realm.utils.biometrics.MatchingInterface;
import com.realm.utils.biometrics.face.CaptureHandler;
import com.realm.utils.biometrics.face.Preview;
import com.realm.utils.biometrics.face.VerificationModel;

public class RealmFaceAndFpVerificationActivity extends SpartaAppCompactFingerPrintActivity {
    private boolean mIsFailed = false;
    private Preview mPreview;
    private CaptureHandler mDraw;
    private String databaseName;
    public static float sDensity = 1.0f;
    private DataMatcher.verification_type verificationType;
    private VerificationModel verificationModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    boolean face_initialized = false;

    public void initFaceRecognition(int cameraIndex, int cameraRotation, Preview preview, CaptureHandler captureHandler, String databaseName, DataMatcher.verification_type verificationType, VerificationModel verificationModel) {
        this.mPreview = preview;
        this.mDraw = captureHandler;
        this.databaseName = databaseName;
        this.verificationType = verificationType;
        this.verificationModel = verificationModel;

        face_initialized = true;
        sDensity = getResources().getDisplayMetrics().scaledDensity;
        int res = 0;
        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            showErrorAndClose("FaceSDK activation failed", res);
        } else {
            FSDK.Initialize();
            mDraw.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.VISIBLE);
            mDraw.vt = verificationType;


            mDraw.setup_handler(new MatchingInterface() {
                @Override
                public void on_match_complete(boolean match_found, String mils) {

                }

                @Override
                public void on_match_found(String sid, String data_index, String match_time, int v_type, int verrification_mode) {

                    onMatchFound(sid, data_index, match_time, v_type, verrification_mode);
                }

                @Override
                public void on_finger_match_found(String fp_id, int score, String match_time) {

                }


                @Override
                public void on_match_progress_changed(int progress) {

                }

                @Override
                public void on_match_faild_reason_found(int reason, String employee_id) {

                }
            });


            mPreview.setup_overlay(mDraw, cameraIndex, cameraRotation);//face :0 for back and 1 for front

            mDraw.mTracker = new FSDK.HTracker();
            mDraw.vm = verificationModel;
            String templatePath = this.getApplicationInfo().dataDir + "/" + databaseName;

            if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
                res = FSDK.CreateTracker(mDraw.mTracker);
                if (FSDK.FSDKE_OK != res) {
                    showErrorAndClose("Error creating tracker", res);
                }
            }

            resetTrackerParameters();

        }
    }



    @Override
    public void onPause() {
        super.onPause();
        if (face_initialized) {
            pauseProcessingFrames();
            String templatePath = this.getApplicationInfo().dataDir + "/" + databaseName;
            FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, templatePath);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (face_initialized) {
            if (mIsFailed) return;
            resumeProcessingFrames();
        }

    }

    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "DetectGender=true;DetectAge=true;ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }

    public void showErrorAndClose(String error, int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error + ": " + code)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .show();
    }

    private void pauseProcessingFrames() {
        mDraw.mStopping = 1;
        for (int i = 0; i < 100; ++i) {
            if (mDraw.mStopped != 0) break;
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
            }
        }
    }

    private void resumeProcessingFrames() {
        mDraw.mStopped = 0;
        mDraw.mStopping = 0;
    }
}