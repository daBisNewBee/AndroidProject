package koal.glide_demo.third.one;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import koal.glide_demo.R;

// https://www.runoob.com/w3cnote/android-tutorial-viewpager.html  ViewPager的简单使用
public class OneActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ArrayList<View> mViewArrayList;
    private MyPagerAdapter mMyPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        mViewPager = findViewById(R.id.vpager_one);

        mViewArrayList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            LayoutInflater li = getLayoutInflater();
            mViewArrayList.add(li.inflate(R.layout.view_one_layout, null, false));
            mViewArrayList.add(li.inflate(R.layout.view_two_layout, null, false));
            mViewArrayList.add(li.inflate(R.layout.view_three_layout, null, false));
        }
        mMyPagerAdapter = new MyPagerAdapter(mViewArrayList);
        mViewPager.setAdapter(mMyPagerAdapter);

    }
}
