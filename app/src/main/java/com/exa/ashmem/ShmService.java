package com.exa.ashmem;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import com.example.user.ndkdebug.IShmService;

public class ShmService extends Service {
	
	private Binder binder = new IShmService.Stub() {
        @Override
        public ParcelFileDescriptor getFD() throws RemoteException {
            Log.v("ashmem", "into ShmService getFD .pid:" + android.os.Process.myPid());
            ParcelFileDescriptor pfd = null;
            try {
                pfd = ParcelFileDescriptor.fromFd(AshmemManager.getInstance().getFd2Ashmem());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pfd;
        }
    };

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}

}
