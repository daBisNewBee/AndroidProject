package com.exa.cusview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by user on 2018/7/26.
 */

public class ViewGroup1 extends LinearLayout {

    public ViewGroup1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        System.out.println("ViewGroup1.ViewGroup1");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        System.out.println("ViewGroup1.dispatchDraw");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("ViewGroup1.onDraw");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("ViewGroup1.onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        System.out.println("ViewGroup1.onLayout");
    }
}
