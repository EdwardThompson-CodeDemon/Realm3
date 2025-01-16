package com.realm.utils.biometrics.face;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.common.base.Stopwatch;
import com.luxand.FSDK;
import com.luxand.FSDK.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.realm.R;
import com.realm.Realm;





import com.realm.utils.svars;


public class face_handler {

 //  static sdbw sd;


   public face_handler()
    {

    }
    static {




    }
   public static void begin_face_extraction(Context context)
    {
//        View aldv= LayoutInflater.from(context).inflate(R.layout.dialog_face_extraction,null);
//final AlertDialog ald =new AlertDialog.Builder(context)
//                .setView(aldv)
//                                .create();
//        ald .getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//ald.show();
//        aldv.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ald.dismiss();
//            }
//        });
//
//        aldv.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                aldv.findViewById(R.id.exit).setVisibility(View.GONE);
//                new Thread(()->{
//
//                    int cnt=0;
//                    String unconverted_only="id NOT IN(SELECT id FROM member_data_table WHERE data_type='"+ svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"')";
//                    for (Object o: Realm.databaseManager.load_dynamic_records(new member_data(),new String[]{"data_type='"+svars.data_type_indexes.photo+"'","data_index='"+ svars.image_indexes.profile_photo+"'",unconverted_only}))
//                    {
//                        member_data mb=((member_data)o);
//
//                        extract_face(mb,aldv.findViewById(R.id.face_img));
//                        cnt++;
//                        int finalCnt = cnt;
//                        aldv.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                int total_imgs=Integer.parseInt(Realm.databaseManager.get_record_count(mb.table_name,new String[]{"data_type='"+svars.data_type_indexes.photo+"'","data_index='"+svars.image_indexes.profile_photo+"'"}));
//
//                                ((ProgressBar)aldv.findViewById(R.id.progress)).setMax(total_imgs);
//                                int total_faces=Integer.parseInt(Realm.databaseManager.get_record_count(mb.table_name,new String[]{"data_type='"+svars.data_type_indexes.photo+"'","data_index='"+svars.image_indexes.croped_face+"'"}));
//                                ((ProgressBar)aldv.findViewById(R.id.progress)).setProgress(finalCnt);
//                                ((TextView)aldv.findViewById(R.id.extracted_label)).setText("Extracted :"+total_faces);
//                                ((TextView)aldv.findViewById(R.id.per)).setText(finalCnt +"/"+total_imgs);
//                            }
//                        });
//
//                    }
//                }).start();
//
//
//
//            }
//        });

    }

