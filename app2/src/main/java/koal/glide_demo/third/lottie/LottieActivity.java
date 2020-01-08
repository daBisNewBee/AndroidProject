package koal.glide_demo.third.lottie;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import koal.glide_demo.R;

/**
 *
 * Lottie 动画渲染神器的使用及原理讲解:
 * https://www.imooc.com/article/29249
 *
 */
public class LottieActivity extends AppCompatActivity {

    private LottieAnimationView mLottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);
        mLottieAnimationView = findViewById(R.id.lottie_view);
        mLottieAnimationView.setImageAssetsFolder("lottie");
        mLottieAnimationView.setAnimation("lottie/data.json");
        mLottieAnimationView.setRepeatCount(ValueAnimator.INFINITE);
        mLottieAnimationView.playAnimation();
        findViewById(R.id.lottie_surface_view);
//        test();
    }

    void saveBitmap2Local() {
        FileOutputStream fos = null;
        Bitmap bitmap = null;
        try {
            fos = new FileOutputStream(new File("/sdcard/DCIM/Camera/0.png"));
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void test() {
        ImageView iv = findViewById(R.id.iv_bitmap);
//        iv.setImageResource(R.drawable.scaled_bitmap);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scaled_bitmap);
        Bitmap circleBitmap = createCircleImage(bitmap);
        iv.setImageBitmap(circleBitmap);
    }

    // 矩形转圆形
    private Bitmap createCircleImage(Bitmap source) {
        if (source == null) return null;
        int height = source.getHeight();
        int width = source.getWidth();

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

        canvas.drawCircle(width/2, width/2 ,width/2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }


}
