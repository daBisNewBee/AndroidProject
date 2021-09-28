package koal.glide_demo.video;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 *
 * "GLSurfaceView是一个很好的基类对于构建一个使用OpenGL ES进行部分或全部渲染的应用程序"
 *
 * 1. 绘制效率不同。GPU加速：GLSurfaceView的效率是SurfaceView的30倍以上，SurfaceView使用画布进行绘制，
 *            GLSurfaceView利用GPU加速提高了绘制效率。
 *
 * 2. 渲染引擎不同。View的绘制onDraw(Canvas canvas)使用Skia渲染引擎渲染，而GLSurfaceView的渲染器Renderer，
 *    其渲染的onDrawFrame(GL10 gl)接口使用opengl绘制引擎进行渲染。
 *
 * 3. OpenGLESv2:
 *    /vendor/lib/egl/libGLESv2_adreno.so
 *
 * 参考：
 * GLSurfaceView的用法：
 * https://www.cnblogs.com/renkangke/archive/2013/04/14/3019825.html
 */
public class GLSurfaceActivity extends AppCompatActivity {


    private GLSurfaceView mGLView;
    private static final String TAG = "GLSurfaceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        final boolean supportsGLES3 = configurationInfo.reqGlEsVersion >= 0x30000
                ||(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                &&(Build.FINGERPRINT.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("unknow")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86");
        // version = 3.2
        Log.d(TAG, "supportsGLES3: " + supportsGLES3 + " version = " + configurationInfo.getGlEsVersion());

        mGLView = new ClearGLSurfaceView(this);
//        mGLView = new GLSurfaceView(this);
//        mGLView.setRenderer(new ClearRenderer());
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    class ClearGLSurfaceView extends GLSurfaceView {

        ClearRenderer mRenderer;

        public ClearGLSurfaceView(Context context) {
            super(context);
            mRenderer = new ClearRenderer();
            setRenderer(mRenderer);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // queueEvent()方法被安全地用于在UI线程和渲染线程之间进行交流
            queueEvent(()->{
                mRenderer.setColor(event.getX()/getWidth(),
                        event.getY()/getHeight(), 1.0f);
            });
            return true;
        }
    }

    private class ClearRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.d(TAG, "onSurfaceCreated() called with: gl = [" + gl + "], config = [" + config + "]");
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
            Log.d(TAG, "onSurfaceChanged() called with: gl = [" + gl + "], width = [" + width + "], height = [" + height + "]");
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // glClear方法清除帧缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            // 这个应用每帧都在清楚屏幕。当你点击屏幕时，它清除颜色基于你触屏时间的X、Y坐标
            gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
            Log.d(TAG, "onDrawFrame() called with: gl = [" + gl + "]");
        }

        public void setColor(float r, float g, float b) {
            Log.d(TAG, "setColor() called with: r = [" + r + "], g = [" + g + "], b = [" + b + "]");
            mRed = r;
            mGreen = g;
            mBlue = b;
        }

        private float mRed;

        private float mGreen;

        private float mBlue;
    }
}