package com.example.wanandroidtest.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.example.wanandroidtest.R;

public class DragBubbleView extends View {
    private static final String TAG = "DragBubbleView";

    /**
     * 气泡的状态
     */
    private enum bubbleState {
        BUBBLE_STATE_DEFAULT,
        BUBBLE_STATE_CONNECT,
        BUBBLE_STATE_APART,
        BUBBLE_STATE_DESTROY,
    }

    ;
    //气泡的默认状态
    private bubbleState mCurState;
    /**
     * 气泡的一些自定义属性
     */
    private float mBubbleRadius;
    private int mBubbleColor;
    private float mTextSize;
    private int mTextColor;
    private String mTextChars;
    //不动气泡的半径大小
    private float mBubbleStillRadius;
    //移动气泡的大小
    private float mBubbleMoveRadius;
    //不动气泡的圆心
    private PointF mBubbleStillCenter;
    //可动气泡的圆心
    private PointF mBubbleMoveCenter;
    //圆心距
    private float mDistance;
    //两气泡相连的最大圆心距离
    private float mMaxDistance;
    //气泡的画笔
    private Paint mBubblePaint;
    //文字的画笔
    private Paint mTextPaint;
    //文本绘制的rect
    private Rect mTextRect;
    //爆炸时的画笔
    private Paint mBurstPaint;
    //爆炸绘制区域
    private Rect mBurstRect;
    //气泡爆炸的图片数组
    private int[] mBurstDrawables = {
            R.drawable.burst_1, R.drawable.burst_2, R.drawable.burst_3,
            R.drawable.burst_4, R.drawable.burst_5

    };
    //气泡爆炸的bitmap数组
    private Bitmap[] mBurstBitmaps;
    //气泡爆炸的当前index
    private int mCurBurstIndex;
    //判断是否正在执行爆炸动画
    private boolean mIsBurstAnim;
    //手指触摸偏移量
    private float MOVE_OFFSET;
    /**
     * 贝塞尔曲线path
     */
    private Path mBezierPath;

    public DragBubbleView(Context context) {
        this(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取到自定义的属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, 0);
        //至于为什么能从xml中获取到值，是因为attrs属性
        mBubbleRadius = array.getDimension(R.styleable.DragBubbleView_bubble_radius, 0);
        mBubbleColor = array.getColor(R.styleable.DragBubbleView_bubble_color, 0);
        mTextSize = array.getDimension(R.styleable.DragBubbleView_bubble_textSize, 0);
        mTextColor = array.getColor(R.styleable.DragBubbleView_bubble_textColor, 0);
        mTextChars = array.getString(R.styleable.DragBubbleView_bubble_text);
        array.recycle();
        //还要对数据进行初始化操作
        //对半径方面
        mBubbleStillRadius = mBubbleRadius;
        mBubbleMoveRadius = mBubbleRadius;
        mMaxDistance = 8 * mBubbleRadius;
        MOVE_OFFSET = mMaxDistance / 4;
        //对三个画笔
        //1.气泡的画笔
        mBubblePaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setColor(mBubbleColor);
        mBubblePaint.setStyle(Paint.Style.FILL);
        //2.文本的画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStyle(Paint.Style.FILL);
        //3.爆炸画笔
        mBurstPaint = new Paint();
        mBurstPaint.setAntiAlias(true);
        mBurstPaint.setFilterBitmap(true);
        mBurstBitmaps = new Bitmap[mBurstDrawables.length];
        for (int i = 0; i < mBurstBitmaps.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mBurstDrawables[i]);
            mBurstBitmaps[i] = bitmap;
        }

        //两个矩阵
        mTextRect = new Rect();
        mBurstRect = new Rect();

