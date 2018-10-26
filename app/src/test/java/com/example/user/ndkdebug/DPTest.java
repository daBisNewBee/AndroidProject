package com.example.user.ndkdebug;

import org.junit.Test;

/*
*
算法：
    二分搜索 Binary Search
    分治 Divide Conquer
    宽度优先搜索 Breadth First Search
        Queue. offer\poll
    深度优先搜索 Depth First Search
        Stack. push\pop
    回溯法 Backtracking
    双指针 Two Pointers
    动态规划 Dynamic Programming
    扫描线 Scan-line algorithm
    快排 Quick Sort

数据结构：
    栈 Stack
    队列 Queue
    链表 Linked List
    数组 Array
    哈希表 Hash Table
    二叉树 Binary Tree
    堆 Heap
    并查集 Union Find
    字典树 Trie

方法：
    - 双指针遍历：
        字符串是否由字典中单词组成（代替排列组合的N！复杂度）
	- 空间换时间：
	    找到数组中和为0的组合。降维：4个数组 -> 2个数组（n^4 -> n^2）。将结果保存到map
	- 数据预处理：
	    判断回文单向链表。先**拆分**链表为子链表1、2，反向子链表2，获得后节点、前节点关系。依次判断

* */


/**
 *
 * 从10亿个数中找频率最高的1000个数：
 *   "分治思想"：
     通常比较好的方案是分治+Trie树/hash+小顶堆（就是上面提到的最小堆），
     即先将数据集按照Hash方法分解成多个小数据集，然后使用Trie树或者Hash
     统计每个小数据集中的query词频，之后用小顶堆求出每个数据集中出现频率
     最高的前K个数，最后在所有top K中求出最终的top K。
 *
 *
 * 递归：
 *      从已知问题出发（比如，求f(n)），用迭代表达式(比如，f(n) = f(n-1) + f(n-2))
 *      逐步推算出问题的开始条件(比如，f(1) = 1 , f(2) = 1)
 *
 * 递推：
 *      通过前面的一些项（比如，f(1),f(2),f(3)...）来得出序列中的指定项(比如，f(n))的值
 *
 * 递归和递推的区别：
 *    递推免除了数据进出栈的过程，
 *    即：不需要函数不断的向边界值靠拢，而是直接从边界值出发，直到求出函数值
 *    两个过程互为相反
 *
 * 迭代：（注意：最好使用迭代（循环）代替递归！
 *      原因：递归算法时间呈指数形式增长：O(2^N)；而使用循环迭代时间上呈线性增长：O(N)。
 * ）
 *    就是循环。
 *    注意：递归太深易造成栈溢出
 *
 * Created by user on 2018/10/18.
 */

public class DPTest {

    /*
    * 斐波那契数列：已知f(1) = 1 , f(2) = 1 , 且满足关系式
    * f(n) = f(n-1) + f(n-2)，则f(50)等于多少？
    *
    * 分析：
    * 必须找到下面两个条件！
    *
    * 递归关系：
    * f(n) = f(n-1) + f(n-2)
    * 递归出口：
    * f(1) = 1 , f(2) = 1
    * */

    private static int Fibonacci(int n){
        if (n == 1 || n == 2)
            return 1;
        else
            return Fibonacci(n-1) + Fibonacci(n-2);
    }

    static int[] g_array = new int[1000];

    /**
     *
     * 改进算法：
     *  解决重复计算的问题！
     * @param n
     * @return
     */
    private static int Fibonacci_impro(int n){
        if (n == 1 || n == 2)
            return 1;
        if (g_array[n] != 0)
            return g_array[n];
        g_array[n] = Fibonacci_impro(n-1) + Fibonacci_impro(n-2);
        return g_array[n];
    }

    public static int diTui(int n){
        int[] a = new int[n+1];
        a[1] = 1;
        a[2] = 1;

        for (int i = 3; i < n+1; i++) {
            a[i] = a[i-1] + a[i-2];
        }
        return a[n];
    }

    @Test
    public void Fibonacci_Test() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Fibonacci.diGui:"+Fibonacci_impro(40));
        long end = System.currentTimeMillis();
        System.out.println("cost:"+(end-start));

        start = System.currentTimeMillis();
        System.out.println("Fibonacci.diGui:"+Fibonacci(40));
        end = System.currentTimeMillis();
        System.out.println("cost:"+(end-start));

        start = System.currentTimeMillis();
        System.out.println("Fibonacci.diTui:"+diTui(40));
        end = System.currentTimeMillis();
        System.out.println("cost:"+(end-start));
    }

    @Test
    public void maxSubSum_Test() throws Exception {
        int[] array = new int[]{1, -2, 3, 10, -4, 7, 2, -5};
        System.out.println("max:"+MaxSubSum(array));

    }

    /**
     *
     * 求子数组的最大和
     题目描述：
     输入一个整形数组，数组里有正数也有负数。
     数组中连续的一个或多个整数组成一个子数组，每个子数组都有一个和。
     求所有子数组的和的最大值。要求时间复杂度为O(n)。

     例如输入的数组为1, -2, 3, 10, -4, 7, 2, -5，和最大的子数组为3, 10, -4, 7, 2，
     因此输出为该子数组的和18。
     *
     * 使用"动态规划"：(可以使用DP的问题特征：能够分解为子问题，具有最优子结构)
     * 1. 设sum[i]为以第i个元素结尾且和最大的连续子数组
     * 2. 那么以第i个元素结尾且和最大的连续子数组实际上，要么是以第i-1个元素结尾且和最大的连续子数组加上这个元素，
     *    要么是只包含第i个元素，即sum[i] = max(sum[i-1] + a[i], a[i])
     * 3. sum[i-1] + a[i] > a[i] ?
     * 4. sum[i-1] > 0 ?
     *
     * 时间复杂度：0(n)
     * @param A
     * @return
     */
    private static int MaxSubSum(int[] A){
        int thisSum = 0;
        int maxSum = 0;
        for (int i = 0; i < A.length; i++){
            if (thisSum > 0){
                thisSum += A[i];
            }else {
                thisSum = A[i];
            }
            if (thisSum > maxSum)
                maxSum = thisSum;
        }
        return maxSum;

    }
}
