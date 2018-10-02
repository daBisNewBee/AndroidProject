package com.example.user.ndkdebug;

import android.os.StrictMode;

import com.squareup.leakcanary.LeakCanary;

import org.junit.Assert;
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
 *      "内存不够时"才回收。
 *      常用于缓存Cache机制。比如图片缓存：
 *      SoftReference<Drawable> softReference = mImageCache.get(imageUrl);
         if (softReference.get() != null)
         {
            return softReference.get();
         }

 *
 * 3. 弱引用（WeakReference）
 *      被"垃圾回收时"发现就回收，不够内存是否足够
 *      可以和"引用队列(ReferenceQueue)"联合使用，回收后，JVM会把该弱引用加入到该队列中。
 *      常用于防止内存泄漏。
 *      比如：
 *       private MyHandler handler = new MyHandler(this);
         private static class MyHandler extends Handler{
             WeakReference<FirstActivity> weakReference;
             MyHandler(FirstActivity activity) {
                weakReference = new WeakReference<>(activity);
             }

             @Override
             public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                }
             }
         }
 *
 * 4. 虚引用(PhantomReference)
 *      任何时候都可能被垃圾回收。类似于没有任何引用一样。
 *      和弱引用的一个区别在：必须和引用队列(ReferenceQueue)联合使用。
 *      虚引用和前面的软引用、弱引用的最大不同：它并不影响对象的生命周期！
 *
 *  TODO：
 *  未验证Android4.0后，软引用特性改变为被gc发现就清除，与弱引用相同！
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

        @Override
        public String toString() {
            return "Store{" +
                    "id='" + id + '\'' +
                    '}';
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
                System.out.println("在队列里发现了对象："+ ref + "......" + ref.get());
            else
                System.out.println("没有在队列里找到对象！");
        }
    }

    @Test
    public void gc_Test() throws Exception {
        HashSet<SoftReference<Store>> softHashSet = new HashSet<>();
        HashSet<WeakReference<Store>> weakHashSet = new HashSet<>();
        HashSet<PhantomReference<Store>> phantomHashSet = new HashSet<>();

        // 1. 创建10个软引用
        for (int i = 0; i < 10; i++) {
            SoftReference<Store> soft = new SoftReference<>(new Store("soft" + i), queue);
            System.out.println(i + " create soft:" + soft.get());
            softHashSet.add(soft);
        }

        System.out.println("这里打印了 finalize 表示 对象被释放！。。。。");
        System.gc();
        checkQueue();

        // 2. 创建10个弱引用
        queue.remove(1000);
        for (int i = 0; i < 10; i++) {
            WeakReference<Store> weak = new WeakReference<Store>(new Store("weak" + i), queue);
            System.out.println(i + " create weak:" + weak.get());
            weakHashSet.add(weak);
        }

        System.out.println("这里打印了 finalize 表示 对象被释放！。。。。");
        System.gc();
        checkQueue();

        // 3. 创建10个虚引用
        queue.remove(1000);
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

        // 基本数据类型是值传递
        boolean shouldRunGC = true;
        boolean tmp = shouldRunGC;
        shouldRunGC = false;
        Assert.assertEquals(tmp,true);
        System.out.println("tmp = " + tmp);

        // 引用传递
        String str = "hello";
        String tmpStr = str;
        str = "world";
        Assert.assertEquals("hello", tmpStr);
        System.out.println("tmpStr = " + tmpStr);

        // 由上面的强引用创建一个软引用
        System.out.println("soft:");
        SoftReference<Store> soft = new SoftReference<>(new Store("cao"), queue);
        System.out.println("create soft = " + soft.get());
        System.gc();
        checkQueue();

        queue.remove(1000);
        System.out.println("weak:");
        WeakReference<Store> weak = new WeakReference<>(new Store("ta"), queue);
        System.out.println("create weak = " + weak.get()+" ref:"+weak);
        System.gc();
        checkQueue();

        // 创建一个虚引用
        queue.remove(1000);
        PhantomReference<Store> phantom = new PhantomReference<>(new Store("daye"), queue);
        System.out.println("create phantom = " + phantom.get());
        System.gc();
        checkQueue();
    }

}
