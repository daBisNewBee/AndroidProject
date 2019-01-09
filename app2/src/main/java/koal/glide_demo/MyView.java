package koal.glide_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class MyView extends android.support.v7.widget.AppCompatTextView {

    public MyView(Context context) {
        super(context);
        System.out.println("MyView.MyView");
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        System.out.println("MyView.MyView AttributeSet");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("MyView.onDraw");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("MyView.onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        System.out.println("MyView.onLayout");
    }
}
