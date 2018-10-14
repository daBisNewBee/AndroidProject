package com.exa.leakcanary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.exa.R;

@Route(path = "/leak/activity")
public class LeakActivity extends Activity {

    private HttpRequestHelper httpRequestHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);

        View button = findViewById(R.id.btn_async_work);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsyncWork();
            }
        });

        httpRequestHelper = (HttpRequestHelper)getLastNonConfigurationInstance();
        if (null == httpRequestHelper)
            httpRequestHelper = new HttpRequestHelper(button);
    }

    @SuppressLint("StaticFieldLeak")
    private void startAsyncWork() {
        // This runnable is an anonymous class and therefore has a hidden reference to the outer
        // class MainActivity. If the activity gets destroyed before the thread finishes (e.g. rotation),
        // the activity instance will leak.
        Runnable work = new Runnable() {
            @Override
            public void run() {
                System.out.println("before -----> ");
                SystemClock.sleep(20000);
                System.out.println("after -----> ");
            }
        };
        new Thread(new NormalRunnable()).start();
//        new Thread(work).start();
    }

    // "静态内部类"代替"匿名内部类"，防止当前Activity的泄漏
    static class NormalRunnable implements Runnable{
        @Override
        public void run() {
            SystemClock.sleep(20000);
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        System.out.println("LeakActivity.onRetainNonConfigurationInstance: " + httpRequestHelper);
        return httpRequestHelper;
    }
}
