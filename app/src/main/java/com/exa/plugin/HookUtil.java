package com.exa.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by user on 2018/8/18.
 */

public class HookUtil {

    private Context context;

    public HookUtil(Context context) {
        this.context = context;
    }

    private static class ActivityThreadHandlerCallback implements Handler.Callback{
        private Handler handler;

        public ActivityThreadHandlerCallback(Handler _handler) {
            handler = _handler;
        }

        @Override
        public boolean handleMessage(Message msg) {

            System.out.println("ActivityThreadHandlerCallback.handleMessage ---> "+msg);

            // public static final int LAUNCH_ACTIVITY         = 100;
            if (msg.what == 100){
                System.out.println(" LAUNCH_ACTIVITY hook ---------> ");
                handleLauchActivity(msg);
            }
            handler.handleMessage(msg);
            /*
            *
            * 注意区别！使用如下会报错：
            *
            * This message is already in use
            * */
//            handler.sendMessage(msg);
            return true;
        }

        private void handleLauchActivity(Message msg) {
            Object object = msg.obj;// ActivityClientRecord
            try {
                Field intentField = object.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                Intent proxyIntent = (Intent)intentField.get(object);

                Intent realIntent = proxyIntent.getParcelableExtra("oldIntent");
                if (realIntent != null){
                    System.out.println("这里收到了被hook的真实Intent："+realIntent);
                    System.out.println("因此，proxyIntent 将重置回 真实Inent！");
                    proxyIntent.setComponent(realIntent.getComponent());
                }else {
                    System.out.println("没有找到真实Intent！ 这不应该发生！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("handleLauchActivity e = " + e);
            }
        }
    }

    /**
     *
     * Hook点二： ActivityThread 中的 "mH"进行替换。
     *
     * 执行 targetActivity 替换 ProxyActivity
     *
     *
     * 即 实现"mCallback.handleMessage"的handler 替换原"mH的handleMessage"。
     *
     * 在 handleMessage 中 恢复伪造的Intent，此时AMS已经检验过。
     *
     * 这是由于
     *
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
     * mH 使用 handleMessage 执行操作，而"mCallback.handleMessage"的优先级高于 handleMessage
     *
     *
     * @throws Exception
     */
    public void hookSystemHandler() throws Exception{
        Class<?> activityThreadClz = Class.forName("android.app.ActivityThread");
        Field sCurrentActivityThreadField = activityThreadClz.getDeclaredField("sCurrentActivityThread");
        sCurrentActivityThreadField.setAccessible(true);
        Object currentActivityThread = sCurrentActivityThreadField.get(null);
        System.out.println("currentActivityThread = " + currentActivityThread);

        Field mHField = activityThreadClz.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler handler = (Handler)mHField.get(currentActivityThread);
        System.out.println("mHObject = " + handler);

        Field mCallbackField = Handler.class.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);

        mCallbackField.set(handler, new ActivityThreadHandlerCallback(handler));

    }

    /**
     *
     * Hook 点一：ActivityManagerNative 中的 "IActivityManagerSingleton"
     *
     * 执行 ProxyActivity 替换 targetActivity
     *
     * 动态代理生成的 proxy对象 替换原来的"IActivityManager"对象。
     *
     private static final Singleton<IActivityManager> IActivityManagerSingleton =
        new Singleton<IActivityManager>() {
            @Override
            protected IActivityManager create() {
            final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
            final IActivityManager am = IActivityManager.Stub.asInterface(b);
            return am;
        }
    };
     *
     * 寻找Hook点的原则：
     *  "静态变量和单例变量"是相对不容易改变，是一个比较好的hook点，
     *  而普通的对象有易变的可能，每个版本都不一样，处理难度比较大。
     *
     * @throws Exception
     */
    public void hookAms() throws Exception{
        Class<?> activityManagerNativeClz = Class.forName("android.app.ActivityManagerNative");
        Field gDefaultFiled = activityManagerNativeClz.getDeclaredField(
                "gDefault");
//        Field gDefaultFiled = activityManagerNativeClz.getField("gDefault");
        gDefaultFiled.setAccessible(true);
        Object defaultValue = gDefaultFiled.get(null);
        System.out.println("singleTon defaultValue = " + defaultValue);

        Class<?> singleTonClz = Class.forName("android.util.Singleton");
        Field mInstanceField = singleTonClz.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        Object iActivityManagerObject = mInstanceField.get(defaultValue);
        //到这里已经拿到ActivityManager对象
        System.out.println("iActivityManagerObject = " + iActivityManagerObject);

        //开始动态代理，用代理对象替换掉真实的ActivityManager，瞒天过海
        Class<?> IActivityManagerIntercept = Class.forName("android.app.IActivityManager");

        AmsInvocationHandler handler = new AmsInvocationHandler(iActivityManagerObject,context);

        Object proxy = Proxy.newProxyInstance(context.getClassLoader(), new Class<?>[]{IActivityManagerIntercept}, handler);
        // 这里设置了我们自己实现了接口的CallBack对象
        mInstanceField.set(defaultValue, proxy);

    }
}
