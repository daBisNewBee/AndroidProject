package com.example.user.ndkdebug;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 *
 * 双亲代理模型作用：
 * 1. 共享：
 *  一些Framework层级的类一旦被顶层的ClassLoader加载过就缓存在内存里面，以后任何地方用到都不需要重新加载
 *  （原因：如果一个类被位于树根的ClassLoader加载过，那么在以后整个系统的生命周期内，这个类永远不会被重新加载）
 *
 * 2. 隔离
 *  不同继承路线上的ClassLoader加载的类肯定不是同一个类，这样的限制避免了用户自己的代码冒充核心类库的类访
 *  问核心类库包可见成员的情况。
 *
 *
 * DexClassLoader 和 PathClassLoader：
 *
   1. DexClassLoader可以加载jar/apk/dex，可以从SD卡中加载"未安装"的apk；

   2. PathClassLoader只能加载系统中"已经安装过"的apk；
  本质："DexClassLoader"携带参数"optimizedDirectory"，可以通过指定缓存内部存储路径，因此可以加载外部dex。
        而"PathClassLoader"不带该参数。
 *
 * Created by user on 2018/8/17.
 */

public class dexClzLoaderTest {

    private final String SHOWSTRING_FULL_NAME = "com.exa.dexClassLoader.ShowString";
    private Context context = null;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
    }

    /**
     *
     * "一个运行的Android应用至少有2个ClassLoader"
     *
     I/System.out( 5088): classLoader = dalvik.system.PathClassLoader

     [DexPathList[[zip file "/system/framework/android.test.runner.jar"
     , zip file "/data/app/com.exa.test-1/base.apk", zip file "/data/app/com.exa-1/base.apk"]
     ,nativeLibraryDirectories=[/data/app/com.exa-1/lib/x86_64, /vendor/lib64, /system/lib64]]]

     I/System.out( 5088): parent: ---->
     I/System.out( 5088): classLoader = java.lang.BootClassLoader@11e33155
     *
     * @throws Exception
     */
    @Test
    public void ClzLoader_Test() throws Exception {

        ClassLoader classLoader = context.getClassLoader();

        System.out.println("classLoader = " + classLoader);

        while (classLoader.getParent() != null){
            System.out.println("parent: ----> ");
            classLoader = classLoader.getParent();
            System.out.println("classLoader = " + classLoader);
        }
    }

    @Test
    public void Dex_Test() throws Exception {

        // dex压缩文件的路径（可以是apk,jar,zip格式）
        String dexPath = "/sdcard/ShowString.dex";

        // dex解压释放后的目录
        String dexOutputDirs = Environment.getExternalStorageDirectory().toString();

        //指定dexoutputpath为APP自己的缓存目录
        File dexOutputDir = context.getDir("dex",0);

        // 定义DexClassLoader
        // 第一个参数：是dex压缩文件的路径
        // 第二个参数：是dex解压缩后存放的目录
        // 第三个参数：是C/C++依赖的本地库文件目录,可以为null
        // 第四个参数：是上一级的类加载器
//        PathClassLoader dexClassLoader = new PathClassLoader(dexPath,null,context.getClassLoader());
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath
//                , dexOutputDirs
                , dexOutputDir.getAbsolutePath()
                ,null
                , context.getClassLoader());
        /*
        * 使用 dexOutputDir报错：
        *
        * Optimized data directory /storage/emulated/0 is not owned by the current user.
        * Shared storage cannot protect your application from code injection attacks
        *
        * */

        Class<?> showStringClz = dexClassLoader.loadClass(SHOWSTRING_FULL_NAME);
        Assert.assertNotNull(showStringClz);
        Object object = showStringClz.newInstance();
        Method method = showStringClz.getDeclaredMethod("sayHello",
                null);
        Assert.assertNotNull(method);
        String ret = (String)method.invoke(object);
        System.out.println("ret = " + ret);

    }
}
