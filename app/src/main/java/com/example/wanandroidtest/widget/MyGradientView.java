package com.example.wanandroidtest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.wanandroidtest.R;
import com.example.wanandroidtest.utils.DisplayUtils;

public class MyGradientView extends View {

    private Bitmap mBitmap;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int[] color = {Color.RED, Color.BLUE, Color.GREEN};
    private int mScreenWidth;

    public MyGradientView(Context context) {
        this(context, null);
    }

    public MyGradientView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.love)).getBitmap();
        mPaint = new Paint();
        mWidth = mBitmap.getWidth();
        mHeight = mBitmap.getHeight();
        Point point = DisplayUtils.getDisplay(context);
        mScreenWidth = point.x;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//        mPaint.setAntiAlias(true);
//        mPaint.setShader(shader);

        LinearGradient linearGradient = new LinearGradient(0, 0, mWidth,mHeight, color, null, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        canvas.drawRect(new Rect(0, 0, mWidth, mHeight), mPaint);

        //SweepGradient sweepGradient = new SweepGradient(300, 300, color, null);
        //mPaint.setShader(sweepGradient);
        //canvas.drawCircle(300, 300, 300, mPaint);

        //RadialGradient radialGradient = new RadialGradient(300, 300, 300, color, null, Shader.TileMode.CLAMP);
//        mPaint.setShader(radialGradient);
//        canvas.drawCircle(300, 300, 300, mPaint);

//        ComposeShader composeShader = new ComposeShader(linearGradient, shader, PorterDuff.Mode.MULTIPLY);
//        mPaint.setShader(composeShader);
//        canvas.drawRect(new Rect(0, 0, mWidth, mHeight), mPaint);
    }
}
