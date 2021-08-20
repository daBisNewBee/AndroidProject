package koal.glide_demo;

import android.util.Log;
import android.view.Choreographer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import koal.glide_demo.utlis.HandlerManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps);
        findViewById(R.id.main_fps_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FpsTest().startFps();
            }
        });
    }

    static class FpsTest implements Choreographer.FrameCallback {
        private long mLastFrameTimeNanos = 0; //最后一次时间
        private long mFrameTimeNanos = 0; //本次的当前时间
        private int mFpsCount = 0;

        public void startFps() {
            mLastFrameTimeNanos = System.nanoTime();
            Choreographer.getInstance().postFrameCallback(this);
            HandlerManager.postOnBackgroundThreadDelay(runnable, 1000);
        }

        //定时任务
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                calculateFPS();
                HandlerManager.postOnBackgroundThreadDelay(runnable, 1000);
            }
        };

        private void calculateFPS() {
            if (mLastFrameTimeNanos == 0) {
                mLastFrameTimeNanos = mFrameTimeNanos;
                return;
            }
            float costTime = (float) (mFrameTimeNanos - mLastFrameTimeNanos) / 1000000000.0F;//纳秒转成毫秒。
            if (mFpsCount <= 0 && costTime <= 0.0F) {
                return;
            }
            int fpsResult = (int) (mFpsCount / costTime);
            Log.d("fps", "当前帧率为：" + fpsResult + " FpsCount = " + mFpsCount + " costTime = " + costTime);
            mLastFrameTimeNanos = mFrameTimeNanos;
            mFpsCount = 0;
        }

        @Override
        public void doFrame(long frameTimeNanos) {
            mFpsCount++;
            mFrameTimeNanos = frameTimeNanos;
            /*
            try {
                // fps会降到10帧
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
             */
            //注册下一帧回调
            Choreographer.getInstance().postFrameCallback(this);
        }
    }
}