package com.realm.utils.biometrics.face;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.luxand.FSDK;
import com.realm.annotations.RealmModel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import com.realm.R;

import com.realm.Realm;


import com.realm.utils.biometrics.DataMatcher;
import com.realm.utils.biometrics.MatchingInterface;
import com.realm.utils.svars;


// Draw graphics on top of the video
public class CaptureHandler extends View {
    public FSDK.HTracker mTracker;

    final int MAX_FACES = 5;
    final FaceRectangle[] mFacePositions = new FaceRectangle[MAX_FACES];
    final long[] mIDs = new long[MAX_FACES];
    final Lock faceLock = new ReentrantLock();
    int mTouchedIndex;
    long mTouchedID;
    public int mStopping;
    public int mStopped;
    public DataMatcher.verification_type vt;

    Context mContext;
    Paint mPaintAccent, mPaintBlue, mPaintGREEN, mPaintGREENbox, mPaintBlueTransparent;
    public byte[] mYUVData;
    public byte[] mRGBData;
    public int mImageWidth;
    public int mImageHeight;
    boolean first_frame_saved;
    public boolean rotated;
    public boolean mirror_image=false;
    float sDensity = 1.0f;

    public interface capturing_interface {
        void OnOkToCapture();

        void OnOkToCapture(int gender, float age);

        void OnNotOkToCapture();

        void OnCaptured(String path);

        void OnCaptured(String path, int gender, float age);
    }

    public enum CaptureMode {
        Registration,
        Verification
    }

    public CaptureMode captureMode = CaptureMode.Verification;
    public VerificationModel vm = new VerificationModel();
    public boolean replaceFromTrackerOnRegistration = false;//replace or ignoor
    public boolean replaceOnRegistration = true;//replace or ignoor
    public capturing_interface cpi = new capturing_interface() {
        @Override
        public void OnOkToCapture() {

        }

        @Override
        public void OnOkToCapture(int gender, float age) {

        }

        @Override
        public void OnNotOkToCapture() {

        }

        @Override
        public void OnCaptured(String path) {

        }

        @Override
        public void OnCaptured(String path, int gender, float age) {

        }
    };

    int GetFaceFrame(FSDK.FSDK_Features Features, FaceRectangle fr) {
        if (Features == null || fr == null)
            return FSDK.FSDKE_INVALID_ARGUMENT;

        float u1 = Features.features[0].x;
        float v1 = Features.features[0].y;
        float u2 = Features.features[1].x;
        float v2 = Features.features[1].y;
        float xc = (u1 + u2) / 2;
        float yc = (v1 + v2) / 2;
        int w = (int) Math.pow((u2 - u1) * (u2 - u1) + (v2 - v1) * (v2 - v1), 0.5);

        fr.x1 = (int) (xc - w * 1.6 * 0.9);
        fr.y1 = (int) (yc - w * 1.1 * 0.9);
        fr.x2 = (int) (xc + w * 1.6 * 0.9);
        fr.y2 = (int) (yc + w * 2.1 * 0.9);
        if (fr.x2 - fr.x1 > fr.y2 - fr.y1) {
            fr.x2 = fr.x1 + fr.y2 - fr.y1;
        } else {
            fr.y2 = fr.y1 + fr.x2 - fr.x1;
        }
        return 0;
    }


    public CaptureHandler(Context context) {
        super(context);
        init(context);


    }

