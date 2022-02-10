package koal.glide_demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import koal.glide_demo.utlis.FpsTest;
import koal.glide_demo.xm.XmTextView;

/**
 *
 * "节拍器" Choreographer的几个理解:
 *
 * 1. 绘制下一帧：
 *    doFrame
 *
 * 2. 在每一帧中，依次执行下列的四种callBackType:(对应四种链表)
 *  CALLBACK_INPUT：输入
 *  CALLBACK_ANIMATION:动画
 *  CALLBACK_TRAVERSAL:遍历，执行measure、layout、draw
 *  CALLBACK_COMMIT：遍历完成的提交操作，用来修正动画启动时间
 *
 * 3. 如何取用事件？
 *  每次处理事件链表中最后一个事件
 *
 * 4. 生产事件：
 *    postCallback
 *    消费事件：
 *    doFrame
 *
 * 5. Vsync 信号(垂直同步信号)从哪来？
 *    1. scheduleVsyncLocked 注册
 *    2. onVsync (FrameDisplayEventReceiver) 回调
 *    3. onVsync 中执行doFrame
 *
 * 6. 每个线程一个"Choreographer实例"，由ThreadLocal保存，类似Looper
 *
 *
 * TODO：
 * 1. 在ViewRootImpl中有这么个方法scheduleTraversals，如果你深入过View的绘制流程，
 * 那你应该知道就是从这个方法开始触发performTraversals，来调出之后的measure，layout，draw
 *
 * 2. Android刷新频率60帧/秒，每隔16ms调onDraw绘制一次？
 * 答: 60帧/秒也是vsync信号的频率，但不一定每次vsync信号都会去绘制，
 *     先要应用端主动发起重绘，才会向"SurfaceFlinger"请求接收vsync信号，
 *     这样当vsync信号来的时候，才会真正去绘制
 *
 *
 * 参考：
 * Android屏幕刷新机制原理分析：
 * https://blog.csdn.net/my_csdnboke/article/details/106685736
 * 绘制过程，注意"常见的问题"
 *
 *
 *
 * "View的onAttachedToWindow和onDetachedFromWindow的调用时机分析" 的理解：
 * https://www.jianshu.com/p/e7b6fa788ae6
 *
 * 几个关键类：
 *
 * "ViewRootImpl" 的作用:
 * 1、管理和绘制view树
 * 2、触摸事件的中转
 * 3、负责和WMS通信
 * 4、负责连接view和window的桥梁事务
 *
 * "ViewTreeObserver" 视图树:
 *
 *
 */
public class FpsActivity extends AppCompatActivity {

    private ViewGroup mRootView;
    private View mXmView;
    private XmTextView mXmTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // "异步加载布局"
        new AsyncLayoutInflater(getBaseContext()).inflate(R.layout.activity_fps, null, (view, resid, parent) -> {
            Log.d("todo", "OnInflateFinishedListener ----> ");
            initUi();
        });
    }

    private void initUi() {
        mRootView = (ViewGroup) LayoutInflater.from(FpsActivity.this).inflate(R.layout.activity_fps, null, false);
        setContentView(mRootView);
        findViewById(R.id.main_fps_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addViewXmTextView();
                new FpsTest().startFps();
            }
        });
        mXmView = findViewById(R.id.main_test_btn);

        /**
         *
         *  android中获取view在布局中的高度和宽度:
         *  https://www.jianshu.com/p/a4d1093e2e59
         *
         *
         *  1. 使用 View.measure 测量 View
         *  该方法测量的宽度和高度可能与视图绘制完成后的真实的宽度和高度不一致
         *
         *  主动调用measure方法
         *  好处:可以立即获得宽和高
         *  坏处:多了一次测量过程
         */
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mXmTextView.measure(width, height);
        Log.d("measure", "mXmTextView.measure 获取到的宽高: ");
        Log.d("measure", "getMeasuredHeight: " + mXmTextView.getMeasuredHeight());
        Log.d("measure", "getMeasuredWidth: " + mXmTextView.getMeasuredWidth());


        /**
         * 2. 使用 ViewTreeObserver. OnPreDrawListener 监听事件
         *
         * 在视图将要绘制时调用该监听事件
         */
        mXmTextView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mXmTextView.getViewTreeObserver().removeOnPreDrawListener(this);
                Log.d("measure", "OnPreDrawListener 获取到的宽高: ");
                Log.d("measure", "getHeight: " + mXmTextView.getHeight());
                Log.d("measure", "getWidth: " + mXmTextView.getWidth());
                return true;
            }
        });

        /**
         * 3. 使用 ViewTreeObserver.OnGlobalLayoutListener 监听事件
         *
         * 在布局发生改变或者某个视图的可视状态发生改变时调用该事件，会被多次调用，
         * 因此需要在获取到视图的宽度和高度后执行 remove 方法移除该监听事件。
         */
        mXmTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mXmTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d("measure", "onGlobalLayout 获取到的宽高: ");
                Log.d("measure", "getHeight: " + mXmTextView.getHeight());
                Log.d("measure", "getWidth: " + mXmTextView.getWidth());
            }
        });

        /**
         * 6. 使用 View.OnLayoutChangeListener 监听事件
         *
         * 在视图的 layout 改变时调用该事件，会被多次调用
         */
        mXmTextView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mXmTextView.removeOnLayoutChangeListener(this);
                Log.d("measure", "onLayoutChange 获取到的宽高: ");
                Log.d("measure", "getHeight: " + mXmTextView.getHeight());
                Log.d("measure", "getWidth: " + mXmTextView.getWidth());
            }
        });
        /**
         * 7. 使用 View.post() 方法
         */
        mXmTextView.post(() -> {
            Log.d("measure", "View.post 获取到的宽高: ");
            Log.d("measure", "getHeight: " + mXmTextView.getHeight());
            Log.d("measure", "getWidth: " + mXmTextView.getWidth());
        });
    }

    /**
     * getWidth()方法和getMeasureWidth()区别:
     *
     * "onWindowFocusChanged: getWidth = 200 getMeasuredWidth = 199"
     *
     * 验证结论：
     *  1. getWidth = mRight - mLeft，在onLayout后决定
     *     getMeasuredWidth = setMeasuredDimension的值，在onMeasure后决定
     *  2. 获得宽度：
     *     在onLayout中，一般使用getMeasuredWidth
     *     在onLayout 以外的地方，一般使用getWidth
     *  3. fixme: "getWidth"更准确！！代表实际宽度，因为onLayout阶段比onMeasure靠后，更准确
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("XmViewGroup", "onWindowFocusChanged: getWidth = " + mXmView.getWidth()
                + " getMeasuredWidth = " + mXmView.getMeasuredWidth());
    }

    // 验证：各种布局下需要几次onMeasure次数
    void addViewXmTextView() {
        XmTextView xmTextView = new XmTextView(this);
        xmTextView.setText("newTextView");
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRootView.addView(xmTextView, params);
    }
}