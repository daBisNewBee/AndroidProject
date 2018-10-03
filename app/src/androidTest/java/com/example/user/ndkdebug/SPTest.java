package com.example.user.ndkdebug;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * 结论：
 *   1. 尽量多次putInt数据后，再一次commit/apply，不要每次commit/apply。
 *   2. commit同步有返回值，效率会比apply异步提交的速度慢；
 *   3. apply异步无返回值，永远无法知道存储是否失败。
 *   4. 在不关心提交结果是否成功的情况下，"优先考虑apply方法"。
 *
 * Created by user on 2018/10/3.
 */

public class SPTest {
    private Context context;
    private SharedPreferences sp;

    private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener(){
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            System.out.println("SPTest.onSharedPreferenceChanged ------> ");
            String recvStr = sharedPreferences.getString(key,"");
            System.out.println(String.format("数据%s 更新成:%s", key, recvStr));
        }
    };

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
         sp = context.getSharedPreferences("user",Context.MODE_PRIVATE);
//         sp.registerOnSharedPreferenceChangeListener(listener);
    }

    @After
    public void tearDown() throws Exception {
//        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Test
    public void SP_Test() throws Exception {
        SharedPreferences.Editor editor = sp.edit();

        final String key = "data_key";
        final String data = "this is data to be write.";
        if (sp.contains(key)){
            System.out.println("remove. find " + key);
            editor.remove(key);
            editor.commit();
        }

        editor.putString(key, data);
        String get = sp.getString(key, "");
        Assert.assertEquals(get, "");
//        editor.commit();
        editor.apply();

        get = sp.getString(key,"");
        Assert.assertEquals(get, data);

        /*
        * 性能测试：
        *
        * 10000次putInt,一次commit：
        *   100ms左右
        * 10000次putInt,10000次commit：
        *   600ms左右
        *
        * 1000次putInt,一次apply：
        *   20ms左右
        * 1000次putInt,1000次apply：
        *   300ms左右
        *
        *
        * */
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            editor.putInt(Integer.toString(i), i);
        }
        editor.apply();
//        editor.commit();
        long end = System.currentTimeMillis();
        System.out.println("signle commit. cost:"+(end - start)+"ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            editor.putInt(Integer.toString(i), i);
            editor.apply();
//            editor.commit();
        }
        end = System.currentTimeMillis();
        System.out.println("every commit. cost:"+(end - start)+"ms");



    }
}
