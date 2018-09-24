package com.example.user.ndkdebug;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import org.junit.Test;

import java.util.Random;

/**
 * Created by user on 2018/9/23.
 */

public class HandlerTest {
    @Test
    public void Handler_Test() throws Exception {
        HandlerThread handlerThread = new HandlerThread("myHandler");
        handlerThread.start();

        Looper subLooper = handlerThread.getLooper();
        Looper mainLooper = Looper.getMainLooper();
        System.out.println("is main Looper: "+(subLooper == mainLooper));
        // expect:false

        Handler handler = new Handler(subLooper){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                System.out.println(Thread.currentThread().getId() + " handleMessage msg = " + msg.what);
            }
        };

        System.out.println("主线程：" + Thread.currentThread().getId());
        handler.sendEmptyMessage(new Random().nextInt());
//        handler.sendEmptyMessageDelayed(new Random().nextInt(), 2000);
    }
}
