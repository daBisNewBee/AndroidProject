package com.example.user.ndkdebug;

import android.os.SystemClock;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * fd数量过多（1024）、线程数量过多（threads-max）导致的OOM，
 * 这类OOM的特点是崩溃时java堆内存和设备物理内存都充足！
 *
 * 导致OOM发生的原因

 综上，可以导致OOM的原因有以下几种：

 1. （较少遇到）文件描述符(fd)数目超限，即proc/pid/fd下文件数目突破/proc/pid/limits中的限制。可能的发生场景有：
 短时间内大量请求导致socket的fd数激增，大量（重复）打开文件等
 2. （较少遇到）线程数超限，即proc/pid/status中记录的线程数（threads项）突破/proc/sys/kernel/threads-max中规定的最大线程数。可能的发生场景有：
 app内多线程使用不合理，如多个不共享线程池的OKhttpclient等等
 3. （常见）传统的java堆内存超限，即申请堆内存大小超过了 Runtime.getRuntime().maxMemory()
 4. （低概率）32为系统进程逻辑空间被占满导致OOM.
 5. 其他
 6. 内存抖动，频繁GC后剩下内存碎屏，申请大对象需要连续内存，发现不够了

 参考：不可思议的OOM
 链接：https://www.jianshu.com/p/e574f0ffdb42
 *
 * Created by user on 2018/10/8.
 */

public class OOMTest {
    /**
     *   1. 报错：
     *   java.lang.OutOfMemoryError: Could not allocate JNI Env:
     *      Failed anonymous mmap(0x0, 8192, 0x3, 0x2, 37, 0): Operation not permitted. See process maps in the log.
         java.lang.OutOfMemoryError: pthread_create (1040KB stack) failed: Try again
         at java.lang.Thread.nativeCreate(Native Method)
         at java.lang.Thread.start(Thread.java:733)
         at com.example.user.ndkdebug.OOMTest.threads_oom_Test(OOMTest.java:26)
     *
     *  2. 可以创建的线程数量实际由决定：
     *  vbox86p:/ # cat /proc/sys/kernel/threads-max
        31593
        参考：
     linux 进程的最大线程个数：
     https://blog.csdn.net/caspiansea/article/details/42731337
     *
     * @throws Exception
     */
    @Test
    public void threads_oom_Test() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/sys/kernel/threads-max"));
        final int threads_max = Integer.valueOf(bufferedReader.readLine());
        System.out.println("threads_max = " + threads_max);

        final int defalut_threads_num = Thread.activeCount();
        System.out.println("activeCount:" + defalut_threads_num);
        // 4

        List<Thread> list = new ArrayList<>();
        // 31593（threads-max） - 4(默认的四个线程)
        for (int i = 0; i < (threads_max - defalut_threads_num); i++) {
            list.add(new Thread(){
                public void run(){
                    while (true){
                        SystemClock.sleep(2000);
                    }
                }
            });
            list.get(i).start();
        }
        while (Thread.activeCount() > 1){
            Thread.yield();
        }
    }

    /**
     *
     * 1. 报错：
     * java.io.FileNotFoundException: /sdcard/tmp.txt (Too many open files)
     *
     * 2. 已打开的其他fd：
     * vbox86p:/proc/21505 # ls -l fd| wc -l
       37
     *
     * 3. 限制数量：
     * vbox86p:/proc/21303 # cat limits
     Limit                     Soft Limit           Hard Limit           Units
     Max cpu time              unlimited            unlimited            seconds
     Max file size             unlimited            unlimited            bytes
     Max data size             unlimited            unlimited            bytes
     Max stack size            8388608              unlimited            bytes
     Max core file size        0                    unlimited            bytes
     Max resident set          unlimited            unlimited            bytes
     Max processes             15796                15796                processes
     Max open files            1024（这里！！）       4096                 files
     *
     * 4. 因此剩余可用的fd数量为： limits - 37 = 987
     *
     * @throws Exception
     */
    @Test
    public void fd_oom_Test() throws Exception {
        List<FileReader> fileReaders = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            System.out.println("i = " + i);
            FileReader fis = new FileReader("/sdcard/tmp.txt");
            fileReaders.add(fis);
        }
    }
}
