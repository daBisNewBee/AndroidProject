package com.exa.plugin;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.exa.R;

/*
*
*  继承 "AppCompatActivity" 会报错：
*
* FATAL EXCEPTION: main
Process: com.exa, PID: 21370
java.lang.RuntimeException: Unable to start activity ComponentInfo{com.exa/com.exa.plugin.TargetActivity}: java.lang.IllegalArgumentException: android.content.pm.PackageManager$NameNotFoundException: ComponentInfo{com.exa/com.exa.plugin.TargetActivity}
   at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2330)
* */
@Route(path = "/target/activity")
public class TargetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
    }
}
