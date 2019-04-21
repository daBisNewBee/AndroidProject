package com.exa.ashmem;

import java.io.FileDescriptor;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import com.example.user.ndkdebug.IShmService;

/**
 *
 * 几个源码：
 * 1. frameworks/base/core/jni/android_os_MemoryFile.cpp
 * 2. system/core/libcutils/ashmem-dev.c
 *
 * 暂未发现ashmem的大小限制.
 *
 * 参考：
 * 1. Android匿名共享内存（Ashmem）原理：
 * https://www.jianshu.com/p/d9bc9c668ba6
 *
 *
 */
public class ShmService extends Service {
	
	private Binder binder = new IShmService.Stub() {
        @Override
        public ParcelFileDescriptor getFD() throws RemoteException {
            Log.v("ashmem", "into ShmService getFD .pid:" + android.os.Process.myPid());
            ParcelFileDescriptor pfd = null;
            try {
                if (AshmemManager.isUseMemoryFile){
                    byte[] buf2Send = new byte[1024];
                    for (int i = 0; i<buf2Send.length; i++){
                        buf2Send[i] = (byte) i;
                    }
                    MemoryFile memoryFile = new MemoryFile("myMemoFile", 1024);

                    // Ashmem的一个特性就是可以在系统内存不足的时候，回收掉被标记为”unpin”的内存
//                    boolean isPurgeAllowed = memoryFile.isPurgingAllowed();
//                    Log.v("ashmem", "isPurgingAllowed:" + isPurgeAllowed);
//                    memoryFile.allowPurging(false);

                    memoryFile.getOutputStream().write(buf2Send);
                    Log.v("ashmem","ShmService memoryFile:" + memoryFile.length());
//                memoryFile.writeBytes(buf2Send, 0, 0, buf2Send.length);
                    Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
                    FileDescriptor fd = (FileDescriptor)method.invoke(memoryFile);
                    pfd = ParcelFileDescriptor.dup(fd);
                }else {
                    pfd = ParcelFileDescriptor.fromFd(AshmemManager.getInstance().getFd2Ashmem());
                }
            } catch (Exception e) {
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
