package koal.glide_demo.ui.basic;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import koal.glide_demo.R;

public class ImageActivity extends Activity {

    private FrameLayout mImageContainer;
    private ImageView mAlbumCover;
    private TextView mInnerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImageContainer = findViewById(R.id.image_container);
        mAlbumCover = findViewById(R.id.album_cover);
        // 用户首选方向，表示支持屏幕旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        System.out.println("ImageActivity.onCreate");
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mAlbumCover.setImageResource(R.drawable.sky16_9);
                // 16:9
                int height = getScreenWidth() / 16 * 9;
                FrameLayout.LayoutParams params = new FrameLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                mAlbumCover.setLayoutParams(params);
                mAlbumCover.setVisibility(View.VISIBLE);
//                mImageContainer.addView(imageView);
//                mImageContainer.addView(imageView, params); // 上两行的简易方式
            }
        }, 1000);
        mInnerText = findViewById(R.id.inner_text);
        mInnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * 1. 注意两种获取params的区别！！new 会重新构造对象，但是get可以继承原来的控件属性!
                * 2. setLayoutParams 其实是子View的属性！
                * */
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mInnerText.getLayoutParams());
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mInnerText.getLayoutParams();
                params.height = dip2px(ImageActivity.this, 70);
                params.width = dip2px(ImageActivity.this, 70);
                mInnerText.setLayoutParams(params);
            }
        });
    }

    private int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

    private int getScreenWidth(){
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        System.out.println("getScreenWidth :" + dm.widthPixels);
        return dm.widthPixels;
    }

    private int getScreenHeight(){
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        System.out.println("getScreenWidth :" + dm.widthPixels);
        return dm.heightPixels;
    }

    public void getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)

        Log.d("System.out", "屏幕宽度（像素）：" + width);
        Log.d("System.out", "屏幕高度（像素）：" + height);
        Log.d("System.out", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("System.out", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("System.out", "屏幕宽度（dp）：" + screenWidth);
        Log.d("System.out", "屏幕高度（dp）：" + screenHeight);
    }

    public void gotoPortrait(View view){
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void gotoLandScape(View view){
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void fitNaviBarAndStatusBar(boolean isPortrait){
        if (!isPortrait) {
            int flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            if (Build.VERSION.SDK_INT >= 19) {
                flag |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            this.getWindow().getDecorView().setSystemUiVisibility(flag);
        }
        setFullScreen(!isPortrait);
    }

    /**
     * landscape 0
     * portrait  1
     * user      2
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("newConfig = " + newConfig.orientation);
        getAndroidScreenProperty();
        if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == newConfig.orientation){
            fitNaviBarAndStatusBar(true);
            dynamicChangeCover();
        }else if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == newConfig.orientation
                || ActivityInfo.SCREEN_ORIENTATION_USER == newConfig.orientation){
            fitNaviBarAndStatusBar(false);
            dynamicChangeCover();
        }
    }

    private void setFullScreen(boolean fullScreen) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullScreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    private void dynamicChangeCover() {
        int height = getScreenWidth() / 16 * 9;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        System.out.println("params height = " + params.height);
        System.out.println("params width = " + params.width);
        mAlbumCover.setLayoutParams(params);
    }
}
