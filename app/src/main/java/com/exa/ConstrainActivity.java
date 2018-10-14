package com.exa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 *
 * Android新特性介绍，ConstraintLayout完全解析：
 * https://blog.csdn.net/guolin_blog/article/details/53122387
 *
 * 解析ConstraintLayout的性能优势：
 * https://mp.weixin.qq.com/s/gGR2itbY7hh9fo61SxaMQQ
 *
 * A. 基本操作：
 *
 * 添加约束：
 *  1. Button位于布局的右下角
 *  2. 一个Button，让它位于第一个Button的正下方，并且间距64dp
 *  3. Baseline基线对齐。适用于文本型控件，比如TextView，Button这些
 *  4. 还有一个是链，就是两个控件建立了双向约束
 *      右键Chain可以建立水平或者竖直方向上的链条
 *
 * 删除约束：
 *  1. 删除一个约束
 *  2. 删除一个控件的所有约束
 *  3. 删除界面中所有的约束
 *
 * B. 进阶：
 *
 *  Inspector:
 *  1. 纵横轴比例改变空间位置
 *  2. 空间大小：
 *     wrap content
 *     fix：固定值
 *     any size（match constraint）
 *          注意：
 *          match parent是用于填充满当前控件的父布局
 *          any size是用于填充满当前控件的约束规则
 *
 *  Guidelines:
 *   1. 解决"两个按钮共同居中对齐"的问题
 *
 *  自动添加约束:
 *  1. Autoconnect:根据我们拖放控件的状态自动判断应该如何添加约束(缺点：只能给当前操作的控件)
 *  2. Inference:会给当前界面中的所有元素自动添加约束
 *
 *
 *
 */
@Route(path = "/constrain/activity")
public class ConstrainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constrain);
    }
}
