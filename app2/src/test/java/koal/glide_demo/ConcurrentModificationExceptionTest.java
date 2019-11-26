package koal.glide_demo;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by wenbin.liu on 2019-11-26
 *
 * @author wenbin.liu
 */
public class ConcurrentModificationExceptionTest {
    @Test
    public void basic_wrong() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
//        for (String s : list) {
//            if ("3".equals(s)) {
//                list.remove(s);
//            }
//        }
        // 注意！！！上述代码的实际反编译结果:
        Iterator var2 = list.iterator();
        while(var2.hasNext()) {
            printMountCount(list);
            String s = (String)var2.next();
            System.out.println("next = " + s);
            if ("2".equals(s)) {
                list.remove(s);
            }
            /*
            * 几个原因：
            * 1. if ("1".equals(s)) .... 可以通过：
            *   实际遍历一遍就结束了。因为，
            *    public boolean hasNext() {
                    return this.cursor != ArrayList.this.size;
                 }
                不成立。cursor=1，size=1
            *
            * 2. if ("2".equals(s)) .... 失败：
            * 在第二次的next报错：
            *   java.util.ConcurrentModificationException
	            at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:903)
	            at java.util.ArrayList$Itr.next(ArrayList.java:853)
            * 在上一次remove后，modCount为3，而expectedModCount为2
            * */
            printMountCount(list);
        }
        System.out.println("END.");
    }

    @Test
    public void basic_correct() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        Iterator var2 = list.iterator();
        while(var2.hasNext()) {
            printMountCount(list);
            String s = (String)var2.next();
            System.out.println("next = " + s);
            if ("2".equals(s)) {
                var2.remove(); // 注意！这里一定从Iterator 执行remove！才能更新里面的"expectedModCount"
            }
            printMountCount(list);
        }
        System.out.println("END.");
    }

    /**
     *
     * (ArrayList 的情况下)(如果是LinkedList，则foreach/Iterator的效率远大于fori)
     * fori cost = 34ms
     * foreach cost = 34ms
     * Iterator cost = 31ms
     *
     * 1. 速度差不多，都在一个数量级
     * 2. Iterator 稍快于 foreach，猜测foreach隐式转换成Iterator所消耗的时间
     *
     * 结论：
     * 1. ArrayList下，三种遍历都差不多
     * 2. LinkedList下，优先"Iterator"
     *
     * TODO: 未能验证"fori"的速度最快！
     */
    @Test
    public void speed_Test() {
        final int SIZE = 100 * 100 * 100 * 30;
        List<Integer> list  = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            list.add(i, i);
        }
        long start = System.currentTimeMillis();
        int j = 0;
        for (int i = 0; i < list.size(); i++) {
              j = list.get(i);
        }
        long end = System.currentTimeMillis();
        System.out.println("fori cost = " + (end - start) + "ms");

        j = 0;
        start = System.currentTimeMillis();
        for (Integer integer : list) {
              j = integer;
        }
        end = System.currentTimeMillis();
        System.out.println("foreach cost = " + (end - start) + "ms");

        j = 0;
        start = System.currentTimeMillis();
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
              j = iterator.next();
        }
        end = System.currentTimeMillis();
        System.out.println("Iterator cost = " + (end - start) + "ms");
    }

    private void printMountCount(ArrayList list) throws Exception {
        Field field = ArrayList.class.getSuperclass().getDeclaredField("modCount");
        field.setAccessible(true);
        int modCount = (Integer) field.get(list);
        System.out.println("modCount = " + modCount);
    }

    boolean isStop;
    @Test
    public void main_test() throws Exception {
//        final Set<Integer> set = new HashSet<Integer>();
        final Set<Integer> set = Collections.synchronizedSet(new HashSet<Integer>());

        //开启A线程向set中放数据
        new Thread(new Runnable() {

            @Override
            public void run() {
                int i;
                while (true) {
                    try {
                        i = new Random().nextInt(1000);
//                        System.out.println("准备加入到Set...");
                        set.add(i);
//                        System.out.println("加入到Set:" + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ADD Exception:" + e.getMessage());
                        isStop = true;
                        break;
                    }
                }
                System.exit(0);
            }
        }, "A").start();

        //开启B线程删除set中的数据
        new Thread(new Runnable() {

            @Override
            public void run() {
                Iterator<Integer> iter = null;
                while (true) {
                    synchronized (set) {
                        try {
                            iter = set.iterator();
//                            System.out.println("开始迭代..");
                            int i;
                            while (iter.hasNext()) {
//                                System.out.println("准备删除Set....");
                                i = iter.next();
                                iter.remove();
                                System.out.println("删除Set：" + i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("DEL Exception:" + e.getMessage());
                            isStop = true;
                            break;
                        }
                    }
                }
            }
        }, "B").start();

        while (!isStop) {
            Thread.sleep(100);
        }

    }
}
