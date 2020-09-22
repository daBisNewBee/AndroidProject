package koal.glide_demo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import koal.glide_demo.utlis.BaseUtil;


public class PicassoActivity extends AppCompatActivity {

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;

    String URL1 = "https://unidesk.alicdn.com/202009/14fce6243a0046559fdb3846cd637570.jpg"; // 10.9M
    String URL2 = "http://imagev2.xmcdn.com/group85/M04/BE/01/wKg5H19PQSHxFpbDAAHIuitElSk879.jpg"; // 2.9M
    String URL3 = "https://unidesk.alicdn.com/202009/244c9f8750ee4c3297ee371da3b91145.jpg"; // 8.6M

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picasso);
        iv1 = findViewById(R.id.pic_iv1);
        iv2 = findViewById(R.id.pic_iv2);
        iv3 = findViewById(R.id.pic_iv3);

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
}
