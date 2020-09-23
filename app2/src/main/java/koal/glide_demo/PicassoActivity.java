package koal.glide_demo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import koal.glide_demo.utlis.BaseUtil;


/**
 *
 * 几个注意点：
 *
 * 1. 尽量不要覆写对象的 finalize 方法! 容易出现OOM！
 *    释放资源，请主动调用release，在release中处理。
 *
 *    原因：
 *    1. 覆写了finalize方法的对象，会被加入到"FinalizerReference"队列
 *    2. (Daemons.java)FinalizerDaemon.INSTANCE 会执行上述队列的清理，但是该守护线程优先级低
 *    3. 新建对象的速度慢于执行finalize队列移除各元素的速度，FinalizerReference就会越来越大，而它获取CPU时间又少的可怜，引发OOM
 *
 * 2.
 *
 * 参考：
 * 神奇的内存占用大户finalizerreference：
 * https://www.dazhuanlan.com/2020/01/02/5e0db2f4ce484/?__cf_chl_jschl_tk__=68e5e5a3b2b2605318350c77976e3eb4a1ea4cae-1600852185-0-AXtzej46zJBs-vsX9G73p4UtbVJOVZHmtiLZqbkS3NF7QniwS2sz2lez-Dn8By41TxvzFcpmeMJQ20vOXcmgxy0vULA0ZriLyc7BfxNGAK8n1EgvDTdgcxLndNE9HeWQrZzis3ztYtReQ0z7ngLAfTfTHCSy4EJfJM73WVt0qskLOMFY6RfK41YyKAo_cuYQ-16HttFUcit1u7d3Jdpy7iVQoc-PEs7ilAxFERp8iz-PECyadovjmuKx-uRNKHXQUGgrNvcFuy41Rro9Puo7tnsHHRTOi2CYNPFHhuFkdJ3on9UCTIS7WY4gBvQ4CkSa5A
 *
 */
public class PicassoActivity extends AppCompatActivity {

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;

    String URL1 = "https://unidesk.alicdn.com/202009/14fce6243a0046559fdb3846cd637570.jpg"; // 10.9M
    String URL2 = "http://imagev2.xmcdn.com/group85/M04/BE/01/wKg5H19PQSHxFpbDAAHIuitElSk879.jpg"; // 2.9M
    String URL3 = "https://unidesk.alicdn.com/202009/244c9f8750ee4c3297ee371da3b91145.jpg"; // 8.6M

    private ListItem40MClass head;
    private ListItem40MClass2 head2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picasso);
        iv1 = findViewById(R.id.pic_iv1);
        iv2 = findViewById(R.id.pic_iv2);
        iv3 = findViewById(R.id.pic_iv3);
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memTest2();
//                memTest();
            }
        });
    }

    /**
     *
     * 验证内存泄露问题！
     * dumpheap 可以看到 FinalizerReference 的retain的size最大，
     * 比40M时(memTest)的还要大，说明回收线程没有执行，发生了泄露
     */
    private void memTest2() {
        if (head2 == null) {
            head2 = new ListItem40MClass2();
        } else {
            for (int i = 0; i < 1000; i++) {
                ListItem40MClass2 tmp = head2;
                while (tmp.next != null) {
                    tmp = tmp.next;
                }
                tmp.next = new ListItem40MClass2();
            }
        }
    }

    /**
     *
     * 验证"retain size"的效果！
     *
     * 点击三次，dumpheap可以看到最大的对象retain size为240M，远超过实际分配的 3*40M，因此retain计算的大小实际存在"重复计算"的现象！
     *
     * ps: profiler中的dump heap得出的结果不同！仅有120M的byte[].
     *
     * 因此，"profiler中的dump heap"得出的结果，仅限于在dump期间分配的对象！
     *
     * 若不在此区间分配的对象，就dump不到！
     */
    private void memTest() {
        if (head == null) {
            head = new ListItem40MClass();
        } else {
            ListItem40MClass tmp = head;
            while (tmp.next != null) {
                tmp = tmp.next;
            }
            tmp.next = new ListItem40MClass();
        }
    }

    private void test3() {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
        @SuppressLint("ResourceType")
        Bitmap bitmap1 = BitmapFactory.decodeStream(this.getResources().openRawResource(R.mipmap.ad));
        iv1.setImageBitmap(bitmap1);

        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inPreferredConfig = Bitmap.Config.RGB_565;
        @SuppressLint("ResourceType")
        Bitmap bitmap2 = BitmapFactory.decodeStream(this.getResources().openRawResource(R.mipmap.ad));
        iv2.setImageBitmap(bitmap2);

        BitmapFactory.Options options3 = new BitmapFactory.Options();
        options3.inPreferredConfig = Bitmap.Config.ARGB_4444;
        @SuppressLint("ResourceType")
        Bitmap bitmap3 = BitmapFactory.decodeStream(this.getResources().openRawResource(R.mipmap.ad));
        iv3.setImageBitmap(bitmap3);
    }

    private void test2() {
        //        Picasso.with(getBaseContext())
//                .load(URL1)
//                .config(Bitmap.Config.ARGB_8888)
//                .into(iv1);

//        Picasso.with(getBaseContext())
//                .load(URL2)
//                .config(Bitmap.Config.ARGB_8888)
//                .into(iv2);

        Picasso.with(getBaseContext())
                .load(URL1)
//                .resize(BaseUtil.getScreenWidth(getBaseContext()), BaseUtil.getScreenHeight(getBaseContext()))
                .config(Bitmap.Config.ARGB_8888)
                .into(iv2);
    }

    private void test() {
        Picasso picasso = new Picasso.Builder(getBaseContext())
//                .memoryCache(mPicassoMemCache)
//                .downloader(mOkHttpDownloader)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                    }
                })
                .build();

        final RequestCreator requestCreator = picasso.load(URL1);
        boolean isHighPriority = false;
        requestCreator.priority(isHighPriority ? Picasso.Priority.HIGH : Picasso.Priority.LOW);
        requestCreator.tag("ImagerManager");
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("todo", "onBitmapLoaded() called with: bitmap = [" + bitmap + "], from = [" + from + "]");
                iv1.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("todo", "onBitmapFailed() called with: errorDrawable = [" + errorDrawable + "]");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d("todo", "onPrepareLoad() called with: placeHolderDrawable = [" + placeHolderDrawable + "]");
            }
        };
        requestCreator.into(target);
    }

    class ListItem40MClass {

        byte[] content = new byte[1000 * 1000 * 40];
        ListItem40MClass() {
            for (int i = 0; i < content.length; i++) {
                content[i] = 1;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            Log.v("todo","finalize ListItem40MClass ------>");
        }

        ListItem40MClass next;
    }

    class ListItem40MClass2 {
        byte[] content = new byte[5];

        ListItem40MClass2() {
            for (int i = 0; i < content.length; i++) {
                content[i] = 1;
            }
        }

        /**
         *
         * "对象是否override了finalize方法，如果有覆盖此方法，对象就会被添加到finalize执行队列(就是FinalizerReference)中"
         * 也是dumpHeap 可以看到 FinalizerReference值很大的原因！
         * 去除覆写，FinalizerReference就没有引用！
         *
         * @throws Throwable
         */
//        @Override
//        protected void finalize() throws Throwable {
//            super.finalize();
//            Log.d("todo", "ListItem40MClass2 -------> inalize() called");
//        }

        ListItem40MClass2 next;
    }
}
