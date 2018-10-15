package com.example.user.ndkdebug;

import org.junit.Test;

import java.util.Stack;

/**
 * Created by user on 2018/10/15.
 */

public class SortTest {

    @Test
    public void quickSort_Test() throws Exception {
        int[] data = new int[]{66,13,51,76,48,99,97,24,77};
        QuickSort(data, 0, data.length-1);
//        quick_Standard(data, 0, data.length-1);
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i]+" ");
        }
    }

    /**
     * 快速排序思路：
     * 1. 选取基准数
     * 2. 分区：头尾分别开始于基准数比较：
     *    左边比基准大的数字，放到右边；
     *    右边比基准小的数字，放到左边；
     * 3. 基准数归位。（核心：每一次遍历，基准数找到自己的位置！）
     * 4. 当前遍历一遍的结果：
     *    比基准小的数字都在左边，比基准大的数字都在右边
     * 5. 循环上述步骤
     *
     * 如何选择基准？
     *  采用正态分布，找到线性分布的那个中间点。
     *  因为没有规律的数据是可以看做正态分布的，也就是符合线性回归的。
     *
     * 参考：
     *  https://blog.csdn.net/vayne_xiao/article/details/53508973
     *
     * @param A
     * @param left
     * @param right
     */
    // 递归版本实现
    private static void quick_Standard(int[] A, int left, int right){
        if (left > right)
            return;
        int i = left;
        int j = right;

        int pivot = A[i];
        while (i != j){
            while (i<j && A[j] >= pivot)
                j--;
            while (i<j && A[i] <= pivot)
                i++;

            if (i<j){
                int tmp = A[i];
                A[i] = A[j];
                A[j] = tmp;
            }
        }
        // 此时选择i或者j都一样，i == j
        A[left] = A[i];
        A[i] = pivot;

        quick_Standard(A, left, i-1);
        quick_Standard(A, i+1, right);
    }

    public void QuickSort(int[] a,int low,int high){
        if(a==null || low<0 || high<0 || low>high )
            return ;
        Stack<Integer> s=new Stack<>();
        s.push(low);s.push(high);
        while(!s.empty()){
            int i=s.peek();
            int j=s.peek();
            if(i<j){
                int k=Partition(a,i,j);
                if(k>i){
                    s.push(i);
                    s.push(k-1);
                }else{
                    s.push(k+1);
                    s.push(j);
                }
            }
        }
    }

    private static int Partition(int[] a,int low,int high){
        if(a==null || low>high || low<0 || high<0)
            return -1;
        int i=low;int j=high;int point=a[low];
        while(i<j){
            while(i<j&&a[j]>=point)
                j--;
            if(i<j)
                a[i++]=a[j];a[j]=point;
            while(i<j&&a[i]<=point)
                i++;
            if(i<j)
                a[j--]=a[i];a[i]=point;
        }
        return i;
    }
}
