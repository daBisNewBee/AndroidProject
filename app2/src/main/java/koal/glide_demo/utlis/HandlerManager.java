package koal.glide_demo.utlis;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by nali on 2017/12/15.
 *
 * @author nali
 */

public class HandlerManager {

    private static ConcurrentMap<WeakReference<Object>
                            , CopyOnWriteArrayList<WeakReference<Runnable>>> mRunnableMap;

    private static class HolderClass {
        private final static Handler instance = new Handler(Looper.getMainLooper());
    }

    public static Handler obtainMainHandler() {
        return HolderClass.instance;
    }

    private static Handler mBackgroundHandler;

    private static Handler obtainBackgroundHandler() {
        if (mBackgroundHandler == null) {
            synchronized (Handler.class) {
                if (mBackgroundHandler == null) {
                    HandlerThread handlerThread = new HandlerThread("background-handler-thread");
                    handlerThread.start();
                    mBackgroundHandler = new Handler(handlerThread.getLooper());
                }
            }
        }

        return mBackgroundHandler;
    }

    public static void postOnUIThread(Runnable runnable) {
        obtainMainHandler().post(runnable);
    }

    public static void postOnUIThreadDelay4Kt(long delay, Runnable runnable) {
        postOnUIThreadDelay(runnable, delay);
    }
    public static void postOnUIThreadDelay(Runnable runnable, long delay) {
        obtainMainHandler().postDelayed(runnable, delay);
    }

    public static void postOnMainAuto(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            obtainMainHandler().post(runnable);
        }
    }

    private static WeakReference getKey(Object tag) {
        if (mRunnableMap == null || mRunnableMap.size() == 0) {
            return null;
        }
        for (WeakReference<Object> weakReference : mRunnableMap.keySet()) {
            Object object = weakReference.get();
            if (object != null && object.equals(tag)) {
                return weakReference;
            }
        }
        return null;
    }

    private static void postPrepare(Object tag, Runnable runnable) {
        WeakReference<Object> weakReference = getKey(tag);
        if (weakReference == null) {
            weakReference = new WeakReference<>(tag);
        }
        if (mRunnableMap == null) {
            mRunnableMap = new ConcurrentHashMap<>();
        }
        CopyOnWriteArrayList<WeakReference<Runnable>> runnableList;
        if (mRunnableMap.get(weakReference) == null) {
            runnableList = new CopyOnWriteArrayList<>();
        } else {
            runnableList = mRunnableMap.get(weakReference);
        }
        runnableList.add(new WeakReference<>(runnable));
        mRunnableMap.put(weakReference, runnableList);
    }

    public static void postOnUiThread(Object tag, Runnable runnable) {
        if (tag == null || runnable == null) {
            return;
        }
        postPrepare(tag, runnable);
        obtainMainHandler().post(runnable);
    }

    public static void postOnUiThreadDelayed(Object tag, Runnable runnable, long delay) {
        if (tag == null || runnable == null) {
            return;
        }
        postPrepare(tag, runnable);
        obtainMainHandler().postDelayed(runnable, delay);
    }

    public static void onTagDestroy(Object tag) {
        if (tag == null) {
            return;
        }
        WeakReference weakReference = getKey(tag);
        if (weakReference == null) {
            return;
        }
        CopyOnWriteArrayList<WeakReference<Runnable>> runnableList = mRunnableMap.get(weakReference);
        if (runnableList == null || runnableList.size() == 0) {
            return;
        }
        for (WeakReference<Runnable> weakReference1 : runnableList) {
            if (weakReference1 != null && weakReference1.get() != null) {
                obtainMainHandler().removeCallbacks(weakReference1.get());
            }
        }
        mRunnableMap.remove(weakReference);
    }

    public static void postOnBackgroundThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        obtainBackgroundHandler().post(runnable);
    }

    public static void postOnBackgroundThreadDelay4Kt(long delay, Runnable runnable) {
        postOnBackgroundThreadDelay(runnable, delay);
    }

    public static void postOnBackgroundThreadDelay(Runnable runnable, long delay) {
        if (runnable == null) {
            return;
        }

        obtainBackgroundHandler().postDelayed(runnable, delay);
    }

    public static void removeBackgroundThreadDelay(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        obtainBackgroundHandler().removeCallbacks(runnable);
    }


    public static void removeCallbacks(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        obtainMainHandler().removeCallbacks(runnable);
    }


}
