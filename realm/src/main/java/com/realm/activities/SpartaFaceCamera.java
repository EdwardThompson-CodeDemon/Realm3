package com.realm.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.luxand.FSDK;

import com.realm.R;
import com.realm.utils.biometrics.face.CaptureHandler;
import com.realm.utils.biometrics.face.Preview;


public class SpartaFaceCamera extends AppCompatActivity {
    private Preview mPreview;
    private CaptureHandler mDraw;
    private final String database = "Memory50.dat";
    public static float sDensity = 1.0f;
    private boolean mIsFailed = false;
String sid=null;
Button capture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_sparta_face_camera);
        capture=findViewById(R.id.capture);


    sDensity = getResources().getDisplayMetrics().scaledDensity;



        FSDK.Initialize();

        // Hide the window title (it is done in manifest too)


if((sid=getIntent().getStringExtra("sid"))==null){
    finish();
    return;
}

      // Camera layer and drawing layer
        mDraw =findViewById(R.id.face_graphics_overlay);
        mPreview =findViewById(R.id.face_graphics_preview);
        mDraw.setVisibility(View.VISIBLE);
        mPreview.setVisibility(View.VISIBLE);

mDraw.transaction_no=sid;
mDraw.cpi=new CaptureHandler.capturing_interface() {
    @Override
    public void OnOkToCapture() {
        capture.post(new Runnable() {
            @Override
            public void run() {
                capture.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void OnOkToCapture(int gender, float age) {

    }

    @Override
    public void OnNotOkToCapture() {
        capture.post(new Runnable() {
            @Override
            public void run() {
                capture.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void OnCaptured(String path) {
      /* closeCamera();
                    stopBackgroundThread();*/

        Intent data=new Intent();
        data.putExtra("ImageUrl", path);
       setResult(Activity.RESULT_OK,data);
        finish();
    }

    @Override
    public void OnCaptured(String path, int gender, float age) {

    }
};

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
mDraw.capture=true;
            }
        });

        mPreview.setup_overlay(mDraw);
        mDraw.mTracker = new FSDK.HTracker();
        String templatePath = this.getApplicationInfo().dataDir + "/" + database;
        if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {


            if (FSDK.FSDKE_OK !=  FSDK.CreateTracker(mDraw.mTracker)) {
                Log.e("Error creating tracker","Errror");
                finish();
            }
        }

        resetTrackerParameters();




}



    @Override
    public void onPause() {
        super.onPause();
        pauseProcessingFrames();
        String templatePath = this.getApplicationInfo().dataDir + "/" + database;
        FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, templatePath);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFailed)
            return;
        resumeProcessingFrames();
    }

    private void pauseProcessingFrames() {
        mDraw.mStopping = 1;

        // It is essential to limit wait time, because mStopped will not be set to 0, if no frames are feeded to mDraw
        for (int i=0; i<100; ++i) {
            if (mDraw.mStopped != 0) break;
            try { Thread.sleep(10); }
            catch (Exception ex) {}
        }
    }

    private void resumeProcessingFrames() {
        mDraw.mStopped = 0;
        mDraw.mStopping = 0;
    }

    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        if (errpos[0] != 0) {
            Log.e("Error"," setting tracker parameters, position"+ errpos[0]);
        }
    }

}
