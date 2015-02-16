package com.aspen.aspensc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SignatureView extends View
{
    private Path mPath;
    private Paint mPaint; //color of signature
    private Paint bgPaint = new Paint(Color.TRANSPARENT); //color of background


    private Bitmap mBitmap;
    private Canvas mCanvas;

    private float curX, curY;
    //needed for bezier
    private float mLastVelocity;
    private float mLastWidth;
    private boolean mUseBezier = true;
    private float mLastX;
    private float mLastY;
    private float mMaxWidth = 9.0f; //maximum width of stroke (ALSO THE DOT WHEN YOU FIRST PRESS DOWN)
    private float mMinWidth = 3.0f; //minimum width of stroke
    private List<TimedPoint> mPoints;
    private float mVelocityFilterWeight = 0.3f; //Weight used to modify new velocity based on the previous velocity

    private static final int TOUCH_TOLERANCE = 4;
    private static final int STROKE_WIDTH = 4;


    private RectF mDirtyRect;

    public SignatureView(Context context)
    {
        super(context);
        init();
    }
    public SignatureView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public SignatureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init()
    {
        setFocusable(true);
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        //added to make writing seem more natural
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        //Dirty rectangle to update only the changed portion of the view
        mDirtyRect = new RectF();

        clearSignature();
    }

    public void setSigColor(int color)
    {
        mPaint.setColor(color);
    }
    public void setSigColor(int a, int red, int green, int blue)
    {
        mPaint.setARGB(a, red, green, blue);
    }
    public boolean clearSignature()
    {
        //for bezier
        mPoints = new ArrayList<TimedPoint>();
        mLastVelocity = 0;
        mLastWidth = (mMaxWidth + mMinWidth) / 2;


        if (mBitmap != null)
            createFakeMotionEvents();
        if (mCanvas != null)
        {
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPath.reset();
            invalidate();
        }
        else
        {
            return false;
        }
        return true;
    }
    public Bitmap getImage(int id)
    {
        View v = (View) findViewById(id);
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);

        return b;
    }

    public void setImage(Bitmap bitmap)
    {
        this.mBitmap = bitmap;
        this.invalidate();
    }

    public byte[] getBytes(int id) {
        Bitmap b = getImage(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        int bitmapWidth = mBitmap != null ? mBitmap.getWidth() : 0;
        int bitmapHeight = mBitmap != null ? mBitmap.getWidth() : 0;
        if (bitmapWidth >= width && bitmapHeight >= height)
            return;
        if (bitmapWidth < width)
            bitmapWidth = width;
        if (bitmapHeight < height)
            bitmapHeight = height;
        Bitmap newBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        //newBitmap.eraseColor(Color.TRANSPARENT);
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if (mBitmap != null)
            newCanvas.drawBitmap(mBitmap, 0, 0, null);
        mBitmap = newBitmap;
        mCanvas = newCanvas;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        canvas.drawPath(mPath, mPaint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mPoints.clear();
                touchDown(x, y);
                mLastX = x;
                mLastY = y;
                addPoint(new TimedPoint(x, y));
                break;
            case MotionEvent.ACTION_MOVE:
                resetDirtyRect(x, y);
                touchMove(x, y);
                addPoint(new TimedPoint(x, y));
                break;
            case MotionEvent.ACTION_UP:
                resetDirtyRect(x, y);
                addPoint(new TimedPoint(x, y));
                touchUp();
                break;
        }

        if(mUseBezier == false)
        {
            invalidate();
        }
        else
        {
            invalidate(
                    (int) (mDirtyRect.left - mMaxWidth),
                    (int) (mDirtyRect.top - mMaxWidth),
                    (int) (mDirtyRect.right + mMaxWidth),
                    (int) (mDirtyRect.bottom + mMaxWidth));
        }
        return true;
    }

    public float strokeWidth(float velocity)
    {
        return Math.max(mMaxWidth / (velocity + 1), mMinWidth);

    }

    public void addPoint(TimedPoint tp)
    {
        mPoints.add(tp);
        if (mPoints.size() > 2)
        {
            if (mPoints.size() == 3)
            {
                mPoints.add(0, mPoints.get(0));

                ControlTimedPoints tmp = calculateCurveControlPoints(mPoints.get(0), mPoints.get(1), mPoints.get(2));
                TimedPoint c2 = tmp.c2;
                tmp = calculateCurveControlPoints(mPoints.get(1), mPoints.get(2), mPoints.get(3));
                TimedPoint c3 = tmp.c1;
                Bezier curve = new Bezier(mPoints.get(1), c2, c3, mPoints.get(2));

                TimedPoint startPoint = curve.startPoint;
                TimedPoint endPoint = curve.endPoint;

                float velocity = endPoint.velocityFrom(startPoint);
                //isNaN just checks to see if its a number, if its not set it to 0, if it is use it
                velocity = Float.isNaN(velocity) ? 0.0f : velocity;

                velocity = mVelocityFilterWeight * velocity + (1 - mVelocityFilterWeight) * mLastVelocity;

                // The new width is a function of the velocity. Higher velocities
                // correspond to thinner strokes.
                float newWidth = strokeWidth(velocity);

                // The Bezier's width starts out as last curve's final width, and
                // gradually changes to the stroke width just calculated. The new
                // width calculation is based on the velocity between the Bezier's
                // start and end mPoints.
                addBezier(curve, mLastWidth, newWidth);

                mLastVelocity = velocity;
                mLastWidth = newWidth;

                // Remove the first element from the list,
                // so that we always have no more than 4 mPoints in mPoints array.
                mPoints.remove(0);
            }
        }
    }
        public ControlTimedPoints calculateCurveControlPoints(TimedPoint s1, TimedPoint s2 ,TimedPoint s3)
        {
            float dx1 = s1.x - s2.x;
            float dy1 = s1.y - s2.y;
            float dx2 = s2.x - s3.x;
            float dy2 = s2.y - s3.y;

            TimedPoint m1 = new TimedPoint((s1.x + s2.x) / 2.0f, (s1.y + s2.y) / 2.0f);
            TimedPoint m2 = new TimedPoint((s2.x + s3.x) / 2.0f, (s2.y + s3.y) / 2.0f);

            float l1 = (float) Math.sqrt(dx1*dx1 + dy1*dy1);
            float l2 = (float) Math.sqrt(dx2*dx2 + dy2*dy2);

            float dxm = (m1.x - m2.x);
            float dym = (m1.y - m2.y);
            float k = l2 / (l1 + l2);
            TimedPoint cm = new TimedPoint(m2.x + dxm*k, m2.y + dym*k);

            float tx = s2.x - cm.x;
            float ty = s2.y - cm.y;

            return new ControlTimedPoints(new TimedPoint(m1.x + tx, m1.y + ty), new TimedPoint(m2.x + tx, m2.y + ty));
         }

    /**----------------------------------------------------------
     * Private methods
     **---------------------------------------------------------*/

    private void touchDown(float x, float y)
    {
        mPath.reset();
        mPath.moveTo(x, y);
        curX = x;
        curY = y;
    }

    private void touchMove(float x, float y)
    {
        float dx = Math.abs(x - curX);
        float dy = Math.abs(y - curY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            mPath.quadTo(curX, curY, (x + curX)/2, (y + curY)/2);
            curX = x;
            curY = y;
        }
    }

    private void touchUp()
    {
        mPath.lineTo(curX, curY);
        if (mCanvas == null)
        {
            mCanvas = new Canvas();
            mCanvas.setBitmap(mBitmap);
        }
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    private void createFakeMotionEvents()
    {
        MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis()+100, MotionEvent.ACTION_DOWN, 1f, 1f ,0);
        MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis()+100, MotionEvent.ACTION_UP, 1f, 1f ,0);
        onTouchEvent(downEvent);
        onTouchEvent(upEvent);
    }

    private void addBezier(Bezier curve, float startWidth, float endWidth) {
        //ensureSignatureBitmap();
        float originalWidth = mPaint.getStrokeWidth();
        float widthDelta = endWidth - startWidth;
        float drawSteps = (float) Math.floor(curve.length());

        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            float t = ((float) i) / drawSteps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            float x = uuu * curve.startPoint.x;
            x += 3 * uu * t * curve.control1.x;
            x += 3 * u * tt * curve.control2.x;
            x += ttt * curve.endPoint.x;

            float y = uuu * curve.startPoint.y;
            y += 3 * uu * t * curve.control1.y;
            y += 3 * u * tt * curve.control2.y;
            y += ttt * curve.endPoint.y;

            // Set the incremental stroke width and draw.
            mPaint.setStrokeWidth(startWidth + ttt * widthDelta);
            mCanvas.drawPoint(x, y, mPaint);
            //expandDirtyRect(x, y);
        }

        mPaint.setStrokeWidth(originalWidth);
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY)
    {

        // The mLastX and mLastY were set when the ACTION_DOWN
        // motion event occurred.
        mDirtyRect.left = Math.min(mLastX, eventX);
        mDirtyRect.right = Math.max(mLastX, eventX);
        mDirtyRect.top = Math.min(mLastY, eventY);
        mDirtyRect.bottom = Math.max(mLastY, eventY);
    }

    private void UploadSignature()
    {
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
       //mBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        //byte[] data = bos.toByteArray();
        //byte[] sendData;
        String sendData;
        sendData = getEncoded64ImageStringFromBitmap(mBitmap);



        // Making HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            //change
            String URL1 = "http://rohit-pc:8078/service1.svc/UploadImage";

            HttpPost httpPost = new HttpPost(URL1);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json" );

            ContentBody bin = null;

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entityBuilder.addTextBody("imageFileName", sendData);


            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null)
            {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);
        } catch (Exception e)
        {
            Log.e(e.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }


    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        //return imgString.getBytes();
        return imgString;
    }



}

