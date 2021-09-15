package koal.glide_demo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 *
 * java : FileInputStream -> IoBridge.open -> Libcore.os.open
 * -> BlockGuardOs.open -> Posix.open
 *
 * 无代理：
 *
 * buf(1024b)
 * /sdcard/1M.file read : 耗时: 21ms
 * /sdcard/10M.file read : 耗时: 133ms
 * 1048576 write : 耗时: 25ms
 * 10485760 write : 耗时: 222ms
 *
 * buf(2048b)
 * /sdcard/1M.file read : 耗时: 20ms
 * /sdcard/10M.file read : 耗时: 81ms
 * 1048576 write : 耗时: 12ms
 * 10485760 write : 耗时: 123ms
 *
 * buf(2048b) - okio
 * /sdcard/1M.file read : 耗时: 10ms
 * /sdcard/10M.file read : 耗时: 60ms
 * 1048576 write : 耗时: 8ms
 * 10485760 write : 耗时: 74ms
 *
 * 有代理：
 *
 * buf(1024b)
 * /sdcard/1M.file read : 耗时: 61ms
 * /sdcard/10M.file read : 耗时: 337ms
 * 1048576 write : 耗时: 46ms
 * 10485760 write : 耗时: 442ms
 *
 * buf(2048b)
 * /sdcard/1M.file read : 耗时: 35ms
 * /sdcard/10M.file read : 耗时: 185ms
 * 1048576 write : 耗时: 24ms
 * 10485760 write : 耗时: 240ms
 *
 * buf(2048b) - okio
 * /sdcard/1M.file read : 耗时: 20ms
 * /sdcard/10M.file read : 耗时: 80ms
 * 1048576 write : 耗时: 10ms
 * 10485760 write : 耗时: 97ms
 */
public class IOMonitorActivity extends AppCompatActivity {

    private Object mOriginOsObject;
    private boolean proxyOn = false;
    private Button proxyBtn;

    private String[] testFile = new String[]{
                "/sdcard/1M.file",
                "/sdcard/10M.file"};

