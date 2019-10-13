package com.exa.cusview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by user on 2018/7/26.
 */

public class MyView1 extends View {

    public MyView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        System.out.println("MyView1.onLayout");
    }

    /**
     * 自定义View之Canvas用法:
     * https://www.jianshu.com/p/541da78c921e
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, 500, 500, paint);

        // 缩放0.5
        canvas.scale(0.5f, 0.5f);
        canvas.drawRect(0, 0, 500, 500, paint);

        // 缩放1.5
        canvas.scale(1.5f, 1.5f);
        canvas.drawRect(0, 0, 500, 500, paint);

        //旋转
//        canvas.rotate(45);
//        paint.setColor(Color.BLUE);
//        canvas.drawRect(0, 0, 500, 500, paint);

        // 平移操作.  第一个参数是 x 轴平移距离，第二个是 y 平移的距离
        canvas.translate(50, 50);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, 500, 500, paint);

        canvas.translate(-100, -100);
        paint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, 500, 500, paint);

        //画一个点
        canvas.drawPoint(400, 400, paint);
        //画多个点
        canvas.drawPoints(new float[]{60,400,65,400,70,400}, paint);

        // 画文字
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText("12345!", 100,100, paint);
        canvas.drawText("12345！",2,5, 200, 200, paint);

        // 画圆
        canvas.drawCircle(300, 300, 100, paint);

        // 画椭圆
        paint.setColor(Color.GREEN);
        RectF oval = new RectF(150, 200, 500, 400);
        canvas.drawOval(oval, paint);

        // 画线
        paint.setColor(Color.RED);
        canvas.drawLine(100, 100, 150, 150, paint);
        // 同时绘制多条线
        canvas.drawLines(new float[]{200f,200f,400f,400f,450f,450f,500f,500f}, paint);

        // 画圆弧
        canvas.drawArc(oval, 20, 180, false, paint);

        // 画贝塞尔曲线
        paint.setStyle(Paint.Style.STROKE);
        Path path2=new Path();
        path2.moveTo(100, 100);//设置Path的起点
        /**
         * 参数1、2：x1，y1为控制点的坐标值
         * 参数3、4：x2，y2为终点的坐标值
         */
        path2.quadTo(300, 100, 400, 400); //设置贝塞尔曲线的控制点坐标和终点坐标
        path2.quadTo(500, 700, 800, 800);
        canvas.drawPath(path2, paint);//画出贝塞尔曲线

        System.out.println("MyView1.onDraw");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("MyView1.onMeasure");
    }
}
