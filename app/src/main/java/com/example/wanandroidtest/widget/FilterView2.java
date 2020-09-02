package com.example.wanandroidtest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.wanandroidtest.R;
import com.example.wanandroidtest.utils.DisplayUtils;

public class FilterView2 extends View {

    private Paint mPaint;
    private Bitmap mBitmap;

    public FilterView2(Context context) {
        super(context);
        initConfig();
    }

    private void initConfig() {
        mPaint = new Paint();

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.love);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int screenWidth = DisplayUtils.getDisplay(getContext()).x;
        RectF rect1 = new RectF(screenWidth/2-200,200,screenWidth/2+200,600);
        mPaint.reset();
        canvas.drawBitmap(mBitmap, null, rect1, mPaint);

        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0.5f, 0, 0.5f, 0, 0,
                0.5f, 0, 0.5f, 0, 0,
                0.5f, 0, 0.5f, 0, 0,
                0, 0, 0, 1, 0,
        });

        RectF rect2 = new RectF(screenWidth / 2 - 200, mBitmap.getHeight(), screenWidth / 2 + 200, mBitmap.getHeight() + 400);
        mPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, null, rect2, mPaint);


    }
}
