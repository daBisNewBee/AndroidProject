package koal.glide_demo.xm;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * "MeasureSpec是什么？"
 *
 * 父布局对子布局相应的宽高要求；即老子对小孩的大小要求！
 *
 * 几种模式：
 * 1. UNSPECIFIED：
 *  想多大就多大。
 *  常见于系统内部控件，例如 ListView、ScrollView。
 *
 * 2. EXACTLY：
 *   父布局对子布局的宽高大小有明确的要求
 *   100dp，或者 match_parent(实质上就是屏幕大小)
 *
 * 3. AT_MOST：
 *   子布局想要多大就可以多大，不超过父
 *   一般对应wrap_content
 *
 * ex:
 * 1. 父match，子wrap:
 *      parent EXACTLY
 *      parent EXACTLY
 *      koal.glide_demo.xm.XmLinearLayout widthSize: 1080
 *      koal.glide_demo.xm.XmLinearLayout heightSize: 2122
 *      child AT_MOST
 *      child AT_MOST
 *      koal.glide_demo.xm.XmView widthSize: 1080
 *      koal.glide_demo.xm.XmView heightSize: 2122
 *
 * 2. 父wrap，子match
 *
 * ps：
 * 1. 为什么你的自定义View wrap_content不起作用？
 * 实际效果为父容器的大小
 * https://www.jianshu.com/p/ca118d704b5e
 *
 */
public class XmTextView extends AppCompatTextView {

    private static final String TAG = "XmTextView";

    public XmTextView(Context context) {
        super(context);
    }

    public XmTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XmTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void printMode(int mode) {
        if (mode == MeasureSpec.UNSPECIFIED) {
            Log.d(TAG, "child UNSPECIFIED");
        } else if (mode == MeasureSpec.EXACTLY) {
            Log.d(TAG, "child EXACTLY");
        } else if (mode == MeasureSpec.AT_MOST) {
            Log.d(TAG, "child AT_MOST");
        } else {
            Log.d(TAG, "child UNKONWN");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
    }

    /**
     * 5. 重写 View 的 onLayout 方法
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: ");
        Log.d("measure", "onLayout 获取到的宽高: ");
        Log.d("measure", "getHeight: " + getHeight());
        Log.d("measure", "getWidth: " + getWidth());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate: ");
    }

    /**
     * 4. 重写 View 的 onSizeChanged 方法
     *
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "], oldw = [" + oldw + "], oldh = [" + oldh + "]");
        Log.d("measure", "onSizeChanged 获取到的宽高: ");
        Log.d("measure", "getHeight: " + getHeight());
        Log.d("measure", "getWidth: " + getWidth());
    }

    /**
     * "普通View的MeasureSpec的创建规则":
     *
     * https://myhub.blog.csdn.net/article/details/82378452
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: ");
        printMode(MeasureSpec.getMode(widthMeasureSpec));
        printMode(MeasureSpec.getMode(heightMeasureSpec));
        Log.d(TAG, this.getClass().getName() +" widthSize: " + MeasureSpec.getSize(widthMeasureSpec));
        Log.d(TAG, this.getClass().getName() +" heightSize: " + MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 下面展示，如何设置view的最大宽高？
         */
        /*
        int mMaxWidth = 200;
        int mMaxHeight = 100;
        // Adjust width as necessary
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mMaxWidth < width) {
            int mode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, mode);
        }
        // Adjust height as necessary
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mMaxHeight < height) {
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, mode);
        }
        Log.d("MeasureSpec", this.getClass().getName() +" widthSize: " + MeasureSpec.getSize(widthMeasureSpec));
        Log.d("MeasureSpec", this.getClass().getName() +" heightSize: " + MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         */
    }
}