        //贝塞尔曲线
        mBezierPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w, h);
    }

    private void init(int width, int height) {
        if (mBubbleStillCenter == null) {
//            这么写是错误的
//            mBubbleStillCenter.x = width / 2;
//            mBubbleStillCenter.y = height / 2;
            mBubbleStillCenter = new PointF(width * 1.0f / 2, height * 1.0f / 2);
        } else {
            mBubbleStillCenter.set(width * 1.0f / 2, height * 1.0f / 2);
        }
        if (mBubbleMoveCenter == null) {
//            mBubbleMoveCenter.x = width/2;
//            mBubbleMoveCenter.y = height/2;
            mBubbleMoveCenter = new PointF(width * 1.0f / 2, height * 1.0f / 2);
        } else {
            mBubbleMoveCenter.set(width * 1.0f / 2, height * 1.0f / 2);
        }
        mCurState = bubbleState.BUBBLE_STATE_DEFAULT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画移动的气泡和文字
        if (mCurState != bubbleState.BUBBLE_STATE_DESTROY) {

            canvas.drawCircle(mBubbleMoveCenter.x, mBubbleMoveCenter.y, mBubbleMoveRadius, mBubblePaint);
            mTextPaint.getTextBounds(mTextChars, 0, mTextChars.length(), mTextRect);
            //一直以为是左上角开始的， drawText正确的画法是从基线开始画的
//            canvas.drawLine(0, mBubbleMoveCenter.y + mTextRect.centerY(), 3000, mBubbleMoveCenter.y + mTextRect.centerY(), mTextPaint);
            canvas.drawLine(0, mBubbleMoveCenter.y - mTextRect.centerY(), 3000, mBubbleMoveCenter.y - mTextRect.centerY(), mTextPaint);
//            Rect rct = new Rect(50, 50 , 100, 100);
//            Log.d(TAG, "onDraw: " + rct.top + ":" + rct.bottom);
            Log.d(TAG, "onDraw: " + mBubbleMoveCenter.y + ":" + mTextRect.centerY() + ":" + mTextRect.top + ":" + mTextRect.bottom);
//            canvas.drawText(mTextChars, mBubbleMoveCenter.x - mTextRect.centerX(), mBubbleMoveCenter.y + mTextRect.centerY(), mTextPaint);
            //最好是用下面的来写
            canvas.drawText(mTextChars, mBubbleMoveCenter.x - mTextRect.width() * 1.0f / 2, mBubbleMoveCenter.y + mTextRect.height() * 1.0f / 2, mTextPaint);
        }
        //画气泡相连的状态，这里要用到贝塞尔曲线
        if (mCurState == bubbleState.BUBBLE_STATE_CONNECT) {

            //只有连接才会画静止的气泡
            canvas.drawCircle(mBubbleStillCenter.x, mBubbleStillCenter.y, mBubbleStillRadius, mBubblePaint);
            //画两根贝塞尔曲线，并把曲线闭合
            //1.得到点的坐标，注意这里的坐标系在哪里
            //这个点是控制点
            float iAnchorX = (mBubbleStillCenter.x + mBubbleMoveCenter.x) / 2;
            float iAnchorY = (mBubbleStillCenter.y + mBubbleMoveCenter.y) / 2;
            //角度
            double atan = Math.atan((mBubbleMoveCenter.y - mBubbleStillCenter.y) / (mBubbleMoveCenter.x - mBubbleStillCenter.x));
            //四个点的坐标
            float bubbleStillStartX = (float) (mBubbleStillCenter.x - mBubbleStillRadius * Math.sin(atan));
            float bubbleStillStartY = (float) (mBubbleStillCenter.y + mBubbleStillRadius * Math.cos(atan));
            float bubbleStillEndX = (float) (mBubbleStillCenter.x + mBubbleStillRadius * Math.sin(atan));
            float bubbleStillEndY =  (float) (mBubbleStillCenter.y - mBubbleStillRadius * Math.cos(atan));
            Log.d(TAG, "onDraw: " + bubbleStillStartX + ":" + bubbleStillEndX);

            float bubbleMoveStartX = (float) (mBubbleMoveCenter.x - mBubbleMoveRadius * Math.sin(atan));
            float bubbleMoveStartY = (float) (mBubbleMoveCenter.y + mBubbleMoveRadius * Math.cos(atan));
            float bubbleMoveEndX = (float) (mBubbleMoveCenter.x + mBubbleMoveRadius * Math.sin(atan));
            float bubbleMoveEndY = (float) (mBubbleMoveCenter.y - mBubbleMoveRadius * Math.cos(atan));

            //清除先前绘制的路线，只保留当前的路线
            mBezierPath.reset();
            //画上半弧
            //moveTo用于移动画笔，不会绘制
            mBezierPath.moveTo(bubbleStillStartX, bubbleStillStartY);
            mBezierPath.quadTo(iAnchorX, iAnchorY, bubbleMoveStartX, bubbleMoveStartY);
            //要形成闭合曲线
            //画下半弧
            mBezierPath.lineTo(bubbleMoveEndX, bubbleMoveEndY);
            mBezierPath.quadTo(iAnchorX, iAnchorY, bubbleStillEndX, bubbleStillEndY);
            //闭合曲线
            mBezierPath.close();
            canvas.drawPath(mBezierPath, mBubblePaint);
        }
        if (mIsBurstAnim) {
            mBurstRect.set((int) (mBubbleMoveCenter.x - mBubbleMoveRadius), (int) (mBubbleMoveCenter.y - mBubbleMoveRadius),
                    (int) (mBubbleMoveCenter.x + mBubbleMoveRadius), (int) (mBubbleMoveCenter.y + mBubbleMoveRadius));
            canvas.drawBitmap(mBurstBitmaps[mCurBurstIndex], null, mBurstRect, mBurstPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //求二者的距离
                mDistance = (float) Math.hypot(event.getX() - mBubbleStillCenter.x, event.getY() - mBubbleStillCenter.y);
                if (mDistance < mBubbleRadius + MOVE_OFFSET) {
                    mCurState = bubbleState.BUBBLE_STATE_CONNECT;
//                    Log.d(TAG, "onTouchEvent: 1");
                } else {
                    mCurState = bubbleState.BUBBLE_STATE_DEFAULT;
//                    Log.d(TAG, "onTouchEvent: 2");
                }
            }

            break;
            case MotionEvent.ACTION_MOVE:
                if (mCurState != bubbleState.BUBBLE_STATE_DEFAULT) {

                    mBubbleMoveCenter.x = event.getX();
                    mBubbleMoveCenter.y = event.getY();
                    mDistance = (float) Math.hypot(event.getX() - mBubbleStillCenter.x, event.getY() - mBubbleStillCenter.y);
                    if (mCurState == bubbleState.BUBBLE_STATE_CONNECT) {
                        if (mDistance < mMaxDistance - MOVE_OFFSET) {
                            mBubbleStillRadius = mBubbleRadius - mDistance / 8;
                        } else {
                            //只要移动到了分离状态，就不会画stillBubble了
                            mCurState = bubbleState.BUBBLE_STATE_APART;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurState == bubbleState.BUBBLE_STATE_CONNECT) {
                    startBubbleRestAnim();
                } else if (mCurState == bubbleState.BUBBLE_STATE_APART) {
                    if (mDistance < 2 * mBubbleRadius) {
                        startBubbleRestAnim();
                    } else {
                        startBubbleBurstAnim();
                    }
                }
                performClick();
                break;
        }
        return true;
    }

    private void startBubbleRestAnim() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator animator = ValueAnimator.ofObject(new PointFEvaluator(), new PointF(mBubbleMoveCenter.x, mBubbleMoveCenter.y),
                    new PointF(mBubbleStillCenter.x, mBubbleStillCenter.y));
            animator.setDuration(200);
            //这个插值器会有回弹的效果
            animator.setInterpolator(new OvershootInterpolator(5f));

            //动画结束后调用
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mCurState = bubbleState.BUBBLE_STATE_DEFAULT;
                }
            });
            animator.addUpdateListener(animation -> {
                //只是让那个移动气泡的位置发生改变
                mBubbleMoveCenter = (PointF) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }
    }

    private void startBubbleBurstAnim() {
        //先将气泡改为消失状态
        mCurState = bubbleState.BUBBLE_STATE_DESTROY;
        mIsBurstAnim = true;
        //做一个int型的属性动画，从0到mBurstBitmaps.length()
        ValueAnimator animator = ValueAnimator.ofInt(0, mBurstBitmaps.length);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsBurstAnim = false;
            }
        });
        animator.addUpdateListener(animation -> {
            mCurBurstIndex = (int) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void recover() {
        init(getWidth(), getHeight());
        invalidate();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
