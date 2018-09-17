package com.example.user.ndkdebug;

import android.support.v4.util.Pools;

import org.junit.Test;

/**
 *
 * 对象池 二
 *
 * 使用系统自带框架：SynchronizedPool
 * 比CachePoolTest中优雅的方式
 *
 * 参考：
 * Android中对象池的使用：
 * https://blog.csdn.net/zuochunsheng/article/details/54980997
 *
 * Created by user on 2018/9/17.
 */

public class SynchronizedPoolTest {

    static class Boy{
        String id;
        String name;

        private static final Pools.SynchronizedPool<Boy> sPool = new Pools.SynchronizedPool<Boy>(10);

        public static Boy Obtain(){
            Boy instance = sPool.acquire();
            return instance != null ? instance : new Boy();
        }

        public void recycle(){
            sPool.release(this);
        }

    }

    @Test
    public void synPool_Test() throws Exception {
        for (int i = 0; i < 10; i++) {
            Boy boy = Boy.Obtain();
            // do sth.....
            boy.recycle();
        }
    }
}
