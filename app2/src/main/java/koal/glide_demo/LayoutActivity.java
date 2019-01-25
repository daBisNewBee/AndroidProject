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
 *  "Include" 的用法注意：（实现"布局模块化"）
 *  1. 技巧：使用IDE提取子layout：
 *     "Refactor" -> "Extract" -> "Layout" （抽取布局）
 *     "Refactor" -> "Extract" -> "Style" （抽取样式）
 *
 *  2. include标签中的属性，会复写子layout的属性
 *     必须复写width、height属性，其他layout属性才会生效
 *     建议：
 *     1. 在子layout设置好好宽高位置
 *     2. id同名
 *
 *  3. 技巧："tools:showIn"的使用方便开发
 *
 *  参考：
 *  https://developer.android.google.cn/training/improving-layouts/reusing-layouts
 *
 *  "Merge"的用法注意：（减少UI层级）
 *
 *  1. ViewStub标签中的layout布局不能使用merge标签
 *  "<merge /> can be used only with a valid ViewGroup root and attachToRoot=true"
 *
 *  2. merge标签必须使用在根布局
 *
 *  3. 对merge标签的属性设置无效，因为merge是个tag，不是view！
 *     比如，设置的id无法find等。
 *
 *  4. 小心：在根布局include标签下声明id， 若子layout为merge，那么该id就找不到了。原因如3
 *
 *  5. 一般配合include使用，常用于根布局为LinearLayout、FrameLayout，
 *    RelativeLayout其实也可以用，只是位置关系容易混乱
 *
 *  6. 根布局是FrameLayout且不需要设置background或padding等属性,可以用merge代替
 *    TODO：为什么不需要设置bg、padding等属性？
 *
 *  大总结：
 *  布局优化三大方法：
 *  1. include：布局模块化，但是有嵌套冗余布局的可能
 *  2. merge：减少层级
 *  3. viewStub：延迟加载
 *
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

        TextView nestedTv = findViewById(R.id.nested_textview);
        nestedTv.setText("在主app中访问include布局中的textview.");
        // 验证子layout的id被outer复写
        View outerLayout = findViewById(R.id.outter_layout_id);
        System.out.println("outerLayout = " + outerLayout);
        /*
        * outerLayout = android.widget.FrameLayout{77dfe96 V.E...... ......ID 0,0-0,0 #7f070054 app:id/outter_layout_id}
         * */
        View interLayout = findViewById(R.id.innder_layout_id);
        System.out.println("不应该有值 interLayout = " + interLayout);

        /*
        * 考虑：
        * "id_cannot_be_found"的id为什么找不到？
        * */
        View viewCannotBeFound = findViewById(R.id.id_cannot_be_found);
        System.out.println("viewCannotBeFound = " + viewCannotBeFound);
    }
}
