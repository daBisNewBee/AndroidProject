package koal.glide_demo;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 * 几种布局嵌套时，view的若干方法回调次数比较：
 *
 *                        onMeasure     onLayout    onDraw
 * 一层   FrameLayout      2              1           1
 *       LinearLayout     2(4,当weight时) 1           1
 *       RelativeLayout   4              1           1
 *
 * 二层   FrameLayout      2              1            1
 *       LinearLayout     2(4,当weight时) 1            1
 *       RelativeLayout   4(TODO: 不是8？) 1             1
 *                        (TODO:为何三层时，relative 的 onMeasure 也是4？)
 *
 *
 *  结论：
 *  1. LinearLayout 如果有可能，尽量避免使用weight。
 *      LinearLayout 在有weight时，也会调用子View2次onMeasure
 *
 *  2. 在不增加层级的情况下优先使用LinearLayout和Framelayout,避免使用RelativeLayout
 *      RelativeLayout会让子View调用2次onMeasure
 *
 *  3. 减少层级，从而减少measure和layout
 *
 */
public class LayoutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.frame_layout);
//        setContentView(R.layout.linear_layout);
        setContentView(R.layout.relative_layout);
    }
}
