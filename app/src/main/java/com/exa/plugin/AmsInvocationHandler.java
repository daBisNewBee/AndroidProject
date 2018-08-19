package com.exa.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by user on 2018/8/18.
 */

public class AmsInvocationHandler implements InvocationHandler {

    private Object iActivityManagerObject;
    private Context context;

    public AmsInvocationHandler(Object _object, Context _context) {
        this.iActivityManagerObject = _object;
        this.context = _context;
    }

    /**
     *
     * 这里对"startActivity"的intent 进行 替换操作：
     * tagetActivity（未注册） 替换为 ProxyActivity （已注册）
     *
     * 其他method不做处理。
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().equals("startActivity")){

            System.out.println("AmsInvocationHandler.invoke --------> 搞事情了 startActivity");
            Intent intent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent){
                    /*
                    * 说明找到了startActivity的Intent参数
                    * 这个意图是不能被启动的，因为Acitivity没有在清单文件中注册
                    * */
                    intent = (Intent) args[i];
                    index = i;
                }
            }

            //伪造一个代理的Intent，代理Intent启动的是proxyActivity
            Intent proxyIntent = new Intent();
            ComponentName componentName = new ComponentName(context, ProxyActivity.class);
            proxyIntent.setComponent(componentName);
            proxyIntent.putExtra("oldIntent",intent);
            args[index] = proxyIntent;
        }else {
            System.out.println("AmsInvocationHandler.invoke -----> "+method.getName());
        }

        return method.invoke(iActivityManagerObject,args);
    }
}
