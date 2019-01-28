package koal.glide_demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * px、dp(dip:device independent pixels)、sp用法相关：
 *
 * 1. 在长度宽度的数值使用"dp"作为单位
 *    dp会根据不同的设备进行转化，适配不同机型；px在不同的设备中会显示不同的效果
 *
 * 2. 在字体大小的数值要使用"sp"
 *    sp会随着系统的字体大小改变；而dp不会
 *
 * 3. 提供备用位图（各像素下的）
 *    如果您为 xhdpi 设备生成了一幅 200x200 的图像，则应分别按 150x150、100x100
 *    和 75x75 图像密度为 hdpi 设备、mdpi 设备和 ldpi 设备生成同一资源。
 *
 * 4. 画分割线时，一般使用px，比如1px。
 *    因为1px较细小，而1dp较粗。
 *
 * 5. 间距（占位符）优先使用<Space/>标签，然后是padding，最后是margin
 *    Space很轻的原因：draw方法是空的
 *
 * 相关：
 *    1. 屏幕密度比例：
 *       xxxhdpi：4.0        640dpi
 *       xxhdpi：3.0         480dpi
 *       xhdpi：2.0          320dpi
 *       hdpi：1.5           240dpi
 *       mdpi：1.0（基准）     160dpi(基准)
 *       ldpi：0.75          120dpi
 *
 *  考虑：
 *  1. 为什么相同px大小的控件，在低dpi设备上看起来小，在高dpi设备上看起来大？
 *
 *  2. 为什么使用160dpi作为标准？
 *     只要"dp是4的公倍数"，方便在某一分辨率下制作的控件缩放到其他控件输出
 *     若选择120作为基准，就要求dp是3的公倍数
 *     若选择240作为基准，就要求dp是6的公倍数
 *     若选择320作为基准，就要求dp是8的公倍数
 *
 *  参考：
 *  px、dp和sp，这些单位有什么区别？
 *  http://www.cnblogs.com/wangjiafang/p/4433912.html
 *
 *
 */
public class DpPxSpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dp_px_sp);

        /*
        * 动态计算px值，达到与150dp相同效果的控件大小：
        * 比如，
        *   density 为 2.625时，px = 150 * 2.625 = 394px
        *   density 为 1.5时，px = 150 * 1.5 = 225px
        *
        * */
        Button dp2pxBtn = findViewById(R.id.btn_dp2px);
        ViewGroup.LayoutParams layoutParams = dp2pxBtn.getLayoutParams();
        float targetDp = 150;
        int width2SetInPx = dp2px(this, targetDp);
        System.out.println("width2SetInPx = " + width2SetInPx);
        layoutParams.width = width2SetInPx;
        dp2pxBtn.setLayoutParams(layoutParams);
        dp2pxBtn.setText(String.format("150dp to: %d px", width2SetInPx));

        showMetrics();

    }

    private void showMetrics() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        /*
        01-28 12:35:13.571  3976  3976 I System.out: density = 2.625
        01-28 12:35:13.571  3976  3976 I System.out: screenWidth = 1080
        01-28 12:35:13.572  3976  3976 I System.out: screenHeight = 2094
        01-28 12:35:13.572  3976  3976 I System.out: scaledDensity = 2.8875
        01-28 12:35:13.572  3976  3976 I System.out: xdpi = 409.432
        01-28 12:35:13.572  3976  3976 I System.out: ydpi = 408.608
        01-28 12:35:13.572  3976  3976 I System.out: desityDpi = 420.0

                                       I/System.out( 2214): density = 1.5
                                       I/System.out( 2214): screenWidth = 480
                                       I/System.out( 2214): screenHeight = 800
                                       I/System.out( 2214): scaledDensity = 1.5
                                       I/System.out( 2214): xdpi = 240.0
                                       I/System.out( 2214): ydpi = 240.0
                                       I/System.out( 2214): desityDpi = 240.0
        *
        * */

        // 屏幕的逻辑密度，是密度无关像素（dip）的缩放因子，160dpi是系统屏幕显示的基线，1dip = 1px， 所以，在160dpi的屏幕上，density = 1， 而在一个120dpi屏幕上 density = 0.75。
        float density = metrics.density;
        System.out.println("density = " + density);

        //  屏幕的绝对宽度（像素）
        int screenWidth = metrics.widthPixels;
        System.out.println("screenWidth = " + screenWidth);

        // 屏幕的绝对高度（像素）
        int screenHeight = metrics.heightPixels;
        System.out.println("screenHeight = " + screenHeight);

        //  屏幕上字体显示的缩放因子，一般与density值相同，除非在程序运行中，用户根据喜好调整了显示字体的大小时，会有微小的增加。
        float scaledDensity = metrics.scaledDensity;
        System.out.println("scaledDensity = " + scaledDensity);

        // X轴方向上屏幕每英寸的物理像素数。
        float xdpi = metrics.xdpi;
        System.out.println("xdpi = " + xdpi);

        // Y轴方向上屏幕每英寸的物理像素数。
        float ydpi = metrics.ydpi;
        System.out.println("ydpi = " + ydpi);

        // 每英寸的像素点数，屏幕密度的另一种表示。densityDpi = density * 160.
        float desityDpi = metrics.densityDpi;
        System.out.println("desityDpi = " + desityDpi);

        System.out.println("metrics = " + metrics);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
