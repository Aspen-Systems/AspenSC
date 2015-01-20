package com.aspen.aspensc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;


public class SignatureView extends View
{
    private Path mPath;
    private Paint mPaint; //color of signature
    private Paint bgPaint = new Paint(Color.TRANSPARENT); //color of background

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private float curX, curY;

    private static final int TOUCH_TOLERANCE = 4;
    private static final int STROKE_WIDTH = 4;

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
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        invalidate();
        return true;
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

/*    private void addBezier(Bezier curve, float startWidth, float endWidth) {
        ensureSignatureBitmap();
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
            mSignatureBitmapCanvas.drawPoint(x, y, mPaint);
            expandDirtyRect(x, y);
        }

        mPaint.setStrokeWidth(originalWidth);
    }*/
    private void createFakeMotionEvents()
    {
        MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis()+100, MotionEvent.ACTION_DOWN, 1f, 1f ,0);
        MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis()+100, MotionEvent.ACTION_UP, 1f, 1f ,0);
        onTouchEvent(downEvent);
        onTouchEvent(upEvent);
    }



}