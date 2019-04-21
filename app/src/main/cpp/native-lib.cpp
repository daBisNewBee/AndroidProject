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
        jclass /* this */) {

//    for(int i = 0;i<100000;i++){
//        LOGD("i:%d\n",i);
//        sleep(2);
//    }

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

#include <fcntl.h>    /* For O_RDWR */
#include <unistd.h>   /* For open(), creat() */
#include <linux/ashmem.h>
#include <sys/mman.h>
#include <stdio.h>
#include    <stdlib.h>
#include    <pthread.h>
#include <sys/stat.h>
#include <sys/types.h>

extern "C"
JNIEXPORT void

JNICALL
Java_com_exa_ashmem_AshmemManager_doOperaNow(
        JNIEnv *env,
        jclass /* this */,
        jint fd) {
    pthread_mutex_t *p_mutex=(pthread_mutex_t*)mmap(NULL, sizeof(pthread_mutex_t), PROT_READ|PROT_WRITE, MAP_SHARED,fd,0);
    if( MAP_FAILED==p_mutex )
    {
        LOGD("mmap:[%d](%s)",errno,strerror(errno));
        return;
    }

    int ret=pthread_mutex_lock(p_mutex);
    if( ret!=0 )
    {
        LOGD("doOperaNow pthread_mutex_lock");
        return;
    }
    //测试是否能够阻止其他进程的进入
    sleep(2);
    LOGD("this is process a.doing AAAAAAAA");
    sleep(1);
    LOGD("this is process a.doing BBBBBBBB");
    sleep(1);
    LOGD("this is process a.doing CCCCCCCC");
    sleep(1);
    LOGD("this is process a.doing DDDDDDDD");

    ret=pthread_mutex_unlock(p_mutex);
    if( ret!=0 )
    {
        LOGD("child pthread_mutex_unlock");
    }

    munmap(p_mutex, sizeof(pthread_mutex_t));

}

extern "C"
JNIEXPORT void

JNICALL
Java_com_exa_ashmem_AshmemManager_doOperaLater(
        JNIEnv *env,
        jclass /* this */,
        jint fdReceived) {
    pthread_mutex_t *p_mutex=(pthread_mutex_t*)mmap(NULL, sizeof(pthread_mutex_t), PROT_READ|PROT_WRITE, MAP_SHARED,fdReceived,0);
    if( MAP_FAILED==p_mutex )
    {
        LOGD("mmap:[%d](%s)",errno,strerror(errno));
        return;
    }
//    sleep(2);// 保证主进程先行
    int ret=pthread_mutex_lock(p_mutex);
    if( ret!=0 )
    {
        LOGD("child pthread_mutex_lock");
    }
    LOGD("this is process b.doing AAAAAAAA");
    sleep(1);
    LOGD("this is process b.doing BBBBBBBB");
    sleep(1);
    LOGD("this is process b.doing CCCCCCCC");
    sleep(1);
    LOGD("this is process b.doing DDDDDDDD");

    ret=pthread_mutex_unlock(p_mutex);
    if( ret!=0 )
    {
        LOGD("child pthread_mutex_unlock");
    }

    munmap(p_mutex, sizeof(pthread_mutex_t));
}

extern "C"
JNIEXPORT jint

JNICALL
Java_com_exa_ashmem_AshmemManager_initAndGetFd2Ashmem(
        JNIEnv *env,
        jclass /* this */) {
    jint fd = open("/dev/ashmem",O_RDWR);
    ioctl(fd,ASHMEM_SET_NAME,"MyAshmemName");
    ioctl(fd,ASHMEM_SET_SIZE,4096);

    pthread_mutex_t *p_mutex=(pthread_mutex_t*)mmap(NULL, sizeof(pthread_mutex_t), PROT_READ|PROT_WRITE, MAP_SHARED,fd,0);
    if( MAP_FAILED==p_mutex )
    {
        LOGD("mmap:[%d](%s)",errno,strerror(errno));
        return -1;
    }

    pthread_mutexattr_t attr;
    pthread_mutexattr_init(&attr);
    int ret=pthread_mutexattr_setpshared(&attr,PTHREAD_PROCESS_SHARED);
    if( ret!=0 )
    {
        LOGD("init_mutex pthread_mutexattr_setpshared");
        return -1;
    }
    pthread_mutex_init(p_mutex, &attr);

    munmap(p_mutex, sizeof(pthread_mutex_t));

    LOGD("initAndGetFd2Ashmem open fd:%d",fd);
    return fd;
}
