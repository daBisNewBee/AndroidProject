package com.example.user.ndkdebug;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.widget.ImageView;

import com.exa.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 为什么Bitmap需要高效加载？
 *
 * 若通过ImageView来显示图片，ImageView需要的图片尺寸较小，而BitMap表示的实际图片很大，
 * 若按照图片实际大小加载：
 *  1. 没有必要，ImageView无法显示原始图片实际大小
 *  2. 图片过大，APP内存有限，易造成OOM
 *
 * 因此，将原始图片按照一定"采样率"缩小图片再加载进来，
 * 1. 正常显示图片
 * 2. 降低内存占用，避免OOM
 * 3. 提供加载BitMap时的性能
 *
 * 如何高效加载BitMap？
 * 核心思路是根据图片需要显示的大小来缩放图片进行显示，缩放的方式为设置采样率。
 *
 * Created by user on 2018/10/5.
 */

public class BitMapTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
    }

    int calculateInSampleSize(BitmapFactory.Options options
            , int reqHeight, int reqWidth){
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth){
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight/inSampleSize)>=reqHeight
                    && (halfWidth/inSampleSize)>=reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        /*
        * inJustDecodeBounds:
        * 不加载图片却能获得图片的宽高信息(轻量级操作)
        * 目的为了计算缩放比inSampleSize
        *
        * */
        options.inJustDecodeBounds = true;
        System.out.println("before decodeResource. options.outHeight:"+options.outHeight+" options.outWidth:"+options.outWidth);
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);
        Assert.assertNull(bitmap);
        System.out.println("after decodeResource. options.outHeight:"+options.outHeight+" options.outWidth:"+options.outWidth);
        /*
        *   inSampleSize 为1，原始图片
            inSampleSize 为2，宽高均为原来的 1/2，像素为原来的 1/4
            inSampleSize 为4，宽高均为原来的 1/4，像素为原来的 1/16
            采样率一般为 2 的指数，如 1、2、4、8、16 等等，如果不为 2 的指数会向下取整。
        * */
        options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
        System.out.println("options.inSampleSize = " + options.inSampleSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    /**
     *
     * 高效加载Bitmap的流程：
     1. 获取原始图片宽高信息：
     ①将BitmapFactory.Options的inJustDecodeBounds参数设为true并加载图片。

     ②从BitmapFactory.Options中取出图片的原始宽高信息，它们对应于outWidth和outHeight参数。

     2. 计算采样率：
     ③根据采样率的规则并结合目标View的所需大小计算出采样率inSampleSize。

     3. 加载图片：
     ④将BitmapFactory.Options的inJustDecodeBounds参数设为false，然后重新加载图片。

     链接：https://www.jianshu.com/p/5f02db4a225d
     *
     * @throws Exception
     */
    @Test
    public void loadBitMapAdvan_Test() throws Exception {
//        Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), R.mipmap.ic_launcher, 24, 24);
        Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), R.mipmap.ic_launcher, 100, 100);
        bitmap.recycle();
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        System.out.println("bitmap = " + bitmap);
    }

    @Test
    public void bitMap_Test() throws Exception {

        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.ic_launcher));
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_mo);
        System.out.println("bitmap getWidth = " + bitmap.getWidth());
        System.out.println("bitmap getHeight = " + bitmap.getHeight());
        System.out.println("bitmap getByteCount = " + bitmap.getByteCount());
    }
}