    public void init(Context context) {
        mTouchedIndex = -1;

        mStopping = 0;
        mStopped = 0;
        rotated = false;
        mContext = context;
        mPaintAccent = new Paint();
        mPaintAccent.setStyle(Paint.Style.FILL);
        mPaintAccent.setColor(context.getColor(R.color.steelblue));
        mPaintAccent.setTextSize(18 * sDensity);
        mPaintAccent.setTextAlign(Paint.Align.CENTER);
        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setTextSize(18 * sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintGREEN = new Paint();
        mPaintGREEN.setStyle(Paint.Style.FILL);
        mPaintGREEN.setColor(Color.GREEN);
        mPaintGREEN.setTextSize(18 * sDensity);
        mPaintGREEN.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(2);
        mPaintBlueTransparent.setColor(Color.BLUE);
        mPaintBlueTransparent.setTextSize(25);

        mPaintGREENbox = new Paint();
        mPaintGREENbox.setStyle(Paint.Style.STROKE);
        mPaintGREENbox.setStrokeWidth(2);
        mPaintGREENbox.setColor(Color.GREEN);
        mPaintGREENbox.setTextSize(25);

        //mBitmap = null;
        mYUVData = null;
        mRGBData = null;

        first_frame_saved = false;
    }

    MatchingInterface main_MatchingInterface = new MatchingInterface() {
        @Override
        public void on_match_complete(boolean match_found, String mils) {

        }

        @Override
        public void on_match_found(String employee_id, String data_index, String match_time, int v_type, int verification_mode) {

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
    };

    public void setup_handler(MatchingInterface main_MatchingInterface) {
        this.main_MatchingInterface = main_MatchingInterface;

    }

    public CaptureHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CaptureHandler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CaptureHandler(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
//	sdbw sd=new sdbw(SpartaApplication.getAppContext());

    DataMatcher dm = new DataMatcher();

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStopping == 1) {
            mStopped = 1;
            super.onDraw(canvas);
            return;
        }

        if (mYUVData == null || mTouchedIndex != -1) {
            super.onDraw(canvas);
            cpi.OnNotOkToCapture();
            return; //nothing to process or name is being entered now
        }

        int canvasWidth = canvas.getWidth();
        //int canvasHeight = canvas.getHeight();

        // Convert from YUV to RGB
        decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);

        // Load image to FaceSDK
        FSDK.HImage Image = new FSDK.HImage();
        FSDK.FSDK_IMAGEMODE imagemode = new FSDK.FSDK_IMAGEMODE();
        imagemode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
        FSDK.LoadImageFromBuffer(Image, mRGBData, mImageWidth, mImageHeight, mImageWidth * 3, imagemode);
        FSDK.MirrorImage(Image, false);
        FSDK.HImage RotatedImage = new FSDK.HImage();
        //	FSDK.HImage s_img = new FSDK.HImage();
        FSDK.CreateEmptyImage(RotatedImage);

        //it is necessary to work with local variables (onDraw called not the time when mImageWidth,... being reassigned, so swapping mImageWidth and mImageHeight may be not safe)
        int ImageWidth = mImageWidth;
        //int ImageHeight = mImageHeight;
        if (rotated) {
            ImageWidth = mImageHeight;
            FSDK.RotateImage90(Image, -1, RotatedImage);
            if(mirror_image){
                FSDK.MirrorImage(RotatedImage, false);
            }
        } else {
            FSDK.CopyImage(Image, RotatedImage);
        }
        FSDK.FreeImage(Image);

        // Save first frame to gallery to debug (e.g. rotation angle)
		/*
		if (!first_frame_saved) {
			first_frame_saved = true;
			String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			FSDK.SaveImageToFile(RotatedImage, galleryPath + "/first_frame.jpg"); //frame is rotated!
		}
		*/

        long IDs[] = new long[MAX_FACES];
        long face_count[] = new long[1];

        FSDK.FeedFrame(mTracker, 0, RotatedImage, face_count, IDs);
        //FSDK.FreeImage(RotatedImage);
        if ((int) face_count[0] < 1) {
            cpi.OnNotOkToCapture();

        }
        faceLock.lock();
        Boolean male = false;
        float age = 0F;
        for (int i = 0; i < MAX_FACES; ++i) {
            mFacePositions[i] = new FaceRectangle();
            mFacePositions[i].x1 = 0;
            mFacePositions[i].y1 = 0;
            mFacePositions[i].x2 = 0;
            mFacePositions[i].y2 = 0;
            mIDs[i] = IDs[i];
        }

        float ratio = (canvasWidth * 1.0f) / ImageWidth;
        for (int i = 0; i < (int) face_count[0]; ++i) {
            FSDK.FSDK_Features Eyes = new FSDK.FSDK_Features();
            FSDK.GetTrackerEyes(mTracker, 0, mIDs[i], Eyes);
            String[] Gender_values = new String[1];
            String[] Age_values = new String[1];
            FSDK.GetTrackerFacialAttribute(mTracker, 0, mIDs[i], "Gender", Gender_values, 100);
            FSDK.GetTrackerFacialAttribute(mTracker, 0, mIDs[i], "Age", Age_values, 100);
//	FSDK.DetectFacialAttributeUsingFeatures(mTracker, Eyes, "Gender",Attribute_values,100);
            for (int z = 0; z < Gender_values.length; z++) {
                Log.e("Attributes " + z, "GEN :" + Gender_values[z]);
                double ml = Double.parseDouble(Gender_values[z].split(";")[0].split("=")[1]);
                double fm = Double.parseDouble(Gender_values[z].split(";")[1].split("=")[1]);
                male = ml > fm;
//	GEN :Male=0.88976; Female=0.11024

            }
            for (int z = 0; z < Age_values.length; z++) {
                Log.e("Attributes " + z, "Age :" + Age_values[z]);
                age = Float.parseFloat(Age_values[z].split("=")[1]);
            }
            GetFaceFrame(Eyes, mFacePositions[i]);
            mFacePositions[i].x1 *= ratio;
            mFacePositions[i].y1 *= ratio;
            mFacePositions[i].x2 *= ratio;
            mFacePositions[i].y2 *= ratio;
        }

        faceLock.unlock();

        int shift = (int) (22 * sDensity);
        int shift2 = (int) (22 * sDensity);

        // Mark and name faces
        for (int i = 0; i < face_count[0]; ++i) {
//            draw_circle_success(new Rect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2), canvas);
//			canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2, mPaintBlueTransparent);

            boolean named = false;
            if (IDs[i] != -1) {
                String names[] = new String[1];
                FSDK.GetAllNames(mTracker, IDs[i], names, 1024);
                if (names[0] != null && names[0].length() > 0) {
//					member discovered_member = Realm.databaseManager.load_employee(names[0]);
                    draw_circle_success(new Rect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2), canvas);
                    VerificationMemberModel discovered_member = vm.loadMember(names[0]);
                    if (discovered_member == null) {
                        if (captureMode == CaptureMode.Registration && transaction_no != null) {
                            if (replaceFromTrackerOnRegistration) {
                                FSDK.PurgeID(mTracker, IDs[i]);

//                                canvas.drawText(IDs[0] + " Your previous record is being removed", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                                canvas.drawText(" Your previous record is being removed", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                            } else {

                                cpi.OnNotOkToCapture();
//                                cpi.OnOkToCapture(male ? 1 : 0, age);
//							      canvas.drawText(IDs[0] + " Person exists", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                                canvas.drawText("Person exists", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                            }
                        }
                        named = true;

                    } else {
                        if (captureMode == CaptureMode.Registration && transaction_no != null) {
                            if(replaceOnRegistration){
                                FSDK.PurgeID(mTracker, IDs[i]);

                                canvas.drawText("Your previous record is being removed", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);


                            }else {
                                canvas.drawText(discovered_member.displayName + "", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                                if (vt == DataMatcher.verification_type.verification) {
                                    main_MatchingInterface.on_match_found(discovered_member.sid, "" + IDs[0], "00", vt.ordinal(), 4);

                                }
                            }

                        } else if(vt == DataMatcher.verification_type.clock_in || vt == DataMatcher.verification_type.clock_out) {
                            if (vm.canClock(vt == DataMatcher.verification_type.clock_in, discovered_member.sid)) {
                                main_MatchingInterface.on_match_found(discovered_member.sid, "" + IDs[0], "00", vt.ordinal(), 4);
                                canvas.drawText(discovered_member.displayName + "", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                            } else {
//								canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2, mPaintGREENbox);
                                canvas.drawText(discovered_member.displayName + " " + (vt == DataMatcher.verification_type.clock_in ? "Clocked in" : "Clocked out"), (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintGREEN);

                            }
                        } else {
                            canvas.drawText(discovered_member.displayName + "", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                            if (vt == DataMatcher.verification_type.verification) {
                                main_MatchingInterface.on_match_found(discovered_member.sid, "" + IDs[0], "00", vt.ordinal(), 4);

                            }
                        }

//                        if (captureMode == CaptureMode.Registration && transaction_no != null) {
//                            cpi.OnOkToCapture();
//                            cpi.OnOkToCapture(male ? 1 : 0, age);
//                            if (capture) {
//                                save_face(RotatedImage, active_false_id, transaction_no, male ? 1 : 0, age);
//                                capture = false;
//                            }
//                        } else {
//
//                        }
                        named = true;
                    }
                }
            }
            if (!named) {
//                draw_circle_normal(new Rect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2), canvas);
                draw_circle_unknown(new Rect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2), canvas);
                if (IDs[0] == active_false_id) {

                    false_count++;
                    if (false_count >= max_false_count) {
                        if (captureMode == CaptureMode.Verification) {
                            if (!searching) {
                                searching = true;
//								search_face(RotatedImage,active_false_id,canvas,(mFacePositions[i].x1+mFacePositions[i].x2)/2,mFacePositions[i].y2+shift, mPaintAccent);
                                canvas.drawText("Cant Recognize you", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintAccent);
                            } else {
//                                canvas.drawText(IDs[0] + "Cant Recognize you", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintAccent);
                                canvas.drawText("Cant Recognize you", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintAccent);

                            }
                        } else if (captureMode == CaptureMode.Registration) {
                            if (transaction_no != null) {
                                cpi.OnOkToCapture();
                                cpi.OnOkToCapture(male ? 1 : 0, age);
                                canvas.drawText("" + (int) age + " Year Old " + (male ? "Male" : "Female"), (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintAccent);
                                if (capture) {
                                    save_face(RotatedImage, active_false_id, transaction_no, male ? 1 : 0, age);
//								save_face(RotatedImage, active_false_id, transaction_no);
                                    capture = false;
                                }
                            } else {

                            }
                            //
                        }
                        //	false_count=0;

                    } else {
                        canvas.drawText(IDs[0] + " :: Learning ..", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintAccent);
                        cpi.OnNotOkToCapture();

                    }
                } else {
                    cpi.OnNotOkToCapture();
                    canvas.drawText(IDs[0] + " :: Learning ..", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintAccent);
                    active_false_id = IDs[0];
                    false_count = 0;
                }

            }
        }
        FSDK.FreeImage(RotatedImage);

        super.onDraw(canvas);
    } // end onDraw method

    public void generate_templates(String path) {
        FSDK.HImage Image = new FSDK.HImage();
        FSDK.LoadImageFromFile(Image, path);

        long IDs[] = new long[MAX_FACES];
        long face_count[] = new long[1];

        FSDK.FeedFrame(mTracker, 0, Image, face_count, IDs);


        faceLock.lock();
        Boolean male = false;
        Float age = 0F;
        for (int i = 0; i < MAX_FACES; ++i) {
            mFacePositions[i] = new FaceRectangle();
            mFacePositions[i].x1 = 0;
            mFacePositions[i].y1 = 0;
            mFacePositions[i].x2 = 0;
            mFacePositions[i].y2 = 0;
            mIDs[i] = IDs[i];
        }

        //float ratio = (canvasWidth * 1.0f) / ImageWidth;
        for (int i = 0; i < (int) face_count[0]; ++i) {
            FSDK.FSDK_Features Eyes = new FSDK.FSDK_Features();
            FSDK.GetTrackerEyes(mTracker, 0, mIDs[i], Eyes);
            String[] Gender_values = new String[1];
            String[] Age_values = new String[1];
            FSDK.GetTrackerFacialAttribute(mTracker, 0, mIDs[i], "Gender", Gender_values, 100);
            FSDK.GetTrackerFacialAttribute(mTracker, 0, mIDs[i], "Age", Age_values, 100);

            for (int z = 0; z < Gender_values.length; z++) {
                Log.e("Attributes " + z, "GEN :" + Gender_values[z]);
                double ml = Double.parseDouble(Gender_values[z].split(";")[0].split("=")[1]);
                double fm = Double.parseDouble(Gender_values[z].split(";")[1].split("=")[1]);
                male = ml > fm;
//	GEN :Male=0.88976; Female=0.11024

            }
            for (int z = 0; z < Age_values.length; z++) {
                Log.e("Attributes " + z, "Age :" + Age_values[z]);
                age = Float.parseFloat(Age_values[z].split("=")[1]);
            }
            GetFaceFrame(Eyes, mFacePositions[i]);

        }

        faceLock.unlock();

        int shift = (int) (22 * sDensity);

        Log.e("FACES FOUND :", "" + face_count[0]);
        for (int i = 0; i < face_count[0]; ++i) {


            boolean named = false;
            if (IDs[i] != -1) {
                String names[] = new String[1];
                FSDK.GetAllNames(mTracker, IDs[i], names, 1024);
                if (names[0] != null && names[0].length() > 0) {
                    //save_face(Image, active_false_id, transaction_no);


                }
            }


        }
        FSDK.FreeImage(Image);


    } // end onDraw method

    public boolean capture = false;
    boolean auto_capture = false;
    //	String transaction_no="SPARTA_REALM_TEST";
    public String transaction_no = null;
    //int verification_type= 2;
    boolean searching = false;

    void search_face(FSDK.HImage frame, final long tracker_index, final Canvas canvas, final float x, final float y, Paint paint) {
        //	sdbw sd;
        String img_name = "";
        img_name = "TA_DAT" + System.currentTimeMillis() + "FB_SCH.JPG";
        String full_path = svars.current_app_config(Realm.context).appDataFolder + img_name;
        //FSDK.SaveImageToFile(frame,svars.current_app_config(Realm.context).file_path_employee_data +img_name);

        if (FSDK.SaveImageToFile(frame, full_path) == FSDK.FSDKE_OK && (full_path = svars.current_app_config(Realm.context).appDataFolder + face_handler.extract_face(svars.current_app_config(Realm.context).appDataFolder + img_name)) != svars.current_app_config(Realm.context).appDataFolder + "!!!") {
            //sd=new sdbw(MatchingActivity.acty);
            searching = true;
            dm.load_face_match(full_path, new MatchingInterface() {
                @Override
                public void on_match_complete(boolean match_found, String mils) {
                    Log.e(" Face match :", "Complete " + mils);
                    //	canvas.drawText(tracker_index+" :: Search complete .."+match_found, x, y, mPaintAccent);
                    searching = false;
                }

                @Override
                public void on_match_found(String employee_id, String data_index, String match_time, int v_type, int verification_mode) {
//                    member discovered_member = Realm.databaseManager.load_employee(employee_id);
//                    if (discovered_member == null) {
//                        Log.e(" Face match :", "Null member :" + employee_id);
//                    } else {
//                        canvas.drawText(tracker_index + " :: Match found .." + discovered_member.father_name.value, x, y, mPaintAccent);
//                        FSDK.LockID(mTracker, tracker_index);
//
//                        FSDK.SetName(mTracker, tracker_index, employee_id);
//                        if (transaction_no.length() <= 0) {
//                            FSDK.PurgeID(mTracker, tracker_index);
//                        }
//                        FSDK.UnlockID(mTracker, tracker_index);
//                        main_MatchingInterface.on_match_found(employee_id, data_index, match_time, v_type, 4);
//                    }

                }

                @Override
                public void on_finger_match_found(String fp_id, int score, String match_time) {

                }

                @Override
                public void on_match_progress_changed(int progress) {
                    canvas.drawText(tracker_index + " :: Searching db ..", x, y, mPaintAccent);

                }

                @Override
                public void on_match_faild_reason_found(int reason, String employee_id) {

                }
            }, vt.ordinal(), true);

        }

    }//saving the fucking face transaction will not work for this fucking bitch as

    void save_face(FSDK.HImage frame, long tracker_index, String transaction_no) {

        save_face_to_tracker(tracker_index, transaction_no);
        String img_name = "";
        img_name = "TA_DAT" + System.currentTimeMillis() + "FB_SCH.JPG";
        FSDK.SaveImageToFile(frame, svars.current_app_config(Realm.context).appDataFolder + img_name);

        try {

            cpi.OnCaptured(img_name);

        } catch (Throwable ex) {
            ex.printStackTrace();


        }
    }

    void save_face(FSDK.HImage frame, long tracker_index, String transaction_no, int gender, float age) {

//        save_face_to_tracker(tracker_index, transaction_no);
        String img_name = "";
        img_name = "TA_DAT" + System.currentTimeMillis() + "FB_SCH.JPG";
        FSDK.SaveImageToFile(frame, svars.current_app_config(Realm.context).appDataFolder + img_name);

        try {

            cpi.OnCaptured(img_name);

        } catch (Throwable ex) {
            ex.printStackTrace();


        }
    }

    void save_face_(FSDK.HImage frame, long tracker_index, String transaction_no) {

        if (FSDK.LockID(mTracker, tracker_index) != FSDK.FSDKE_OK) {
            Log.e("ID locking error", "");
            return;
        }
//	String userName = input.getText().toString();
        FSDK.SetName(mTracker, tracker_index, transaction_no);
        if (transaction_no.length() <= 0) FSDK.PurgeID(mTracker, tracker_index);
        FSDK.UnlockID(mTracker, tracker_index);
        String img_name = "";
        img_name = "TA_DAT" + System.currentTimeMillis() + "FB_SCH.JPG";
        FSDK.SaveImageToFile(frame, svars.current_app_config(Realm.context).appDataFolder + img_name);

        try {

            cpi.OnCaptured(img_name);

        } catch (Throwable ex) {
            ex.printStackTrace();


        }
    }

    void save_face_to_tracker(long tracker_index, String transaction_no) {

        FSDK.LockID(mTracker, tracker_index);
        FSDK.SetName(mTracker, tracker_index, transaction_no);
        if (transaction_no.length() <= 0) FSDK.PurgeID(mTracker, tracker_index);
        FSDK.UnlockID(mTracker, tracker_index);


    }

    void draw_error_circle(Rect r, Canvas c) {
        Drawable d = getResources().getDrawable(R.drawable.bg_circle_detect_result_failed, null);
        d.setBounds(r.left - 120, r.top + 100, r.right + 120, r.bottom + 200);
        d.draw(c);
    }
    int angle=0;

    void draw_circle_normal(Rect r, Canvas c) {
//        Drawable d = getResources().getDrawable(R.drawable.bg_circle_v2, null);
//        d.setBounds(r.left, r.top, r.right, r.bottom);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bg_circle_v2);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
        drawable.setBounds(r.left, r.top, r.right, r.bottom);
        angle=angle+10;
        drawable.draw(c);
    }

    void draw_circle_success(Rect r, Canvas c) {
        Drawable d = getResources().getDrawable(R.drawable.bg_circle_detect_result_success, null);
        d.setBounds(r.left - 120, r.top - 100, r.right + 120, r.bottom + 400);
        d.draw(c);
    }
  void draw_circle_unknown(Rect r, Canvas c) {
        Drawable d = getResources().getDrawable(R.drawable.bg_circle_detect_result_unknown, null);
//        d.setBounds(r.left - 120, r.top + 100, r.right + 120, r.bottom + 500);
      d.setBounds(r.left - 120, r.top - 100, r.right + 120, r.bottom + 400);
      d.draw(c);
    }

    void draw_horns(Rect r, Canvas c) {
        Drawable d = getResources().getDrawable(R.drawable.horns, null);
        d.setBounds(r.left - 120, r.top - 100, r.right + 120, r.bottom + 100);
        d.draw(c);
    }

    int false_count = 0;
    int max_false_count = 2;
    long active_false_id = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) { //NOTE: the method can be implemented in Preview class
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();

                faceLock.lock();
                FaceRectangle rects[] = new FaceRectangle[MAX_FACES];
                long IDs[] = new long[MAX_FACES];
                for (int i = 0; i < MAX_FACES; ++i) {
                    rects[i] = new FaceRectangle();
                    rects[i].x1 = mFacePositions[i].x1;
                    rects[i].y1 = mFacePositions[i].y1;
                    rects[i].x2 = mFacePositions[i].x2;
                    rects[i].y2 = mFacePositions[i].y2;
                    IDs[i] = mIDs[i];
                }
                faceLock.unlock();

                for (int i = 0; i < MAX_FACES; ++i) {
                    if (rects[i] != null && rects[i].x1 <= x && x <= rects[i].x2 && rects[i].y1 <= y && y <= rects[i].y2 + 30) {
                        mTouchedID = IDs[i];

                        mTouchedIndex = i;

                        // requesting name on tapping the face
                        final EditText input = new EditText(mContext);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Enter person's name")
                                .setView(input)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        FSDK.LockID(mTracker, mTouchedID);
                                        String userName = input.getText().toString();
                                        FSDK.SetName(mTracker, mTouchedID, userName);
                                        if (userName.length() <= 0)
                                            FSDK.PurgeID(mTracker, mTouchedID);
                                        FSDK.UnlockID(mTracker, mTouchedID);
                                        mTouchedIndex = -1;
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        mTouchedIndex = -1;
                                    }
                                })
                                .setCancelable(false) // cancel with button only
                                .create();

                        break;
                    }
                }
        }
        return true;
    }

    static public void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int yp = 0;
        for (int j = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[3 * yp] = (byte) ((r >> 10) & 0xff);
                rgb[3 * yp + 1] = (byte) ((g >> 10) & 0xff);
                rgb[3 * yp + 2] = (byte) ((b >> 10) & 0xff);
                ++yp;
            }
        }
    }
} // end of ProcessImageAndDrawResults class
