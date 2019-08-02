package koal.glide_demo;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.Map;

/**
 * Created by wenbin.liu on 2019-08-02
 *
 * @author wenbin.liu
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("todo", "MyApplication 安装 MyCrashHandler....");
        MyCrashHandler.getInstance();

        String appid = "60a90b2da0";
        CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(this);
        userStrategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback(){
            /*
                public static final int CRASHTYPE_JAVA_CRASH = 0;
                public static final int CRASHTYPE_JAVA_CATCH = 1;
                public static final int CRASHTYPE_NATIVE = 2;
                public static final int CRASHTYPE_U3D = 3;
                public static final int CRASHTYPE_ANR = 4;
                public static final int CRASHTYPE_COCOS2DX_JS = 5;
                public static final int CRASHTYPE_COCOS2DX_LUA = 6;
                public static final int CRASHTYPE_BLOCK = 7;
            *
            * */
            @Override
            public synchronized Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                                       String errorMessage, String errorStack) {
                /*
                * 在 "errorStack"中匹配前缀来过滤业务，
                * 比如过滤录音业务发生的异常：
                *
                * Java Crash：
                *   if (errorStack.contains("com.ximalaya.ting.android.record"))
                * Native Crash:
                *   so所在jni类前缀
                * */
                Log.d("todo", "onCrashHandleStart() called with: crashType = [" + crashType + "], errorType = [" + errorType + "], errorMessage = [" + errorMessage + "], errorStack = [" + errorStack + "]");
                return super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack);
            }
        });

        CrashReport.initCrashReport(this, appid, true, userStrategy);
        CrashReport.setHandleNativeCrashInJava(true);

        // 在业务中测试异常：
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("todo", "准备投递异常！！！！");
//                throw new RuntimeException("这里发生运行时异常！！！！！！");
//                CrashReport.testJavaCrash(); // CRASHTYPE_JAVA_CRASH = 0;
//                CrashReport.testNativeCrash(); // CRASHTYPE_NATIVE = 2;
//                CrashReport.testANRCrash(); // TODO: 验证未成功
            }
        }, 3000);


    }
}
