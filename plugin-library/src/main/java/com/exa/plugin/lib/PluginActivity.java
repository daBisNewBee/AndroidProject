package com.exa.plugin.lib;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route(path = "/plugin/activity")
public class PluginActivity extends Activity {

    @Autowired
    String name;

    @Autowired
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        ARouter.getInstance().inject(this);
        ((TextView)findViewById(R.id.text2))
                .setText(String.format("name:%s age:%d",name, age));
    }
}
