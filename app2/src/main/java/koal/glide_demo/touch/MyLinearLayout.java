package koal.glide_demo.touch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

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


    /**
     * 正常，不做任何拦截：
     *
     * 外外 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, act
     * 外外 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN,
     * 内内 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, act
     * 内内 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN,
     * MyTextView dispatchTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionB
     * MyTextView onTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionButton=
     * 内内 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_DOWN, action
     * 外外 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_DOWN, action
     * fixme: 注意没有后续move、up事件了！因为都没消费，就不会再传到这里来
     *
     * @param context
     * @param attrs
     */

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 在这里拦截"ACTION_DOWN"：
     *
     * 外外 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, ac
     * 外外 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN,
     * 内内 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, ac
     * 内内 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN,
     *  // fixme:不会再给MyTextView了
     * 内内 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_DOWN, actio
     * 外外 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_DOWN, actio
     * fixme: 注意没有后续move、up事件了！因为都没消费，就不会再传到这里来
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v("aa", this.getContentDescription() + " MyLinearLayout onInterceptTouchEvent ev = [" + ev + "]");
        if (ev.getAction() == MotionEvent.ACTION_DOWN
                && this.getContentDescription().equals("内内")) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     *
     * 在这里拦截"ACTION_DOWN"：
     *
     * 外外 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, a
     * 外外 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN
     * 内内 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, a
     * 内内 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN
     * MyTextView dispatchTouchEvent event = [MotionEvent { action=ACTION_DOWN, actio // fixme:子view还能收到事件，说明并未终止DOWN事件的传递
     * MyTextView onTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionButto
     * 内内 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_DOWN, acti // fixme:在这里被消费，到此为止，不再往"外外"传
     * 外外 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, a
     * 外外 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_MOVE
     * 内内 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, a
     * 内内 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_MOVE, acti // fixme:后续"move、up"直接传到这里
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("aa", this.getContentDescription() + " MyLinearLayout onTouchEvent event = [" + event + "]");
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && this.getContentDescription().equals("内内")) {
//            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     *
     *  在这里拦截"ACTION_DOWN"：
     *
     * 外外 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, ac
     * 外外 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN,
     * 内内 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, ac // fixme:Down在这里被拦截了，不再往子view传，比较彻底
     * 外外 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, ac
     * 外外 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_MOVE,
     * 内内 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, ac // fixme:后续"move、up"直接传到这里
     * 内内 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_MOVE, actio
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.v("aa", this.getContentDescription() + " MyLinearLayout dispatchTouchEvent ev = [" + ev + "]");
        if (ev.getAction() == MotionEvent.ACTION_DOWN
                && this.getContentDescription().equals("内内")) {
//            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
