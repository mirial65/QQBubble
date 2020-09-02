package com.example.wanandroidtest.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {
    public static Point getDisplay(Context context) {
        Point point = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        point.x = dm.widthPixels;
        point.y = dm.heightPixels;
        return point;

    }
}
