package koal.glide_demo.jetpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import koal.glide_demo.R;

public class LiveDataActivity extends AppCompatActivity {

    private static final String TAG = "LiveDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        LiveDataModel model = ViewModelProvider.AndroidViewModelFactory.
                getInstance(this.getApplication()).create(LiveDataModel.class);
        model.getElapsedTime().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.d(TAG, "onChanged() called with: aLong = [" + aLong + "]");
            }
        });
    }
}