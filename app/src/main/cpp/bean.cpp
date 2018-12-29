//
// Created by Salmon on 2018/12/29.
//
#include <jni.h>
#include <unistd.h>

#include <Android/Log.h>
#include <cstdlib>

#define TAG "jni"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)

/*
 * 两个函数从Jni层接收到Java层传递过来的byte[]数组：
 *
 * 1. GetByteArrayElements
 *    优点：
 *      操作的是指针映射（Java堆数组-本地数组），效率较高
 *      （不一定？某些实现中，也是需要拷贝的：
 *      https://developer.android.google.cn/training/articles/perf-jni#faq-how-do-i-share-raw-data-with-native-code）
 *    缺点：
 *      需要GetXXX、ReleaseXXX，code较麻烦，易出错，忘记release会泄漏内存
 *
 * 2.（推荐）GetByteArrayRegion
 *    优点：
 *      仅一步调用，方便
 *    缺点：
 *      会执行具体的值拷贝操作，速度、效率较低
 *
 *  参考：
 *  Android开发实践：Java层与Jni层的数组传递：
 *  https://www.linuxidc.com/Linux/2014-03/97561.htm
 *
 *  堆外内存 之 DirectByteBuffer 详解：
 *  https://www.jianshu.com/p/007052ee3773
 * */
extern "C"
JNIEXPORT jint JNICALL
Java_com_exa_JavaBean_process(JNIEnv *env, jobject instance, jbyteArray inbuf1_, jbyteArray inbuf2_,
                              jbyteArray outbuf1_, jbyteArray outbuf2_) {
    jbyte *outbuf1 = env->GetByteArrayElements(outbuf1_, NULL);

    // 1. 指针形式。将本地的数组指针inbuf1指向Java端的数组地址inbuf1_，本质为堆中的该对象增加引用计数，保证不被gc
    unsigned char *pBuff = (unsigned char*)env->GetByteArrayElements(inbuf1_, NULL);
    if (NULL == pBuff){
        LOGD("NULL == pBuff");
        return -1;
    }

    // 由于是数组映射，对本地数组的修改，会同步影响到Java堆数组
    int inbuf1_len = env->GetArrayLength(inbuf1_);
    for (int i = 0; i < inbuf1_len; ++i) {
        pBuff[i] = inbuf1_len-i;
    }

//    jbyte *inbuf1 = env->GetByteArrayElements(inbuf1_, NULL);
    memcpy(outbuf1, pBuff, inbuf1_len);

    // 其实释放指针，否则内存泄漏
    env->ReleaseByteArrayElements(inbuf1_, (jbyte*)pBuff, 0);
    env->ReleaseByteArrayElements(outbuf1_, outbuf1, 0);

    /////////////////////////

    int inbuf2_len = env->GetArrayLength(inbuf2_);
    unsigned char native_array[inbuf2_len];

    // 2. 值拷贝。在Java端数组拷贝到本地数组中
    env->GetByteArrayRegion(inbuf2_, 0, inbuf2_len, (jbyte*)native_array);

    for (int i = 0; i < inbuf2_len; ++i) {
        // 可以通过array[]来访问这段数组，但修改的仅是本地的值，Java端数组不会改变
        native_array[i] = i;
    }

    // 可以考虑直接拷贝到输出的数组中去，作为返回值
    jbyte *outbuf2 = env->GetByteArrayElements(outbuf2_, NULL);
    env->GetByteArrayRegion(inbuf2_, 0, inbuf2_len, outbuf2);
    env->ReleaseByteArrayElements(outbuf2_, outbuf2, 0);

    return 0;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_exa_JavaBean_nativeGetByteArray(JNIEnv *env, jobject instance) {

    unsigned char buff[10];

    for (int i = 0; i < 10; ++i) {
        buff[i] = i;
    }

    // 1. 通过 NewByteArray 在堆上分配数组对象
    jbyteArray array = env->NewByteArray(10);

    // 2. 通过SetByteArrayRegion 把本地的数组数据拷贝到堆上分配的数组中去
    env->SetByteArrayRegion(array, 0, 10, (jbyte*)buff);
    // 所谓的："Region calls"

    // 3. 通过返回值将分配的数组对象返回到Java层即可
    return array;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_exa_JavaBean_shareByteArrayToNative(JNIEnv *env, jobject instance, jobject byteBuffer) {

    int len = env->GetDirectBufferCapacity(byteBuffer);

    /*
     * 获取Java字节缓冲区:
     *
     * Java也可以创建字节缓冲区，
     * 在原生方法中调用 GetDirectBufferAddress
     * 函数获取原生字节数组的内存地址
     * */
    unsigned char* pBuff = (unsigned char*)env->GetDirectBufferAddress(byteBuffer);
    LOGD("len:%d", len);

    for (int i = 0; i < len; ++i) {
        pBuff[i] = len-i;
        LOGD("pBuff, i:%d, value:%d", i, pBuff[i]);
    }
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_exa_JavaBean_getDirectBufferFromNative(JNIEnv *env, jobject instance) {

    unsigned char* buff = (unsigned char*)malloc(10);
    for (int i = 0; i < 10; ++i) {
        buff[i] = i+i;
    }
    // 创建字节缓冲区
    jobject directBuff = env->NewDirectByteBuffer(buff, 10);

    // 记得free！需要手动管理内存，不在JVM的管理范围！
    return directBuff;
}
