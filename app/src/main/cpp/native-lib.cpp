#include <jni.h>
#include <string>
#include <unistd.h>
#include <Android/Log.h>

#define TAG "jni"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_exa_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {

//    for(int i = 0;i<100000;i++){
//        LOGD("i:%d\n",i);
//        sleep(2);
//    }

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
