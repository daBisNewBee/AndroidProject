package com.exa.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * Created by user on 2018/8/25.
 */

public class IPCServer extends Service {

    private IBinder binder = new BinderImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("IPCServer.onBind ------> ");
        try {

            Field mObjectField = Binder.class.getDeclaredField(
                    "mObject");
            mObjectField.setAccessible(true);
            long mObjectValueServer = mObjectField.getLong(binder);
            System.out.println("mObjectValueServer = " + mObjectValueServer);

        }catch (Exception e){
            System.out.println("onBind ----> e = " + e);
        }
        return binder;
    }

    public class BinderImpl extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code){
                case 1000:
                    int num1 = data.readInt();
                    int num2 = data.readInt();
                    reply.writeInt(num1 + num2);
                    return true;
                default:
                    break;
            }

            return super.onTransact(code, data, reply, flags);
        }
    }

    @Override
    public void onCreate() {
        System.out.println("IPCServer.onCreate ------>");
        super.onCreate();
    }
}
