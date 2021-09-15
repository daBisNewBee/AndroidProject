package koal.glide_demo.jetpack;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class XmLifecycleObserver implements LifecycleObserver, LifecycleOwner {

    private static final String TAG = "livedata";

    private Lifecycle mRegistry;

    public XmLifecycleObserver(Lifecycle registry) {
        mRegistry = registry;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void XmCreate() {
//        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
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
//        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        Log.d(TAG, "XmStart() called");
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
//        mRegistry = new LifecycleRegistry(this);
        return mRegistry;
    }
}
