package com.exa.cusview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class HighImageView extends View {

    private int mImageWidth;
    private int mImageHeight;
    // "加载大图、加载局部图片：图片区域解码BitmapRegionDecoder"
    private BitmapRegionDecoder mDecoder;
    private static BitmapFactory.Options mDecodeOptions = new BitmapFactory.Options();
    static{
        mDecodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }
    private Rect mRect = new Rect();
    private GestureDetector customGestureDetector;
    private static final String TAG = "HighImageView";

    public HighImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setImage(InputStream is,int width ,int height) {
        try {
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
            mImageWidth = width;
            mImageHeight = height;

            requestLayout();
            invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private void init() {
        customGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "onScroll() called with: e1 = [" + e1 + "], e2 = [" + e2 + "], distanceX = [" + distanceX + "], distanceY = [" + distanceY + "]");
                if (mImageWidth > getWidth()) {
                    mRect.offset((int) distanceX, 0);
                    checkWidth();
                    invalidate();
                }
                if (mImageHeight > getHeight()) {
                    mRect.offset(0, (int) distanceY);
                    checkHeight();
                    invalidate();
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    private void checkHeight() {
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = mImageHeight;
            mRect.top = mRect.bottom - getHeight();
        }
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = mRect.top + getHeight();
        }
    }

    private void checkWidth() {
        if (mRect.right > mImageWidth) {
            mRect.right = mImageWidth;
            mRect.left = mImageWidth - getWidth();
        }
        if (mRect.left < 0) {
            mRect.left = 0;
            mRect.right = mRect.left + getWidth();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        customGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mRect.left + width;
        mRect.bottom = mRect.top + height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = mDecoder.decodeRegion(mRect, mDecodeOptions);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
