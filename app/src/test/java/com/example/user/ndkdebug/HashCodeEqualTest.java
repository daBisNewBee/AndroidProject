package com.example.user.ndkdebug;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;

/**
 *
 1.若重写了equals(Object obj)方法，则有必要重写hashCode()方法。

 2.若两个对象equals(Object obj)返回true，则hashCode（）有必要也返回相同的int数。

 3.若两个对象equals(Object obj)返回false，则hashCode（）不一定返回不同的int数。

 4.若两个对象hashCode（）返回相同int数，则equals（Object obj）不一定返回true。

 5.若两个对象hashCode（）返回不同int数，则equals（Object obj）一定返回false。

 6.同一对象在执行期间若已经存储在集合中，则不能修改影响hashCode值的相关信息，否则会导致内存泄露问题。

参考：从一道面试题彻底搞懂hashCode与equals的作用与区别及应当注意的细节：

 https://blog.csdn.net/lijiecao0226/article/details/24609559
 *
 * Created by user on 2018/10/3.
 */

public class HashCodeEqualTest {

    /*
    *  int add(int, int);
    Code:
       0: iload_1
       1: iload_2
       2: iadd
       3: istore_3
       4: iload_3
       5: ireturn
    * */
    int add(int a1, int a2){
        int a3 = a1 + a2;
        return a3;
    }

    /**
     *
     * 为何返回的是"100"，不是"150" ？
     *
     * 第二次 "b=150"操作的是locals表索引为1的entry，而实际返回到操作数栈顶的是locals表索引为2的entry，
     * 150已经赋值，只不过仍在locals表里。
     *
     int test();
     Code:
     0: bipush        20
     2: istore_1
     3: iinc          1, 80
     6: iload_1
     7: istore_2
     8: sipush        150
     11: istore_1
     12: iload_2
     13: ireturn // 关键一步：直接返回的是操作数栈顶元素，即：iload_2后的值，而iload_2的值是istore_2产生的，即100.
     14: astore_2
     15: bipush        50
     17: istore_1
     18: sipush        150
     21: istore_1
     22: goto          32
     25: astore_3
     26: sipush        150
     29: istore_1
     30: aload_3
     31: athrow
     32: sipush        200
     35: ireturn
     *
     * @return
     */
    int test(){
        int b = 20;
        try {
//            System.out.println("11111");
            b += 80;
            return b;
        }catch (Exception e){
            b = 50;
        }finally {
//            System.out.println("into finally.");
            b = 150;
//            return b;
        }
//        System.out.println("into end.");
        return 200;
    }

    @Test
    public void returnFinally_Test() throws Exception {
        System.out.println("call test:"+test());
    }

    @Test
    public void HashCode_Test() throws Exception {
        HashSet<Point> set = new HashSet();
        Point p1 = new Point(1,1);
//        Point p2 = new Point(1,1);
        Point p2 = new Point(1,2); // 验证内存泄漏

        System.out.println("p1 equals p2:" + p1.equals(p2));
        System.out.println("p1 hashCode = " + p1.hashCode());
        System.out.println("p2 hashCode = " + p2.hashCode());

        System.out.println(set.add(p1));
        System.out.println(set.add(p2));
        System.out.println(set.add(p1));

        /*
        * 内存泄漏：
        * "不要在执行期间修改与hashCode值有关的对象信息，
        * 如果非要修改，则必须先从集合中删除，更新信息后再加入集合中。"
        *
        * p2对象泄漏了！
        *   p1 equals p2:false
            p1 hashCode = 32
            p2 hashCode = 33
            true
            true
            false
            false // remove失败了！
            tmp = Point{x=1, y=1}
            tmp = Point{x=10, y=10}

          原因：在p2已经置入set期间，修改了x、y，而hashCode
          与x、y有关，因此remove时无法查到原有对象
        * */
        p2.setX(10);
        p2.setY(10);
        System.out.println(set.remove(p2));

        Iterator<Point> iterator = set.iterator();
        while (iterator.hasNext()){
            Point tmp = iterator.next();
            System.out.println("tmp = " + tmp);
        }
        /*
        * 1. 复写"equals",未复写"hashCode"的结果：
            p1 equals p2:true
            p1 hashCode = 1213415012
            p2 hashCode = 1688376486
            true
            true
            false
            tmp = Point{x=1, y=1}
            tmp = Point{x=1, y=1}
        *
        * 原因：
        *   p1、p2的hashCode值不同，hashCode未复写，使用默认的jdk产生值。
        *
        *
        * 2. 未复写"equals",复写"hashCode"的结果：
        *   p1 equals p2:false
            p1 hashCode = 32
            p2 hashCode = 32
            true
            true
            false
            tmp = Point{x=1, y=1}
            tmp = Point{x=1, y=1}
        *
        * 原因：
        *   p1、p2的hashCode相同，但是equals不同，默认使用"=="比较，因此jdk认为是不同的对象
        *
        *
        * 3. 因此，结论是：
        * 必须同时复写"hashCode"、"equals"方法！
        *
        * */

    }

    class Point{
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            return y == point.y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
}
