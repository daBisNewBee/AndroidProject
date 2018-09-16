package com.exa.eventbus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by user on 2018/9/9.
 */

public class PublishService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventOnMain(EventBean eventBean){

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventOnPost(MessageEvent messageEvent){

    }

    @PrintMe
    public void start(String ip, int port){}

    @PrintMe
    public int stop(){return 0;}

}
