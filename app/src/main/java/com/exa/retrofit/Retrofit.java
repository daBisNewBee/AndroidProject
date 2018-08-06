package com.exa.retrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by user on 2018/7/22.
 */

public class Retrofit {

    public <T> T create(final Class<T> service){
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        Get get = method.getAnnotation(Get.class);
                        if (get == null){
                            System.out.println("can not find Get.Annota");
                            return null;
                        }

                        return http(get.value());
                    }
                });
    }


    private String http(String url){

        /*
        *
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response.body().string();
        * */

        return url.equals("http://www.qq.com")? "hello":"world";
    }

    public static void main(String[] args) {
        Retrofit retrofit = new Retrofit();
        INews news = retrofit.create(INews.class);
        System.out.println(news.getQQ());
        System.out.println(news.getSina());
    }

}
