package com.example.user.ndkdebug;

import org.junit.Test;

/**
 * Created by user on 2018/9/17.
 */

public class ClassTest {

    static class BeanA{
        static {
            System.out.println("BeanA.static initializer");
        }

        public BeanA() {
            System.out.println("BeanA.BeanA");
        }
    }

    static class BeanB{
        static {
            System.out.println("BeanB.static initializer");
        }

        public BeanB() {
            System.out.println("BeanB.BeanB");
        }
    }

    /**
     *
     * Class.forName 和 loadClass 的异同：
     * 相同：
     *      将类的.class文件加载到jvm
     * 不同：
     *      class.forName 还会对类进行解释，执行类中的static 块
     *      loadClass 仅加载class，不执行static块，只在newInstance时才执行static块
     *
     * 结论：
     *      在类中的static块有初始化等业务时，必须使用Class.ForName进行加载！！
     *
     *
     运行结果：
     BeanA.static initializer
     clzA = class com.example.user.ndkdebug.ClassTest$BeanA
     clzB = class com.example.user.ndkdebug.ClassTest$BeanB
     *
     * 为什么？
     *
     * @throws Exception
     */
    @Test
    public void beanTest() throws Exception {
        System.out.println("-------> ");
        // class.forName 可以通过参数控制拒绝执行static中的内容
//        Class<?> clzA = Class.forName("com.example.user.ndkdebug.ClassTest$BeanA", false, Thread.currentThread().getContextClassLoader());
        Class<?> clzA = Class.forName("com.example.user.ndkdebug.ClassTest$BeanA");
        System.out.println("Class.forName clzA = " + clzA);
        Class<?> clzB = Thread.currentThread().getContextClassLoader().loadClass("com.example.user.ndkdebug.ClassTest$BeanB");
        System.out.println("loadClass clzB = " + clzB);


    }
}
