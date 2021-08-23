package koal.glide_demo.xm;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

class XmViewGroup extends ViewGroup {

    public XmViewGroup(Context context) {
        super(context);
    }

    public XmViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XmViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("XmViewGroup", "onMeasure() called with: widthMeasureSpec = [" + widthMeasureSpec + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View view = getChildAt(0);
        /**
         * 设置宽度值为100，MeasureSpec.EXACTLY是测量模式
         */
        measureChild(view, MeasureSpec.EXACTLY + 199, MeasureSpec.EXACTLY + 299);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("XmViewGroup", "onLayout() called with: changed = [" + changed + "], l = [" + l + "], t = [" + t + "], r = [" + r + "], b = [" + b + "]");
        View view = getChildAt(0);
        view.layout(100,100,300,500);
    }
}
