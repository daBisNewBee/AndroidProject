package koal.glide_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : feiwen
 * desc   : 时光唱片
 */
public class XmRecorderSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    //刷新时间 1帧60ms(实际绘制一帧耗费60ms左右，大于指定refreshTime，refreshTime不起作用)
    private static int refreshTime = 60;

    /**
     * 1帧转1.6度
     */
    private static final float ROTATE_RADIUS_PER_TIME = 0.5f;

    private float refreshRotation;
    private int mCenterX;
    private int mCenterY;

    private Paint bgPaint;
    private Paint mCoverPaint;
    private Paint mShadowPaint;

    private SurfaceHolder mSurfaceHolder;

    private boolean isDrawing;
    private boolean isCreated;

    private Bitmap bitmapBg;
    private Bitmap mCenterCoverBitmap;

    private Canvas mCanvas;
    private int width;
    private int height;
    private int INIT_ALPHA = 100;

    private List<Integer> spreadRadius = new ArrayList<>();//扩散圆层级数，元素为扩散的距离
    private List<Integer> alphas = new ArrayList<>();//对应每层圆的透明度
    private int radius = 210; //中心圆半径
    private Paint spreadPaint; //扩散圆paint
    private int distance = 2; //每次圆递增间距
    private int maxRadius = 55; //最大圆半径
    private Thread mDrawThread;

    private boolean isShowWave;

    public XmRecorderSurfaceView(Context context) {
        super(context);
        initView();
    }

    public XmRecorderSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public XmRecorderSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        // 去黑底，背景透明 TODO: 为什么绘制 "bitmapBg"有抗锯齿的效果？
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);   // 全透明
//        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT); // 半透明

        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        initParams();
    }

    private void initParams() {
        bgPaint = new Paint();
//        防止边缘的锯齿
//        bgPaint.setAntiAlias(true);
//        对位图进行滤波处理
//        bgPaint.setFilterBitmap(true);
        // 去抖动
//        bgPaint.setDither(true);

        mCoverPaint = new Paint();
        mCoverPaint.setAntiAlias(true);

        //最开始不透明且扩散距离为0
        alphas.add(INIT_ALPHA);
        spreadRadius.add(0);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setStrokeWidth(10);
        mShadowPaint.setColor(Color.BLACK);
        mShadowPaint.setAlpha(50);

        spreadPaint = new Paint();
        spreadPaint.setAntiAlias(true);
        spreadPaint.setAlpha(INIT_ALPHA);
        spreadPaint.setColor(Color.WHITE);
        spreadPaint.setStrokeWidth(3);
        spreadPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isCreated = true;
        width = getWidth();
        height = getHeight();
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mDrawThread = new Thread(this, "_draw_Thread");
        mDrawThread.start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isDrawing = false;
        isCreated = false;
        if (mDrawThread != null && mDrawThread.isAlive()) {
            try {
                mDrawThread.join(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (isCreated) {
            if (isDrawing) {
                drawSomething();
            }
        }
    }

    private void drawSomething() {
        Canvas canvas = null;
        long t = System.currentTimeMillis();
        try {
            canvas = mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
                if (bitmapBg != null) {
                    canvas.drawBitmap(bitmapBg, 0, 0, bgPaint);
                }
                if (mCenterCoverBitmap != null) {
                    drawAlbumCover(canvas);
                }
                if (isShowWave) {
                    drawWave(canvas);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
            SystemClock.sleep(Math.max(refreshTime - (System.currentTimeMillis() - t), 0));
        }
    }

    public Bitmap captureOneFrame(int width, int height) {
        if (!isCreated) {
            initParams();
            mCenterX = width / 2;
            mCenterY = height / 2;
        }

        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (bitmapBg != null) {
            canvas.drawBitmap(bitmapBg, 0, 0, bgPaint);
        }
        if (mCenterCoverBitmap != null) {
            drawAlbumCover(canvas);
        }
        if (isShowWave) {
            drawWave(canvas);
        }
        return bitmap;
    }

    /**
     * 那一刻模糊的影像停留在心间
     */
    public Bitmap generateSingleMomentPicture(long time) {
        // 加保险
        if (isDrawing) {
            setDrawing(false);
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        try {
            if (mCanvas == null) {
                mCanvas = new Canvas();
            }
            if (mCanvas == null) {
                return null;
            }
            mCanvas.setBitmap(bitmap);
            // 先清空
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (bitmapBg != null) {
                mCanvas.drawBitmap(bitmapBg, 0, 0, new Paint());
            }
            float rotate = (time * ROTATE_RADIUS_PER_TIME / 60) % 360;
            if (mCenterCoverBitmap != null) {
                drawAlbumCover(mCanvas, rotate);
            }
            if (isShowWave) {
                drawWave(mCanvas);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void drawAlbumCover(Canvas canvas, float rotate) {
        canvas.drawCircle(mCenterX, mCenterY, mCenterCoverBitmap.getWidth()/2 + 7, mShadowPaint);
        drawRotateBitmap(canvas, mCoverPaint, mCenterCoverBitmap, rotate, mCenterX, mCenterY);
    }

    private void drawAlbumCover(Canvas canvas) {
        if (refreshRotation + ROTATE_RADIUS_PER_TIME > 360) {
            refreshRotation = refreshRotation + ROTATE_RADIUS_PER_TIME - 360;
        } else {
            refreshRotation += ROTATE_RADIUS_PER_TIME;
        }
        canvas.drawCircle(mCenterX, mCenterY, mCenterCoverBitmap.getWidth()/2 + 7, mShadowPaint);
        drawRotateBitmap(canvas, mCoverPaint, mCenterCoverBitmap, refreshRotation, mCenterX, mCenterY);
    }

    private void drawWave(Canvas canvas) {
        for (int i = 0; i < spreadRadius.size(); i++) {
            int alpha = alphas.get(i);
            spreadPaint.setAlpha(alpha);
            int width = spreadRadius.get(i);
            //绘制扩散的圆
            canvas.drawCircle(mCenterX, mCenterY, radius + width, spreadPaint);
            //每次扩散圆半径递增，圆透明度递减
            alpha = alpha - 1 > 0 ? alpha - 1 : 1;
//            alpha = alpha - distance > 0 ? alpha - distance : 1;
            alphas.set(i, alpha);
            spreadRadius.set(i, width + distance);
        }
        //当最外层扩散圆半径达到最大半径时添加新扩散圆
        if (spreadRadius.get(spreadRadius.size() - 1) > maxRadius) {
            spreadRadius.add(0);
            alphas.add(INIT_ALPHA);
        }
        //超过8个扩散圆，删除最先绘制的圆，即最外层的圆
        if (spreadRadius.size() >= 5) {
            alphas.remove(0);
            spreadRadius.remove(0);
        }
    }

    private void drawRotateBitmap(Canvas canvas, Paint paint, Bitmap bitmap,
                                  float rotation, float posX, float posY) {
        Matrix matrix = new Matrix();
        int offsetX = bitmap.getWidth() / 2;
        int offsetY = bitmap.getHeight() / 2;
        matrix.postTranslate(-offsetX, -offsetY);
        matrix.postRotate(rotation);
        matrix.postTranslate(posX, posY);
        canvas.drawBitmap(bitmap, matrix, paint);

    }

    public void setDrawing(boolean drawing) {
        isDrawing = drawing;
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public void setBitmapBg(Bitmap bitmapBg) {
        // Bitmap拉伸后出现锯齿的几种解决办法:
        // https://blog.csdn.net/zcwfengbingdongguke/article/details/10914493
        this.bitmapBg = bitmapBg;
    }

    public void setCoverBitmap(Bitmap bitmap) {
        mCenterCoverBitmap = bitmap;

    }

    public void setShowWave(boolean showWave) {
        isShowWave = showWave;
    }
}
