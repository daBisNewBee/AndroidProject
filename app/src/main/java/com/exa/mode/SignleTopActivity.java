package com.exa.mode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.exa.R;

/**
 *
 * 1. 只在实例位于栈顶时复用，且回调"onNewIntent"
 *
 * 2. 指定了"android:taskAffinity"后，会变化。（默认任务栈的名字为应用的包名）
 *
 *
 * taskAffinity：任务相关性
 * 具有相同的affinity的activity（即设置了相同taskAffinity属性的activity）属于同一个任务
 *
 * Created by user on 2018/7/18.
 */

public class SignleTopActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletop);
        findViewById(R.id.btn_signtop_signtop).setOnClickListener(this);
        findViewById(R.id.btn_single_other).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id  = v.getId();
        switch (id){
            case R.id.btn_signtop_signtop:
                intent = new Intent(this, SignleTopActivity.class);
                break;
            case R.id.btn_single_other:
                intent = new Intent(this, OtherActivity.class);
                break;
        }
        startActivity(intent);
    }
}

/*
*
I/System.out(24605): BaseActivity.onCreate =====
I/System.out(24605): onCreate:SignleTopActivity TaskID:362 hashCode:236264212
I/System.out(24605): taskAffinity:com.exa
I/System.out(24605): BaseActivity.onNewIntent =====
I/System.out(24605): onNewIntent:SignleTopActivity TaskID:362 hashCode:236264212
I/System.out(24605): taskAffinity:com.exa
I/System.out(24605): BaseActivity.onNewIntent =====
I/System.out(24605): onNewIntent:SignleTopActivity TaskID:362 hashCode:236264212
I/System.out(24605): taskAffinity:com.exa
I/System.out(24605): BaseActivity.onCreate =====
I/System.out(24605): onCreate:OtherActivity TaskID:362 hashCode:150794055
I/System.out(24605): taskAffinity:com.exa
I/System.out(24605): BaseActivity.onCreate =====
I/System.out(24605): onCreate:SignleTopActivity TaskID:362 hashCode:131200815
I/System.out(24605): taskAffinity:com.exa
I/System.out(24605): BaseActivity.onNewIntent =====
I/System.out(24605): onNewIntent:SignleTopActivity TaskID:362 hashCode:131200815
I/System.out(24605): taskAffinity:com.exa
I/System.out(24605): BaseActivity.onCreate =====
I/System.out(24605): onCreate:OtherActivity TaskID:362 hashCode:316305006
I/System.out(24605): taskAffinity:com.exa
I/System.out(24605): BaseActivity.onCreate =====
I/System.out(24605): onCreate:SignleTopActivity TaskID:362 hashCode:286139382

*
* */
