package koal.glide_demo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

import koal.glide_demo.R;

/**
 *
 * 几种布局嵌套时，view的若干方法回调次数比较：
 *
 *                        onMeasure     onLayout    onDraw
 * 一层   FrameLayout      1              1           1
 *       LinearLayout     1(2,当weight时) 1           1
 *       RelativeLayout   2              1           1
 *  (ps:
 *      首次加载时，由于"ViewRootImpl"的关系，FrameLayout/LinearLayout的"onMeasure"会走两次，
 *      RelativeLayout的"onMeasure"走四次
 *  )
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
 *  4. 为什么避免"布局嵌套"很重要？
 *     因为父布局会影响子布局的测量、布局、绘制三个流程
 *
 *  5. 官方为啥建议用"RelativeLayout"？
 *     减少View层级：一个RelativeLayout好于两层LinearLayout
 *
 *  6. RelativeLayout 如何 onMeasure 两次？
 *     横向遍历一次
 *     竖向遍历一次
 *
 *  7. FrameLayout 的onMeasure 逻辑最少，相对简单
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
 *  2. merge：减少层级：干掉一个view层级
 *  3. viewStub：延迟加载
 *
 *  几个比较有用的结论：
 *  1. 怎样优化你的布局层级结构之RelativeLayout和LinearLayout及FrameLayout性能分析：
 *  https://blog.csdn.net/qq_18757557/article/details/80495405
 *
 *  2. Android UI布局的性能分析和优化措施：(比较全)
 *  https://blog.csdn.net/qq_28260521/article/details/78746396
 *
 */
public class LayoutActivity extends Activity {

    private static int count = 0;
    private ViewGroup mRootView;
    private View mLayoutAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.frame_layout);
//        setContentView(R.layout.linear_layout);
//        setContentView(R.layout.relative_layout);
        mRootView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.compomnent_layout, null, false);
        setContentView(mRootView);
//        setContentView(R.layout.compomnent_layout);

        infalteLayoutByJava();

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

    private void infalteLayoutByJava() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        // 必须设置textview的center！字体才会居中，这里设置没用！
//        params.gravity = Gravity.CENTER;
        TextView tv1 = new TextView(this);
        tv1.setTextSize(20);
        tv1.setGravity(Gravity.CENTER);
        tv1.setText("操他妈1");
        linearLayout.addView(tv1,params);

        TextView tv2 = new TextView(this);
        tv2.setTextSize(20);
        tv2.setGravity(Gravity.CENTER);
        tv2.setText("操他妈2");
        linearLayout.addView(tv2,params);

        mRootView.addView(linearLayout);
    }

    /*
    * 动态加载xml布局几个注意点：
    * 1. 若要使用"addView"加载，inflate时的"attachToRoot"必须为false
    * 2. attachToRoot 为 false 时，需要注意的"root"的输入（TODO: 原因未知）
    *    attachToRoot 为 true 时，root传啥都一样
    *
    * 其他：
    *   "动态添加Java布局":"infalteLayoutByJava"
    *
    * 参考：
    *   Android 动态添加布局的两种方式：
    *       https://www.jianshu.com/p/06c9c6685108
    * */
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.btn_dynamic_load_layout:
                // 可选方式：
//                View view1 = View.inflate(this, R.layout.record_ll_time_count, null);
                // 方法一：直接添加布局到父布局（缺点：无法自定义在父布局中的位置）
//                mLayoutAdded = LayoutInflater.from(this).inflate(R.layout.record_ll_time_count, mRootView, true);

                // 方法二：实际和方法一没有卵区别，不指定params的情况下.
                // ps: mRootView 若为"null", 子布局宽度会match_parent效果
//                mLayoutAdded = LayoutInflater.from(this).inflate(R.layout.record_ll_time_count, mRootView, false);
//                mRootView.addView(mLayoutAdded);

                // 方法三：
                int width  = DpPxSpActivity.dp2px(this, 118);
                int height  = DpPxSpActivity.dp2px(this, 118);
                mLayoutAdded = LayoutInflater.from(this).inflate(R.layout.record_ll_time_count, null, false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                mRootView.addView(mLayoutAdded, params);
                break;
            case R.id.btn_dynamic_unload_layout:
                if (mLayoutAdded != null) {
                    mRootView.removeView(mLayoutAdded);
                    mLayoutAdded = null;
                }
                break;
            default:
                break;
        }
    }
}
