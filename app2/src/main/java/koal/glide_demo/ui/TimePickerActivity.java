package koal.glide_demo.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import koal.glide_demo.R;

public class TimePickerActivity extends Activity implements View.OnClickListener{

    private ViewGroup mRootView;
    private TextView mTvTarget;
    private Button mBtnTarget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_time_picker, null, false);
        setContentView(mRootView);

        mTvTarget = findViewById(R.id.tv_target);
        mBtnTarget = findViewById(R.id.btn_target);

        findViewById(R.id.btn_scroll_to).setOnClickListener(this::onClick);
        findViewById(R.id.btn_scroll_by).setOnClickListener(this::onClick);
        findViewById(R.id.btn_reset).setOnClickListener(this::onClick);

        NumberPickerView hourNumberPickerView  = findViewById(R.id.hour_number_picker);
        hourNumberPickerView.setDisplayedValues(new String[]{"1","2","3","4","5","6","7"});
        hourNumberPickerView.setMinValue(0);
        hourNumberPickerView.setMaxValue(6);
        hourNumberPickerView.setValue(5);

        NumberPickerView minNumberPickerView  = findViewById(R.id.minute_number_picker);
        minNumberPickerView.setDisplayedValues(new String[]{"1","2","3","4","5","6","7"});
        minNumberPickerView.setMinValue(0);
        minNumberPickerView.setMaxValue(6);
        minNumberPickerView.setValue(3);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Field fieldY0= NumberPickerView.class.getDeclaredField("dividerY0");
                    Field fieldY1 = NumberPickerView.class.getDeclaredField("dividerY1");

                    fieldY0.setAccessible(true);
                    fieldY1.setAccessible(true);
                    float valueY0 = (float)fieldY0.get(hourNumberPickerView);
                    float valueY1 = (float)fieldY1.get(hourNumberPickerView);
                    Log.d("todo", "valueY0 = " + valueY0);
                    Log.d("todo", "valueY1 = " + valueY1);
                    View view0 = new View(TimePickerActivity.this);
                    View view1 = new View(TimePickerActivity.this);
                    RelativeLayout.LayoutParams params0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    params0.topMargin=(int)valueY0;
                    view0.setBackgroundColor(Color.BLACK);
                    mRootView.addView(view0, params0);

                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    params1.topMargin=(int)valueY1;
                    view1.setBackgroundColor(Color.BLACK);
                    mRootView.addView(view1, params1);


                    TextView child = new TextView(TimePickerActivity.this);
                    child.setTextSize(20);
                    child.setTextColor(getResources().getColor(R.color.colorAccent));
                    // 获取当前的时间并转换为时间戳格式, 并设置给TextView
                    child.setText("hello");
                    // 调用一个参数的addView方法
                    mRootView.addView(child);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    /**         |正
     *          |
     *  (10,10) |
     *          | View 的中心
     * ---------|---------- x
     * 正       |          负
     *          |  (-10,-10)
     *          |
     *          |y 负
     *
     * 几个认识：
     * 1. scrollTo 绝对移动，scrollBy 相对移动
     * 2. 移动的只是View的内容，其位置不变，getX，getY不变
     * 3. 移动的中心，是View的中心
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        float x = mTvTarget.getX();
        float y = mTvTarget.getY();
        Log.d("test", "onClick: x:" + x + " y:" + y);
        switch (id) {
            case R.id.btn_scroll_to:
                mTvTarget.scrollTo(0,20);
                mBtnTarget.scrollTo(0,20);
                break;
            case R.id.btn_scroll_by:
                mTvTarget.scrollBy(-10,-10);
                mBtnTarget.scrollBy(10,10);
                break;
            case R.id.btn_reset:
                mTvTarget.scrollTo(0, 0);
                mBtnTarget.scrollTo(0, 0);
                break;
            default:
                break;
        }
    }
}
