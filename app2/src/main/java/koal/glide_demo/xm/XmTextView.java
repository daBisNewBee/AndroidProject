package koal.glide_demo.xm;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

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
public class XmTextView extends android.support.v7.widget.AppCompatTextView {

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
            Log.d("MeasureSpec", "child UNSPECIFIED");
        } else if (mode == MeasureSpec.EXACTLY) {
            Log.d("MeasureSpec", "child EXACTLY");
        } else if (mode == MeasureSpec.AT_MOST) {
            Log.d("MeasureSpec", "child AT_MOST");
        } else {
            Log.d("MeasureSpec", "child UNKONWN");
        }
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
        printMode(MeasureSpec.getMode(widthMeasureSpec));
        printMode(MeasureSpec.getMode(heightMeasureSpec));
        Log.d("MeasureSpec", this.getClass().getName() +" widthSize: " + MeasureSpec.getSize(widthMeasureSpec));
        Log.d("MeasureSpec", this.getClass().getName() +" heightSize: " + MeasureSpec.getSize(heightMeasureSpec));
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
