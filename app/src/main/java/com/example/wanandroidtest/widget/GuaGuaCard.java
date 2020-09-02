package com.example.wanandroidtest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.wanandroidtest.R;

import java.util.Map;

public class GuaGuaCard extends View {

    private static final String TAG = "GuaGuaCard";
    private Paint mPaint;
    private Bitmap mBaseBitmap;
    private Bitmap mSRCBitmap;
    private Bitmap mDSTBitmap;
    private Path mPath;

    public GuaGuaCard(Context context) {
        this(context, null);
    }

    public GuaGuaCard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaGuaCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(40);
        mBaseBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.guaguaka_text, null));
        mSRCBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.guaguaka, null));
        mDSTBitmap = Bitmap.createBitmap(mSRCBitmap.getWidth(), mSRCBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mPath = new Path();
    }

    /**
     * 这个函数最先调用，所以mPath需要在init中初始化。
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: " + 1);
        super.onDraw(canvas);
        //底图
        canvas.drawBitmap(mBaseBitmap, 0, 0, mPaint);
        //保存图层
        int layer = canvas.saveLayer(0, 0, mBaseBitmap.getWidth(), mBaseBitmap.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //新图层上的新画布
        Canvas c = new Canvas(mDSTBitmap);
        c.drawPath(mPath, mPaint);
        //目标图像
        canvas.drawBitmap(mDSTBitmap, 0, 0, mPaint);
        //xfermode模式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        //源图
        canvas.drawBitmap(mSRCBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        //恢复图层
        canvas.restoreToCount(layer);
    }
    float lastX = 0, lastY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + 2);


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                mPath.moveTo(lastX, lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = event.getX();
                float curY = event.getY();
                mPath.quadTo(lastX, lastY, curX, curY);
                lastX = curX;
                lastY = curY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        //这里应该是主线程中（this）
        invalidate();
        //postInvalidate();
        return true;
    }
}
