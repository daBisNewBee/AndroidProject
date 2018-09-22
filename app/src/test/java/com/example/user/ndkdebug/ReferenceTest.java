package com.example.user.ndkdebug;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 *
 * java中的引用有哪些？如何运用？
 *
 * 1. 强引用(StrongReference)
 *      默认类型。JVM宁愿抛出OOM，也不会随意回收该类型对象
 *
 *
 * 2. 软引用(SoftReference)
 *      内存不够才回收。
 *
 * 3. 弱引用（WeakReference）
 *      被"垃圾回收器线程"发现就回收，不够内存是否足够
 *      可以和"引用队列(ReferenceQueue)"联合使用，回收后，JVM会把该弱引用加入到该队列中。
 *
 * 4. 虚引用(PhantomReference)
 *      任何时候都可能被垃圾回收。类似于没有任何引用一样。
 *      和弱引用的一个区别在：必须和引用队列(ReferenceQueue)联合使用。
 *
 * 参考：
 * https://www.jianshu.com/p/c0e5c13d5ecb
 *
 * Created by user on 2018/9/18.
 */

public class ReferenceTest {

    class Store {
        public static final int SIZE = 10000;
        private double[] arr = new double[SIZE];
        private String id;

        public Store() {
        }

        public Store(String id) {
            this.id = id;
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println(id + " Store.finalize ----------->");
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static ReferenceQueue<Store> queue = new ReferenceQueue<>();

    public static void checkQueue(){
        if (queue != null)
        {
            Reference<Store> ref = (Reference<Store>)queue.poll();
            if (ref != null)
                System.out.println(ref + "......" + ref.get());
        }
    }

    @Test
    public void gc_Test() throws Exception {
        HashSet<SoftReference<Store>> softHashSet = new HashSet<>();
        HashSet<WeakReference<Store>> weakHashSet = new HashSet<>();
        HashSet<PhantomReference<Store>> phantomHashSet = new HashSet<>();

        // 创建10个软引用
        for (int i = 0; i < 10; i++) {
            SoftReference<Store> soft = new SoftReference<>(new Store("soft" + i), queue);
            System.out.println(i + " create soft:" + soft.get());
            softHashSet.add(soft);
        }

        System.out.println("这里打印了 finalize 表示 对象被释放！。。。。");
        System.gc();
        checkQueue();

        for (int i = 0; i < 10; i++) {
            WeakReference<Store> weak = new WeakReference<Store>(new Store("weak" + i), queue);
            System.out.println(i + " create weak:" + weak.get());
            weakHashSet.add(weak);
        }

        System.out.println("这里打印了 finalize 表示 对象被释放！。。。。");
        System.gc();
        checkQueue();

        for (int i = 0; i < 10; i++) {
            PhantomReference<Store> phantom = new PhantomReference<>(new Store("phan" + i), queue);
            System.out.println(i + " create phantom:" + phantom.get());
            phantomHashSet.add(phantom);
        }

        System.out.println("这里打印了 finalize 表示 对象被释放！。。。。");
        System.gc();
        checkQueue();
    }

    @Test
    public void ref_Test() throws Exception {

        // 这是一个强引用
        String str = "hello";
        // 由上面的强引用创建一个软引用
        SoftReference<String> soft = new SoftReference<String>(str);
        str = null;
        System.out.println("soft:" + soft.get());

        str = "hello";
        ReferenceQueue<? super String> q = new ReferenceQueue<String>();
        // 创建一个虚引用
        PhantomReference<String> p = new PhantomReference<>(str, q);
        System.out.println(q.poll());
    }
}
