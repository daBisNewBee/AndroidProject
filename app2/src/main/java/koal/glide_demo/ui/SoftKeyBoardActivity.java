package koal.glide_demo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import koal.glide_demo.R;

/**
 *
 * 对动画Animation的几个认识：
 *
 * 1. 分为：补间动画和属性动画
 *
 * 2. 补间动画
 *    TranslateAnimation
 *    缺点：仅改变视觉效果，未真正改变View的属性
 *
 * 3. 属性动画 (Android 3.0 之后引入)
 *    ObjectAnimator / ValueAnimator
 *    可以改变View的属性
 *    ps：操作的属性需要有 set 和 get 方法，否则无效。
 *    比如，translationX 属性设置方法接受的是 float 值(因为有"public float getTranslationX()"方法)，设置成ofInt无效
 *
 * 4. ObjectAnimator / ValueAnimator 区别：
 *    (优先考虑)ObjectAnimator： 先改变值，然后 "自动赋值" 给对象的属性从而实现动画，更加智能、自动化程度更高。直接操作属性
 *    ValueAnimator：  先改变值，然后 手动赋值 给对象的属性从而实现动画，本质上是一种 改变值 的操作机制。间接操作属性
 *
 * 5. 原理区别：
 *    补间动画：
 *      在startAnimation中调用了invalidate(),导致了 View 执行 onDraw() 方法.
 *      核心本质就是在一定的持续时间内，不断改变 Matrix 变换，并且不断刷新的过程。
 *
 *      具体一点：
 *      当view调用了 View.startAnimation() 时动画并没有马上就执行，会触发遍历view树的绘制，
 *      调用到 View 的 draw() 方法，如果 View 有绑定动画，那么会去调用applyLegacyAnimation()，
 *      内部调用 getTransformation() 来根据当前时间计算动画进度，紧接着调用 applyTransformation()
 *      并传入动画进度来应用动画。getTransformation() 会返回动画是否执行完成的状态， applyLegacyAnimation()
 *      会根据 getTransformation() 的返回值来决定是否通知 ViewRootImpl 再发起一次遍历请求，遍历 View 树绘制，
 *      重复上面的步骤，直到动画结束。
 *
 *      补间动画的绘制实际上是父布局不停地改变自己的Canvas坐标，而子view虽然位置没有变化，
 *      但是画布所在Canvas的坐标发生了变化视觉效果也就发生了变化，其实并没有修改任何属性，
 *      所以只能在原位置才能处理触摸事件。
 *
 *
 *      链接：https://www.jianshu.com/p/26ce9078fef4
 *
 *      视图动画的局限性：
 *
 *     1. 只能作用于view。 但有时需求不是对于整个view的，而只是对view的某个属性的，例如颜色的变化，也无法对非View的对象进行动画处理。
 *
 *     2. 没有改变view的属性。只改变了view的视觉效果而已，只是修改了视图绘制的地方，而没有改变视图的本身。
 *
 *     3. 动画效果固定有限。动画类型只有四种，缩放，平移，旋转，透明度的基本动画，无法对其他属性进行操作。
 *
 *     4. 动画虽然可以添加监听，但是动画开始后无法对动画的执行过程进行控制。
 *
 * 链接：https://www.jianshu.com/p/f8b82950e351
 *
 *
 *    属性动画：
 *      在一定的时间间隔内，通过不断地对值进行改变，并不断将该值赋给对象的属性，从而实现该对象在属性上的动画效果。
 *
 * 参考：谈谈属性动画和补间动画的原理及区别：
 * https://www.jianshu.com/p/5d0899dca46e
 *
 * 软键盘用法
 *
 * 对应：
 * android:windowSoftInputMode =
 */
public class SoftKeyBoardActivity extends AppCompatActivity {

    private View mContentView;
    private View mSecView;
    private ImageView mIvTarget;
    private FrameLayout mCompleteFl;

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
        mSecView = findViewById(R.id.textview_sec);
        mIvTarget = findViewById(R.id.iv_icon_animator);
        mIvTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 考虑：TranslateAnimation后点击，为何不会被回调？
                Log.d("todo", "mIvTarget onClick() called with: v = [" + v + "]");
            }
        });
        mCompleteFl = findViewById(R.id.fl_complete);
        mCompleteFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        testAnimator();
//                testTranslateAnimation();
                testObjectAnimator();
            }
        });
    }

    private void testTranslateAnimation() {
        /*
        * 该方法动画只是在onDraw方法中重新绘制，只是视图的移动，布局还在原来位置。(Inspector可以验证)
        * */
        TranslateAnimation trans = new TranslateAnimation(0, 200,0,0);
        trans.setDuration(1000);
        trans.setFillAfter(true); // 设置保留动画后的状态
        mIvTarget.startAnimation(trans);
    }

    /**
     * ObjectAnimator能力：
     * 轻松实现平移、缩放、旋转、透明度
     *
     * 但是不同于TranslateAnimation，包括视图移动和布局移动！
     *
     * 缺点：
     * 此类需要不停的在onDraw方法进行绘制，特别浪费手机的GPU资源，我们该如何优化了，这时候，另一个类的出现就在某些方面替代了ObjectAnimator
     */
    private void testObjectAnimator() {
        // 平移
//        ObjectAnimator animator1 = ObjectAnimator.ofInt(mIvTarget, "translationX",  0, 200); // 为什么这个不行？考虑下
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mIvTarget, "translationX",  0f, 200f);
        // 旋转
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvTarget, "rotation",  0f, 360f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvTarget, "translationY",  0f, 200f);
        // 缩放倍数
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mIvTarget, "scaleX",  1f, 2f);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mIvTarget, "scaleY",  1f, 2f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mIvTarget, "alpha",  0f, 1f);
//        animator1.setDuration(1000).start();
//        animator2.setDuration(1000).start(); // 旋转360度
//        animator3.setDuration(1000).start();

        // 复合动画
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator1, animator2, animator3, animator4, animator5, animator6);   // 同时运行
//        set.playSequentially(animator1 ,animator2, animator3, animator4); // 指定运行先后顺序
        set.setDuration(1000);
        set.start();
    }

    // 两秒渐渐显示效果
    private void testAnimator() {
        mSecView.animate().setDuration(1000).scaleX(2.0f);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(mSecView, "alpha", 0f, 1f);
        alpha.setDuration(1000);
        // 亮点：可以只覆写其中某个方法，而不用全部实现，可以借鉴！
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("todo", "onAnimationEnd() called with: animation = [" + animation + "]");
            }
        });
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("todo", "onAnimationStart() called with: animation = [" + animation + "]");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("todo", "onAnimationEnd() called with: animation = [" + animation + "]");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("todo", "onAnimationCancel() called with: animation = [" + animation + "]");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d("todo", "onAnimationRepeat() called with: animation = [" + animation + "]");
            }
        });
        alpha.start();
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
