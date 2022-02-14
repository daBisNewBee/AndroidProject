package com.exa.messenger;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by user on 2018/7/14.
 */

public class RemoteService extends Service {

    public final static int SERVICEID = 0x0001;
    public final static int CLIENTID = 0x0002;

    @SuppressLint("HandlerLeak")
    private Messenger messenger = new Messenger(new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SERVICEID){
                // 4. 收到来自客户端的消息
                String received = (String)msg.getData().get("content");
                System.out.println("RemoteService received = " + received);
                System.out.println("Sender thread ID = " + msg.getData().get("thread"));

                // 5. 构造 Message，准备返回给客户端
                Message msgTo = Message.obtain();
                msgTo.what = CLIENTID;

                Bundle bundle = new Bundle();
                bundle.putString("content","这个数据来自RemoteService");
                msgTo.setData(bundle);

                try {
                    // 6. 将msg中发送给客户端的Messenger：CMessenger
                    msg.replyTo.send(msgTo);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("RemoteService.onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
