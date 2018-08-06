package com.exa;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.exa.cusview.MyViewActivity;
import com.exa.messenger.RemoteService;
import com.exa.mode.BaseActivity;
import com.exa.mode.SignleTopActivity;
import com.exa.mode.SingleTaskActivity;
import com.exa.mode.StandardActivity;

/**
 *
 * 一句话概括：
 *
 *      "Messnger" 其实就是 一个包装了的Binder 的普通类 ！
 *
 *      这个Binder 是 IMessenger. 其IMessenger.Stub 定义在 Handler 中。
 *
 *      把xxx.aidl封装在了一个类中，免去Client自己通过aidl生成对应java的过程,
 *      用Messnger就不需要显式使用aidl文件了
 *
 *      Messnger 线程安全的原因：handler是线程安全的！
 *
 *      常用于：
 *
 *         1. 跨APP的双向通信 （通过给发送到服务端的message中的replyTo赋值CMessenger）
 *         2. 只能传输Bundle 支持的数据类型。
 *         3. 支持一对多的串行通信，无RPC需求
 *         4. 一般用作消息传递
 *
 *      Android的进阶学习（五）--Messenger的使用和理解:
 *          https://www.jianshu.com/p/af8991c83fcb
 *
 *
 *
 */
public class MainActivity extends BaseActivity
        implements View.OnClickListener{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Messenger CMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RemoteService.CLIENTID){
                System.out.println("RemoteService 传来了消息：");
                String str = (String)msg.getData().get("content");
                System.out.println("str = " + str);

            }
        }
    });

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("MainActivity.onServiceConnected");
            // 1. 获得服务端传来的Messenger
            final Messenger SMessenger = new Messenger(service);

            // 2. 构造Message，准备发送给服务端
            final Message msg = Message.obtain();
            msg.what = RemoteService.SERVICEID;
            // 2.1 携带 Client 中的 Messenger，用于服务端回调
            msg.replyTo = CMessenger;

            for (int i = 0; i < 10; i++) {
                new Thread(){
                    public void run(){
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString("content","这个数据来自客户端");
                            bundle.putString("thread",""+Thread.currentThread().getId());
                            msg.setData(bundle);
                            // 3. 将msg发送到服务端
                            SMessenger.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_main_signleTask).setOnClickListener(this);
        findViewById(R.id.btn_main_singleInstance).setOnClickListener(this);
        findViewById(R.id.btn_main_singleTop).setOnClickListener(this);
        findViewById(R.id.btn_main_stand).setOnClickListener(this);
        findViewById(R.id.btn_myView).setOnClickListener(this);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
//        startAndBindService();
    }

    private void startAndBindService() {
        Intent service = new Intent(this,RemoteService.class);
        startService(service);
        bindService(service, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        switch (id){
            case R.id.btn_main_signleTask:
                intent = new Intent(this, SingleTaskActivity.class);
                break;
            case R.id.btn_main_singleInstance:
                break;
            case R.id.btn_main_singleTop:
                intent = new Intent(this, SignleTopActivity.class);
                break;
            case R.id.btn_main_stand:
                intent = new Intent(this, StandardActivity.class);
                break;
            case R.id.btn_myView:
                intent = new Intent(this, MyViewActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
