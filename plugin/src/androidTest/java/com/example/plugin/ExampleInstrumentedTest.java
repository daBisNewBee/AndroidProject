package com.example.plugin;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

import static org.junit.Assert.*;

/**
 * Instrumented test, which
 * will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Context context = null;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
    }

    /**
     *
     * Android插件中如何加载so文件 ？
     *
     * 主要思路：
     *  在加载插件的时候，先把插件中的so文件释放到本地目录，
     *  然后在把目录设置到DexClassLoader类加载器的nativeLib中。
     *
     * 实例：
     *  这里设置的目录是：
     *  将 "/sdcard/app-debug.apk" 释放到宿主"com.exa.plugin"的"app_plugin"目录下：
     *   shell@latte:/data/data/com.exa.plugin/app_plugin $ busybox find ./
         ./lib
         ./lib/libnative-lib.so
         ./app-debug.dex
     *
     * 注意：
     *  1. 无法释放插件so到宿主"lib"下。系统目录，普通app无写权限。
     *  shell@latte:/data/data/com.exa.plugin/lib $ ls -l
        -rwxr-xr-x system   system          0 1979-12-31 00:00 libfake.so

        2.
         java.lang.UnsatisfiedLinkError: dlopen failed:
         "/data/data/com.exa.plugin/lib/armeabi/libnative-lib.so"
         is 32-bit instead of 64-bit
         at java.lang.Runtime.loadLibrary(Runtime.java:440)
        因为64位的Zygote进程创建的虚拟机中加载了32位的so文件。
        解决办法：
            需要在宿主指定"armeabi"的lib类型。即指定宿主app为 32位APP。

        3. 这里只考虑宿主、插件仅使用armeabi类型的so。
     *
     * 参考：
     *  Android中so使用知识和问题总结以及插件开发过程中加载so的方案解析：
     *  https://www.2cto.com/kf/201608/543243.html
     *
     * @throws Exception
     */
    @Test
    public void loadPluginSo_Test() throws Exception {
        final String zipFile = "/sdcard/app-debug.apk";
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = null;
        File pluginOutputDir = context.getDir("plugin", 0);
        String pluginLib = pluginOutputDir.getAbsolutePath() + "/lib";

        byte[] buf = new byte[1024];
        int readLen = 0;

        while ((zipEntry = zipInputStream.getNextEntry())!=null){
            String fileName
                    = zipEntry.getName();
            if (!fileName.startsWith("lib/armeabi/"))
                continue;

            File dstFile = new File(pluginLib, fileName.split("/")[2]);
            File parent = dstFile.getParentFile();
            if (!parent.exists()){
                parent.mkdirs();
            }
            if (!dstFile.exists()){
                dstFile.createNewFile();
            }

            // 1. 解压插件 lib/armeabi/下的libxxx.so 到 宿主pluginLib下。
            FileOutputStream fos = new FileOutputStream(dstFile);
            while ((readLen = zipInputStream.read(buf, 0, 1024)) != -1){
                fos.write(buf, 0, readLen);
            }
            fos.flush();
            fos.close();
        }
        zipInputStream.close();

        // 2. 新建插件的DexClassLoader
        DexClassLoader dexClassLoader = new DexClassLoader(
                zipFile,
                pluginOutputDir.getAbsolutePath(),
                pluginLib,
                context.getClassLoader());
        System.out.println("dexClassLoader = " + dexClassLoader);
        /*
        *
        * dexClassLoader = dalvik.system.DexClassLoader
        * [DexPathList[[zip file "/sdcard/app-debug.apk"],
        * nativeLibraryDirectories=
        * [/data/data/com.exa.plugin/app_plugin/lib, /vendor/lib, /system/lib]]]
        * */

        // 3. 加载插件中的class
        Class<?> mainClz = dexClassLoader.loadClass("com.exa.MainActivity");
        Method method = mainClz.getDeclaredMethod("stringFromJNI");
        // 4. 调用class中的jni方法
        String valueFromPluginSo = (String) method.invoke(null);
        System.out.println("valueFromPluginSo = " + valueFromPluginSo);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();


        int resId = appContext.getResources().getIdentifier("app_name_plugin","string","com.exa.plugin");
        String value = appContext.getResources().getString(resId);

        assertEquals("com.example.plugin", appContext.getPackageName());
    }
}
