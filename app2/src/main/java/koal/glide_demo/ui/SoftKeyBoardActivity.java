package koal.glide_demo.ui;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import koal.glide_demo.R;

/**
 * 软键盘用法
 *
 * 对应：
 * android:windowSoftInputMode =
 */
public class SoftKeyBoardActivity extends AppCompatActivity {

    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int softMode = getWindow().getAttributes().softInputMode;
        Log.d("todo", "softMode = " + Integer.toHexString(softMode));

        final int targetMode =
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        /*
                        * 整个Layout重新编排，键盘"覆盖"在Activity上，
                        * 与底部关系的组件自动调整，比如：layout_alignParentBottom
                        * 根布局RelativeLayout的高度发生了变化：1118 -> 520
                        * */
                      | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        /*
                        * 布局上移，键盘不会覆盖Activity
                        * 根布局RelativeLayout的高度不会变化
                        * */
//                      | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                ;
        getWindow().setSoftInputMode(targetMode);
        mContentView = LayoutInflater.from(this).inflate(R.layout.activity_soft_key_board, null, false);
        setContentView(mContentView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerGlobalLayoutListener();
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

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
}
