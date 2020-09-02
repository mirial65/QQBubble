package com.example.wanandroidtest;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;


import com.example.wanandroidtest.widget.BezierDemo;
import com.example.wanandroidtest.widget.DragBubbleView;
import com.example.wanandroidtest.widget.FilterView;
import com.example.wanandroidtest.widget.FilterView2;
import com.example.wanandroidtest.widget.GuaGuaCard;
import com.example.wanandroidtest.widget.LinearShader;

public class MainActivity extends AppCompatActivity  {
    private DragBubbleView mDragBubbleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragBubbleView = findViewById(R.id.drag_bubble_view);
        findViewById(R.id.btn_recover).setOnClickListener(v -> {
            mDragBubbleView.recover();
        });
//        setContentView(new BezierDemo(this));
//        setContentView(new GuaGuaCard(this));
    }

}