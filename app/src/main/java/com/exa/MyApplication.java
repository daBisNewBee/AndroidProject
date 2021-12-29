package com.exa;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.exa.ashmem.AshmemManager;
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

    // Used to load the 'native-lib' library on application startup.
    static {
        /**
         * java_vm_ext.cc:542] JNI DETECTED ERROR IN APPLICATION: JNI NewGlobalRef called with pending exception java.lang.NoSuchMethodError: no static or non-static method "Lcom/exa/JavaBean;.modifiedUTF8Test_aa(Ljava/lang/String;)Ljava/lang/String;"
         * java_vm_ext.cc:542]   at java.lang.String java.lang.Runtime.nativeLoad(java.lang.String, java.lang.ClassLoader) (Runtime.java:-2)
         * java_vm_ext.cc:542]   at void java.lang.Runtime.loadLibrary0(java.lang.ClassLoader, java.lang.String) (Runtime.java:1014)
         * java_vm_ext.cc:542]   at void java.lang.System.loadLibrary(java.lang.String) (System.java:1669)
         * java_vm_ext.cc:542]   at void com.exa.MyApplication.<clinit>() (MyApplication.java:27)
         * java_vm_ext.cc:542]   at java.lang.Object java.lang.Class.newInstance() (Class.java:-2)
         * java_vm_ext.cc:542]   at android.app.Application android.app.AppComponentFactory.instantiateApplication
         */
        try {
            System.loadLibrary("native-lib");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("todo", "loadLibrary error:  " + e.getLocalizedMessage());
        }
        // System.loadLibrary源码分析: https://blog.csdn.net/u014099894/article/details/111655768
    }

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

    private String getCurrentProcessName(){
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process: activityManager.getRunningAppProcesses()){
            if (process.pid == pid)
                processName = process.processName;
        }
        return processName;
    }

    private boolean isMainProcess(){
        return context.getPackageName().equals(getCurrentProcessName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 其实 可以在 "attachBaseContext"后就执行context相关的初始化
        context = this;
        if (isMainProcess()){
            Log.v("ashmem", "into isMainProcess...");
            AshmemManager.getInstance().setFd2Ashmem(AshmemManager.initAndGetFd2Ashmem());
        }

        /*
        * ARouter 相关
        *
        * 原理本质：
        * 1. 注解处理器生成"分组表"类
        *    比如，"ARouter$$Group$$leak.class"
        *
        * 2. 在"分组表"类中，指定了"url"与对应类的"映射关系"
        *    比如，"/leak/activity" 对应 "LeakActivity.class"
        *
        * 3. 运行时，url根据"映射关系",找到对应实际类，跳转、打开指定调用
        *    比如，ARouter.getInstance().build("/glide/ContactUri").navigation();
        *    类似于startActivity(ContactUriActivity.java)
        *
        * 要点：
        * 能够精确匹配url，这个映射关系是关键！
        * 但是直接保存映射关系，太慢！
        * "分组表类"封装映射关系，解决直接保存映射关系体积太大的问题，但是扫描分组表类太慢成了最大问题！！
        *
        * 作用：
        * 组件化解耦：startActivity时指定"url"代替类限定名
        *
        * 2019.9.16 技术分享讨论
        * 较大项目弃用的原因是：
        * 新版本初始化时，需要从apk中扫描class得到分组表，很耗时！！影响启动时间！！
        * TODO： 时间需要验证
        * */
        ARouter.openLog();
        ARouter.openDebug();// 该步骤决定需要分组表是从apk中扫描获得还是从本地sp中加载
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
