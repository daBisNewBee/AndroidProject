package jay.com.viewpagerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 *
 * TODO:
 * 1. DecorView 的结构
 * 2. SpannableStringBuilder 用法
 * 3. TranslateAnimation 用法
 *
 * 参考：
 * ViewPager的简单使用
 * https://www.runoob.com/w3cnote/android-tutorial-viewpager.html
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_one;
    private Button btn_two;
    private Button btn_three;
    private Button btn_four;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
        btn_four = (Button) findViewById(R.id.btn_four);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                startActivity(new Intent(this, OneActivity.class));
                break;
            case R.id.btn_two:
                startActivity(new Intent(this, TwoActivity.class));
                break;
            case R.id.btn_three:
                startActivity(new Intent(this, ThreeActivity.class));
                break;
            case R.id.btn_four:
                startActivity(new Intent(this, FourActivity.class));
                break;
        }
    }


}
