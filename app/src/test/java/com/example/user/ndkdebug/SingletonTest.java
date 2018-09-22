package com.example.user.ndkdebug;

import org.junit.Test;

/**
 * Created by user on 2018/9/22.
 */

public class SingletonTest {

    // 1. 饿汉式
    // 2. 懒汉式

    // 3. 双重检验
    public static class SingleTonDouble{
        private static SingleTonDouble instance = null;

        private SingleTonDouble(){}

        // 线程安全
        public static SingleTonDouble getInstance(){
            if (null == instance){
                synchronized (SingleTonDouble.class){
                    if (null == instance)
                        instance = new SingleTonDouble();
                }
            }
            return instance;
        }
    }

    // 4. 静态内部类
    // 线程安全，又避免了同步带来的性能影响，比懒汉式好些
    public static class SignleTonInner{
        private static class SingleTonHolder{
            private static final SignleTonInner instance = new SignleTonInner();
        }

        private SignleTonInner(){}

        public static final SignleTonInner getInstance(){
            return SingleTonHolder.instance;
        }
    }

    // 5. 枚举类
    // 代码少，又实现线程安全，且支持序列化
    public enum SignleTonEnum{
        INSTANCE;

        SignleTonEnum(){
            System.out.println("SignleTonEnum.SignleTonEnum 构造");
        }

        void func(){

        }
    }

    @Test
    public void singleTon_Test() throws Exception {
        SingleTonDouble singleTonDouble = SingleTonDouble.getInstance();
        System.out.println("singleTonDouble = " + singleTonDouble);

        SignleTonInner signleTonInner = SignleTonInner.getInstance();
        System.out.println("signleTonInner = " + signleTonInner);

        SignleTonEnum signleTonEnum = SignleTonEnum.INSTANCE;
        System.out.println("signleTonEnum = " + signleTonEnum);
        signleTonEnum.func();
    }
}
