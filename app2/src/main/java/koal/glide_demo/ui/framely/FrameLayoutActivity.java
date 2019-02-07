package koal.glide_demo.ui.framely;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import koal.glide_demo.R;

/**
 *
 * 特点：
 * FrameLayout中的子元素总是以屏幕的左上角层叠在一起。
 * 但是，可以通过"android:layout_gravity"、"margin"等来设置子元素位置。
 *
 * 常用属性：
 * android:foreground:
 *      设置该帧布局容器的前景图像
 *      （永远处于帧布局最上面,直接面对用户的图像,就是不会被覆盖的图片）
 * android:foregroundGravity:
 *      设置前景图像显示的位置
 *
 *  使用场景：
 *  1. 有层叠效果。
 *      ex. 最外层用半透明效果覆盖全部布局, 实际开发中，用素材来摆放位置来达到引导用户的效果。
 *  2. 层布局
 *
 * 实例：
 *  android的launcher
 *
 * 参考：
 * FrameLayout(帧布局):
 * http://www.runoob.com/w3cnote/android-tutorial-framelayout.html
 */
public class FrameLayoutActivity extends Activity {

    FrameLayout mFrame = null;

    MeziView mMeziView = null;

    private int count = 0;
    private long timeMillis = 0;
    private long timeMillis2 = 0;
    private long timeMillis3 = 0;
    private View view;

    private Handler mHandler = new Handler(){
        private int count = 0;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != 0x1111)
                return;
            count++;
            Drawable drawable = null;
            switch (count % 8) {
                case 1:
                    drawable = getDrawable(R.mipmap.s_1);
                    break;
                case 2:
                    drawable = getDrawable(R.mipmap.s_2);
                    break;
                case 3:
                    drawable = getDrawable(R.mipmap.s_3);
                    break;
                case 4:
                    drawable = getDrawable(R.mipmap.s_4);
                    break;
                case 5:
                    drawable = getDrawable(R.mipmap.s_5);
                    break;
                case 6:
                    drawable = getDrawable(R.mipmap.s_6);
                    break;
                case 7:
                    drawable = getDrawable(R.mipmap.s_7);
                    break;
                case 0:
                    drawable = getDrawable(R.mipmap.s_8);
                    break;
                default:
                    throw new RuntimeException("unknown count % 8:" + count % 8);
            }
            mFrame.setForeground(drawable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        speedTest();

        frameTest();
    }

    /*
    * Android 不同布局类型measure、layout、draw耗时对比
    * https://blog.csdn.net/qq_18757557/article/details/80495405
    * TODO: 无法验证
    * */
    private void speedTest() {
        ViewGroup root = new FrameLayout(this) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                long l = System.nanoTime();
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                timeMillis += (System.nanoTime() - l);
                if (++count == 1000) {
                    Log.e("cww", "finish measure: " + timeMillis);
                } else {
                    System.out.println("onMeasure count = " + count);
                    if (count < 1000) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.requestLayout();
                                view.invalidate();
                            }
                        }, 0);
                    }
                }
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                long l = System.nanoTime();
                super.onLayout(changed, left, top, right, bottom);
                timeMillis2 += (System.nanoTime() - l);
                if (count == 1000) {
                    Log.e("cww", "finish layout: " + timeMillis2);
                }
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                long l = System.nanoTime();
                super.dispatchDraw(canvas);
                timeMillis3 += (System.nanoTime() - l);
                if (count == 1000) {
                    Log.e("cww", "finish draw: " + timeMillis3);
                }
            }

        };

//        ViewGroup root = new FrameLayout(this);
        for (int i = 0; i < 10; i++) {
            FrameLayout rootLocal = new FrameLayout(this);
            root.addView(rootLocal);
            root = rootLocal;
        }

        TextView tv = new TextView(this);
        tv.setTextSize(100);
        tv.setText("hello world.");
        root.addView(tv);
    }

    private void frameTest() {
        setContentView(R.layout.activity_frame_layout);

        mFrame = (FrameLayout)findViewById(R.id.mylayout);
        mMeziView = new MeziView(this);
        mMeziView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("event = " + event);
                // 另外还需要-150,不然那个坐标是自定义View的左上角
                ((MeziView)v).bitmapX = event.getX() - 150;
                ((MeziView)v).bitmapY = event.getY() - 150;
                v.invalidate();
                // 返回false，继续让onClick处理
                return false;
            }
        });
        mFrame.addView(mMeziView);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0x1111);
            }
        }, 0, 200);
    }

    private boolean isMask = false;
    private TextView maskTv = null;

    // 遮罩层
    public void onClick(View view){
        if (isMask){
            mFrame.removeView(maskTv);
            isMask = false;
        }else {
            maskTv = new TextView(this);
            maskTv.setText("I`m a mask textview");
            maskTv.setTextColor(Color.BLUE);
            maskTv.setTextSize(20);
            maskTv.setGravity(Gravity.CENTER);
            maskTv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            maskTv.setBackgroundColor(Color.parseColor("#38000000"));
            mFrame.addView(maskTv);
            isMask = true;
        }

    }
}
