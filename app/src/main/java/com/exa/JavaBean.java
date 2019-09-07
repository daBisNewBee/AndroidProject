package com.exa;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 *
 * Java与原生代码通信的几个要点：
 *
 * （根据分配内存所在的位置不同）
 *
 * 1. Java堆。
 *      在Java创建(new), 在JNI有两种方式接收java堆内存数组：
 *      a. GetByteArrayElements (指针映射)
 *      b. GetByteArrayRegion（值拷贝）
 *      在Native创建，
 *      a. NewByteArray + SetByteArrayRegion
 *
 * 2. 原生堆(DirectBuffer)。
 *      两种创建方式：
 *      a. 在Java创建，ByteBuffer.allocateDirect(5)
 *         在Native获取，GetDirectBufferAddress
 *      b. 在Native创建，NewDirectByteBuffer
 *         在Java获取，ShortBuffer
 *
 *  思考，
 *    分配数据缓存时，选择何种内存类型？Java堆，or，原生堆？
 *    判断数据的最终出口，若要与外界通信，
 *    最后的处理者是Native，则分配到原生堆更好！可以避免一次Java堆到Native堆的内存拷贝！
 *
 */
public class JavaBean {

    /*
    * JVM 查找 native 方法:
    * 1.静态注册： 按照 JNI 规范的命名规则
    * 2.（推荐）动态注册：调用 JNI 提供的 RegisterNatives 函数，将本地函数注册到 JVM 中
    *
    * */
    static {
        // 回调 so中的 "JNI_OnLoad"
        System.loadLibrary("native-lib");
    }

    /* 测试方法*/
    public native int sendArrayWithCopy(byte[] in);

    public native int sendArrayWithDirectBuf(ByteBuffer buffer);

    public int sendArray(byte[] in){
        return 0;
    }

    public native int sendArrayShortWithCopy(short[] in);

    public native int sendArrayShortWithDirectBuf(ShortBuffer buffer);

    /*
    *  从Java传递数组到Jni层
    * */
    public native int process(byte[] inbuf1, byte[] inbuf2, byte[] outbuf1, byte[] outbuf2);

    /*
    *  从Jni层传递数组到Java层
    * */
    public native byte[] nativeGetByteArray();

    // 这里的ByteBuffer 必须是"allocateDirect"
    public native void shareByteArrayToNative(ByteBuffer byteBuffer);

    public native ByteBuffer getDirectBufferFromNative();

    /*  JNI字符串处理 开始 */
    public native String sendUTF8String(String msg);
    public native void sendUTF8StringOnly(String msg);

    public native String sendUTF16String(String msg);
    public native void sendUTF16StringOnly(String msg);

    public native String sendStringPointer(String msg);

    public native String sendUTF8StringRegion(String msg);
    /*  JNI字符串处理 结束 */

    public native String modifiedUTF8Test(String msg);

    public static native String myClassFunc();
}