    public  static String extract_face(String image_name, FaceImageView fiv)
    {
        String picture_path=svars.current_app_config(Realm.context).appDataFolder +image_name;
        Stopwatch stw= Stopwatch.createUnstarted();
        stw.start();
        int result=-1;


        FSDK.TFacePosition faceCoords = new FSDK.TFacePosition();
        faceCoords.w = 0;

        FSDK.HImage picture = new FSDK.HImage();
        FSDK.HImage picture2 = new FSDK.HImage();
        FSDK.FSDK_Features features=new FSDK.FSDK_Features();
        FSDK.FSDK_Features features2=new FSDK.FSDK_Features();

        result = FSDK.LoadImageFromFile(picture, picture_path);
        Log.e("Extraction :","File loading  :"+stw.elapsed(TimeUnit.MILLISECONDS));
        if (result == FSDK.FSDKE_OK) {
            result = FSDK.DetectFace(picture, faceCoords);
            Log.e("Extraction :",result+"Face loading :"+stw.elapsed(TimeUnit.MILLISECONDS));
            String finalPicture_path1 = picture_path;
            fiv.post(new Runnable() {
                @Override
                public void run() {
                    // show_image(findViewById(R.id.imageView1), finalPicture_path1);
                   show_image(fiv, finalPicture_path1);

                }
            });
            if (result == FSDK.FSDKE_OK) {
                result = FSDK.DetectFacialFeaturesInRegion(picture, faceCoords, features);
                Log.e("Extraction :","Features loading :"+stw.elapsed(TimeUnit.MILLISECONDS));

                if (result == FSDK.FSDKE_OK) {

                    result = FSDK.ExtractFaceImage(picture, features, 100, 100, picture2, features2);
                    Log.e("Extraction :","Face extracted  :"+result);
                    String img_name="TA_DAT"+ System.currentTimeMillis()+"FB_SCH.JPG";
                    picture_path = svars.current_app_config(Realm.context).appDataFolder +img_name;
                    result = FSDK.SaveImageToFile(picture2, picture_path);
                    FSDK.FreeImage(picture);
                    FSDK.FreeImage(picture2);
//                    mb.data.value=img_name;
//                    mb.data.storage_mode=2;
//                    mb.sid.value="";
//                    mb.id.value="";
//
//
//                    mb.data_index.value=svars.image_indexes.croped_face+"";
//                    Realm.databaseManager.register_object(null,Realm.databaseManager.load_JSON_from_object(mb),mb,"");
                    String finalPicture_path = picture_path;
                    fiv.post(new Runnable() {
                        @Override
                        public void run() {
                            show_image(fiv, finalPicture_path);
                        }
                    });
                    return img_name;

                }else {
                    Log.e("FACE match :","Error creating template live model ");
                    FSDK.FreeImage(picture);
                    return null;
                }




            }else {
                Log.e("FACE match :","Error detecting face live model ");
                FSDK.FreeImage(picture);
                return null;
            }

        }else {
            Log.e("FACE match :","Error loading live model ");
            return null;
        }




    }

    void clear_tracker_memory(FSDK.HTracker mTracker)
    {


        FSDK.ClearTracker(mTracker);


    }
  public static String extract_face(String picture_path)
    {

        Stopwatch stw= Stopwatch.createUnstarted();
        stw.start();
        int result=-1;


        FSDK.TFacePosition faceCoords = new FSDK.TFacePosition();
        faceCoords.w = 0;

        FSDK.HImage picture = new FSDK.HImage();
        FSDK.HImage picture2 = new FSDK.HImage();
        FSDK.FSDK_Features features=new FSDK.FSDK_Features();
        FSDK.FSDK_Features features2=new FSDK.FSDK_Features();

        result = FSDK.LoadImageFromFile(picture, picture_path);
        Log.e("Extraction :","File loading  :"+stw.elapsed(TimeUnit.MILLISECONDS));
        if (result == FSDK.FSDKE_OK) {
            result = FSDK.DetectFace(picture, faceCoords);
            Log.e("Extraction :",result+"Face loading :"+stw.elapsed(TimeUnit.MILLISECONDS));

            if (result == FSDK.FSDKE_OK) {
                result = FSDK.DetectFacialFeaturesInRegion(picture, faceCoords, features);
                Log.e("Extraction :","Features loading :"+stw.elapsed(TimeUnit.MILLISECONDS));

                if (result == FSDK.FSDKE_OK) {

                    result = FSDK.ExtractFaceImage(picture, features, 100, 100, picture2, features2);
                    Log.e("Extraction :","Face extracted  :"+result);
                    String img_name="TA_DAT"+ System.currentTimeMillis()+"FB_SCH.JPG";
                    picture_path = svars.current_app_config(Realm.context).appDataFolder +img_name;
                    result = FSDK.SaveImageToFile(picture2, picture_path);
                    if (result == FSDK.FSDKE_OK) {

                        return img_name;
                    }else {

                    Log.e("FACE match :","Error saving template live model ");
                    FSDK.FreeImage(picture);
                    return null;
                }
}else {
                    Log.e("FACE match :","Error creating template live model ");
                    FSDK.FreeImage(picture);
                    return null;
                }




            }else {
                Log.e("FACE match :","Error detecting face live model ");
                FSDK.FreeImage(picture);
                return null;
            }

        }else {
            Log.e("FACE match :","Error loading live model ");
            return null;
        }




    }

