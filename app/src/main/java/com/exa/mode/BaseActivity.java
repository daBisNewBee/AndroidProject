package com.exa.mode;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 *
 * 彻底弄懂Activity四大启动模式:
 * https://blog.csdn.net/mynameishuangshuai/article/details/51491074
 *
 * Created by user on 2018/7/18.
 */

public class BaseActivity extends AppCompatActivity {

    void log(String msg){
        System.out.println(msg + ":"+getClass().getSimpleName()+" TaskID:"
                +getTaskId()+" hashCode:"+this.hashCode());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("BaseActivity.onCreate ===== ");
        log("onCreate");
        dumpTaskAffinity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("BaseActivity.onNewIntent ===== "+intent);
        log("onNewIntent");
        /*
        复用的Activity，在这里设置intent，更新UI。
        setIntent(intent);
        initData();
        initView();
         * */
        dumpTaskAffinity();
    }

    protected void dumpTaskAffinity(){
        try {
            ActivityInfo info = this.getPackageManager()
                    .getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            System.out.println("taskAffinity:"+info.taskAffinity);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
