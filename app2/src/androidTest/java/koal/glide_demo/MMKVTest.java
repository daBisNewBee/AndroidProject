package koal.glide_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVHandler;
import com.tencent.mmkv.MMKVLogLevel;
import com.tencent.mmkv.MMKVRecoverStrategic;
import com.tencent.mmkv.NativeBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

/**
 *
 * MMKV(实时读写)
 *
 * 核心：mmap
 *
 * 原理：（官方说明）
 *      内存准备：
 *      通过 mmap 内存映射文件，提供一段可供随时写入的内存块，App 只管往里面写数据，
 *      由操作系统负责将内存回写到文件，不必担心 crash 导致数据丢失。
 *
 *      数据组织：
 *      数据序列化方面我们选用 protobuf 协议，pb 在性能和空间占用上都有不错的表现。
 *
 *      写入优化：
 *      考虑到主要使用场景是频繁地进行写入更新，我们需要有增量更新的能力。我们考虑将
 *      增量 kv 对象序列化后，append 到内存末尾。
 *
 *      空间增长：
 *      使用 append 实现增量更新带来了一个新的问题，就是不断 append 的话，文件大小
 *      会增长得不可控。我们需要在性能和空间上做个折中。
 *
 * 思考：
 * 1. 为什么 mmap 效率高于 read/write？
 *      mmap 一次数据拷贝。
 *           物理磁盘 -> 用户空间 (缺页中断时)
 *      read/write 两次数据拷贝。
 *           物理磁盘 -> 内核空间 -> 用户空间
 *
 * 2. Android 多进程访问
 *
 * 参考：
 * 1、 为什么mmap后，比直接对文件read/write效率更高？
 * https://blog.csdn.net/lin20044140410/article/details/88899468
 *
 */
