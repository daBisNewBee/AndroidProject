package koal.glide_demo;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * Android 扩展OkHttp支持请求优先级调度:
 * https://www.cnblogs.com/wzzkaifa/p/7294637.html
 *
 * Created by wenbin.liu on 2019-08-15
 *
 * @author wenbin.liu
 */
public class OkHttpTest {
    @Test
    public void okhttpMain() throws Exception {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request.Builder builder = new Request.Builder();
        builder.url("https://www.baidu.com").tag(this.hashCode());

        final Callback callback = new Callback() {
            @Override
            public void onFailure( Call call, IOException e) {
                System.out.println("onFailure call = [" + call + "], e = [" + e + "]");
            }

            @Override
            public void onResponse( Call call, Response response) throws IOException {
                System.out.println("onResponse call = [" + call + "], response = [" + response + "]");
            }
        };

        List<Call> callList = new ArrayList<>();
        for (int i = 0; i <10; i++) {
//            callList.add(okHttpClient.newCall(builder.build()));
            okHttpClient.newCall(builder.build()).enqueue(callback);
        }

        Thread.sleep(50);

        int sizeRunningCalls = okHttpClient.dispatcher().runningCalls().size();
        System.out.println("runningCalls size = " + sizeRunningCalls);
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
//            System.out.println("runningCalls:" + call.request().tag());
//            if (Integer.toString(this.hashCode()).equals(call.request().tag())) {
                call.cancel();
//            }
        }

        int sizeQueuedCalls = okHttpClient.dispatcher().runningCalls().size();
        System.out.println("sizeQueuedCalls = " + sizeQueuedCalls);
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
//            System.out.println("queuedCalls:" + call.request().tag());
//            if (Integer.toString(this.hashCode()).equals(call.request().tag())) {
                call.cancel();
//            }
        }

        Thread.currentThread().join();

    }

    class Person {
        int age;
        String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        /*
        @Override
        public int compareTo(@NonNull Person o) {
            return this.age - o.age; // 从大到小
        }
        */

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    // 不修改Person类支持排序的办法
    class PersonComparator implements Comparator<Person> {

        @Override
        public int compare(Person o1, Person o2) {
            return o2.age - o1.age;
        }
    }

    @Test
    public void sortTest() {
        List<Person> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int age = new Random().nextInt(100);
            Person person = new Person(age, Integer.toString(age));
            list.add(person);
        }
        System.out.println("list = " + list);
        Collections.sort(list, new PersonComparator());
        System.out.println("After sort...");
        System.out.println("list = " + list);
    }
}
