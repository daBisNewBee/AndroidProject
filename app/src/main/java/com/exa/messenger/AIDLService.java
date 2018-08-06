package com.exa.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.example.user.ndkdebug.IMyAidlInterface;

import java.util.HashMap;

/**
 * Created by user on 2018/7/15.
 */

public class AIDLService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        IMyAidlInterface instance = null;

        IBinder service = null ;
        instance = IMyAidlInterface.Stub.asInterface(service);
        Binder binder = new ServiceBinder();
    }

    private class ServiceBinder extends IMyAidlInterface.Stub{
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override
        public void start(String ip) throws RemoteException {
            testMethod();
            AIDLService.this.testMethod();
        }

        @Override
        public void stop() throws RemoteException {

        }
    }

    private void testMethod(){

        Intent intent = new Intent();
        HashMap<String,String> map = new HashMap<>();
        intent.putExtra("map",map);

        intent.getSerializableExtra("map");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
