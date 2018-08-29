package com.exa.binder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.exa.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * Android Binder机制详解：手写IPC通信
 *
 * 参考：https://blog.csdn.net/zhenghaisen/article/details/76639392
 *
 */
public class BinderActivity extends Activity {

    private IBinder binderClient;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*
            *
            * 这个引用就是服务在binder驱动中对应的mRemote对象
            *
            * 相关：
            *   任意一个binder服务端被创建的时候，在binder驱动中就会相应的创建一个对应的mRemote对象，
            *   这个对象也是binder类，客户端访问远程服务的时候，都是通过mRemote对象来完成的。
            *
            * */
            binderClient = service;
            System.out.println("binderClient = " + binderClient);
            /*
            *
            *
            *   <service
                android:name=".binder.IPCServer"
                android:process=":ipc" /> 时：

            * binderClient = android.os.BinderProxy@1825c5b9
            *
            *
            * binderClient = com.exa.binder.IPCServer$BinderImpl@1069bdfe
            * */
            try {
                findRemote();
                call();
            } catch (Exception e) {
                System.out.println("call e = " + e);
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binderClient = null;
        }
    };

    long getmObjectFromBinderProxy(IBinder binder)throws Exception{
        Class<?> binderProxyClz = Class.forName("android.os.BinderProxy");
        Field mObjectField = binderProxyClz.getDeclaredField("mObject");
        mObjectField.setAccessible(true);
        long mObjectValue = mObjectField.getLong(binder);
        return mObjectValue;
    }

    void findRemote() throws Exception {
        long mObjectValue = getmObjectFromBinderProxy(binderClient);
        System.out.println("mObjectValue = " + mObjectValue);

        Class<?> serviceManagerClz = Class.forName("android.os.ServiceManager");
        Method listServicesMethod = serviceManagerClz.getDeclaredMethod("listServices");
        Method getServiceMethod = serviceManagerClz
                .getDeclaredMethod("getService",String.class);

        String[] services = (String[])listServicesMethod.invoke(null);
        /*
        userdeMacBook-Pro:Downloads user$ adb shell dumpsys -l|wc -l
        121
        *
        * services size = 121
        * */
        System.out.println("services size = " + services.length);
        //
        for (String service : services) {
//            System.out.println("\n" + "service = " + service);
            IBinder iBinder = (IBinder)getServiceMethod.invoke(null, service);
//            System.out.println("iBinder = " + iBinder);
            long value = getmObjectFromBinderProxy(iBinder);
            if (value == mObjectValue){
                System.out.println("找到了IBinder ："+service);
            }
        }


    }

    /**
     * Client的请求是如何一步步到达Server的:
     * 1. binderClient.transact(code, data, reply, flag);
     *
     * 2. 实际调用的是：
     *    BinderProxy.transact...

     final class BinderProxy implements IBinder {
         private long mObject;

         public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ...
            return transactNative(code, data, reply, flags);
         }

         public native boolean transactNative(int code, Parcel data, Parcel reply,
         int flags) throws RemoteException;
         ...
     }
     *
     * 3. 调用了jni的"transactNative"
     * （位于{framework}/core/jni/android_util_Binder.cpp）
     *
     * 4. 实际调用了 "android_os_BinderProxy_transact"
     * 动态注册的方法
        static const JNINativeMethod gBinderProxyMethods[] = {
             ...
             {"transactNative",      "(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z", (void*)android_os_BinderProxy_transact}
             ...
        };
     *
     * 5. 反射获取 BinderProxy 的"mObject"对象，并转化为"IBinder"的target
     *
     static jboolean android_os_BinderProxy_transact(JNIEnv* env, jobject obj,
     jint code, jobject dataObj, jobject replyObj, jint flags)
     {
         IBinder* target = (IBinder*)
         env->GetLongField(obj, gBinderProxyOffsets.mObject);

         status_t err = target->transact(code, *data, reply, flags);

         if (err == NO_ERROR) {
            return JNI_TRUE;
         } else if (err == UNKNOWN_TRANSACTION) {
            return JNI_FALSE;
         }
     }
     *
     * 6. 调用 "target->transact"，（名为target的这个IBinder实际上就是Server中onBind返回的这个Binder）
     *
     * 7. Server中，对"transact"最终走到了"onTransact"。
     *     public final boolean transact(int code, Parcel data, Parcel reply,
        int flags) throws RemoteException {
         ...
         boolean r = onTransact(code, data, reply, flags);
         ...
         return r;
     }
     *
     * target 怎么就是 Server的 IBinder了？？？
     *
     * @throws RemoteException
     */
    void call() throws RemoteException {
        final int code = 1000;
        Parcel data = Parcel.obtain();
        data.writeInt(1);
        data.writeInt(2);

        /*
        * 0:
        *   表示这是一个双向的IPC调用;
        *
        * IBinder.FLAG_ONEWAY:
        *   单向IPC调用
        *
        * */
        final int flag = 0;
        Parcel reply = Parcel.obtain();
        boolean suc = binderClient.transact(code, data, reply, flag);
        if (suc){
            System.out.println("transact 成功！");
            System.out.println("reply:" + reply.readInt());
        }else {
            System.out.println("transact 失败！");
        }

        data.recycle();
        reply.recycle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
        Intent intent = new Intent(this, IPCServer.class);
        startService(intent);
        bindService(intent,connection, BIND_AUTO_CREATE);
    }
}
