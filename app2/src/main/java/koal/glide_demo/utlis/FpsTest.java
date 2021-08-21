package koal.glide_demo.utlis;

import android.util.Log;
import android.view.Choreographer;

public class FpsTest implements Choreographer.FrameCallback {

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
