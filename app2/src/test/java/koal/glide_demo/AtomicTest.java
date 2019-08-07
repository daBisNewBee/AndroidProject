package koal.glide_demo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 结论：
 * 并非Atomic方法一定优于synchronized
 * 当低并发时（小于150000），Atomic效率优于synchronized
 * 当高并发时（大于150000），synchronized效率优于Atomic
 * *150000为本机测试数据，不准
 *
 * 总结:
 * synchronized ：重量级操作，基于悲观锁，可重入锁。
 * AtomicInteger：乐观 ，用CAS实现
 * 当并发量大时，Atomic 出错概率会增大，不断校正错误更费时间
 *
 * TODO： 和tm的结果不一样！！
 *
 * 参考：
 * synchronized和AtomicXXX效率比较：
 * https://blog.csdn.net/baidu_35773818/article/details/89604328
 *
 */
public class AtomicTest {

    private static final int TIMES = 150 * 1000;

    static volatile int count_volatile = 0;
    int count_synchronized = 0;
    AtomicInteger count_atomic = new AtomicInteger(0);//2283   1895 2328 2015 2264

    void k(){
        for (int i = 0; i < TIMES; i++) {
            count_volatile++;
        }
    }

    synchronized void m() {
        for (int i = 0; i < TIMES; i++) {
            count_synchronized++;
        }
    }

    void n() {
        for (int i = 0; i < TIMES; i++) {
            count_atomic.incrementAndGet();
        }
    }

    @Test
    public void main() {
        // 1. volatile
        AtomicTest t0 = new AtomicTest();
        List<Thread> thread0 = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            thread0.add(new Thread(t0::k, "thread " + i));
        }
        thread0.forEach((o) -> o.start());
        thread0.forEach((o) -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("volatile -->" + t0.count_volatile);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println("----------------------------------");

        // 2. synchronized
        List<Thread> thread1 = new ArrayList<>();
        AtomicTest t1 = new AtomicTest();
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            thread1.add(new Thread(t1::m, "thread " + i));
        }
        thread1.forEach((o) -> o.start());
        thread1.forEach((o) -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("synchronized-->" + t1.count_synchronized);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println("----------------------------------");

        // 3. atomic
        List<Thread> thread2 = new ArrayList<>();
        AtomicTest t2 = new AtomicTest();
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            thread2.add(new Thread(t2::n, "thread " + i));
        }
        thread2.forEach((o) -> o.start());
        thread2.forEach((o) -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("atomic-->" + t2.count_atomic);
        System.out.println(System.currentTimeMillis() - start);
    }

    static class MyBean {

        public MyBean() {
            System.out.println("MyBean.MyBean");
        }

        public void fuckYou(){
            try {
                System.out.println("name :" + Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("MyBean.fuckYou");
        }

        public static void fuckYouAll(){
            try {
                System.out.println("name :" + Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("MyBean.fuckYouAll");
        }
    }

    @Test
    public void threadTest() throws Exception {
        System.out.println("cur thread Name:" + Thread.currentThread().getName());
        MyBean myBean = new MyBean();
//        Thread thread = new Thread(MyBean::fuckYouAll, "_myThread");
        // 直接指定了"fuckyou"方法运行在线程中
        Thread thread = new Thread(myBean::fuckYou, "_myThread");
        thread.start();
        thread.join();
    }
}
