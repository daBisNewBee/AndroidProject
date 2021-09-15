package koal.glide_demo.third.one;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

/**
 * Created by wenbin.liu on 2019-06-17
 *
 * @author wenbin.liu
 */
public class MyPagerAdapter extends PagerAdapter {

    private ArrayList<View> mViewsList;

    public MyPagerAdapter() {
    }

    public MyPagerAdapter(ArrayList<View> viewsList) {
        super();
        this.mViewsList = viewsList;
    }

    @Override
    public int getCount() {
        return mViewsList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.v("todo", "instantiateItem container = [" + container + "], position = [" + position + "]");
        View view = mViewsList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.v("todo", "destroyItem container = [" + container + "], position = [" + position + "], object = [" + object + "]");
        container.removeView(mViewsList.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
