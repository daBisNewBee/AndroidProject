package com.exa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

/**
 *
 * 冷启动白屏的原因？
 * WindowManager会先加载APP里的主题样式里的窗口背景
 * （windowBackground）作为预览元素，然后才去真正的加载布局
 *
 * 消除启动时的白屏/黑屏？
 * 设置首页的 theme 的style 中的android:windowBackground 为 透明色
 *
 * 首先我们要知道当打开一个Activity的时候发生了什么，在一个Activity打开时，
 * 如果该Activity所属的Application还没有启动，那么系统会为这个Activity创
 * 建一个进程（每创建一个进程都会调用一次Application，所以Application的
 * onCreate()方法可能会被调用多次），在进程的创建和初始化中，势必会消耗一些
 * 时间，在这个时间里，WindowManager会先加载APP里的主题样式里的窗口背景
 * （windowBackground）作为预览元素，然后才去真正的加载布局，如果这个时间过长，
 * 而默认的背景又是黑色或者白色，这样会给用户造成一种错觉，这个APP很卡，很不流畅，
 * 自然也影响了用户体验。
 *
 * https://www.jianshu.com/p/03c0fd3fc245
 *
 *
 *
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
