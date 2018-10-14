package com.exa;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.alibaba.android.arouter.launcher.ARouter;
import com.exa.cusview.MyViewActivity;
import com.exa.plugin.HookUtil;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.leakcanary.LeakCanary;

import okhttp3.OkHttpClient;

/**
 * Created by user on 2018/7/17.
 */

public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    /*
    * 会崩溃，想想为什么？
    *
    * java.lang.RuntimeException: Unable to instantiate application com.exa.MyApplication: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String android.content.Context.getPackageName()' on a null object reference
           at android.app.LoadedApk.makeApplication(LoadedApk.java:971)
           at android.app.ActivityThread.handleBindApplication(ActivityThread.java:5765)
           at

      联系：
          static public Application newApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Application app = (Application)clazz.newInstance();
        app.attach(context);
        return app;
    }
    public MyApplication() {
        String packAgeName = getPackageName();
        System.out.println("MyApplication ----> packAgeName = " + packAgeName);
    }
    * */


    @Override
    public void onCreate() {
        super.onCreate();
        // 其实 可以在 "attachBaseContext"后就执行context相关的初始化
        context = this;

        // ARouter 相关
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(this);

        setupLeakCanary();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        OkHttpClient client=new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

//        System.out.println("MyApplication.onCreate ========== ");

        /*
        * 使用RePlugin时，此时作为插件apk，需要关闭hook
        * */
        boolean isHookActivity = false;
        if (isHookActivity) {
            HookUtil hookUtil = new HookUtil(
                    this);
            try {
                hookUtil.hookSystemHandler();
                hookUtil.hookAms();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("e = " + e);
            }
        }
    }

    private void setupLeakCanary() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
//                .penaltyDeath()
                .build());
        LeakCanary.install(this);
    }

}
