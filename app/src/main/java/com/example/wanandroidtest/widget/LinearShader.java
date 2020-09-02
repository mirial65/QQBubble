package com.example.wanandroidtest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;

public class LinearShader extends AppCompatTextView {

    private static final String TAG = "LinearShader";
    private Paint mPaint;

    //这里最好不要用Color.颜色，有点不同。建议直接用16进制的数。
    private int[] color = {
            0xffffff, 0xffffffff, 0xffffff};
    private int mTranslate = 0;
    private int mInt = 20;

    private LinearGradient mLinearGradient;
    private float mTextWidth;
    private float mGradientWidth;

    public LinearShader(Context context) {
        this(context, null);
    }

    public LinearShader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearShader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = getPaint();
        mTextWidth = mPaint.measureText((String) getText());
        mGradientWidth = mTextWidth/getText().length() * 3;
        //注意这个模式的作用，将最后一个像素进行拉伸。
        mLinearGradient = new LinearGradient(-mGradientWidth, 0, 0, 0, color, null, Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: " + 1);
        mTranslate += mInt;
        if (mTranslate > mTextWidth + mGradientWidth || mTranslate < 1) {
            mInt = -mInt;
        }
        Matrix matrix = new Matrix();
        matrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(matrix);
        postInvalidateDelayed(50);
    }

}
