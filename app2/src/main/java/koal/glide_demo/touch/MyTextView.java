package koal.glide_demo.touch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 *
 * 事件传递的几个要点：
 *
 * 1. 事件传递方向？
 *    分发流程：由父ViewGroup 传递到 子ViewGroup/子View
 *    处理流程：相反，由下到上
 *
 * 2. 拦截对象？
 *    ACTION_DOWN
 *
 * 3. dispatchTouchEvent、onTouchEvent谁来拦截有什么区别？
 *    dispatchTouchEvent/onInterceptTouchEvent：
 *         终止ACTION_DOWN，不再往子view传递，后续move、up直接传到这里。比较彻底！
 *    onTouchEvent:
 *         终止ACTION_DOWN，不再往父的onTouchEvent传递，后续move、up直接传到这里
 *
 * 4. ACTION_MOVE、ACTION_UP的流向
 *    由ACTION_DOWN的拦截view来决定，被谁拦截了，就分发后续事件给谁
 *
 * 5. "onInterceptTouchEvent"：
 *      只是一个分流作用，不能消费事件，view是没有这个函数的
 *
 *  ViewGroup 先要走分发流程，再走处理流程
 *  View 只能走处理流程
 *s
 *
 * "图解 Android 事件分发机制"：
 * https://www.jianshu.com/p/e99b5e8bd67b/
 *
 * Created by wenbin.liu on 2021/11/29
 *
 * @author wenbin.liu
 */
public class MyTextView extends AppCompatTextView {

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.v("aa", "MyTextView dispatchTouchEvent event = [" + event + "]");
        return super.dispatchTouchEvent(event);
    }

    // 为什么没有"onInterceptTouchEvent"？
    // 由于View没有子View所以不需要onInterceptTouchEvent 来控件是否把事件传递给子View还是拦截

    /**
     *
     * 未对"ACTION_DOWN"任何拦截时：
     * 一层布局 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, actionButton=0, i
     * 一层布局 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN, actionButton=0
     * MyTextView dispatchTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionButton=0, id[0]=0
     * MyTextView onTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionButton=0, id[0]=0, x[0]
     * 一层布局 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionButton=0, id[0  // fixme:注意这里，由于MyTextView没有消费"ACTION_DOWN"，导致该事件又传了上去，且后面的"move、up"就不再传下来了，由上层处理了
     * 一层布局 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, actionButton=0, i
     * 一层布局 MyLinearLayout onTouchEvent event = [MotionEvent { action=ACTION_MOVE, actionButton=0, id[0
     * 一层布局 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, actionButton=0, i
     *
     * 对"ACTION_DOWN"拦截时：
     * 一层布局 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_DOWN, ac
     * 一层布局 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_DOWN,
     * MyTextView dispatchTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionBu
     * MyTextView onTouchEvent event = [MotionEvent { action=ACTION_DOWN, actionButton=0 // fixme:由于return true."ACTION_DOWN"就被消费了，不再向上回传
     * 一层布局 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, ac
     * 一层布局 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_MOVE,
     * MyTextView dispatchTouchEvent event = [MotionEvent { action=ACTION_MOVE, actionBu // fixme:且后面持续收到"move、up"事件
     * MyTextView onTouchEvent event = [MotionEvent { action=ACTION_MOVE, actionButton=0
     * 一层布局 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, ac
     * 一层布局 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_MOVE,
     * MyTextView dispatchTouchEvent event = [MotionEvent { action=ACTION_MOVE, actionBu
     * MyTextView onTouchEvent event = [MotionEvent { action=ACTION_MOVE, actionButton=0
     * 一层布局 MyLinearLayout dispatchTouchEvent ev = [MotionEvent { action=ACTION_MOVE, ac
     * 一层布局 MyLinearLayout onInterceptTouchEvent ev = [MotionEvent { action=ACTION_MOVE,
     *
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("aa", "MyTextView onTouchEvent event = [" + event + "]");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            return true;
        }
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
        return super.onTouchEvent(event);
    }
}
