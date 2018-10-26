package com.example.user.ndkdebug;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by user on 2018/9/23.
 */

public class BlockingQueueTest {

    private static final int SIZE = 5;
    private static BlockingQueue queue = new LinkedBlockingDeque<Integer>(SIZE);

    static class Producer implements Runnable{

        @Override
        public void run() {
            try {
                while (true){
                    int tmp = new Random().nextInt();
                    System.out.println(Thread.currentThread().getId()+" 生产者：添加 = " + tmp);
//                    queue.add(tmp);
                    queue.put(tmp);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer implements Runnable{

        @Override
        public void run() {
            try {
                while (true){
                    int get = (Integer) queue.take();
                    System.out.println(Thread.currentThread().getId() + " 消费者：获取 = " + get);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void blockQueue_Test() throws Exception {
        for (int i = 0; i < 5; i++) {
            new Thread(new Producer()).start();
        }
        new Thread(new Consumer()).start();
        while (Thread.activeCount() > 2){
            Thread.yield();
        }
    }
}
