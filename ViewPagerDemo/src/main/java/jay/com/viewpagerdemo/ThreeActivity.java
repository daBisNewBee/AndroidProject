package jay.com.viewpagerdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

/**
 * PagerTabStrip效果演示
 *
 * 与"PagerTitleStrip"的区别：
 * 1. tab有下划线，title无
 * 2. tab文字点击可以切换页面，title无
 * 3. tab是"interactive"， title是"non-interactive"
 *
 * 相同点：
 * 1. tab、title都是ViewPager的子控件！
 *
 * Created by Jay on 2015/10/8 0008.
 */
public class ThreeActivity extends AppCompatActivity {

    private ViewPager vpager_three;
    private ArrayList<View> aList;
    private ArrayList<String> sList;
    private MyPagerAdapter2 mAdapter;

    private PagerTabStrip mPagerTabStrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        vpager_three = (ViewPager) findViewById(R.id.vpager_three);

        mPagerTabStrip = findViewById(R.id.pagertitle);
        // 十六进制颜色码转换成RGB颜色值
        // 更改下划线颜色
        mPagerTabStrip.setTabIndicatorColor(Color.parseColor("#7FFFAA"));

        aList = new ArrayList<View>();
        LayoutInflater li = getLayoutInflater();
        aList.add(li.inflate(R.layout.view_one, null, false));
        aList.add(li.inflate(R.layout.view_two, null, false));
        aList.add(li.inflate(R.layout.view_three, null, false));

        sList = new ArrayList<String>();
        sList.add("橘黄");
        sList.add("淡黄");
        sList.add("浅棕");

        mAdapter = new MyPagerAdapter2(aList, sList, this);
        vpager_three.setAdapter(mAdapter);
    }
}
