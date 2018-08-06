package com.exa.mode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.exa.R;

/**
 *

 singleTask启动模式启动Activity时，首先会根据taskAffinity去寻找当前是否存在一个对应名字的任务栈

 如果不存在，则会创建一个新的Task，并创建新的Activity实例入栈到新创建的Task中去
 如果存在，则得到该任务栈，查找该任务栈中是否存在该Activity实例
 如果存在实例，则将它上面的Activity实例都出栈，然后回调启动的Activity实例的onNewIntent方法
 如果不存在该实例，则新建Activity，并入栈
 此外，我们可以将两个不同App中的Activity设置为相同的taskAffinity，这样虽然在不同的应用中，但是Activity会被分配到同一个Task中去。
 *
 *
 *  finish()会导致原实例销毁！
 *
 * adb shell dumpsys activity activities
 *
 * Created by user on 2018/7/18.
 */

public class SingleTaskActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signletask);
        findViewById(R.id.btn_signletask_signtask).setOnClickListener(this);
        findViewById(R.id.btn_signletask_other).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id){
            case R.id.btn_signletask_signtask:
                intent = new Intent(this, SingleTaskActivity.class);
                break;
            case R.id.btn_signletask_other:
                intent = new Intent(this, OtherActivity.class);
                break;
        }
        startActivity(intent);
    }
}

/*

I/System.out(25708): BaseActivity.onCreate =====
I/System.out(25708): onCreate:SingleTaskActivity TaskID:370 hashCode:313839549
I/System.out(25708): taskAffinity:com.exa
I/System.out(25708): BaseActivity.onNewIntent ===== Intent { flg=0x10000000 cmp=com.exa/.mode.SingleTaskActivity }
I/System.out(25708): onNewIntent:SingleTaskActivity TaskID:370 hashCode:313839549
I/System.out(25708): taskAffinity:com.exa
I/System.out(25708): BaseActivity.onNewIntent ===== Intent { flg=0x10000000 cmp=com.exa/.mode.SingleTaskActivity }
I/System.out(25708): onNewIntent:SingleTaskActivity TaskID:370 hashCode:313839549
I/System.out(25708): taskAffinity:com.exa
I/System.out(25708): BaseActivity.onNewIntent ===== Intent { flg=0x10000000 cmp=com.exa/.mode.SingleTaskActivity }
I/System.out(25708): onNewIntent:SingleTaskActivity TaskID:370 hashCode:313839549
I/System.out(25708): taskAffinity:com.exa
I/System.out(25708): BaseActivity.onCreate =====
I/System.out(25708): onCreate:OtherActivity TaskID:370 hashCode:478393204
I/System.out(25708): taskAffinity:com.exa
I/System.out(25708): BaseActivity.onNewIntent ===== Intent { flg=0x10000000 cmp=com.exa/.mode.SingleTaskActivity }
I/System.out(25708): onNewIntent:SingleTaskActivity TaskID:370 hashCode:313839549
I/System.out(25708): taskAffinity:com.exa
I/System.out(25708): BaseActivity.onNewIntent ===== Intent { flg=0x10000000 cmp=com.exa/.mode.SingleTaskActivity }
I/System.out(25708): onNewIntent:SingleTaskActivity TaskID:370 hashCode:313839549
I/System.out(25708): taskAffinity:com.exa
*
* */
