package com.realm.utils.FormTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.realm.Services.DatabaseManager;
import com.realm.utils.FileManager;
import com.realm.utils.s_bitmap_handler;


/**
 * Created by Thompson on 3/6/2018.
 */

public class Signature extends View
{
    private static final float STROKE_WIDTH = 5f;
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    public boolean locked=false;
    private Paint paint = new Paint();
    public Path path = new Path();

    private float lastTouchX;
    private float lastTouchY;

    private final RectF dirtyRect = new RectF();



//    LinearLayout mContent;

    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;

    Context contx;

    public Signature(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
//        this.mContent=mContent;
        contx=context;

    }

  public Signature(Context context)
    {
        super(context);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
//        this.mContent=mContent;
        contx=context;

    }


    @SuppressLint("WrongThread")
    public String load_img_bs64()
    {
        Bitmap decoded = null;
        if(mBitmap == null)
        {
            mBitmap =  Bitmap.createBitmap (getWidth(), getHeight(), Bitmap.Config.RGB_565);;
        }
        Canvas canvas = new Canvas(mBitmap);
        try
        {

            draw(canvas);
            Bitmap original = mBitmap;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.PNG, 100, out);
            decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        }
        catch(Exception e)
        {
            Log.v("log_tag", e.toString());
        }
        return Base64.encodeToString(s_bitmap_handler.getBytes(decoded),0);
    }
    @SuppressLint("WrongThread")
    public String load_img()
    {
        Bitmap decoded = null;
        if(mBitmap == null)
        {
            mBitmap =  Bitmap.createBitmap (getWidth(), getHeight(), Bitmap.Config.RGB_565);;
        }
        Canvas canvas = new Canvas(mBitmap);
        try
        {

            draw(canvas);
            Bitmap original = mBitmap;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.PNG, 100, out);
            decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        }
        catch(Exception e)
        {
            Log.v("log_tag", e.toString());
        }
        return FileManager.save_doc(Base64.encodeToString(s_bitmap_handler.getBytes(decoded),0));
    }

    public void save()
    {
        Log.v("log_tag", "Width: " + getWidth());
        Log.v("log_tag", "Height: " +getHeight());
        if(mBitmap == null)
        {
            mBitmap =  Bitmap.createBitmap (getWidth(), getHeight(), Bitmap.Config.RGB_565);;
        }
        Canvas canvas = new Canvas(mBitmap);
        try
        {

            draw(canvas);
               /*FileOutputStream mFileOutStream = new FileOutputStream(mypath);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
         mFileOutStream.flush();
            mFileOutStream.close();
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
            Log.v("log_tag","url: " + url);*/
            //In case you want to delete the file
            //boolean deleted = mypath.delete();
            //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
            //If you want to convert the image to string use base64 converter

        }
        catch(Exception e)
        {
            Log.v("log_tag", e.toString());
        }
    }
interface InputListener{
        void onInputAvailable(boolean valid);
}
    InputListener inputListener=new InputListener() {
        @Override
        public void onInputAvailable(boolean valid) {

        }
    };
    public void setInputListener(InputListener inputListener){
        this.inputListener=inputListener;
    }
    public void clear()
    {
        path.reset();
        invalidate();
        inputListener.onInputAvailable(false);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(locked){return true;}
        float eventX = event.getX();
        float eventY = event.getY();


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++)
                {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                path.lineTo(eventX, eventY);
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(!path.isEmpty()){
                        inputListener.onInputAvailable(true);
                    }
                }

                break;

            default:
                debug("Ignored touch event: " + event.toString());
                return false;
        }

        invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    private void debug(String string){
    }

    private void expandDirtyRect(float historicalX, float historicalY)
    {
        if (historicalX < dirtyRect.left)
        {
            dirtyRect.left = historicalX;
        }
        else if (historicalX > dirtyRect.right)
        {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top)
        {
            dirtyRect.top = historicalY;
        }
        else if (historicalY > dirtyRect.bottom)
        {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY)
    {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }
}