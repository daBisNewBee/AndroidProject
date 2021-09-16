package koal.glide_demo.jetpack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import koal.glide_demo.R;

/**
 * 1、LiveData是如何关联生命周期的？
 *      a. 从LifecycleRegistry获取组件生命周期
 *      b. fragemnt、activity内部维护会LifecycleRegistry的state、event
 *
 * 2、LiveData的发送事件、接收事件原理
 *      发送：setValue()和postValue()
 *      接收：onChange
 *
 * 3、为什么LiveData可以先发射数据再注册？类似EventBus的粘性消息
 *      当生命周期变化(handleLifecycleEvent)的时候都会回调onStateChanged()方法，
 *      在这里面会去调用activeStateChanged(),而它最后会调用considerNotify()方法去分发消息。
 *      而粘性消息也是这个原理。
 *
 * 4、LiveData如何保证不会内存泄漏的？
 *  在一开始的observe()方法中会把Activity中创建的Observer对象添加到一个Map集合中，
 *  最后当生命周期方法执行ondestory之后会移除观察者，这样就避免了内存泄漏
 *  LifecycleBoundObserver的"onStateChanged"
 *
 *
 * ps:
 * 1. 通过实现LifecycleOwner接口可以将不支持Lifecycle的组件手动支持Lifecycle
 *
 * 2. 比如，fragments和Activity都已经继承了LifecycleOwner接口，就不用另外单独实现
 *
 *
 */
public class LiveDataActivity extends AppCompatActivity {

    private static final String TAG = "livedata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        LiveDataModel model = ViewModelProvider.AndroidViewModelFactory.
                getInstance(this.getApplication()).create(LiveDataModel.class);
        model.getElapsedTime().setValue(-1000L); // 还没observe，仍旧可以打印

        XmLifecycleObserver observer = new XmLifecycleObserver();
        getLifecycle().addObserver(observer);
        model.getElapsedTime().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.d(TAG, "onChanged() called with: aLong = [" + aLong + "]");
            }
        });
//        getLifecycle().addObserver(new XmLifecycleObserver(getLifecycle()));
    }
}