package com.example.user.ndkdebug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;

import com.exa.plugin.PluginUtil;
import com.exa.plugin.lib.Callback;
import com.exa.plugin.lib.IBean;
import com.exa.plugin.lib.IDynamic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
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

    private final String SHOWSTRING_FULL_NAME = "com.exa.plugin.dexClassLoader.ShowString";

    private final String BEAN_CLASS_FULL_NAME = "com.exa.plugin.Bean";

    private final String DYNAMIC_CLASS_FULL_NAME = "com.exa.plugin.Dynamic";

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

    /**
     *
     * 加载插件中的类！
     *
     * @throws Exception
     */
    @Test
    public void Dex_Test() throws Exception {

        // dex压缩文件的路径（可以是apk,jar,zip格式）
        String dexPath = "/sdcard/plugin-debug.apk";
//        String dexPath = "/sdcard/ShowString.dex";

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

        /*
        * 通过面向"接口"（抽象）编程调用插件的代码
        *
        * 反射调用插件的代码可读性差！
        *
        * */
        Class<?> beanClass = dexClassLoader.loadClass(BEAN_CLASS_FULL_NAME);
        IBean iBean = (IBean)beanClass.newInstance();
        iBean.setName("Alice");
        System.out.println("bean getName = " + iBean.getName());


        /*
        *
        * 通过面向"切面"编程调用插件中的带回调方法
        *
        * */
        Class<?> dynamicClass = dexClassLoader.loadClass(DYNAMIC_CLASS_FULL_NAME);
        IDynamic iDynamic = (IDynamic)dynamicClass.newInstance();
        iDynamic.methodWithCallback(new Callback() {
            @Override
            public void update(IBean bean) {
                System.out.println("bean = " + bean.getName());
            }
        });

        /*
        *
        * 切换皮肤: 加载插件APK中的 drawable/splash.jpg
        *
        * */
        Class<?> RClz = dexClassLoader.loadClass("com.exa.plugin.R$drawable");
        Field splashField = RClz.getDeclaredField("splash");
        int splash_id = (int)splashField.get(RClz);

        System.out.println("splash_id = " + splash_id);

        Resources newRes = PluginUtil.createResources(dexPath, context);
        Assert.assertNotNull(newRes);
        Drawable Spashdrawable = newRes.getDrawable(splash_id);
        System.out.println("Spashdrawable = " + Spashdrawable);

        int spash_id_ex = newRes.getIdentifier("splas" +
                "h","drawable","com.exa.plugin");
        System.out.println("spash_id_ex = " + spash_id_ex);
        Assert.assertEquals(splash_id, spash_id_ex);
    }

    /**
     *
     * Android如何加载插件APK里面的资源 ？
     *
     * 宿主访问插件中的资源文件，包括assets、res下的各类资源。
     *
     * 注意：
     *  这里的"AssetManager"其实对应了"插件apk中的所有资源"，并不只是assets目录下的！
     *  是按照 "dexPath"加载的。
     *
     *  原理：
     *  1. 获取资源文件必须要获得Resource对象
     *
     *  2. 获取插件的资源文件，必须获得插件的Resource对象
     *
     *  3. 插件apk构造出插件对应的AssetManager对象，并作为参数构造插件对应的Resource对象
     *
     * @throws Exception
     */
    @Test
    public void loadPluginResources_Test() throws Exception {
        String dexPath = "/sdcard/plugin-debug.apk";

        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);
        System.out.println(dexPath+" " +
                "packageInfo = " + packageInfo.packageName);

        AssetManager assetManager = AssetManager.class.newInstance();

        System.out.println("before addAssetPath ------> ");
        String[] assetFiles = assetManager.list("");
        for (String assetFile : assetFiles) {
            System.out.println("assetFile = " + assetFile);
        }

        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);

        addAssetPath.invoke(assetManager, dexPath);

        System.out.println("\nafter addAssetPath ------> ");
        assetFiles = assetManager.list("");
        for (String assetFile : assetFiles) {
            System.out.println("assetFile = " + assetFile);
        }

        InputStream is = assetManager.open("plugin.ini");
        byte[] tmpArray = new byte[is.available()];
        is.read(tmpArray);
        System.out.println("Read plugin.ini ---->\n"+new String(tmpArray));

        Resources superRes = context.getResources();

        Resources pluginRes = new Resources(assetManager
                , superRes.getDisplayMetrics(), superRes.getConfiguration());

        /*
        *  注意与插件资源中的名称对应：
        *
        *  <string name="app_name_plugin">this is plugin string</string>
        *
        *  最后一个参数是插件的包名！
        * */
        int resId = pluginRes.getIdentifier("app_name_plugin","string","com.exa.plugin");
        String value = pluginRes.getString(resId);
        System.out.println("value = " + value);

        // 宿主apk中的资源还必须使用原来的 superRes对象！
        resId = superRes.getIdentifier("app_name","string","com.exa");
        value = superRes.getString(resId);
        System.out.println("value = " + value);
    }
}
