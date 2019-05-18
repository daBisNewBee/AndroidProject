package koal.glide_demo.ui.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import koal.glide_demo.R;

/**
 *
 * "从LayoutParams说起到代码动态布局"
 * 参考：
 *  https://www.jianshu.com/p/0d6f753fdd92
 *
 *
 */
public class DynamicActivity extends Activity {

    private ImageView mIvBlack;
    private TextView mTvBlue;
    private TextView mTvGreen;
    private ViewGroup.LayoutParams mVgLp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        mIvBlack = findViewById(R.id.mIvBlack);
        mTvBlue = findViewById(R.id.mTvBlue);
        mTvGreen = findViewById(R.id.mTvGreen);

        // 取出来的是父布局容器参数，即：RelativeLayoutParams
        mVgLp = mIvBlack.getLayoutParams();

        findViewById(R.id.mTvMoveRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams vg_lp = mIvBlack.getLayoutParams();
                // RelativeLayout.LayoutParams 不可抽取为成员变量
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(vg_lp);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.mIvBlack);
                /*
                * "利用setLayoutParams方法对控件的layout进行布局更新"
                * 设置的布局类型参数与控件所在父布局一致，但实际更新的是控件的属性（通过layout inpector可以查看到）
                * */
                mIvBlack.setLayoutParams(params); //使layout更新
            }
        });

        findViewById(R.id.mTvMoveLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 左 （默认处于顶部，所以看起来是左上）
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mVgLp);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                mIvBlack.setLayoutParams(params);
            }
        });

        findViewById(R.id.mTvCenter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 居中
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mVgLp);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mIvBlack.setLayoutParams(params);
            }
        });

        findViewById(R.id.mTvLeftVer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 左部垂直居中
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mVgLp);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT|RelativeLayout.CENTER_VERTICAL);
                mIvBlack.setLayoutParams(params);
            }
        });

        findViewById(R.id.mTvRightBot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 右下 。 以下两个方法效果不同！！因为获取params的方式不同！！
                // 1. 方法一：new 一个 LayoutParams，注意：不带控件的默认属性，比如：不带有mIvBlack的"layout_alignParentLeft"标志位
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mVgLp);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mIvBlack.setLayoutParams(params);

                // 2. 方法二：getLayoutParams 一个 LayoutParams，带有控件的默认属性
                ViewGroup.LayoutParams paramsGeted = mIvBlack.getLayoutParams();
                ((RelativeLayout.LayoutParams)paramsGeted).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                ((RelativeLayout.LayoutParams)paramsGeted).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                mIvBlack.setLayoutParams(paramsGeted);
                // 建议使用方法一！！因为方法二带有默认属性，展现的效果容易有歧义
            }
        });
        //  绿在蓝右边
        findViewById(R.id.mTvaGRightB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams vgLp = mTvGreen.getLayoutParams();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(vgLp);
                params.addRule(RelativeLayout.RIGHT_OF,R.id.mTvBlue);
                mTvGreen.setLayoutParams(params);
            }
        });

        // 绿在蓝下边
        findViewById(R.id.mTvGElowB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams vgLp = mTvGreen.getLayoutParams();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(vgLp);
                params.addRule(RelativeLayout.BELOW,R.id.mTvBlue);
                mTvGreen.setLayoutParams(params);
            }
        });
    }
}
