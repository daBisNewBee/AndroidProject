package com.exa.listview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.exa.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 与ListView的区别：
 *
 * 1. 缓存的对象不同。
 * ListView：view
 * RecyclerView：RecyclerView.ViewHolder
 *      View+ViewHolder(避免每次createView时调用findViewById) + flag(标示状态);
 * 1.1 ViewHolder的编写"规范化"，
 *  ListView是需要自己定义的，
 *  RecyclerView是规范好的
 *
 * 2. （最大区别）数据源改变时的处理
 * ListView："一锅端"，将所有的mActiveViews都移入了二级缓存mScrapViews.
 * RecyclerView：更加灵活地对每个View修改标志位，区分"是否重新bindview".
 *
 * 3. 缓存原理类似，但是层级不同
 * ListView：两级缓存
 * RecyclerView：四级缓存
 *
 * 4. 布局效果:
 * ListView:只有纵向，较单一
 * RecyclerView：线性布局（纵向，横向），表格布局，瀑布流布局（得益于LayoutMananger）
 *
 * 5. HeaderView 与 FooterView：
 * ListView:可以通过addHeaderView() 与 addFooterView()来添加头部item与底部item，
 *          来当我们需要实现下拉刷新或者上拉加载的情况；而且这两个API不会影响Adapter的编写；
 * RecyclerView:并没有这两个API，所以当我们需要在RecyclerView添加头部item或者底部item的时候，
 *              我们可以在Adapter中自己编写，根据ViewHolder的Type与View来实现自己的Header，
 *              Footter与普通的item，但是这样就会影响到Adapter的数据，比如position，添加了
 *              Header与Footter后，实际的position将大于数据的position；
 *
 * 6. 局部刷新
 * ListView:默认全局。（也可以实现局部：实现Adapter中的onItemChanged）
 * RecyclerView：局部。notifyItemChanged
 *
 * 7. 动画效果：
 * ListView：不支持，需要在Adater中自己实现
 * RecyclerView：默认支持
 *
 * 8. 嵌套滚动机制：（即：子View与父View同时处理这个Touch事件）
 * 没有。
 * 有
 *
 * 参考：ListView 与 RecyclerView 简单对比
 * https://blog.csdn.net/shu_lance/article/details/79566189
 *
 * 结论：
 * 1. 数据源在频繁的变更,这种情况下,RecyclerView的优势就会很明显.
 * 2. 列表展示的界面,需要支持动画或者频繁的更新,局部刷新,建议使用RecyclerView
 * 3. ListView在使用上面更加方便
 *
 * 源码：
 *
 * 缓存机制入口：
 * RecyclerView.getViewForPosition
 *
 */
@Route(path = "/recyclerview/activity")
public class RecyclerViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //实例化瀑布流布局管理器
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        List<String> dataStrList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            dataStrList.add("item "+i);
        }

        MyRecycleAdapter adapter = new MyRecycleAdapter(dataStrList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.ViewHolder>{

        List<String> dataList;

        //绑定传入的数据源
        public MyRecycleAdapter(List<String> _dataList) {
            this.dataList = _dataList;
        }

        /**
         * 这里构造itemView，并关联到ViewHolder，返回VH
         *
         * 区别于ListView中的getview处理：
         *  返回的是itemView，并且刷新itemView中的数据
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null,false);
            // 设置root为 parent 会造成itemView的布局过大

            final ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    String getData = dataList.get(pos);
                    Toast.makeText(v.getContext(), "你点击了:"+getData, Toast.LENGTH_SHORT).show();
                }
            });

            return holder;
        }

        /**
         *
         * 这里取出ViewHolder中的ItemView，并设置数据
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            View itemView;
            TextView textView;

            public ViewHolder(View _itemView) {
                super(_itemView);
                itemView = _itemView;
                textView = itemView.findViewById(R.id.text);
            }
        }

    }
}
