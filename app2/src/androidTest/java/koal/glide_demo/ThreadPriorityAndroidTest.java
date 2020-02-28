package koal.glide_demo;

import android.os.Process;

import org.junit.Test;

import java.util.Random;

/**
 *
 * 线程优先级的继承特性:
 * 也就是如果线程A启动线程B,那么线程A和B的优先级是一样的;
 *
 * 1. "优先级" 代表 "被CPU优先分配的概率"
 * 2. android 上的运行结果，比jvm上，更绝对。比如6始终比5的优先级线程先运行
 *
 * Created by wenbin.liu on 2020-02-29
 *
 * @author wenbin.liu
 */
public class ThreadPriorityAndroidTest {

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
                // TODO: setThreadPriority 不会对 "getPriority"的结果产生影响！
//            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            long start = System.currentTimeMillis();
            long count = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 100000; j++) {
                    Random random = new Random();
                    random.nextInt();
                    count = count + i;
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("############# " + Thread.currentThread().getName() + " cost:" + (end - start));
        }
    };

    @Test
    public void main() throws Exception {

        ThreadGroup group = Thread.currentThread().getThreadGroup();
        int activeCount = group.activeCount();
        Thread[] threads = new Thread[activeCount];
        group.enumerate(threads);

        for (Thread thread : threads) {
//            System.out.println("thread getName = " + thread.getName());
//            System.out.println("thread getPriority = " + thread.getPriority());
        }

        for (int i = 0; i < 10; i++) {
            Thread thread1 = new Thread(mRunnable);
            thread1.setName("thread1");
            int defPriority = thread1.getPriority();
//            System.out.println("defPriority = " + defPriority);
            thread1.setPriority(6);
//            thread1.setPriority(Thread.MAX_PRIORITY);

            Thread thread2 = new Thread(mRunnable);
            thread2.setName("thread2");
//            thread2.setPriority(6);
            thread2.setPriority(Thread.NORM_PRIORITY);

            thread1.start();
            thread2.start();
        }
        Thread.sleep(1000000);
    }
}
