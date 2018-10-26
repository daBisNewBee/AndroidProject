package com.exa.plugin;

/**
 *
 * 生成dex文件，并在后台执行：
 *
 * /Users/user/Documents/git/NDKDebug/app/src/main/java$
 *
 * 1. 生成class文件：
 * javac com/exa/dexClassLoader/ShowString.java
 *
 * 2. 生成dex文件
 * dx --dex --output ShowString.dex com/exa/dexClassLoader/ShowString.class
 *
 * 3. 将dex文件拷贝到/sdcard/，并执行：
 * /sdcard/$
 * dalvikvm -cp ShowString.dex com.exa.dexClassLoader.ShowString
 *
 * 4. 显示结果：
 * ShowString.main
   ShowString.sayHello
 *
 * Created by user on 2018/8/17.
 */

public class ShowString {

    public String sayHello() throws InterruptedException {
//        for (int i = 0; i < 100; i++) {
//            Thread.sleep(2000);
            System.out.println("ShowString.sayHello");
//        }
        return "this is return from method sayHello! ------> plugin-debug.apk";
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(ShowString.class.getClass().getName());
        new ShowString().sayHello();
    }
}
