package com.example.user.ndkdebug;

import org.junit.Before;
import org.junit.Test;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 生产者、消费者模型。
 *
 * 主要步骤：
 *   1. 生产者生产数据到缓冲区中，消费者从缓冲区中取数据。
     2. 如果缓冲区已经满了，则生产者线程阻塞；
     3. 如果缓冲区为空，那么消费者线程阻塞。
 *
 * 几个方法：
 *   1. synchronized、wait和notify
 *   2. lock和condition的await、signalAll
 *   3. BlockingQueue
 *   4. 信号量semaphore。acquire、release
 *
 * Created by user on 2018/9/23.
 */

public class ConCurrencyTEst {

    private volatile boolean turnA = true;

    private static Object lock = new Object();

    private ExecutorService executorService;

    private Queue<Integer> queue = new PriorityQueue<>();

    private static final int POOL_SIZE = 10;

    @Before
    public void setUp() throws Exception {
        executorService = Executors.newCachedThreadPool();
    }

    @Test
    public void con_Test() throws Exception {
        // 消费者
        new Thread(){
            public void run(){
                try {
                    while (true){
                        synchronized (queue){
                            while (queue.size() == 0){
                                System.out.println("消费者：进入等待！队列为空，通知生产者生产.");
                                queue.wait();
                            }
                            Thread.sleep(1000);
                            int tmp = queue.poll();
                            System.out.println("消费者：已消费一个物品:" + tmp);
                            queue.notify();
//                            if (turnA){
//                                System.out.println("A ----> do work...");
//                                Thread.sleep(2000);
//                                System.out.println("A ----> do work done");
//                                turnA = false;
//                                lock.notifyAll();
//                            }else {
//                                System.out.println("A into wait.");
//                                lock.wait();
//                            }
                        }
                    }
                }catch (Exception e){
                    // ignore
                }
            }
        }.start();

        // 生产者
        new Thread(){
            public void run(){
                try {
                    while (true){
                        synchronized (queue){
                            while (queue.size() == POOL_SIZE){
                                System.out.println("生产者：进入等待！队列已满，通知消费者消费");
                                queue.wait();
                            }
                            Thread.sleep(1000);
                            int gen = new Random().nextInt();
                            System.out.println("生产者：生产一个物品:"+gen);
                            queue.offer(gen);
                            queue.notify();

//                            if (!turnA){
//                                System.out.println("B ----> do work...");
//                                Thread.sleep(2000);
//                                System.out.println("B ----> do work done");
//                                turnA = true;
//                                lock.notifyAll();
//                            }else {
//                                System.out.println("B into wait.");
//                                lock.wait();
//                            }
                        }
                    }
                }catch (Exception e){
                    // ignore
                }
            }
        }.start();

        while (Thread.activeCount()>2){
            Thread.yield();
        }
    }

    @Test
    public void waitNotify_Test() throws Exception {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    waitMethod();
                } catch (Exception e) {
                    System.out.println("waitMethod e = " + e);
                    e.printStackTrace();
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyMethod();
                } catch (Exception e) {
                    System.out.println("notifyMethod e = " + e);
                    e.printStackTrace();
                }
            }
        });

        Thread.currentThread().getThreadGroup().list();

        while (Thread.activeCount()>2){
//            System.out.println(Thread.activeCount());
            Thread.yield();
        }

    }

    void waitMethod()  throws Exception{
        synchronized (lock){
            System.out.println(Thread.currentThread().getId()+" before wait.");
            lock.wait();
            System.out.println(Thread.currentThread().getId()+" after wait.");
        }
    }

    void notifyMethod() throws Exception{
        synchronized (lock){
            System.out.println(Thread.currentThread().getId()+ " before notify.");
            lock.notify();
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getId()+" after notify.");
        }
        System.out.println("this is outer lock area.");
    }
}
