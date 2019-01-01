//
// Created by Salmon on 2018/12/29.
//
#include <jni.h>
#include <unistd.h>

#include <Android/Log.h>
#include <cstdlib>

#define TAG "System.out"

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

void PrintBuffer(void* pBuff, unsigned int nLen)
{
    if (NULL == pBuff || 0 == nLen)
    {
        return;
    }

    const int nBytePerLine = 16;
    unsigned char* p = (unsigned char*)pBuff;
    char szHex[3*nBytePerLine+1] = {0};

    LOGD("-----------------begin-------------------\n");
    for (unsigned int i=0; i<nLen; ++i)
    {
        int idx = 3 * (i % nBytePerLine);
        if (0 == idx)
        {
            memset(szHex, 0, sizeof(szHex));
        }
#ifdef WIN32
        sprintf_s(&szHex[idx], 4, "%02x ", p[i]);// buff长度要多传入1个字节
#else
        snprintf(&szHex[idx], 4, "%02x ", p[i]); // buff长度要多传入1个字节
#endif

        // 以16个字节为一行，进行打印
        if (0 == ((i+1) % nBytePerLine))
        {
            LOGD("%s\n", szHex);
        }
    }

    // 打印最后一行未满16个字节的内容
    if (0 != (nLen % nBytePerLine))
    {
        LOGD("%s\n", szHex);
    }

    LOGD("------------------end-------------------\n");
}

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

/*
 *  JNI字符串处理的几个记忆规律：
 *
 *  根据Java源字符串传递到JNI时，1. 是否需要拷贝，2. 拷贝的内存位置不同，
 *
 *  1. 拷贝，在JVM内存中。涉及到的几个函数:
 *     GetStringUTFChars： 返回的是UTF-8编码的char*
 *     GetStringChars   ： 返回的是UTF-16编码的jchar*
 *     GetStringCritical： 返回类型同上，只是增加了返回源字符串指针的可能性，但不确保！
 *                         用于源字符串较大(>1M)的场景
 *  2. 不拷贝，在Native中预先分配的缓冲区。
 *     GetStringUTFRegion：返回的是UTF-8编码的char*
 *
 *  其他：
 *  1. JVM内部使用Unicode字符集（UTF-16编码）
 *     因此，
 *     a. GetStringChars 接收字符串不需要copy，而GetStringUTFChars要（TODO：实际验证时isCopy都为1）
 *     b. 只有GetStringCritical方法，没有GetStringUTFCritical方法（转码UTF8必然要copy）
 *  2. JNI使用"modified UTF-8"
 *
 *  参考：
 *  1.JVM、JNI、CPP的编码类型，及编码流向
 *  https://blog.csdn.net/a785984/article/details/39254373
 *  2. Android Native 开发之 NewString 与 NewStringUtf 解析
 *  https://www.jianshu.com/p/ceb73cd39c10
 *  3. C++访问Java的String字符串对象
 *  https://blog.csdn.net/ku726999/article/details/39204399
 *
 *  结论：
 *  1. 推荐使用GetStringRegion 和 GetStringUTFRegion，对于小字符串来说。
 *  原因：
 *      a. 缓冲区可以被编译器提前分配(TODO: why？)，而且永远不会产生内存溢出的异常
 *      b. 处理原字符串的一部分，提供了index、len等param
 *      c. 复制少量字符串的消耗是非常小的
 *  2. 在JNI，优先使用 UTF-16相关的API。
 *      与JVM内部编码方式一致，减少转码成UTF8的损耗
 *
 * */

extern "C"
JNIEXPORT jstring JNICALL
Java_com_exa_JavaBean_sendUTF8String(JNIEnv *env, jobject instance, jstring msg_) {
    jboolean isCopy = false;

    const char *msg = env->GetStringUTFChars(msg_, &isCopy);
//    const char *msg = env->GetStringUTFChars(msg_, 0);
    if (msg == NULL){
        // 检查的原因：JVM 需要为新诞生的字符串分配内存空间
        LOGD("msg == NULL!!");
        return NULL;
    }

    /*
     * 获取 UTF-8 编码字符串的长度，
     * 与相同：也可以通过标准 C 函数 strlen 获取。
     * */
    int len = env->GetStringUTFLength(msg_);
    LOGD("GetStringUTFLength:%d", len);

    int lenStr = strlen(msg);
    LOGD("strlen:%d", lenStr);

    /*
     * isCopy:(一般传"0")
     *  true:
     *      返回 JVM 内部源字符串的一份拷贝，并为新产生的字符串分配内存空间。
     *  false:
     *      表示返回原字符串的指针。意味着可以通过指针修改源字符串的内容，但不推荐
     * */

    LOGD("GetStringUTFChars. isCopy:%d", isCopy);
    char buf[128] = {0};
    sprintf(buf, "add jni prefix. %s", msg);

    // release的原因是：在Get时，分配的新内存位于JVM内，
    // 用于存储源字符串的拷贝，以便本地代码访问和修改。
    // 因此，用完了要通知释放。
    env->ReleaseStringUTFChars(msg_, msg);
    // 通过调用 NewStringUTF 函数，会构建一个新的
    // java.lang.String 字符串对象。这个新创建的
    // 字符串会自动转换成 Java 支持的 Unicode 编码
    return env->NewStringUTF(buf);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_exa_JavaBean_sendUTF16String(JNIEnv *env, jobject instance, jstring msg_) {
    /*
     * "GetStringChars"取得UTF-16编码的宽字符串jchar*
     * */
    jboolean isCopy = false;
    const jchar* msg = env->GetStringChars(msg_, &isCopy);
    LOGD("GetStringChars. isCopy:%d", isCopy);
    int len = env->GetStringLength(msg_);

    env->ReleaseStringChars(msg_, msg);

    return env->NewString(msg, len);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_exa_JavaBean_sendStringPointer(JNIEnv *env, jobject instance, jstring msg_) {
    jboolean isCopy = false;
    const jchar *msg = env->GetStringCritical(msg_, &isCopy);
    // 还是要检查！该函数只是增加返回字符串指针的可能性，但不一定！
    if (msg == NULL){
        return NULL;
    }
    LOGD("GetStringCritical. isCopy:%d", isCopy);
    /*
     * 区别于上述两个调用（GetStringUTFChars、GetStringChars）的isCopy返回1，
     * 此处返回0，表示传递的是字符串指针！
     * 用于源字符串内容较大，大约1M左右，拷贝效率较低的问题。
     * */
    env->ReleaseStringCritical(msg_, msg);
    return env->NewString(msg, env->GetStringLength(msg_));
    /*
     * 考虑：
     * 为何"JNI 中没有 Get/ReleaseStringUTFCritical 这样的函数"？
     * */
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_exa_JavaBean_sendUTF8StringRegion(JNIEnv *env, jobject instance, jstring msg_) {
    int len = env->GetStringLength(msg_);
    char buf[128] = {0};
    env->GetStringUTFRegion(msg_, 0, len, buf);
    LOGD("GetStringUTFRegion:%s", buf);
    return env->NewStringUTF(buf);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_exa_JavaBean_modifiedUTF8Test(JNIEnv *env, jobject instance, jstring msg_) {
    const char *msg = env->GetStringUTFChars(msg_, 0);

    const jchar *msgg = env->GetStringChars(msg_, 0);

    LOGD("GetStringUTFLength");
    PrintBuffer((void*)msg, env->GetStringUTFLength(msg_));
    LOGD("GetStringLength");
    PrintBuffer((void*)msgg, env->GetStringLength(msg_)<<1);

    env->ReleaseStringUTFChars(msg_, msg);

    return env->NewStringUTF(msg);
}

