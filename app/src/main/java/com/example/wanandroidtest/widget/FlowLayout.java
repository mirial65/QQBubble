package com.example.wanandroidtest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    private static final String TAG = "FlowLayout";
    //统一设置间隔，比较方便
    //注意在什么地方用到了间隔！
    private int horizontalSpace = dip_2px(16);
    private int verticalSpace = dip_2px(8);


    private int dip_2px(int dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (scale * dpValue + 0.5f);
    }

    List<List<View>> widList = new ArrayList<>();
    List<Integer> heiList = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //儿子需要的最大长宽，当模式为AT_MOST时。
    int childMostWidth = 0;
    int childMostHeight = 0;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int selWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selHeight = MeasureSpec.getSize(heightMeasureSpec);
        //目前宽高
        int curWidth = 0;
        int curHeight = 0;
        //如果他写在外面，就会多一倍的行数，因为onMeasure方法要执行两次，应该是能解决这个测量优化问题
        //widList = new ArrayList<>();
        //记录一行的view
        List<View> lineList = new ArrayList<>();
        int childCount = getChildCount();
        //爸爸的padding
        int PLeft = getPaddingLeft();
        int PTop = getPaddingTop();
        int PRight = getPaddingRight();
        int PBottom = getPaddingBottom();


        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            //第二个参数：父View左右（上下）Padding+子View左右（上下）Margin, 源码也没说详细, 但是你能看系统布局的源码，
            // 最好不设置padding。
            //感觉不太好操作
            int childWidMeasureSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin
                    + PLeft + PRight, lp.width);
            Log.d(TAG, "onMeasure: " + lp.leftMargin + "---" + PLeft);
            int childHeiMeasureSpec = getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin
                    + PTop + PBottom, lp.width);

            childView.measure(childWidMeasureSpec, childHeiMeasureSpec);
            //得到子view的长宽
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight() ;

            //换行操作
            if (curWidth + childWidth + horizontalSpace> selWidth) {
                //添加这一行的view及行高
                widList.add(lineList);
                heiList.add(curHeight);

                //记录最大的行宽及行高
                childMostWidth = Math.max(childMostWidth, curWidth + horizontalSpace);
                childMostHeight += curHeight + verticalSpace;
                //清空，重新记录
                lineList = new ArrayList<>();
                curHeight = 0;
                curWidth = 0;

            }
            //添加每个子view,这个需要放在第二个if的上方，
            lineList.add(childView);
            //当前行的宽度及高度
            curWidth += childWidth + horizontalSpace;
            curHeight = Math.max(curHeight, childHeight );
            //最后一行执行的操作
            if (curWidth < selWidth && i == childCount - 1) {
                widList.add(lineList);
                heiList.add(curHeight);

                childMostWidth = Math.max(childMostWidth, curWidth);
                childMostHeight += curHeight + verticalSpace;
            }


        }
        //模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //模式，打个log就知道是爸爸的还是儿子的模式了
        //onMeasure: 宽的mode：MeasureSpec.AT_MOST
        Log.d(TAG, "onMeasure: 宽的mode：" + (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST?
                "MeasureSpec.AT_MOST": "MeasureSpec.UNSPECIFIED"));
        int finallyWidth = (widthMode == MeasureSpec.EXACTLY ? selWidth : childMostWidth);
        int finallyHeight = (heightMode == MeasureSpec.EXACTLY ? selHeight : childMostHeight);
        setMeasuredDimension(finallyWidth, finallyHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int curL = 0 ;
        int curT = 0;
        for (int i = 0; i < widList.size(); i++) {
            List<View> lineView = widList.get(i);
            Log.d(TAG, "onLayout: " + widList.size() + "---" + lineView.size());
            for (int j = 0; j < lineView.size(); j++) {
                View childView = lineView.get(j);
                int left = curL ;
                int top = curT ;
                int right = left + childView.getMeasuredWidth();
                int bottom = top + childView.getMeasuredHeight();
                //如是是把 widList = new ArrayList<>()写在onMeasure     外        ，会有4行
                // 那么第一行和第三行的数据相同，所以相当于重新layout了， 所以第一行就为空了，就会出现这种情况

                childView.layout(left, top, right, bottom);
                curL = right + horizontalSpace;

            }
            curL = 0;
            curT = curT + heiList.get(i) + verticalSpace;
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}