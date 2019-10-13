package com.exa.cusview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PressLineView extends View {

    Paint paint;
    PointF pointF;
    private List<PointF> grafics = new ArrayList<>();
    int[] colors = new int[]{Color.RED, Color.BLACK, Color.YELLOW, Color.BLUE};

    public PressLineView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public PressLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("test", "onMeasure() called with: widthMeasureSpec = [" + widthMeasureSpec + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d("test", "onLayout() called with: changed = [" + changed + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "]");
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("test", "onDraw() called with: canvas = [" + canvas + "] size:" + grafics.size());
        // 变色效果
        int colorIndex = new Random().nextInt(colors.length);
        Log.d("test", "onDraw: colorIndex :" + colorIndex);
        paint.setColor(colors[colorIndex]);
        for (PointF one:grafics) {
            canvas.drawPoint(one.x, one.y, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("test", "onTouchEvent() called with: event = [" + event + "]");
        grafics.add(new PointF(event.getX(), event.getY()));
        invalidate();
        return true;
    }
}
