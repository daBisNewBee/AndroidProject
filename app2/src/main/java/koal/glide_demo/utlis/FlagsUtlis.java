package koal.glide_demo.utlis;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 *
 * 参考：
 * Android沉浸式全屏讲解(状态栏、导航栏处理)
 * https://www.jianshu.com/p/ce65dc7b0b56
 *
 * Created by wenbin.liu on 2019-05-27
 *
 * @author wenbin.liu
 */
public class FlagsUtlis {

    private static int flag_array[] = {
            /*
            * 隐藏"状态栏"
            * 清除flag时间：滑动system bar、点击home键、menu键
            * */
            View.SYSTEM_UI_FLAG_FULLSCREEN,
            /*
            * 隐藏"导航栏"
            * 点击任意布局中的任意位置都会导致导航栏导航栏重新显示出来。
            * 布局也会随着状态栏导航栏的显隐进行布局调整进行重绘。
            * */
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
            /*
            * 为何要使用布局相关标志位？避免状态栏、导航栏显隐的时候布局重绘！
            * 在新的Android4.1以及之后新的SystemUI设置里，仅单独设置隐藏状态栏和导航栏的flag会导致布局重绘，
            * 为了在显隐状态栏和导航栏的时候保持布局的稳定的显示效果，就需要以下属性了。
            * */
            /*
            * 布局稳定
            * 状态栏显隐时，布局不会延伸到全屏（实际：会延伸）
            * 清除flag时间：滑动system bar、点击home键、menu键
            * */
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE,
            /*
            * 布局延伸到状态栏的位置(无论状态栏显隐，即布局可能会被状态栏遮挡)
            * */
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            /*
            * 让布局延伸到导航栏的位置(无论导航栏显隐，即布局可能会被导航栏遮挡)
            * */
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
            /*
            * 沉浸式相关（Android4.4新增特性下面俩个）
            * 帮助稳定隐藏状态栏、导航栏，减少其被唤起的可能性
            * 原来：点击屏幕、手动调出两栏就可以恢复显示两栏
            * 现在：仅手动调出两栏可以恢复显示两栏
            * */
            View.SYSTEM_UI_FLAG_IMMERSIVE,
            /*
            * 手动调出状态栏导航栏，显示一会儿随后就会隐藏掉，当状态栏隐藏后
            * */
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY,
    };

    public static void setFlags(Activity activity){
        Log.v("todo", "MainFragment.onTestClick 处理的flag_array长度：" + flag_array.length);
        View decorView5 = activity.getWindow().getDecorView();
//        View decorView5 = .getDecorView();
        int uiOptions5 = 0;
        for (int i = 0; i < flag_array.length; i++) {
            uiOptions5 |= flag_array[i];
        }
        decorView5.setSystemUiVisibility(uiOptions5);
    }

    public static void unsetFlags(Activity activity){
        View decorView5 =  activity.getWindow().getDecorView();
        int uiOptions5 = 0;
        for (int i = 0; i < flag_array.length; i++) {
            uiOptions5 &= ~flag_array[i];
        }
        decorView5.setSystemUiVisibility(uiOptions5);
    }

    private static boolean hasBooleanFlag(int target, int flag) {
        return (target & flag) == flag;
    }

    public static void showFlags(Activity activity){
        View decorView5 = activity.getWindow().getDecorView();
        int target = decorView5.getSystemUiVisibility();
        Class clz = null;
        int num = 0;
        try {
            clz = Class.forName("android.view.View");
            Field[] fields = clz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field one = fields[i];
                int mod = one.getModifiers();
                Class clzz = one.getDeclaringClass();
                if (Modifier.isFinal(mod)
                        && Modifier.isStatic(mod)
                        && Modifier.isPublic(mod)
                        && one.getType().getName().equals("int")) {
                    int value = one.getInt(null);
                    if (hasBooleanFlag(target, value)){
                        num++;
                        Log.v("todo", "找到 value = " + value + " 名称:" + one.getName());
                    }
                }
//                System.out.println("one = " + one);
            }
            Log.v("todo", "总计 num = " + num);
        } catch (Exception e) {
            Log.v("todo", "showFlags e = " + e);
            e.printStackTrace();
        }
    }

    public static void setScreenOrientation(Activity activity, boolean isFullScreen) {
        if (activity != null) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            if (isFullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }
}
