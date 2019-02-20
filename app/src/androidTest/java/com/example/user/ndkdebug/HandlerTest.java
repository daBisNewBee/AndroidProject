package com.example.user.ndkdebug;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;

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
        System.out.println("is main Looper: "+(subLooper == mainLooper)
        );
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

    class Worker extends Thread {
        private Object lock = new byte[0];
        private boolean isRunning = true;

        public Worker() {
            setName("_workderThread");
            start();
        }

        public void startWork(){
            System.out.println("Worker.startWork");
            synchronized (lock){
                lock.notifyAll();
            }
        }

        @Override
        public void run() {
            while (isRunning){
                synchronized (lock){
                    try {
                        System.out.println("Worker准备进入等待.");
                        lock.wait();
                        System.out.println("Worker结束等待，开始干活.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("doing cost job....");
                SystemClock.sleep(2000);
            }
        }
    }

    @Test
    public void hand_Test() {
        Worker worker = new Worker();
        SystemClock.sleep(200);
        worker.startWork();
        SystemClock.sleep(4000);
        System.out.println("prepare do again..");
        worker.startWork();
    }

    static class Worker2 extends HandlerThread {

        public Worker2(String name) {
            super(name);
        }
    }

    static class Worker2Stub {

        static class MyHandler extends Handler{
            
            public MyHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
                System.out.println("msg = " + msg);
            }
        }

        private Handler mHandler;

        public Worker2Stub(Handler mHandler) {
            this.mHandler = mHandler;
        }

        public void startWork(){
            mHandler.sendEmptyMessage(1);
        }

        public void setParams(){
            mHandler.sendEmptyMessage(2);
        }
        public void stop(){
            Thread thread = mHandler.getLooper().getThread();
            System.out.println("thread = " + thread.getId());
            boolean isQuit = ((Worker2)thread).quit();
            System.out.println("isQuit = " + isQuit);
        }
    }

    @Test
    public void worker2_Test() {
        Worker2 worker2 = new Worker2("_worker2Thread");
        worker2.start();
        System.out.println("worker2 = " + worker2.getId());
        Worker2Stub stub = new Worker2Stub(
                new Worker2Stub.MyHandler(worker2.getLooper()));
        stub.startWork();
        SystemClock.sleep(2000);
        stub.setParams();
        SystemClock.sleep(2000);
        stub.stop();
    }
}
