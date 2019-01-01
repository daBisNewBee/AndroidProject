package com.example.user.ndkdebug;

import com.exa.JavaBean;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    @Test
    public void MUTF8_Test() {
        JavaBean bean = new JavaBean();
        String rec = bean.modifiedUTF8Test("abc");
        System.out.println("modifiedUTF8Test rec = " + rec);
    }
}
