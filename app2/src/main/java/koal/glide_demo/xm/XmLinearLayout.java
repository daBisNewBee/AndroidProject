package koal.glide_demo.xm;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class XmLinearLayout extends LinearLayout {

    public XmLinearLayout(Context context) {
        super(context);
    }

    public XmLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XmLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XmLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void printMode(int mode) {
        if (mode == MeasureSpec.UNSPECIFIED) {
            Log.d("MeasureSpec", "parent UNSPECIFIED");
        } else if (mode == MeasureSpec.EXACTLY) {
            Log.d("MeasureSpec", "parent EXACTLY");
        } else if (mode == MeasureSpec.AT_MOST) {
            Log.d("MeasureSpec", "parent AT_MOST");
        } else {
            Log.d("MeasureSpec", "parent UNKONWN");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        printMode(widthMode);
        printMode(heightMode);
        Log.d("MeasureSpec", this.getClass().getName() + " widthSize: " + widthSize);
        Log.d("MeasureSpec", this.getClass().getName() + " heightSize: " + heightSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
