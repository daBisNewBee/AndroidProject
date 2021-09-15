package koal.glide_demo.jetpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import koal.glide_demo.R;

public class LiveDataActivity extends AppCompatActivity {

    private static final String TAG = "livedata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        LiveDataModel model = ViewModelProvider.AndroidViewModelFactory.
                getInstance(this.getApplication()).create(LiveDataModel.class);
        model.getElapsedTime().setValue(-1000L); // 还没observe，仍旧可以打印

        XmLifecycleObserver observer = new XmLifecycleObserver(getLifecycle());
        getLifecycle().addObserver(observer);
        model.getElapsedTime().observe(observer, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.d(TAG, "onChanged() called with: aLong = [" + aLong + "]");
            }
        });
//        getLifecycle().addObserver(new XmLifecycleObserver(getLifecycle()));
    }
}