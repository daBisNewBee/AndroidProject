package com.example.user.ndkdebug;

import android.os.SystemClock;

import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by user on 2018/10/1.
 */

public class SoftRefLostTest {

    class Store {
        public static final int SIZE = 1 * 1024 * 5;
        private byte[] arrBytes = new byte[1024 * SIZE];
//        private double[] arr = new double[SIZE];
        private String id;

        public Store() {
        }

        public Store(String id) {
            this.id = id;
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println(id + " Store.finalize ----------->" + this.toString());
        }

    }


    private static ReferenceQueue<Store> queue = new ReferenceQueue();

    private static ReferenceQueue<byte[]> queueBytes = new ReferenceQueue();

    void checkQueue(ReferenceQueue<?> queue) throws Exception{

        int count = 0;
        Reference<Store> reference = null;
        while ((reference = (Reference<Store>)queue.remove(5000)) != null){
//        while ((reference = (Reference<Store>)queue.poll()) != null){
            /*
            *
            * 当一个obj被gc掉之后，其相应的包装类，即ref对象会被放入queue中。
            *
            * "找到了对象：java.lang.ref.WeakReference@e9b2b18  get:null"
            *
            * 注意：是对象关联的reference对象放到了queue，不是对象本身！此时对象是取不到的，因为已被gc
            * */
            System.out.println(count++ + " " +
                    "对象被回收了！："+reference +"  get:" + reference.get());
        }
        System.out.println("checkQueue 结束！");
    }

    @Test
    public void soft_Test() throws Exception {
        Store store = null;

        System.out.println("这里开始软引用测试。。。");
        for (int i = 0; i < 10; i++) {
            store = new Store(i+"");
            System.out.println("store = " + store);
            SoftReference<Store> soft = new SoftReference<>(store, queue);
            System.out.println("soft = " + soft);
        }
        System.out.println("开始gc...");
        System.gc();
//        SystemClock.sleep(100);
        checkQueue(queue);


        System.out.println("\n这里开始弱引用测试。。。");
        queue.remove(1000);
        for (int i = 0; i < 10; i++) {
            store = new Store(i+"");
            System.out.println("store = " + store);
            WeakReference<Store> weak = new WeakReference<>(store, queue);
            System.out.println("weak = " + weak);
        }
        System.out.println("开始gc...");

        System.gc();
//        SystemClock.sleep(100);
        checkQueue(queue);
    }

    /**
     *
     * 使用队列进行数据监控.
     *
     * 存在问题：
     *  被gc的对象，仍在hashMap中被持有，实际已经失效。
     *
     * 目的：
     *  引出"WeakHashMap"的作用：
     *      被gc的对象，会自动在map中清除。
     *
     * @throws Exception
     */
    @Test
    public void dataMonitot_Test() throws Exception {

        Thread thread = new Thread(){
            public void run(){
                try {
                    checkQueue(queueBytes);
                } catch (Exception e) {
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        Object object = new Object();
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            byte[] arr = new byte[1024 * 1024];
            WeakReference<byte[]> weak  = new WeakReference<byte[]>(arr, queueBytes);
            map.put(weak, object);
        }
        /*
        *
        * System.out: 9997 对象被回收了！：java.lang.ref.WeakReference@237e01d  get:null
        *
        * queue中的count表示已经回收的对象数量9997， 但是"queueBytes"中size 始终是 10000！
        * 实际在map中 9997个对象都是无效的，因此期望queue中打印的size值是 3，而不是10000！
        * "WeakHashMap"就是做这样的事情！
        *
        * */
        System.out.println("end . size:" + map.size());
        SystemClock.sleep(10000);
    }

    /**
     *
     * weakHashMap即使用weakReference当作key来进行数据的存储，当key中的引用被gc掉之后，
     * 它会自动(类似自动)的方式将相应的entry给移除掉，即我们会看到size发生了变化。
     *
     * 原理：
     * "expungeStaleEntries"在处理点时，移除map中被gc掉的entry。
     *
     * @throws Exception
     */
    @Test
    public void weakHashMap_Test() throws Exception {

        WeakHashMap<Object,Object> weakHashMap = new WeakHashMap<>();
        Object object = new Object();
        for (int i = 0; i < 10000; i++) {
            byte[] arr = new byte[1024 * 1024];
            weakHashMap.put(arr, object);
        }
        System.out.println("end . size:" + weakHashMap.size());
        /*
        *
10-01 17:24:42.514  6840  6856 I System.out: end . size:93
10-01 17:25:48.849  6937  6953 I System.out: end . size:53
10-01 17:25:55.889  7001  7017 I System.out: end . size:47
        * */
    }

    /*
    队列监控的反向操作:
    反向操作，即意味着一个数据变化了，可以通过weakReference对象反向拿相关的数据，从而进行业务的处理。
    比如，我们可以通过继承weakReference对象，加入自定义的字段值，额外处理。
    一个类似weakHashMap如下，这时，我们不再将key值作为弱引用处理，而是封装在weakReference对象中，
    以实现额外的处理。

    实例：google guava

    与WeakHashMap的区别：
    weak***: key（key是占用内存的大对象）是弱引用！
    反向操作：value（value是占用内存的大对象）是弱引用，并通过继承的弱引用子类查询到所在的key，
            当监控到value被gc时，根据value找到key，手动在map中将entry清除。

    具体参考：
    https://blog.csdn.net/zjd934784273/article/details/50499025

     *  */
}
