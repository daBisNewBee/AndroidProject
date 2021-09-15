package koal.glide_demo.video;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import koal.glide_demo.R;

/**
 *
 * TextureView:
 * 1. 更像是一般的View，像TextView那样能被缩放、平移，也能加上动画。
 * 2. 在Andriod4.0之后的API中才能使用。
 *
 * SurfaceView:
 * 1. 在子线程（叫做渲染线程）中对Surface进行绘制，将绘制好的画面呈现给View，不会影响主线程与用户的交互
 * 2. 当涉及到UI的快速刷新时就需要用到SurfaceView（如游戏，视频等）
 * 3. 由于是独立的一层View，更像是独立的一个Window，不能加上动画、平移、缩放；
 * 4. SurfaceView的双缓冲功能，可以是画面更加流畅的运行
 *
 * 结论：
 * 1. 能够在子线程中更新UI是上述两种View相比于View的最大优势
 * 2. 对于一些类似于坦克大战等需要不断告诉更新画布的游戏来说，SurfaceView绝对是极好的选择。
 *    但是比如视频播放器或相机应用的开发，TextureView则更加适合
 * 3. 如果一个View需要频繁的刷新，或者在刷新时数据处理量大（可能引起卡顿），可以考虑使用SurfaceView来替代View。
 *
 *
 * Tips:
 * SurfaceView和View一大不同就是SurfaceView是被动刷新的，但我们可以控制刷新的帧率，
 * 而View并且通过invalidate方法通知系统来主动刷新界面的，但是View的刷新是依赖于系统
 * 的VSYSC信号的，其帧率并不受控制，而且因为UI线程中的其他一些操作会导致掉帧卡顿。而
 * 对于SurfaceView而言，它是在子线程中绘制图形，根据这一特性即可控制其显示帧率，通过
 * 简单地设置休眠时间，即可，并且由于在子线程中，一般不会引起UI卡顿。
 *
 * SurfaceView的双缓冲机制：
 * 即对于每一个SurfaceView对象而言，有两个独立的graphic buffer。
 * 在Android SurfaceView的双缓冲机制中是这样实现的：
 * 在Buffer A中绘制内容，然后让屏幕显示Buffer A；在下一个循环中，在Buffer B中绘制内容，
 * 然后让屏幕显示Buffer B，如此往复。而由于这个双缓冲机制的存在，可能会引起闪屏现象，。在
 * 第一个"lockCanvas-drawCanvas-unlockCanvasAndPost "循环中，更新的是buffer A的内
 * 容；到下一个"lockCanvas-drawCanvas-unlockCanvasAndPost"循环中，更新的是buffer B
 * 的内容。 如果buffer A与buffer B中某个buffer内容为空，当屏幕轮流显示它们时，就会出现画
 * 面黑屏闪烁现象。
 *
 *
 */
public class LiveCameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private TextureView mTextureView;
    private SeekBar mSeekBar;
    private TextView mProgTv;
    private Camera mCamera;
    private String [] permissions = {Manifest.permission.CAMERA};

    private EditText mEditText;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    private float rotate = 90.0f;
    private View mContentView;

    /**
     *  注意：这里表示可以在子线程中更新UI！！！！TextureView、SurfaceView都是！
     */
    class WorkThread extends Thread {
        private boolean isFromTextureView;

        public WorkThread(boolean isFromTextureView) {
            this.isFromTextureView = isFromTextureView;
        }

        @Override
        public void run() {
            while (true){
                //2.开画
                Paint p =new Paint();
                p.setColor(Color.rgb( (int)(Math.random() * 255),
                        (int)(Math.random() * 255) ,  (int)(Math.random() * 255)));
                Rect aa  =  new Rect( (int)(Math.random() * 100) ,
                        (int)(Math.random() * 100)
                        ,(int)(Math.random() * 500)
                        ,(int)(Math.random() * 500) );

                if (isFromTextureView) {
                    Canvas c_tv = mTextureView.lockCanvas();
                    c_tv.drawRect(aa, p);
                    //3. 解锁画布   更新提交屏幕显示内容
                    mTextureView.unlockCanvasAndPost(c_tv);
                } else {
                    Canvas c_sh = mSurfaceHolder.lockCanvas();
                    c_sh.drawRect(aa, p);
                    mSurfaceHolder.unlockCanvasAndPost(c_sh);
                }
                try {
                    // 1s 刷新10次，相当于10Hz，显示控制帧率
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentView = LayoutInflater.from(this).inflate(R.layout.activity_texture_view, null, false);
        setContentView(mContentView);

        mSurfaceView = findViewById(R.id.surface_view_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("test", "surfaceCreated() called with: holder = [" + holder + "]");
                new WorkThread(false).start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("test", "surfaceChanged() called with: holder = [" + holder + "], format = [" + format + "], width = [" + width + "], height = [" + height + "]");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("test", "surfaceDestroyed() called with: holder = [" + holder + "]");
            }
        });

        mSeekBar = findViewById(R.id.seekbar_rorate);
        mProgTv = findViewById(R.id.percent_seek);
        mProgTv.setText(rotate + "%");
        mSeekBar.setMax(360);
        mSeekBar.setProgress((int)rotate);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextureView.setRotation((float)progress);
                mProgTv.setText(progress + "%");
                mSurfaceView.setRotation((float)progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mTextureView = findViewById(R.id.texture_view_camera);
        mTextureView.setSurfaceTextureListener(this);

//        ActivityCompat.requestPermissions(this, permissions, 200);

        mEditText = findViewById(R.id.live_edit_text);
        mEditText.requestFocus();
    }

    private void registerGlobalLayoutListener() {
        if (mOnGlobalLayoutListener == null) {
            mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect rect = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                    int rootInvisibleHeight = getWindow().getDecorView().getRootView().getHeight() - rect.bottom;
                    Log.d("todo", "rect = " + rect);
                    Log.d("todo", "onGlobalLayout rootInvisibleHeight = " + rootInvisibleHeight);
                    if (rootInvisibleHeight > 200) {
                        Log.d("todo", "若不可视区域高度大于100，这里键盘显示.");
                    } else {
                        Log.d("todo", "这里键盘隐藏.");
                    }
                }
            };
        }
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private void unregisterGlobalLayoutListener() {
        if (mOnGlobalLayoutListener != null && mContentView != null) {
            mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSoftInputMethod();
        registerGlobalLayoutListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInputMethod();
        unregisterGlobalLayoutListener();
    }

    private void showSoftInputMethod() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftInputMethod() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("onRequestPermissionsResult PERMISSION_GRANTED.");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("test", "onSurfaceTextureAvailable() called with: surface = [" + surface + "], width = [" + width + "], height = [" + height + "]");
//        new WorkThread(true).start(); // 这里用来验证子线程也可以更新TextureView

        try {
            int num = Camera.getNumberOfCameras(); // 2
            // 0: 后置    1: 前置
            mCamera = Camera.open(1);
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            mTextureView.setAlpha(1.0f);
            mTextureView.setRotation(rotate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.d("test", "onSurfaceTextureSizeChanged() called with: surface = [" + surface + "], width = [" + width + "], height = [" + height + "]");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("test", "onSurfaceTextureDestroyed() called with: surface = [" + surface + "]");
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        Log.d("test", "onSurfaceTextureUpdated() called with: surface = [" + surface + "]");
    }
}
