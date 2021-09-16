package koal.glide_demo.jetpack;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class XmLifecycleObserver implements LifecycleObserver {

    private static final String TAG = "livedata";

    public XmLifecycleObserver() {
    }

    // 好处：在内部感知Activity或者Fragment的变化，追踪生命周期并做相应处理
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void XmCreate() {
        Log.d(TAG, "XmCreate() called");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void XmResume() {
        Log.d(TAG, "XmOnResume() called");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void XmPause() {
        Log.d(TAG, "XmPause() called");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void XmStop() {
        Log.d(TAG, "XmStop() called");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void XmDestroy() {
        Log.d(TAG, "XmDestroy() called");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void XmStart() {
        Log.d(TAG, "XmStart() called");
    }
}
