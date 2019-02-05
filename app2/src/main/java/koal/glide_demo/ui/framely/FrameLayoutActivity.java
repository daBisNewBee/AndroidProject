package koal.glide_demo.ui.framely;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

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
                return true;
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
}