    private int[] lenArray = new int[]{
            1024 * 1024,
            10 * 1024 * 1024};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_monitor);
        findViewById(R.id.button1).setOnClickListener((view)->{
            try {
                // IO
//                java_io();
                getPid();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.button2).setOnClickListener((view)->{
//            proxy();
            try {
                ok_io();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        proxyBtn = findViewById(R.id.button3);
        proxyBtn.setOnClickListener((view)->{
            try {
                test();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPid() {
        long start = System.currentTimeMillis();
        int pid = 0;
        for (int i = 0; i < 10000; i++) {
            pid = android.os.Process.myPid();
        }
        long end = System.currentTimeMillis();
        Log.d("todo", pid + " getPid: 耗时：" + (end - start) + "ms");
    }

    private void ok_io() throws Exception {
        // 读取测试
        for (String file : testFile) {
            okReadTest(file);
        }
        // 写入测试
        for (int len : lenArray) {
            okWriteTest(len);
        }
    }

    private void okReadTest(String filePath) throws Exception {
        BufferedSource source = Okio.buffer(Okio.source(new File(filePath)));
        byte[] buf = new byte[1024 * 2];
        int read = 0, count = 0;
        long start = System.currentTimeMillis();
        while ((read = source.read(buf)) != -1) {
            count += read;
        }
        long end = System.currentTimeMillis();
        Log.d("todo", "count = " + count);
        Log.d("todo", filePath + " read : 耗时: " + (end - start) + "ms");
        source.close();
    }

    private void okWriteTest(int len) throws Exception {
        BufferedSink sink = Okio.buffer(Okio.sink(new File("/sdcard/ok-" + len + ".tmp")));
        byte[] buf = new byte[1024 * 2];
        int left = len;
        int write = 0;
        long start = System.currentTimeMillis();
        while (left > 0) {
            write = Math.min(buf.length, left);
            sink.write(buf, 0, write);
            left -= write;
        }
        long end = System.currentTimeMillis();
        if (left != 0) {
            throw new RuntimeException("写入长度错误:" + left);
        }
        Log.d("todo", len + " write : 耗时: " + (end - start) + "ms");
        sink.close();
    }

    private void java_io() throws Exception {
        // 读取测试
        for (String file : testFile) {
            readTest(file);
        }
        // 写入测试
        for (int len : lenArray) {
            writeTest(len);
        }
    }

    void readTest(String filePath) throws Exception {
        Log.d("todo", "readTest() called with: filePath = [" + filePath + "]");
        FileInputStream fis = new FileInputStream(filePath);
        int len = fis.available();
        Log.d("todo", "长度 = " + len);
        byte[] buf = new byte[1024 * 2];
        int read = 0, count = 0;
        long start = System.currentTimeMillis();
        while ((read = fis.read(buf)) != -1) {
            count += read;
        }
        long end = System.currentTimeMillis();
        if (count != len) {
            throw new RuntimeException("读取长度错误！");
        }
        Log.d("todo", filePath + " read : 耗时: " + (end - start) + "ms");
        fis.close();
    }

    void writeTest(int len) throws Exception {
        FileOutputStream fos = new FileOutputStream("/sdcard/" + len + ".tmp");
        byte[] buf = new byte[1024 * 2];
        int left = len;
        int write = 0;
        long start = System.currentTimeMillis();
        while (left > 0) {
            write = Math.min(buf.length, left);
            fos.write(buf, 0, write);
            left -= write;
        }
        long end = System.currentTimeMillis();
        if (left != 0) {
            throw new RuntimeException("写入长度错误:" + left);
        }
        Log.d("todo", len + " write : 耗时: " + (end - start) + "ms");
        fos.close();
    }

    private void test() throws Exception {
        Class clzLibcore = Class.forName("libcore.io.Libcore");
        Class clzOs = Class.forName("libcore.io.Os");
        Field fieldOs = clzLibcore.getDeclaredField("os");

        if (proxyOn) {
            fieldOs.set(null, mOriginOsObject);
        } else {
            mOriginOsObject = fieldOs.get(null);
            // public static Os os = new BlockGuardOs(new Posix());
            Log.d("todo", "mOriginOsObject = " + mOriginOsObject);
            MyInvocationHandler handler = new MyInvocationHandler(mOriginOsObject);
            Object proxy = Proxy.newProxyInstance(getClassLoader(), mOriginOsObject.getClass().getSuperclass().getInterfaces(), handler);
            fieldOs.set(null, clzOs.cast(proxy));
            Log.d("todo", "proxy = " + proxy.getClass().getName());
        }
        proxyOn = !proxyOn;
        proxyBtn.setText(proxyOn ? "取消IO代理" : "设置IO代理");
    }

    private void proxy() {
        Student student = new Student();
        MyInvocationHandler handler = new MyInvocationHandler(student);
        Person proxy = (Person)Proxy.newProxyInstance(getClassLoader(), student.getClass().getInterfaces(), handler);
        proxy.sayGoodBye(true, 10000);
        proxy.sayHello("曹尼玛", 999);
    }

    class MyInvocationHandler implements InvocationHandler {

        private Object mObject;

        public MyInvocationHandler(Object object) {
            mObject = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            Log.d("todo", "method = " + method.getName());
//            if (args != null && args.length > 0) {
//                for (Object arg : args) {
//                    Log.d("todo", "arg = " + arg);
//                }
//            }
            return method.invoke(mObject, args);
        }
    }

    /**
     * 需要被代理的类 实现了一个接口Person
     * @author yujie.wang
     *
     */
    public class Student implements Person{

        @Override
        public void sayHello(String content, int age) {
            Log.d("todo", "student sayHello() called with: content = [" + content + "], age = [" + age + "]");
        }

        @Override
        public void sayGoodBye(boolean seeAgin, double time) {
            Log.d("todo", "student sayGoodBye() called with: seeAgin = [" + seeAgin + "], time = [" + time + "]");
        }

    }

    interface Person {
        void sayHello(String content, int age);
        void sayGoodBye(boolean seeAgin, double time);
    }
}