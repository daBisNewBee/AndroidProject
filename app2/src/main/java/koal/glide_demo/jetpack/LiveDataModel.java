package koal.glide_demo.jetpack;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataModel extends ViewModel {

    private MutableLiveData<Long> mElapsedTime = new MutableLiveData<>();
    private long mInitialTime;

    public LiveDataModel() {
        Log.d("livedata", "LiveDataModel constructor called");
        mInitialTime = System.currentTimeMillis();
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long newValue = (System.currentTimeMillis() - mInitialTime) / 1000;

                    mElapsedTime.postValue(newValue);
                    Log.d("livedata", "do postValue = " + newValue);
                }
            }
        }.start();
    }

    public MutableLiveData<Long> getElapsedTime() {
        return mElapsedTime;
    }
}
