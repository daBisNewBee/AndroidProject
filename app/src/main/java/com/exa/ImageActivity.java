package com.exa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.exa.cusview.HighImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * bitmap 的几个注意点：
 *
 * 1. 内存占用如何计算？
 *    资源文件：width * height * 一个像素所占的内存
 *            * TargetDensity/inDensity * TargetDensity/inDensity（decodeResource）
 *    其他文件：width * height * 一个像素所占的内存
 *
 *    ps:mdpi、hdpi、xdpi加载顺序：
 *    会先到对应dpi文件夹下的找，找不到会往上比自己dpi高的文件找，然后才会往比自己dpi低的下面找
 *
 *
 * 2. 内存如何复用？
 *    指定 inMutable、inBitmap
 *
 * 3. bitmap如何压缩？
 *    compress 质量压缩 jpeg 不会对内存产生影响
 *    inSampleSize 内存压缩
 *
 *    压缩的意义？ps.
 *    一张宽高为2048x1536的图片，设置inSampleSize为4之后，实际加载到内存中的图片宽高是512x384。
 *    占有的内存就是0.75M而不是12M，足足节省了15倍。
 *
 * 4. bitmap内存模型？
 *    Android 2.3.3（API10）之前，bitmap像素在native里，Bitmap对象本身则存放在Dalvik Heap
 *    在Android3.0之后，Bitmap的像素数据也被放在了Dalvik Heap中
 *    Android 3.0到8.0 之间Bitmap像素数据存在Java堆，而8.0之后像素数据存到native堆中
 *    < 3.0 : Native heap
 *    >= 3.0 && < 8.0 Java heap，只此时是Java堆
 *    >= 8.0 Native (fixme: 好处，Bitmap不再为OOM背锅了！Bitmap导致OOM的问题基本不会在8.0以上设备出现了（没有内存泄漏的情况下）)
 *
 * 参考：
 * Android性能优化（五）之细说Bitmap：
 * https://cloud.tencent.com/developer/article/1190950
 */

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

    private Bitmap mXBitmap;

    /**
     *
     * 只有 decodeResource 会用到资源所处文件夹对应密度和手机系统密度进行缩放，其他方法都不会。
     * 此时Bitmap默认占用的内存 = width * height * 一个像素所占的内存
     *
     * @param savedInstanceState
     */
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
        mXBitmap = bitmap;
        compressTest();
        reuseBitmapTest();
        largeImageViewTest();
    }

    private void largeImageViewTest() {
        HighImageView hiv = findViewById(R.id.main_high_iv);
        try {
            InputStream is = getAssets().open("shuai.jpg");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            hiv.setImage(is, bitmap.getWidth(), bitmap.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ImageView mImageView;

    void compressTest() {
        SeekBar seekBar = findViewById(R.id.main_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                new Thread(){
                    @Override
                    public void run() {
                        updateBitmap(seekBar.getProgress());
                    }
                }.start();
            }
        });
        mImageView = findViewById(R.id.main_quality_iv);
    }


    /**
     * bitmap 复用
     */
    void reuseBitmapTest() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inTargetDensity = 320;
        options.inDensity = 320;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.shuai_h, options);
        Log.d(TAG, "bitmap: " + bitmap);
        Log.d(TAG, "bitmap:getByteCount: " + bitmap.getByteCount() + " getAllocationByteCount: " + bitmap.getAllocationByteCount());

        options.inBitmap = bitmap;
        options.inMutable = true;
        options.inTargetDensity = 160;
        options.inDensity = 320;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.shuai_h, options);
        Log.d(TAG, "After bitmap: " + bitmap);
        Log.d(TAG, "After bitmap:getByteCount: " + bitmap.getByteCount() + " getAllocationByteCount: " + bitmap.getAllocationByteCount());
        /**
         * bitmap: android.graphics.Bitmap@d9588a0
         * bitmap:getByteCount: 11141120 getAllocationByteCount: 11141120
         * After bitmap: android.graphics.Bitmap@d9588a0
         * After bitmap:getByteCount: 2785280 getAllocationByteCount: 11141120
         *
         * 结论：
         * 1. bitMap被复用：地址一样
         * 2. getAllocationByteCount为地址最大空间：大于实际占用getByteCount
         * 3. 设置缩放宽高为原始宽高一半后，内存占用仅1/4
         */
    }

    /**
     * 1. bitmap 质量压缩:
     *
     * 不会减少图片的像素。
     * 进过它压缩的图片文件大小会变小，但是解码成bitmap后占得内存是不变的。
     *
     * 只JPEG，PNG不行，因为无损格式
     *
     * 2. bitmap 大小压缩：
     * 参考"BitMapTest"，通过"inJustDecodeBounds=true"获得图片宽高信息，
     * 计算采样率inSampleSize，以较小方式加载图片，达到节省内存的目的。
     *
     * @param progress
     */
    @WorkerThread
    private void updateBitmap(int progress) {
        // 大小不会变。quality为10会质量很差！
        Log.d(TAG, "updateBitmap() called with: progress = [" + progress + "]");
        long start = System.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mXBitmap.compress(Bitmap.CompressFormat.JPEG, progress, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Log.d(TAG, "bitmap getByteCount : " + bitmap.getByteCount());
        Log.d(TAG, "bitmap getAllocationByteCount : " + bitmap.getAllocationByteCount());
        Log.d(TAG, "bitmap getHeight : " + bitmap.getHeight());
        Log.d(TAG, "bitmap getWidth : " + bitmap.getWidth());
        Log.d(TAG, "bitmap bytes : " + bytes.length); // 从这个判断变化！其他值不会变
        Log.d(TAG, "bitmap cost ----> : " + (System.currentTimeMillis() - start) + " ms");
        mImageView.post(()-> mImageView.setImageBitmap(bitmap));
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