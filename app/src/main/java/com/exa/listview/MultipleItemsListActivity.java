package com.exa.listview;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.exa.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * 1. ListView 的缓存机制，几个要点：
 * （为什么ListView可以加载大量的Item也不会OOM ？）
 *
     * 1. 复用的其实是 "convertView"(防止多次layout带来的性能即数据重复问题而存在的)
     * 2. ViewHolder的作用是 免去了"convertView"中的findViewById过程
 *
 * 2. 缓存过程：
 * 当item view 在屏幕之外，RecycleBin将其缓存起来，当需要新的itemView，
 * 除非RecycleBin中没有缓存，否则就直接从缓存中取，还记得在getView中有一
 * 个参数convertView就是从RecycleBin中取出来的。
 *
 * 3. RecycleBin (AbsListView缓存机制的核心类,管理AbsListView的item存储和取得)的两个级别存储:
 * ActiveViews：存储的是第一次显示在屏幕上的View；所有的ActiveViews最终都会被移到ScrapViews，
 * ScrapViews：存储的是有可能被adapter复用的View。
 *
 * 4. 在ListView的"LayoutChildren"(ActiveViews、ScrapViews的转换)
     * 1. dataChanged = true，recycleBin.addScrapView
     * 2. dataChanged = false, recycleBin.fillActiveViews
 *
 * 5. 那么咱们关键的getView方法到底是在哪调用呢
    在AbsListView中我们看obtainView中的方法：
 *
 *
 */
@Route(path = "/listview/activity")
public class MultipleItemsListActivity extends ListActivity {

    /*
    *
    * ListView的优化:
    * 参考：
    * https://www.jianshu.com/p/f0408a0f0610
    *
    * 0. convertView的复用
    *
    * 1. 使用ViewHolder模式来提高效率(避免findViewById，提升50%效率)
    *
    * 2. 异步加载：耗时的操作放在异步线程中
    *
    * 3. listView错位加载问题：一张图片在多个位置显示。
    *   原因：重用了 convertView 且有异步操作。多个异步获取的图片指向同一块内存
    *   解决：设置标志位
    *
    * 4. 为图片设置缓存
    * LRUCache
    *
    * 5. ListView的滑动时停止加载和分页加载
    * 在下载滑动停止时 ，从start到end之间的item图片
    *
    *
    * */
    private MyCustomAdapter mAdapter;

    // 如何统计ListView加载速度?
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        private int previousFirstVisibleItem = 0;
        private long previousEventTime = 0;
        private double speed = 0;
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (previousFirstVisibleItem != firstVisibleItem){
                long currTime = System.currentTimeMillis();
                long timeToScrollOneElement = currTime - previousEventTime;
                System.out.println("cost:"+timeToScrollOneElement);
                speed = ((double)1/timeToScrollOneElement)*1000;
                previousFirstVisibleItem = firstVisibleItem;
                previousEventTime = currTime;
                System.out.println("Speed: " +speed + " elements/second");
            }
        }
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyCustomAdapter();
        for (int i = 0; i < 50; i++) {
            mAdapter.addItem("item " + i);
            if (i % 4 == 0){
                mAdapter.addSeparatorItem("separator "+i);
            }
        }
        setListAdapter(mAdapter);
        getListView().setOnScrollListener(onScrollListener);
    }

    private class MyCustomAdapter extends BaseAdapter{

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private List<String> mData = new ArrayList();
        private LayoutInflater mInflater;

        private Set<Integer> mSeparatorsSet = new TreeSet<>();

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item){
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item){
            mData.add(item);
            mSeparatorsSet.add(mData.size()-1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         *
         *1. 当你有一亿个条目的时候， 只有可看见的View保存在内存中+Recycle过的View
          2. 当ListView第一次向适配器请求一个VIew的时候，convertView为null，因此需要新建一个convertView.
          3. 当ListView请求一个条目item1的VIew，并且item1已经超出屏幕之外，并进来一个相同类型的条目从底部
            进入到屏幕里面，这时convertVIew 不为null，而是等于item1。 你只需要获取新的数据装载到该View里面并
            返回回去。而不必要重新创建一个新的VIew
         *
         * 10-13 13:43:38.676  1874  1874 I System.out: taskAffinity:Main.task
         10-13 13:43:43.891  1874  1874 I System.out: MyCustomAdapter.getView：position：0 convertView:null
         10-13 13:43:43.897  1874  1874 I System.out: MyCustomAdapter.getView：position：1 convertView:null
         10-13 13:43:43.901  1874  1874 I System.out: MyCustomAdapter.getView：position：2 convertView:null
         10-13 13:43:43.906  1874  1874 I System.out: MyCustomAdapter.getView：position：3 convertView:null
         10-13 13:43:43.916  1874  1874 I System.out: MyCustomAdapter.getView：position：4 convertView:null
         10-13 13:43:43.921  1874  1874 I System.out: MyCustomAdapter.getView：position：5 convertView:null
         10-13 13:43:43.925  1874  1874 I System.out: MyCustomAdapter.getView：position：6 convertView:null
         10-13 13:43:43.932  1874  1874 I System.out: MyCustomAdapter.getView：position：7 convertView:null
         10-13 13:43:43.938  1874  1874 I System.out: MyCustomAdapter.getView：position：8 convertView:null
         10-13 13:43:43.943  1874  1874 I System.out: MyCustomAdapter.getView：position：9 convertView:null
         10-13 13:43:43.945  1874  1874 I System.out: MyCustomAdapter.getView：position：10 convertView:null
         10-13 13:43:43.953  1874  1874 I System.out: MyCustomAdapter.getView：position：11 convertView:null
         10-13 13:43:43.959  1874  1874 I System.out: MyCustomAdapter.getView：position：12 convertView:null
         10-13 13:43:43.971  1874  1874 I System.out: MyCustomAdapter.getView：position：13 convertView:null
         10-13 13:43:53.312  1874  1874 I System.out: MyCustomAdapter.getView：position：14 convertView:android.widget.LinearLayout{d4ec5b4 V.E...... ........ 0,0-1080,129}
         10-13 13:43:53.315  1874  1874 I System.out: MyCustomAdapter.getView：position：15 convertView:android.widget.LinearLayout{7ff35dd V.E...... ........ 0,-36-1080,93}
         10-13 13:43:53.368  1874  1874 I System.out: MyCustomAdapter.getView：position：16 convertView:android.widget.LinearLayout{17552 V.E...
         *
         * @param position
         * @param convertView 其实是从RecycleBin中取出来的
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            System.out.println("MyCustomAdapter.getView：position：" + position+" convertView:" + convertView +" type:"+type);
            ViewHolder holder = null;
            if (convertView == null){
                holder = new ViewHolder();
                switch (type){
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout
                                .list_item,null);
                        holder.textView = convertView.findViewById(R.id.text);
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.list_item_sep, null);
                        holder.textView = convertView.findViewById(R.id.tv_sep);
                        holder.textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    default:
                        break;
                }
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(mData.get(position));
            return convertView;
        }
    }

    public static class ViewHolder{
        public TextView textView;
    }
}
