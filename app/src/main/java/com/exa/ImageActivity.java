package com.exa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 *
 * bitmap 占用内存：
 *
 * bitmap h : 37 444 860
 *      推导：
 *         (440 / 240 * 2048 + 0.5) * (440 / 240 * 1360 + 0.5) * 4 = 37 444 860
 * bitmap x : 21 063 680
 *      推导：
 *         (440 / 320 * 2048) * (440 / 320 * 1360) * 4 = 21 063 680
 *
 * "* 4"表示：每个像素4个字节
 *      ARGB_8888格式，
 *      整个Bitmap占用的就是宽度 * 高度 * 4 byte
 *
 * 图片最终的宽度和高度就是：
 * scaledWidth = int(scaledWidth * scale + 0.5f);
 * scaledHeight = int(scaledHeight * scale + 0.5f);
 *
 * 而scale为：
 * fixme：关键！！ scale = (float) targetDensity / density（屏幕像素密度/文件夹密度）
 * 即： 界面上显示的图片大小 = 设备dpi / 图片所在资源文件夹的最大dpi * 图片原始尺寸
 * 440 / (320 xdpi)
 * 或者 2.75 / 2
 *
 * inDensity 原始资源文件夹密度，与图片所在文件夹相关，
 *      ldpi    ~120
 *      mdpi    ~160      320 * 480     (基准！！1dp = 1px)
 *      hdpi    ~240      480 * 800     1dp = 1.5px
 *      xdpi    ~320      720 * 1080    1dp = 2px
 *      xxdpi   ~480      1080 * 1920   1dp = 3px
 *
 * inTargetDensity 就是设备的屏幕密度，即：densityDpi
 *
 * 结论：
 * 1. 注意图片尽量放到精度偏大的资源文件夹
 *    同样的图片放到越小dpi的文件夹，放大倍数越大，消耗内存越大！
 *
 * 2. 如果手机densityDpi为 340，则匹配的是xxdpi下的图片，
 *    因为如果匹配的是xdpi，那么图片在设备上显示时，需要放大，可能引起模糊
 *
 * 参考：
 * Android中Bitmap占用内存计算：
 * https://www.jianshu.com/p/578357ab6838
 *
 */
public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "bitmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // density=2.75, width=1080, height=2276, scaledDensity=2.75, xdpi=394.705, ydpi=394.307
        int densityDpi = this.getResources().getDisplayMetrics().densityDpi;
        Log.d(TAG, "densityDpi: " + densityDpi); // 440
        Log.d(TAG, "DisplayMetrics: " + this.getResources().getDisplayMetrics());
        // 2048 * 1360
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.shuai_h);
        Log.d(TAG, "bitmap h getByteCount : " + bitmap.getByteCount());
        Log.d(TAG, "bitmap h getAllocationByteCount : " + bitmap.getAllocationByteCount());
        Log.d(TAG, "bitmap h getHeight : " + bitmap.getHeight());
        Log.d(TAG, "bitmap h getWidth : " + bitmap.getWidth());

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.shuai_x);
        Log.d(TAG, "bitmap x getByteCount : " + bitmap.getByteCount());
        Log.d(TAG, "bitmap x getAllocationByteCount : " + bitmap.getAllocationByteCount());
        Log.d(TAG, "bitmap x getHeight : " + bitmap.getHeight());
        Log.d(TAG, "bitmap x getWidth : " + bitmap.getWidth());
        /**
         * bitmap h getByteCount : 37444860
         * bitmap h getAllocationByteCount : 37444860
         * bitmap h getHeight : 2493
         * bitmap h getWidth : 3755
         *
         * bitmap x getByteCount : 21063680
         * bitmap x getAllocationByteCount : 21063680
         * bitmap x getHeight : 1870
         * bitmap x getWidth : 2816
         *
         * 观察得出：
         * 同样图片，相比x中加载后的，从h中加载后的图片宽、高都更大！
         *
         */

    }

    /**
     * 无复用时，
     *  getAllocationByteCount 和 getByteCount 一样大
     *
     * 存在复用时，
     *  getAllocationByteCount >= getByteCount
     *
     * 具体可见：
     *  https://www.jianshu.com/p/3cb016edbd44
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap){
        if (bitmap == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //api 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){ //api 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight(); //other version
    }

}