package com.exa.ashmem;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import com.example.user.ndkdebug.IShmService;

public class RemoteService extends Service {
	
	private IShmService service;
	
	private ServiceConnection serviceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			service = IShmService.Stub.asInterface(arg1);
			try {
				final ParcelFileDescriptor pfd = service.getFD();
				Log.v("ashmem", "RemoteService getFD:"+pfd.getFd());
				AshmemManager.doOperaLater(pfd.getFd());
				RemoteService.this.stopSelf();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			service = null;
		}};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Intent intent = new Intent(RemoteService.this,ShmService.class);
		bindService(intent,serviceConnection,BIND_AUTO_CREATE);
		startService(intent);
	
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v("ashmem", "RemoteService onDestroy");
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
