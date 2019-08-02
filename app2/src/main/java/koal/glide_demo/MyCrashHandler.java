package koal.glide_demo;

import android.util.Log;


/**
 *
 * 这里捕获Java下的异常！
 *
 * Created by wenbin.liu on 2019-08-02
 *
 * @author wenbin.liu
 */
public class MyCrashHandler implements Thread.UncaughtExceptionHandler {

    private static MyCrashHandler INSTANCE;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static MyCrashHandler getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new MyCrashHandler();
        }
        return INSTANCE;
    }

    private MyCrashHandler(){
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("todo", "uncaughtException() called with: t = [" + t + "], e = [" + e + "]");
        if (!handleException(e) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(t, e);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Log.e("todo", "error : ", ex);
            }
            Log.d("todo", "uncaughtException: 这里准备退出程序....");
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        return true;
    }
}
