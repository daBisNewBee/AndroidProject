package koal.glide_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
 *
 * ViewStub（"延迟化加载"）的用法注意点：
 *
 * 1. 若要反复显示、隐藏一个布局，需要使用View的可见性来控制。
 *    不是ViewStub的原因是：viewStub只能被inflat一次，之后会被置空
 *    (可以用"Layout Inspector"来分析inflat前后的布局差异，参考doc下png)
 *
 * 2. ViewStub只能用来Inflate一个布局文件，而不是某个具体的View，当然也可以把View写在某个布局文件中。
 *
 * 3. setVisibility 的巧妙使用：
 *    通过该方法实现inflat
 *
 */
public class LayoutActivity extends Activity {

    private static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.frame_layout);
//        setContentView(R.layout.linear_layout);
//        setContentView(R.layout.relative_layout);
        setContentView(R.layout.compomnent_layout);

        findViewById(R.id.btn_inflat_viewstub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visib = count%2 == 0 ? View.VISIBLE : View.INVISIBLE;
                if (count %2 ==  0){
                    // 第二次 find 返回 null!由于第一次inflat时ViewStub已被移除
                    ViewStub viewStub = findViewById(R.id.viewstub_img);
//                    viewStub.setVisibility(visib);
//                    viewStub.setVisibility(visib);
                    viewStub.inflate();
                    ImageView imageView = findViewById(R.id.viewstub_imageView);
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else if (count %2 == 1){
                    ViewStub viewStub = findViewById(R.id.viewstub_tv);
                    // 实际第一次set的作用是inflat，第二次set才是设置可见性
                    viewStub.setVisibility(visib);
                    viewStub.setVisibility(visib);
//                    viewStub.inflate();
                    TextView tv = findViewById(R.id.viewstub_textview);
                    tv.setText("this is text set after inflate.");
                }else {

                }
                count++;
            }
        });
    }
}
