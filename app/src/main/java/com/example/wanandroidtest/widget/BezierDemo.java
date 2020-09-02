package com.example.wanandroidtest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BezierDemo extends View {

    private Paint mPaint;
    private float startPointX, startPointY;
    private float endPointX, endPointY;
    private float controlX, controlY;
    private Path mPath;


    public BezierDemo(Context context) {
        this(context, null);
    }

    public BezierDemo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierDemo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        startPointX = 200;
        startPointY = 200;
        endPointX = 800;
        endPointY = 200;
        controlX = 400;
        controlY = 400;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(startPointX, startPointY);
        mPath.quadTo(controlX, controlY, endPointX, endPointY);
        canvas.drawPath(mPath, mPaint);
        canvas.drawCircle(controlX, controlY, 8, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //注意这里时event。getX方法， 而不只是getX方法；
            setBackgroundColor(Color.WHITE);
            controlX = event.getX();
            controlY = event.getY();
            invalidate();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setBackgroundColor(Color.rgb(66, 66,66));
            performClick();

        }
        return true;
    }

    @Override
    public boolean performClick() {
        Toast.makeText(getContext(), "changed", Toast.LENGTH_SHORT).show();
        return super.performClick();
    }
}
