package koal.glide_demo.touch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 *
 * 事件分发机制的几个注意点：
 * 1. 几个重要回调接口：
 *  dispatchTouchEvent、onInterceptTouchEvent、onTouchEvent
 *
 * 2. 父控件下包括子控件，点击子控件时的正常回调顺序：
 *  dispatchTouchEvent(父) -> onInterceptTouchEvent(父) -> onTouchEvent(子)
 *
 * 3. 某个点击事件(MotionEvent)想要在"父控件中处理"：
 *  在父控件的onInterceptTouchEvent中，return true(false的意思为 传递给子控件处理)
 *
 * 4. 某个点击事件(MotionEvent)想要在"子控件中处理"：
 *  在子控的onTouchEvent中，设置requestDisallowInterceptTouchEvent
 *
 * 5. 若第3、第4同时设置，则优先被子控件处理。因为：（ViewGroup.java中:）
 *  final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
 *  if (!disallowIntercept) {
 *      intercepted = onInterceptTouchEvent(ev);
 *      ev.setAction(action); // restore action in case it was changed
 *  } else {
 *      intercepted = false;
 *  }
 *
 *  6. 布局嵌套的几个注意点：
 *      1. dispatchTouchEvent、onInterceptTouchEvent循环调用
 *
 *      2. 在本层ViewGroup拦截onInterceptTouchEvent，实际不一定在本层处理，onTouchEvent可以向上传递
 *
 *  7. 如果ACTION_DOWN操作被消费，那么本层的View还会去响应ACTION_MOVE和ACTION_UP操作
 *     如果没有进行消费，那么就会返回信息，并且不会执行ACTION_MOVE和ACTION_UP操作
 *
 *  参考：
 *  dispatchTouchEvent和OnInterceptTouchEvent和OnTouchEvent三个方法之间的联系：
 *  https://www.cnblogs.com/RGogoing/p/4657722.html
 *
 * Created by wenbin.liu on 2019/5/11
 *
 * @author wenbin.liu
 */
public class MyLinearLayout extends LinearLayout {

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v("aa", this.getContentDescription() + " MyLinearLayout onInterceptTouchEvent ev = [" + ev + "]");
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                // (默认)表示子View需要，接下来走到子View的onTouchEvent
//                return false;
            case MotionEvent.ACTION_MOVE:
                if (this.getContentDescription().equals("内层布局")) {
                    // 拦截内层布局的事件，验证"onTouchEvent"可以向上传递，即"6.2"
                    return true;
                }
                // 表示父View需要，接下来走到父View的onTouchEvent
//                return true;
                /*
                * 注意：
                * 这里被拦截后，给了子控件一个"ACTION_CANCEL"
                * MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, ]
                  MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_MOVE, ]
                  MyButton onTouchEvent event = [MotionEvent { action=ACTION_CANCEL ]
                * */
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("aa", this.getContentDescription() + " MyLinearLayout onTouchEvent event = [" + event + "]");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 返回true，表示事件在本层消费了，那么本层还会去响应ACTION_MOVE和ACTION_UP操作；
            // 否则不会响应后续操作, 验证"第7"
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.v("aa", this.getContentDescription() + " MyLinearLayout dispatchTouchEvent ev = [" + ev + "]");
        return super.dispatchTouchEvent(ev);
    }
}
