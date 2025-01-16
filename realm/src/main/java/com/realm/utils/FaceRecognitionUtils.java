package com.realm.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.luxand.FSDK;

import com.realm.utils.biometrics.DataMatcher;
import com.realm.utils.biometrics.MatchingInterface;
import com.realm.utils.biometrics.face.CaptureHandler;
import com.realm.utils.biometrics.face.Preview;
import com.realm.utils.biometrics.face.VerificationModel;


public class FaceRecognitionUtils {
    Context context;
    VerificationModel verificationModel;
    String dataFile;
public FaceRecognitionUtils(Context context){
    this.context = context;
}
public FaceRecognitionUtils(Context context,VerificationModel verificationModel){
    this.context = context;
    this.verificationModel = verificationModel;
}
public FaceRecognitionUtils(Context context,VerificationModel verificationModel,String dataFile){
    this.context = context;
    this.verificationModel = verificationModel;
    this.dataFile = dataFile;
}

    private void resetTrackerParameters(FSDK.HTracker tracker) {
        int errpos[] = new int[1];
//        FSDK.SetTrackerMultipleParameters(tracker, "ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        FSDK.SetTrackerMultipleParameters(tracker, "DetectGender=true;DetectAge=true;ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }
    public void showErrorAndClose(String error, int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(error + ": " + code)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    //android.os.Process.killProcess(android.os.Process.myPid());
                })
                .show();
    }
    public static float sDensity = 1.0f;
    private boolean mIsFailed = false;
    public interface MatchFoundInterface{
        void onMachFound(String member);
    }

    public void initFaceRecognition(CaptureHandler faceGraphicsOverlay, Preview faceGraphicsPreview, MatchFoundInterface matchFoundInterface) {
        sDensity = context.getResources().getDisplayMetrics().scaledDensity;

        int res = 0;
        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            showErrorAndClose("FaceSDK activation failed", res);
        } else {
            FSDK.Initialize();


            faceGraphicsOverlay.setVisibility(View.VISIBLE);
            faceGraphicsPreview.setVisibility(View.VISIBLE);
            faceGraphicsOverlay.vt = DataMatcher.verification_type.verification;


            faceGraphicsOverlay.setup_handler(new MatchingInterface() {
                @Override
                public void on_match_complete(boolean match_found, String mils) {

                }

                @Override
                public void on_match_found(String employee_transaction_no, String data_index, String match_time, int v_type, int verrification_mode) {
//                    VerificationMemberModel m = Realm.databaseManager.loadObject(Member.class, new Query().setTableFilters("transaction_no='" + employee_transaction_no + "'"));
//                    m.profile_photo= Realm.databaseManager.loadObject(MemberImage.class, new Query().setTableFilters("parent_transaction_no='" + employee_transaction_no + "'"));
//
//                    if (m != null) {
                        matchFoundInterface.onMachFound(employee_transaction_no);
//                    }


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

            faceGraphicsPreview.setup_overlay(faceGraphicsOverlay, 1, 90);//face :0 for back and 1 for front

            faceGraphicsOverlay.mTracker = new FSDK.HTracker();
            faceGraphicsOverlay.vm = verificationModel;
            String templatePath = "";

//            svars.set_current_device(act, svars.DEVICE.GENERAL.ordinal());

            templatePath = context.getApplicationInfo().dataDir + "/" + dataFile;

            if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(faceGraphicsOverlay.mTracker, templatePath)) {
                res = FSDK.CreateTracker(faceGraphicsOverlay.mTracker);
                if (FSDK.FSDKE_OK != res) {
                    showErrorAndClose("Error creating tracker", res);
                }
            }

            resetTrackerParameters(faceGraphicsOverlay.mTracker);
        }
    }





}
