package koal.glide_demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

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
 */
public class FpsActivity extends AppCompatActivity {

    private ViewGroup mRootView;
    private View mXmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_fps, null, false);
        setContentView(mRootView);
        findViewById(R.id.main_fps_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addViewXmTextView();
                new FpsTest().startFps();
            }
        });
        mXmView = findViewById(R.id.main_test_btn);
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