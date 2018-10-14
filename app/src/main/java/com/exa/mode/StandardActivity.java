package com.exa.mode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.exa.R;

/**
 *
 *
 * 1. 谁启动了该模式的Activity，该Activity就属于启动它的Activity的任务栈中.
 * "TaskID"相同
 *
 * 2. 每次启动一个Activity都会重写创建一个新的实例
 * "hashCode"不同
 *
 * Created by user on 2018/7/18.
 */

@Route(path = "/standard/activity")
public class StandardActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);
        findViewById(R.id.btn_standard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StandardActivity.this, StandardActivity.class);
                startActivity(intent);
            }
        });
    }
}


/*
*
*
I/System.out(23518): BaseActivity.onCreate =====
I/System.out(23518): onCreate:StandardActivity TaskID:358 hashCode:393623986
I/System.out(23518): taskAffinity:com.exa
I/System.out(23518): BaseActivity.onCreate =====
I/System.out(23518): onCreate:StandardActivity TaskID:358 hashCode:383061178
I/System.out(23518): taskAffinity:com.exa
I/System.out(23518): BaseActivity.onCreate =====
I/System.out(23518): onCreate:StandardActivity TaskID:358 hashCode:185682626
I/System.out(23518): taskAffinity:com.exa
I/System.out(23518): BaseActivity.onCreate =====
I/System.out(23518): onCreate:StandardActivity TaskID:358 hashCode:1446858
I/System.out(23518): taskAffinity:com.exa
*
*
* */