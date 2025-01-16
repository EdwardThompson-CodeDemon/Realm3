package com.realm.utils.biometrics.face;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.luxand.FSDK;

import java.io.File;

import com.realm.R;
import com.realm.Realm;
import com.realm.utils.svars;


public class SpartaFaceCamera extends Activity {
    private Preview mPreview;
    public CaptureHandler mDraw;
    private final String database = "MemoryR080.dat";
    public static float sDensity = 1.0f;
    private boolean mIsFailed = false;
    String sid = null;
    Button capture, generate;
    int ok_count = 0;
    int ok_max_count = 100;
    int camera_index = 1;//face :0 for back and 1 for front
    int camera_rotation = 270;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_sparta_face_camera);
        capture = findViewById(R.id.capture);
        generate = findViewById(R.id.generate_templates);
        camera_index = getIntent().getIntExtra("camera_index", camera_index);
        camera_rotation = getIntent().getIntExtra("camera_rotation", camera_rotation);


        sDensity = getResources().getDisplayMetrics().scaledDensity;

        FSDK.Initialize();

        if ((sid = getIntent().getStringExtra("sid")) == null) {
            Toast.makeText(this, "Transaction no aint available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Camera layer and drawing layer
        mDraw = findViewById(R.id.face_graphics_overlay);
        mPreview = findViewById(R.id.face_graphics_preview);
        mDraw.setVisibility(View.VISIBLE);
        mPreview.setVisibility(View.VISIBLE);

        mDraw.transaction_no = sid;
        mDraw.sDensity = sDensity;
        mDraw.captureMode = CaptureHandler.CaptureMode.Registration;
        mDraw.replaceFromTrackerOnRegistration= getIntent().getBooleanExtra("replaceFromTrackerOnRegistration", false);
        mDraw.replaceOnRegistration= getIntent().getBooleanExtra("replaceOnRegistration", false);

        mDraw.vm=new VerificationModel();
        mDraw.cpi = new CaptureHandler.capturing_interface() {
            @Override
            public void OnOkToCapture() {
                capture.post(new Runnable() {
                    @Override
                    public void run() {
                        //   ok_count++;
                        capture.setVisibility(View.VISIBLE);
//                mDraw.capture=true;
                    }
                });

            }

            @Override
            public void OnOkToCapture(int gender, float age) {
                mDraw.capture = true;
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
                Intent data = new Intent();
                data.putExtra("ImageUrl", path);
                setResult(Activity.RESULT_OK, data);
                finish();
            }

            @Override
            public void OnCaptured(String path, int gender, float age) {
      /* closeCamera();
                    stopBackgroundThread();*/
                mDraw.capture = false;
                Intent data = new Intent();
                data.putExtra("ImageUrl", path);
                data.putExtra("Age", age);
                data.putExtra("Gender", gender);
                setResult(Activity.RESULT_OK, data);
                // finish();
            }
        };

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDraw.capture = true;
            }
        });
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File main_file = new File(Environment.getExternalStorageDirectory().toString() + "/realm_BUKINA/.RAW_EMPLOYEE_DATA/");
                if (main_file.exists()) {
                    File[] images = main_file.listFiles();
                    Log.e("Number of Images :", "" + images.length);
                    for (int i = 0; i < images.length; i++) {
                        mDraw.generate_templates(images[i].getAbsolutePath());
                    }
                }
            }
        });

        mPreview.setup_overlay(mDraw, camera_index, camera_rotation);
        mDraw.mTracker = new FSDK.HTracker();
//        int[] err=new int[2];
//        int err=0;
//        Log.e("SET PARAMs :"," AGE : "+  FSDK.SetTrackerMultipleParameters(mDraw.mTracker,"DetectGender=true;DetectAge=true;",err)+"   Err :"+err[0]);
//        "Parameter1=Value1[;Parameter2=Value2[;…]]"  //format
//        String templatePath = this.getApplicationInfo().dataDir + "/" + database;
        String templatePath = this.getApplicationInfo().dataDir + "/" + database;
        int load_tracker = FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath);
        if (FSDK.FSDKE_OK != load_tracker) {


            if (FSDK.FSDKE_OK != FSDK.CreateTracker(mDraw.mTracker)) {
                Log.e("Error creating tracker", "Errror");
                finish();
            }
            //       Log.e("SET PARAM :"," GENDER : "+   FSDK.SetTrackerParameter(mDraw.mTracker,"DetectGender", "1"));
            //        Log.e("SET PARAM :"," AGE : "+  FSDK.SetTrackerParameter(mDraw.mTracker,"DetectAge", "true"));

            int[] err = new int[2];
//        int err=0;
            Log.e("SET PARAMs :", " AGE : " + FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "DetectGender=true;DetectAge=true;", err) + "   Err :" + err[0]);
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

    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "DetectGender=true;DetectAge=true;ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        if (errpos[0] != 0) {
            Log.e("Error", " setting tracker parameters, position" + errpos[0]);
        }
    }

}