    public static void show_image(FaceImageView fiv,String file_path)
    {
        FSDK.HImage picture = new FSDK.HImage();
        FSDK.TFacePosition faceCoords = new FSDK.TFacePosition();
        FSDK.FSDK_Features features = new FSDK.FSDK_Features();
        int result = FSDK.LoadImageFromFile(picture, file_path);
        int[] realWidth=new int[1];
        if (result == FSDK.FSDKE_OK) {
            faceCoords = new FSDK.TFacePosition();
            faceCoords.w = 0;
            result = FSDK.DetectFace(picture, faceCoords);
            features = new FSDK.FSDK_Features();
            result = FSDK.DetectFacialFeaturesInRegion(picture, faceCoords, features);

        }
        fiv.setImageBitmap(BitmapFactory.decodeFile(file_path));
        fiv.detectedFace = faceCoords;

        if (features!=null&&features.features!=null&&features.features[0] != null) // if detected
            fiv.facial_features = features;

        FSDK.GetImageWidth(picture, realWidth);
        FSDK.FreeImage(picture);

        fiv.faceImageWidthOrig = realWidth[0];
        fiv.invalidate(); // redraw, marking up faces and features


    }


    public static Boolean match_ok(byte[] model1,String model2path)
    {

        Stopwatch stw= Stopwatch.createUnstarted();
        stw.start();
        int result=-1;


        TFacePosition faceCoords = new TFacePosition();
        faceCoords.w = 0;

        HImage picture = new HImage();
        HImage croped_picture = new HImage();

        FSDK_FaceTemplate ft=new FSDK_FaceTemplate();

        FSDK_FaceTemplate ft2=new FSDK_FaceTemplate();

        result = FSDK.LoadImageFromJpegBuffer(picture, model1,model1.length);
        Log.e("SMatch :","Time loading 1 :"+stw.elapsed(TimeUnit.MILLISECONDS));
        if (result == FSDK.FSDKE_OK) {
            result = FSDK.DetectFace(picture, faceCoords);

            if (result == FSDK.FSDKE_OK) {


                result=FSDK.GetFaceTemplateInRegion(picture,faceCoords,ft);

                //result = FSDK.LoadImageFromJpegBuffer(picture, model1,model1.length);
                if (result == FSDK.FSDKE_OK) {

                    FSDK.FreeImage(picture);
                    picture = new HImage();
                    faceCoords = new TFacePosition();
                    faceCoords.w = 0;
                    //byte[] model2= s_bitmap_handler.getBytes_JPG(s_bitmap_handler.getImage(sbgw.get_file_data(model2path)));
                    //---byte[] model2= s_bitmap_handler.getBytes_JPG(sbgw.get_file_bitmap(model2path));
                    //byte[] model2= s_bitmap_handler.getBytes_JPG(sbgw.get_file_bitmap_bytes(model2path));

                    result = FSDK.LoadImageFromFile(picture, model2path);
                    //result = FSDK.LoadImageFromJpegBuffer(picture, model2,model2.length);
                    Log.e("SMatch :","Time loading 1 :"+stw.elapsed(TimeUnit.MILLISECONDS));

                    //
                    if (result == FSDK.FSDKE_OK) {
                        result = FSDK.DetectFace(picture, faceCoords);

                        if (result == FSDK.FSDKE_OK) {


                            result=FSDK.GetFaceTemplateInRegion(picture,faceCoords,ft2);
                            if (result == FSDK.FSDKE_OK) {
                                FSDK.FreeImage(picture);

                                float[]	match_results = new float[1];



                                result=FSDK.MatchFaces(ft,ft2,match_results);
                                Log.e("SMatch :",result+"  :: "+ Arrays.toString(match_results));
                                Log.e("SMatch :","Time :"+stw.elapsed(TimeUnit.MILLISECONDS));
                                if(result==FSDK.FSDKE_OK)
                                {
                                    return match_results[0]*100>=svars.matching_acuracy;
                                }else {
                                    return false;
                                }


                            }else {
                                Log.e("FACE match :","Error creating template path model ");
                                return false;
                            }
                        }else {
                            Log.e("FACE match :","Error detecting face path model ");
                            return false;
                        }

                    }else {
                        Log.e("FACE match :","Error loading path model ");
                        return false;
                    }
                }else {
                    Log.e("FACE match :","Error creating template live model ");
                    return false;
                }




            }else {
                Log.e("FACE match :","Error detecting face live model ");
                return false;
            }

        }else {
            Log.e("FACE match :","Error loading live model ");
            return false;
        }







    }
    public static Boolean match_ok(String model1path,String model2path)
    {

        Stopwatch stw= Stopwatch.createUnstarted();
        stw.start();
        int result=-1;


        TFacePosition faceCoords = new TFacePosition();
        faceCoords.w = 0;

        HImage picture = new HImage();
        //HImage croped_picture = new HImage();

        FSDK_FaceTemplate ft=new FSDK_FaceTemplate();

        FSDK_FaceTemplate ft2=new FSDK_FaceTemplate();

        //result = FSDK.LoadImageFromJpegBuffer(picture, model1,model1.length);
        result = FSDK.LoadImageFromFile(picture, model1path);
        Log.e("SMatch :","Time loading 1 :"+stw.elapsed(TimeUnit.MILLISECONDS));
        if (result == FSDK.FSDKE_OK) {
            result = FSDK.DetectFace(picture, faceCoords);

            if (result == FSDK.FSDKE_OK) {


                result=FSDK.GetFaceTemplateInRegion(picture,faceCoords,ft);

                //result = FSDK.LoadImageFromJpegBuffer(picture, model1,model1.length);
                if (result == FSDK.FSDKE_OK) {

                    FSDK.FreeImage(picture);
                    picture = new HImage();
                    faceCoords = new TFacePosition();
                    faceCoords.w = 0;
                    //byte[] model2= s_bitmap_handler.getBytes_JPG(s_bitmap_handler.getImage(sbgw.get_file_data(model2path)));
                    //---byte[] model2= s_bitmap_handler.getBytes_JPG(sbgw.get_file_bitmap(model2path));
                    //byte[] model2= s_bitmap_handler.getBytes_JPG(sbgw.get_file_bitmap_bytes(model2path));

                    result = FSDK.LoadImageFromFile(picture, model2path);
                    //result = FSDK.LoadImageFromJpegBuffer(picture, model2,model2.length);
                    Log.e("SMatch :","Time loading 1 :"+stw.elapsed(TimeUnit.MILLISECONDS));

                    //
                    if (result == FSDK.FSDKE_OK) {
                        result = FSDK.DetectFace(picture, faceCoords);

                        if (result == FSDK.FSDKE_OK) {


                            result=FSDK.GetFaceTemplateInRegion(picture,faceCoords,ft2);
                            if (result == FSDK.FSDKE_OK) {
                                FSDK.FreeImage(picture);

                                float[]	match_results = new float[1];



                                result=FSDK.MatchFaces(ft,ft2,match_results);
                                Log.e("SMatch :",result+"  :: "+Arrays.toString(match_results));
                                Log.e("SMatch :","Time :"+stw.elapsed(TimeUnit.MILLISECONDS));
                                if(result==FSDK.FSDKE_OK)
                                {
                                    return match_results[0]*100>=svars.matching_acuracy;
                                }else {
                                    return false;
                                }


                            }else {
                                Log.e("FACE match :","Error creating template path model ");
                                return false;
                            }
                        }else {
                            Log.e("FACE match :","Error detecting face path model ");
                            return false;
                        }

                    }else {
                        Log.e("FACE match :","Error loading path model ");
                        return false;
                    }
                }else {
                    Log.e("FACE match :","Error creating template live model ");
                    return false;
                }




            }else {
                Log.e("FACE match :","Error detecting face live model ");
                return false;
            }

        }else {
            Log.e("FACE match :","Error loading live model ");
            return false;
        }







    }
}
