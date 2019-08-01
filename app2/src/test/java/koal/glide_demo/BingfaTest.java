package koal.glide_demo;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 同步、互斥的几种方式：
 * 1. synchronized
 * 2. lock
 * 3. Semaphore
 *
 * 同步、互斥的差异：
 * 1. 都是同一时间只能有一个单位对资源进行访问
 * 2. 同步强调有序，互斥可以无序
 *
 * synchronized、Semaphore 的区别：
 * 1. synchronized 同一时间只能有一个线程获得执行代码的锁；
 * 2. Semaphore 可以有多个！！比 synchronized 的同步访问资源的线程数容量更大！比如买票时，只有5个窗口问题
 *
 *
 * Created by wenbin.liu on 2019-07-31
 *
 * @author wenbin.liu
 */
public class BingfaTest {

    private int num;
    private byte[] lock = new byte[0];

    /**
     "synchronized"带来500倍的代价：

     cost:
     syn > sem > lock > normal

     syn的耗费：
     cost:1943 ms
     cost:1995 ms
     cost:1970 ms
     cost:1959 ms
     cost:1960 ms

     Semaphore的耗费：
     cost:1807 ms
     cost:1805 ms
     cost:1791 ms
     cost:1789 ms
     cost:1789 ms

     ReentrantLock的耗费：
     cost:1645 ms
     cost:1512 ms
     cost:1498 ms
     cost:1477 ms
     cost:1470 ms

     普通的耗费：
     cost:3 ms
     cost:4 ms
     cost:4 ms
     cost:3 ms
     */
    @Test
    public void syn_cost() throws InterruptedException {
        System.out.println("syn的耗费：");
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000 * 1000 * 100; i++) {
                synchronized (lock){
                    num = i;
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("cost:" + (end -start) + " ms");
        }

        System.out.println("Semaphore的耗费：");
        Semaphore sem = new Semaphore(1);
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000 * 1000 * 100; i++) {
                sem.acquire(); // -1
                num = i;
                sem.release(); // +1
            }
            long end = System.currentTimeMillis();
            System.out.println("cost:" + (end -start) + " ms");
        }
        System.out.println("ReentrantLock的耗费：");
        Lock lock = new ReentrantLock();
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000 * 1000 * 100; i++) {
                lock.lock(); // -1
                num = i;
                lock.unlock(); // +1
            }
            long end = System.currentTimeMillis();
            System.out.println("cost:" + (end -start) + " ms");
        }

        System.out.println("普通的耗费：");
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000 * 1000 * 100; i++) {
                    num = i;
            }
            long end = System.currentTimeMillis();
            System.out.println("cost:" + (end -start) + " ms");
        }
    }
}
