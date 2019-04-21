package com.exa.ashmem;

/**
 * Created by wenbin.liu on 2019/4/21
 *
 * @author wenbin.liu
 */
public class AshmemManager {

    private static AshmemManager INSTANCE;

    public static AshmemManager getInstance(){
        if (null == INSTANCE)
            INSTANCE = new AshmemManager();
        return INSTANCE;
    }

    private int fd2Ashmem;

    public int getFd2Ashmem() {
        return fd2Ashmem;
    }

    public void setFd2Ashmem(int fd2Ashmem) {
        this.fd2Ashmem = fd2Ashmem;
    }

    // ashmem start
    public static native void doOperaNow(int fd);

    public static native void doOperaLater(int fdReceived);

    public static native int initAndGetFd2Ashmem();
    // ashmem end
}
