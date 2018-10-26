package com.example.user.ndkdebug;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * SkipList 跳表
 * 由多层级单向有序链表组成。
 * 搜索，插入，删除的平均复杂度是O(logn)
 *
 * 为什么 o(logn)?
 *  TODO:
 *  SkipList时间复杂度分析O(log n):
 *  参考https://blog.csdn.net/yaling521/article/details/78130271
 *
 * Skip list的性质:

 (1) 由很多层结构组成，level是通过一定的概率随机产生的。
 (2) 每一层都是一个有序的链表，默认是升序
 (3) 最底层(Level 1)的链表包含所有元素。
 (4) 如果一个元素出现在Level i 的链表中，则它在Level i 之下的链表也都会出现。
 (5) 每个节点包含两个指针，一个指向同一链表中的下一个元素，一个指向下面一层的元素。
 *
 * 优点：
 * 它的效率甚至可以与红黑树等二叉平衡树相提并论，而且实现的难度要比红黑树简单多了
 *
 * 缺点：
 * 耗内存（改进：上层可以只存key不存数据）
 *
 * 应用：
 * Redis
 *
 * 数据结构的选择？跳表？红黑树？
 * 1. 单纯性能：
 *  跳表 与 红黑树 近似，都是o(logn)
 * 2. 并发下：
 *  跳表性能优于红黑树
 *  因为更新数据时，跳表需要更新的部分少，锁的区域较小，不同线程竞争锁的代价更小
 *  而红黑树有平衡（rebalance）的过程，牵涉到大量的节点，争锁的代价更高
 *
 *
 * Created by user on 2018/10/15.
 */

public class SkipListTest {
    @Test
    public void skipList_Test() throws Exception {
        Map<Integer, String> map = new ConcurrentSkipListMap<>();
        map.put(2,"22");
        map.put(20,"20");
        map.put(1,"1");
        map.put(5,"5");

        for (Integer one:map.keySet()){
            System.out.println(one + " " + map.get(one));
        }
        /*
         默认有序：
            1 1
            2 22
            5 5
            20 20
        * */

        Set<Integer> set = new ConcurrentSkipListSet<>();
        set.add(2);
        set.add(55);
        set.add(30);
        set.add(1);

        for (Integer one: set){
            System.out.println(one);
        }
        /*
        * 默认有序：
        *   1
            2
            30
            55
        * */
    }
}
