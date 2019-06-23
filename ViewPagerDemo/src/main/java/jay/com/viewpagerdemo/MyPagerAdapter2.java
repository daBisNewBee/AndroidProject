package jay.com.viewpagerdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Jay on 2015/10/8 0008.
 */
public class MyPagerAdapter2 extends PagerAdapter {
    private ArrayList<View> viewLists;
    private ArrayList<String> titleLists;
    private Context context;

    public MyPagerAdapter2() {}
    public MyPagerAdapter2(ArrayList<View> viewLists,ArrayList<String> titleLists, Context ctx)
    {
        this.viewLists = viewLists;
        this.titleLists = titleLists;
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewLists.get(position));
        return viewLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));
    }

    // 有"tab"、"title"时，getPageTitle 的覆写不能少！！
    @Override
    public CharSequence getPageTitle(int position) {
        // 在Tab标题前加了一个图片
        SpannableStringBuilder ssb = new SpannableStringBuilder("  "+titleLists.get(position)); // space added before text
        // for
        Drawable myDrawable = context.getResources().getDrawable(
                R.drawable.ic_launcher);
        myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(),
                myDrawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(myDrawable,
                ImageSpan.ALIGN_BASELINE);

        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.RED);// 字体颜色设置为红色
        ssb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);// 设置图标
        ssb.setSpan(fcs, 1, ssb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);// 设置字体颜色
        ssb.setSpan(new RelativeSizeSpan(1.2f), 1, ssb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
        // 直接返回文字
//        return titleLists.get(position);
    }
}
