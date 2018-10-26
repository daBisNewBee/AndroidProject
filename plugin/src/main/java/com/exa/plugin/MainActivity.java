package com.exa.plugin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.qihoo360.replugin.IHostBinderFetcher;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.RePluginApplication;
import com.qihoo360.replugin.RePluginConfig;
import com.qihoo360.replugin.component.ComponentList;
import com.qihoo360.replugin.component.provider.PluginProviderClient;
import com.qihoo360.replugin.component.service.PluginServiceClient;
import com.qihoo360.replugin.model.PluginInfo;
import com.qihoo360.replugin.packages.PluginRunningList;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvInfo;

    private static final String PLUGIN_NAME = "com.exa";

    private static final String PLUGIN_APK_NAME = "com.example.plugin_syssetting";

    private static final String PLUGIN_APK_MAINACTIVITY_NAME = "com.example.plugin_syssetting.SysMainActivity";

    private static final String PLUGIN_MAINACTIVITY_NAME = "com.exa.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = findViewById(R.id.tv_info);
        findViewById(R.id.btn_apk).setOnClickListener(this);
        findViewById(R.id.btn_jar).setOnClickListener(this);
        findViewById(R.id.btn_jump_to_plugin).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_plugin_jni).setOnClickListener(this);
    }

    private void callPluginJni() throws Exception{
        // 测试直接调用插件中的JNI方法
        ClassLoader classLoader = RePlugin.fetchClassLoader(PLUGIN_NAME);
        System.out.println("classLoader = " + classLoader);
        /*
I/System.out( 1557): classLoader = com.qihoo360.replugin.PluginDexClassLoader
[DexPathList[[zip file "/data/user/0/com.exa.plugin/app_p_a/2104690500.jar"],
nativeLibraryDirectories=[/data/data/com.exa.plugin/app_p_n/2104690500, /vendor/lib64, /system/lib64]]]
I/System.out( 1557): valueFromJni = Hello from C++
        *
        * */
            Class<?> mainClz = classLoader.loadClass(PLUGIN_MAINACTIVITY_NAME);
            Method stringFromJNIMethod = mainClz.getMethod("stringFromJNI");
            String valueFromJni = (String)stringFromJNIMethod.invoke(null);
            System.out.println("valueFromJni = " + valueFromJni);
            appendInfo(valueFromJni);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_apk:
                /*
                * build生成的位于assets下 “plugins-builtin.json”：
                *   [
                        {
                            "high": null,
                            "frm": null,
                            "ver": 1,
                            "low": null,
                            "pkg": "com.exa",
                            "path": "plugins/app-debug.jar",
                            "name": "app-debug"
                        }
                    ]
                * */
                boolean isPluginInstalled = RePlugin.isPluginInstalled(PLUGIN_APK_NAME);
                if (!isPluginInstalled){
                    PluginInfo info = RePluginUtli.simulateInstallExternalPlugin(MainActivity.this, "plugin_syssetting-debug.apk");
                    if (info != null) {
                        appendInfo("插件apk安装成功:\n"+info.toString());
                    } else {
                        appendInfo("插件apk安装失败！");
                    }
                }else {
                    appendInfo("已经安装:"+PLUGIN_NAME);
                }
                break;
            case R.id.btn_jar:
                boolean isSuc = RePlugin.preload(PLUGIN_NAME);
                appendInfo("预加载结果：" + isSuc);
                break;
            case R.id.btn_jump_to_plugin:
                RePlugin.startActivity(MainActivity.this, RePlugin.createIntent(PLUGIN_NAME, PLUGIN_MAINACTIVITY_NAME));
//                RePlugin.startActivity(MainActivity.this, RePlugin.createIntent(PLUGIN_APK_NAME, PLUGIN_APK_MAINACTIVITY_NAME));
                break;
            case R.id.btn_plugin_jni:
                try {
                    callPluginJni();
                } catch (Exception e) {
                    appendInfo("异常："+e.getMessage());
                    e.printStackTrace();
                }
                break;
            case R.id.btn_clear:
                break;
            case R.id.btn_other:
                List<PluginInfo> infoList = RePlugin.getPluginInfoList();
                System.out.println("infoList.size:" + infoList.size());
                for (PluginInfo pluginInfo : infoList) {
                    System.out.println("pluginInfo = " + pluginInfo);
                }
                RePluginConfig rePluginConfig = RePlugin.getConfig();
                System.out.println("\nrePluginConfig = " + rePluginConfig);

                PluginRunningList pluginRunningList = RePlugin.getRunningPlugins();
                System.out.println("\npluginRunningList = " + pluginRunningList);

                ComponentList componentList = RePlugin.fetchComponentList(PLUGIN_NAME);
                System.out.println("\ncomponentList = " + componentList);

                // RePlugin.App
                // RePluginApplication
                // PluginServiceClient : 对【插件】的service操作
                // PluginProviderClient: 对【插件】的Provider操作
                // IHostBinderFetcher : 用来实现主程序提供IBinder给其他插件
                // IPC
                // ThreadUtils
                break;
            default:
                break;
        }
    }

    void appendInfo(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvInfo.append(msg+"\n");
            }
        });
    }
}
