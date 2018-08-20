package com.exa;

import android.app.Application;
import android.content.Context;

import com.exa.cusview.MyViewActivity;
import com.exa.plugin.HookUtil;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by user on 2018/7/17.
 */

public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        OkHttpClient client=new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        System.out.println("MyApplication.onCreate ========== ");

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

}
