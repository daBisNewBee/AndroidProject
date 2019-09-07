package com.example.user.ndkdebug;

import android.os.SystemClock;

import com.exa.JavaBean;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JNITest {
    @Test
    public void byteConvert_Test() {
//        byte[] inBuf = new byte[]{0x12, 0x34, 0x56, 0x78};
        byte[] inbuf1 = new byte[]{1, 2, 3, 4};
        byte[] inbuf2 = new byte[]{5, 6, 7, 8, 9};

        byte[] outbuf1 = new byte[inbuf1.length];
        byte[] outbuf2 = new byte[inbuf2.length];

        JavaBean bean = new JavaBean();

        int ret = bean.process(inbuf1, inbuf2, outbuf1, outbuf2);
        System.out.println("inbuf1:" + Arrays.toString(inbuf1));
        System.out.println("outbuf1:" + Arrays.toString(outbuf1));

        System.out.println("inbuf2:" + Arrays.toString(inbuf2));
        System.out.println("outbuf2:" + Arrays.toString(outbuf2));

        byte[] reciveArray = bean.nativeGetByteArray();
        System.out.println("reciveArray" + Arrays.toString(reciveArray));
    }

    @Test
    public void directBuffer_Test() {
        JavaBean bean = new JavaBean();

        ByteBuffer byteBuffer;
        byteBuffer = ByteBuffer.allocateDirect(5);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(new byte[]{1,2,3,4,5});

//        System.out.println("之前：" + Arrays.toString(byteBuffer.array()));
        System.out.println("之前：");
        for (int i = 0; i < byteBuffer.limit(); i++) {
            System.out.println(byteBuffer.get(i));
        }

        // 1. 在Java创建DirectBuf，在Native修改
        bean.shareByteArrayToNative(byteBuffer);

        System.out.println("之后：");
        for (int i = 0; i < byteBuffer.limit(); i++) {
            System.out.println(byteBuffer.get(i));
        }

        // 不准，实际会cap会大于分配的容量5。
//        System.out.println("之后：" + Arrays.toString(byteBuffer.array()));

        // 2. 在Native创建DirectBuf，在Java获取
        ByteBuffer buffer = bean.getDirectBufferFromNative();
        System.out.println("获取从原生代码创建的DirectBuffer：");
        for (int i = 0; i < byteBuffer.limit(); i++) {
            System.out.println(buffer.get(i));
        }
    }

    @Test
    public void encodeTypeSpeedTest() {
        String rawData = "大大萨达撒大萨达撒大硕大的撒大所萨达所大所多大萨达撒abcdefghijklm";
        JavaBean bean = new JavaBean();
        long start,end;

        for (int j = 0; j < 5; j++) {
            start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                bean.sendUTF8StringOnly(rawData);
            }
            end = System.currentTimeMillis();
            System.out.println("UTF8 cost = " + (end-start) +" ms" );

            start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                bean.sendUTF16StringOnly(rawData);
            }
            end = System.currentTimeMillis();
            System.out.println("UTF16 cost = " + (end-start) +" ms" );
        }
    }

    @Test
    public void string_Test() {
        Charset charset = Charset.defaultCharset();
        System.out.println("defaultCharset = " + charset);

//        charset = StandardCharsets.UTF_16;
        String raw = new String("fuck you. 包括中文字符。".getBytes(charset), charset);

        JavaBean bean = new JavaBean();
        String rec = bean.sendUTF8String(raw);
        System.out.println("sendUTF8String rec = " + rec);

        rec = bean.sendUTF16String(raw);
        System.out.println("sendUTF16String rec = " + rec);

        rec = bean.sendStringPointer(raw);
        System.out.println("sendStringPointer rec = " + rec);

        rec = bean.sendUTF8StringRegion(raw);
        System.out.println("sendUTF8StringRegion rec = " + rec);
    }

    /**
     * 为何 JNI的api支持的是Modified UTF-8？不是标准UTF-8？
     *
     * 方便在JNI中调用libc字符串函数(以零结尾的字符串)
     *
     * 分析过程：
     *
     * 若字符串为：
     * "我爱祖国abcd\\u0000efgh"
     * 1. 标准的UTF-8是：
     *      e6 88 91 e7 88 b1 e7 a5 96 e5 9b bd 61 62 63 64 0     65 66 67 68
     * 2. Modified-UTF-8是：
     *      e6 88 91 e7 88 b1 e7 a5 96 e5 9b bd 61 62 63 64 c0 80 65 66 67 68
     *      即，对"\\u0000"的空字符编码，以双字节"0xc080"代替单字节"0x00"
     * 3. 标准的libc 字符串函数遇"0x00"结束，而此处由于是"0xc080"，因此可以继续处理。
     *
     * https://developer.android.com/training/articles/perf-jni#utf-8-and-utf-16-strings
     *
     * @throws Exception
     */
    @Test
    public void MUTF8_Test() throws Exception {

        String raw = "我爱祖国abcd\u0000efgh";
        byte[] byte_in_standard_utf8 = raw.getBytes("UTF-8");
        for (byte i :
                byte_in_standard_utf8) {
            System.out.format("%x ", i);
        }
        System.out.println();

        JavaBean bean = new JavaBean();
        String rec = bean.modifiedUTF8Test(raw);
        System.out.println("modifiedUTF8Test rec = " + rec);
    }

    /**
     * JNI拷贝存在代价(正向Java -> Native)
     *
     * 耗时统计：(ms)
     *                      Java堆   Native堆    常规调用  循环次数         数据量
     * 数据块大小：1kb        375     353           1      100 * 1024      100MB
     *           2kb        480     357           1      100 * 1024      200MB
     *           4kb        580     350           1      100 * 1024      400MB
     *           8kb        775     355           1      100 * 1024      800MB
     *
     * 结论：
     *  1. 使用Native堆时，单次不同的数据块大小在JNI通信时，耗时不变。可以理解为数据块大小不影响JNI拷贝效率
     *  2. 使用Java堆时，单次数据块越大，耗时越长。
     *          在数据块为4KB时，达到native的2倍不到；
     *          1KB及以下时，耗时与native堆基本相同
     */
    @Test
    public void speed_Test() {
        JavaBean bean = new JavaBean();
        final int raw_size_in_byte = 8 * 1024;
        byte[] raw = new byte[raw_size_in_byte];
        for (int i = 0; i < raw_size_in_byte; i++) {
            raw[i] = (byte) i;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(raw_size_in_byte);
        byteBuffer.put(raw);

        for (int j = 0; j < 5; j++) {
            long start = SystemClock.currentThreadTimeMillis();
            for (int i = 0; i < 100 * 1024; i++) {
                bean.sendArrayWithCopy(raw);
            }
            long end = SystemClock.currentThreadTimeMillis();
            System.out.println("Use Java Heap. cost:" + (end-start));

            start = SystemClock.currentThreadTimeMillis();
            for (int i = 0; i < 100 * 1024; i++) {
                bean.sendArrayWithDirectBuf(byteBuffer);
            }
            end = SystemClock.currentThreadTimeMillis();
            System.out.println("Use Native Heap. cost:" + (end-start));

            start = SystemClock.currentThreadTimeMillis();
            for (int i = 0; i < 100 * 1024; i++) {
                bean.sendArray(raw);
            }
            end = SystemClock.currentThreadTimeMillis();
            System.out.println("Use java call. cost:" + (end-start));
        }
    }

    /**
     * JNI拷贝存在代价(反向，Native -> Java)
     *
     * 1kb  485ms
     * 2kb  650ms
     * 4kb  1057ms
     * 8kb  1509ms
     *
     * 数据量：1kb * 1024 * 100
     */
    @Test
    public void getByteArrayCostTest() {
        JavaBean bean = new JavaBean();
        long start, end;
        start = System.currentTimeMillis();
        for (int i = 0; i < 1024 * 100; i++) {
            byte[] arrayGet = bean.nativeGetByteArray();
        }
        end = System.currentTimeMillis();
        System.out.println("cost = " + (end - start) + "ms");
//        System.out.println("arrayGet size = " + arrayGet.length);
    }

    @Test
    public void shortSpeed_Test() {
        JavaBean bean = new JavaBean();
        final int raw_size_in_short = 4 * 1024; // short : 4 * 1024 == byte ：8 * 1024
        short[] raw = new short[raw_size_in_short];
        for (int i = 0; i < raw_size_in_short; i++) {
            raw[i] = (short)i;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(raw_size_in_short << 1);
        ShortBuffer shortBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();

        for (int j = 0; j < 5; j++) {
            long start = SystemClock.currentThreadTimeMillis();
            for (int i = 0; i < 100 * 1024; i++) {
                bean.sendArrayShortWithCopy(raw);
            }
            long end = SystemClock.currentThreadTimeMillis();
            System.out.println("Use Java Heap. cost:" + (end-start));

            start = SystemClock.currentThreadTimeMillis();
            for (int i = 0; i < 100 * 1024; i++) {
                bean.sendArrayShortWithDirectBuf(shortBuffer);
            }
            end = SystemClock.currentThreadTimeMillis();
            System.out.println("Use Native Heap. cost:" + (end-start));
        }
    }

    @Test
    public void registerMethods_Test() throws Exception {
        // static method.
        String ret = JavaBean.myClassFunc();
        System.out.println("myClassFunc ret = " + ret);

        /*
        * UTF-16:
        * fe ff 0 64 0 61 0 42 0 69 0 73 0 4e 0 65 0 77 0 42 0 65 0 65
        *
        * UTF-8:
        * e6 88 91 e6 98 af 64 61 42 69 73 4e 65 77 42 65 65
        * */
        String raw = "我是daBisNewBee";
        byte[] rawByte = raw.getBytes("UTF-16");
        System.out.println("UTF-16 编码后的字符串：");
        for (byte i: rawByte) {
            System.out.format("%x ", i);
        }
        System.out.println();
        // object method.
        JavaBean bean = new JavaBean();
        ret = bean.modifiedUTF8Test(raw);
        System.out.println("modifiedUTF8Test ret = " + ret);
    }
}
