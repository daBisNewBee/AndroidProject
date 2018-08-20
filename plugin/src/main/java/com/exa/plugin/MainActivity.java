package com.exa.plugin;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.qihoo360.replugin.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnStartAppMain = findViewById(R.id.btn_start_app_main_ac);
        btnStartAppMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RePlugin.isPluginInstalled("com.exa")){
                    RePlugin.startActivity(MainActivity.this, RePlugin.createIntent("com.exa","com.exa.MainActivity"));
                }else {
                    Toast.makeText(MainActivity.this, "长按安装插件APK:app-deug.apk", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnStartAppMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                simulateInstallExternalPlugin();
                return true;
            }
        });

        TextView tvHelloWorld = findViewById(R.id.tv_helloWorld);

        // 测试直接调用插件中的JNI方法
        ClassLoader classLoader = RePlugin.fetchClassLoader("com.exa");
        System.out.println("classLoader = " + classLoader);
        /*
I/System.out( 1557): classLoader = com.qihoo360.replugin.PluginDexClassLoader
[DexPathList[[zip file "/data/user/0/com.exa.plugin/app_p_a/2104690500.jar"],
nativeLibraryDirectories=[/data/data/com.exa.plugin/app_p_n/2104690500, /vendor/lib64, /system/lib64]]]
I/System.out( 1557): valueFromJni = Hello from C++
        *
        * */
        try {
            Class<?> mainClz = classLoader.loadClass("com.exa.MainActivity");
            Method stringFromJNIMethod = mainClz.getMethod("stringFromJNI");
            String valueFromJni = (String)stringFromJNIMethod.invoke(null);
            System.out.println("valueFromJni = " + valueFromJni);
            tvHelloWorld.setText(valueFromJni);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟安装或升级（覆盖安装）外置插件
     * 注意：为方便演示，外置插件临时放置到Host的assets/external目录下，具体说明见README</p>
     */
    private void simulateInstallExternalPlugin() {
        String demo3Apk= "app-debug.apk";
        String demo3apkPath = "external" + File.separator + demo3Apk;

        // 文件是否已经存在？直接删除重来
        String pluginFilePath = getFilesDir().getAbsolutePath() + File.separator + demo3Apk;
        File pluginFile = new File(pluginFilePath);
        if (pluginFile.exists()) {
            FileUtils.deleteQuietly(pluginFile);
        }

        // 开始复制
        copyAssetsFileToAppFiles(demo3apkPath, demo3Apk);
        PluginInfo info = null;
        if (pluginFile.exists()) {
            info = RePlugin.install(pluginFilePath);
        }

        if (info != null) {
            Toast.makeText(MainActivity.this, "插件apk安装成功！"+info, Toast.LENGTH_SHORT).show();
//            RePlugin.startActivity(MainActivity.this, RePlugin.createIntent(info.getName(), "com.qihoo360.replugin.sample.demo3.MainActivity"));
        } else {
            Toast.makeText(MainActivity.this, "install external plugin failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从assets目录中复制某文件内容
     *  @param  assetFileName assets目录下的Apk源文件路径
     *  @param  newFileName 复制到/data/data/package_name/files/目录下文件名
     */
    private void copyAssetsFileToAppFiles(String assetFileName, String newFileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        int buffsize = 1024;

        try {
            is = this.getAssets().open(assetFileName);
            fos = this.openFileOutput(newFileName, Context.MODE_PRIVATE);
            int byteCount = 0;
            byte[] buffer = new byte[buffsize];
            while((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
