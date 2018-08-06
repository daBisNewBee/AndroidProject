package com.exa.mode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.exa.R;

/**
 * Created by user on 2018/7/18.
 */

public class OtherActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        findViewById(R.id.btn_other_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherActivity.this, SignleTopActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_other_return_signle_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherActivity.this, SingleTaskActivity.class);
                startActivity(intent);
            }
        });
    }
}