public class MMKVTest {
    private MMKV mmkv;
    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        String rootDir = MMKV.initialize(context);
        System.out.println("rootDir = " + rootDir);
        // /data/user/0/koal.glide_demo/files/mmkv
        /* 默认目录：
        * HWANE:/data/data/koal.glide_demo/files/mmkv $ find
           ./mmkv.default.crc
           ./mmkv.default
           ./MyID1.crc
           ./MyID1
           ./MyID2.crc
           ./MyID2
           ./MyID3.crc
           ./MyID3
        * */
        mmkv = MMKV.defaultMMKV();
        // 转发 MMKV 的日志
        MMKV.registerHandler(new MyMMKVHandler());
    }

    @After
    public void tearDown() throws Exception {
        MMKV.unregisterHandler();
    }

    class MyMMKVHandler implements MMKVHandler{

        @Override
        public MMKVRecoverStrategic onMMKVCRCCheckFail(String s) {
            return MMKVRecoverStrategic.OnErrorRecover;
        }

        @Override
        public MMKVRecoverStrategic onMMKVFileLengthError(String s) {
            return MMKVRecoverStrategic.OnErrorRecover;
        }

        @Override
        public boolean wantLogRedirecting() {
            return true;
        }

        @Override
        public void mmkvLog(MMKVLogLevel mmkvLogLevel, String s, int i, String s1, String s2) {
            Log.d("System.out", "mmkvLog() called with: mmkvLogLevel = [" + mmkvLogLevel + "], s = [" + s + "], i = [" + i + "], s1 = [" + s1 + "], s2 = [" + s2 + "]");
        }
    }

    @Test
    public void mmkv_Test() {
        // 1. CRUD 操作：
        mmkv.encode("key1", true);
        boolean bValue = mmkv.decodeBool("key1");
        System.out.println("bValue = " + bValue);

        mmkv.encode("key2", 99999);
        int iValue = mmkv.decodeInt("key2");
        System.out.println("iValue = " + iValue);

        mmkv.encode("key3", "hello world");
        String sValue = mmkv.decodeString("key3");
        System.out.println("sValue = " + sValue);

        boolean ret = mmkv.containsKey("key3");
        System.out.println("containsKey ret = " + ret);
        mmkv.removeValueForKey("key3");
        ret = mmkv.containsKey("key3");
        System.out.println("After removeValueForKey containsKey ret = " + ret);

        // 2. 分别创建实例，可以用于不同业务! 区别存储
        MMKV mmkv1 = MMKV.mmkvWithID("MyID1");
        mmkv1.encode("key", "this is from mmkv myid1");
        String value = mmkv1.decodeString("key");
        System.out.println("value = " + value);

        // 3. 支持多进程访问
        MMKV mmkv2 = MMKV.mmkvWithID("MyID2", MMKV.MULTI_PROCESS_MODE);

        // 4. 从sp迁移数据
        MMKV fakePrefer = MMKV.mmkvWithID("MyID2");

        SharedPreferences oldSp = context.getSharedPreferences("ting", Context.MODE_PRIVATE);
        oldSp.edit().putString("albumId", "123456").apply();
        int retInt = fakePrefer.importFromSharedPreferences(oldSp);
        System.out.println("retInt = " + retInt);
        oldSp.edit().clear().commit();

        if (fakePrefer.containsKey("albumId")) {
            String retStr = fakePrefer.decodeString("albumId");
            System.out.println("retStr = " + retStr);
        }

        // 5. 跟以前用法一样，类似Sp
        SharedPreferences.Editor editor = fakePrefer.edit();
        editor.putBoolean("bool", true);
        editor.putInt("int", Integer.MIN_VALUE);
        editor.putLong("long", Long.MAX_VALUE);
        editor.putFloat("float", -3.14f);
        editor.putString("string", "hello, imported");
        HashSet<String> set = new HashSet<String>();
        set.add("W"); set.add("e"); set.add("C"); set.add("h"); set.add("a"); set.add("t");
        editor.putStringSet("string-set", set);
        // 无需调用 commit()
        //editor.commit();
        String[] allKeys = fakePrefer.allKeys();
        for (String key : allKeys) {
            System.out.println("get key = " + key);
        }

        // 6. 加密 MMKV(默认明文)
        String cryptKey = "My-Encrypt-Key";
        MMKV mmkv3 = MMKV.mmkvWithID("MyID3", MMKV.SINGLE_PROCESS_MODE, cryptKey);
        mmkv3.encode("trackid", "987654321");
        mmkv3.reKey(null); // 清除秘钥，变成明文
        mmkv3.encode("trackid2", "11111111");

        // 7. NativeBuffer 用法。避免native 到 JVM 的内存来回拷贝
        mmkv.encode("bytes", new byte[]{0x00,0x01,0x02,0x03});
        int sizeNeeded = mmkv.getValueActualSize("bytes");
        NativeBuffer nativeBuffer = MMKV.createNativeBuffer(sizeNeeded);
        if (nativeBuffer != null) {
            int size = mmkv.writeValueToNativeBuffer("bytes", nativeBuffer);
            System.out.println("sizeNeeded = " + sizeNeeded + " size = " + size);

            //....这里传递 nativeBuffer 到另一个native library处理
            MMKV.destroyNativeBuffer(nativeBuffer);
        }

        // 8. 匿名共享内存，映射的是/dev/ashmem，不会落地到文件上，用于敏感数据的读写
        MMKV mmkv4 = MMKV.mmkvWithAshmemID(context, "mmkv_ashmem", 4096, MMKV.SINGLE_PROCESS_MODE, null);
        mmkv4.encode("money", "1200");
        String val = mmkv4.decodeString("money");
        System.out.println("val = " + val);


    }

    /**
     * MMKV:
     *   cost: 3ms
     *   cost: 3ms
     *   cost: 2ms
     *   cost: 3ms
     * SP:
     *   cost: 211ms
     *   cost: 165ms
     *   cost: 56ms
     *   cost: 54ms
     *   cost: 93ms
     */
    @Test
    public void speed_Test() {
        System.out.println("MMKV:");
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
//                mmkv.encode("key", i+"");
                mmkv.encode("key", i);
//                mmkv.decodeInt("key");
            }
            long end = System.currentTimeMillis();
            System.out.println("cost: " + (end-start) + "ms");
        }

        SharedPreferences sp = context.getSharedPreferences("ting", Context.MODE_PRIVATE);

        System.out.println("SP:");
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
//                sp.edit().putString("key", i+"").apply();
                sp.edit().putInt("key", i).apply();
//                sp.getInt("key", -1);
            }
            long end = System.currentTimeMillis();
            System.out.println("cost: " + (end-start) + "ms");
        }
    }
}
