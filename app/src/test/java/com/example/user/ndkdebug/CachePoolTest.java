package com.example.user.ndkdebug;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by user on 2018/9/12.
 */

public class CachePoolTest {

    private static final int GIRL_POOL_SIZE = 10;
    private static final Girl[] GIRL_POOL =new Girl[GIRL_POOL_SIZE];

    static class Girl{
        String name;
        String address;

        public Girl() {
            System.out.println("Girl.构造");
        }

        void recycle(){
            name = null;
            address = null;
        }

        void init(String _name, String _address){
            name = new String(_name);
            address = new String(_address);
        }

        @Override
        public String toString() {
            return "Girl{" +
                    "name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    /**
     *
     * 获取对象实例，两种情况：
     * 1. 缓存池中有对象，直接返回使用，并清空缓存中的实例（防止其他过程错误复用）
     * 2. 缓存池中无任何对象，新建对象返回
     *
     * @return
     */
    Girl obtailGirlObject(){
        synchronized (GIRL_POOL){
            for (int i = 0; i < GIRL_POOL.length; i++) {
                Girl girl = GIRL_POOL[i];
                if ( girl != null) {
                    System.out.println(Thread.currentThread().getId() + " 这里找到了缓存的Girl：" + i);
                    GIRL_POOL[i] = null;
                    return girl;
                }
            }
        }
        System.out.println(Thread.currentThread().getId() + " 这里新建Girl对象.");
        return new Girl();
    }

    /**
     *
     * 释放对象
     * 在缓存中找到空余的位置，回收对象。
     *
     * @param girl
     */
    void releaseGirlObject(Girl girl){
        girl.recycle();
        synchronized (GIRL_POOL){
            for (int i = 0; i < GIRL_POOL.length; i++) {
                if (GIRL_POOL[i] == null) {
                    GIRL_POOL[i] = girl;
                    System.out.println(String.format(Thread.currentThread().getId() + " 在这里:%d 将使用过的Girl缓存起来.",i));
                    return;// 不要忘了退出
                }
            }
        }
        /*
        *
        * 想想走到了这里意味着什么？
        *
        * */
        System.out.println(Thread.currentThread().getId() + " 没有在缓存中找到空余的位置，这里将造成对象堆积，等待JVM回收");
    }

    /**
     *
     * 使用缓存池的好处：
     *      避免对象的创建和销毁，避免引发内存抖动
     *
     * 比如，此处循环获取5次对象，但实际只调用了一次Girl的"构造函数"
     *
     * 场景：
     *      需要高频临时对象的场合。
     *      比如：FindState、PendingPost
     *
     * @throws Exception
     */
    @Test
    public void CacheTest() throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int j = 0; j < 10; j++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        Girl one = obtailGirlObject();
                        one.init(String.format("name_%d", i), String.format("address_%d",i));
                        System.out.println(Thread.currentThread().getId() + " do fuck = " + one);
                        releaseGirlObject(one);
                    }
                }
            });
        }
    }

    /**
     *
     * 错误的示例：根据循环次数，线性增加new的次数，重复调用Gril的构造，增加jvm的gc压力
     *
     * @throws Exception
     */
    @Test
    public void wrongSampleTest() throws Exception {
        for (int i = 0; i < 5; i++) {
            Girl one = new Girl();
            one.init(String.format("name_%d", i), String.format("address_%d",i));
            one.recycle();
            one = null;
        }
    }
}
