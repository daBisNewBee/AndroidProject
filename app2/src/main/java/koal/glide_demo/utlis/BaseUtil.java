package koal.glide_demo.utlis;

import android.content.Context;

/**
 * Created by wenbin.liu on 2020-01-16
 *
 * @author wenbin.liu
 */
public class BaseUtil {

    public static int dp2px(Context context, float dipValue) {
        if (context == null)
            return (int) (dipValue * 1.5);
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float dp2pxReturnFloat(Context context, float dipValue) {
        if (context == null)
            return dipValue * 1.5f;
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    public static int sp2px(Context context, float dipValue) {
        if (context == null)
            return (int) (dipValue * 1.5);
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        if (context == null)
            return (int) (pxValue * 1.5);
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

}
