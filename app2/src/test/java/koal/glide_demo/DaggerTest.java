package koal.glide_demo;

import android.util.Log;

import koal.glide_demo.dagger.MyActivity;
import koal.glide_demo.dagger.module.MyService;
import org.junit.Test;

import java.util.Random;

/**
 *
 * Dagger2 最清晰的使用教程:
 *
 * https://www.jianshu.com/p/24af4c102f62
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DaggerTest {

    @Test
    public void dagger_Test() throws Exception {

        MyActivity.normal();

        MyActivity.entry();
    }

    @Test
    public void moduleTest() throws Exception {
        MyService.speak();
    }

    class RecordThread extends Thread {
        @Override
        public void run() {
            System.out.println("RecordThread");
            for (int i = 0; i < 10; i++) {
                System.out.println("RecordThread : " + i);
                try {
                    Thread.sleep(new Random().nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class MixerThread extends Thread {
        @Override
        public void run() {
            System.out.println("-----> MixerThread");
            for (int i = 0; i < 10; i++) {
                System.out.println("-----> MixerThread : " + i);
                try {
                    Thread.sleep(new Random().nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void priorityTest() throws InterruptedException {
        Thread t1 = new RecordThread();
        Thread t2 = new MixerThread();
        int t1Pri = t1.getPriority();
        int t2Pri = t2.getPriority();
        System.out.println("-----> t1Pri:" + t1Pri);
        System.out.println("-----> t2Pri:" + t2Pri);
        // 优先级高的线程获取CPU资源的概率较大，优先级低的并非没机会执行。
        t1.setPriority(Thread.MAX_PRIORITY);
        t2.setPriority(Thread.MIN_PRIORITY);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}