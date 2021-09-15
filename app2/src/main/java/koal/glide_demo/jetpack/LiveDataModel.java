package koal.glide_demo.jetpack;

import android.os.SystemClock;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataModel extends ViewModel {

    private MutableLiveData<Long> mElapsedTime = new MutableLiveData<>();
    private long mInitialTime;

    public LiveDataModel() {
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
                }
            }
        }.start();
    }

    public MutableLiveData<Long> getElapsedTime() {
        return mElapsedTime;
    }
}
