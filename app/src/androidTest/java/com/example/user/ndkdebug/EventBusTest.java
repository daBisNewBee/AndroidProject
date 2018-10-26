package com.example.user.ndkdebug;

import com.exa.eventbus.MessageEvent;
import com.exa.eventbus.MyEventBusIndex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * EventBus高级使用姿势:
 * https://blog.csdn.net/aa1028181143/article/details/52171175
 *
 * Created by user on 2018/9/9.
 */

public class EventBusTest {

    private EventBus eventBus;

    private String lastEvent = null;

    @Before
    public void setUp() throws Exception {
        eventBus = EventBus.getDefault();
    }

    @Test
    public void stickyTest() throws Exception {
        /*
        * 1. Sticky Events（置顶事件）
        *
        * 后来的消息会把先来的消息覆盖掉，保证消息的最新，不关心以前的消息内容
        *
        * */
        final String msgFirst = "first";
        final String msgSecond = "second";
        eventBus.postSticky(new MessageEvent(msgFirst));
        eventBus.postSticky(new MessageEvent(msgSecond));
        Assert.assertNull(lastEvent);
        // 注意 注册在post之后
        eventBus.register(this);
        Thread.sleep(100);
        // msgSecond 覆盖了 msgFirst
        Assert.assertEquals(msgSecond, lastEvent);
        eventBus.unregister(this);

        /*
        * 2. 直接获取置顶事件
        * 不需要注册EventBus，也不需要编写事件处理函数
        *
        * */
        MessageEvent eventToPost = new MessageEvent(msgFirst);
        MessageEvent eventToPost2 = new MessageEvent(msgSecond);
        eventBus.postSticky(eventToPost);
        eventBus.postSticky(eventToPost2);
        MessageEvent event = eventBus.getStickyEvent(MessageEvent.class);
        Assert.assertEquals(eventToPost2, event);
        boolean suc = eventBus.removeStickyEvent(event);
        Assert.assertTrue(suc);

        eventBus.postSticky(eventToPost);
        MessageEvent event1 = eventBus.removeStickyEvent(MessageEvent.class);
        Assert.assertEquals(eventToPost, event1);
    }

    /*
        *
        * 注意此处:
        * 1. 必须声明为 public的方法
        * 2. 必须使用@Subscribe
        * 3. 需要增加sticky
        *
        * EventBusTest and its super classes have no public methods with the @Subscribe annotation
        * */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void handleEvent(MessageEvent event){
        System.out.println("EventBusTest.handleEvent ----> " + event.getMessage());
        lastEvent = event.getMessage();
    }

    /**
     *
     * 2. 观察者的优先级
     *
     * a. priority 值越大，优先级越高
     * b. cancelEventDelivery 可以取消事件传递
     * c. 优先级只是在相同的ThreadMode中才有效，不同的ThreadMode不起作用
     *
     I/System.out(21308): EventBusTest.eventHigh
     I/System.out(21308): EventBusTest.eventLow
     *
     * @throws Exception
     */
    @Test
    public void priorityTest() throws Exception {
        eventBus.register(this);
        eventBus.post(new MessageEvent("mglb"));
        eventBus.unregister(this);
    }

    @Subscribe(priority = 2)
    public void eventHigh(MessageEvent event){
        System.out.println("EventBusTest.eventHigh");
        // 取消事件传递，事件到此为止
//        eventBus.cancelEventDelivery(event);
    }

    // 无法回调，threadMode不同
    @Subscribe(priority = 1, threadMode = ThreadMode.MAIN)
    public void eventMiddle(MessageEvent event){
        System.out.println("EventBusTest.eventMiddle");
    }

    @Subscribe(priority = 0)
    public void eventLow(MessageEvent event){
        System.out.println("EventBusTest.eventLow");
    }

    /**
     *
     * 3. 新特性Subscriber Index
     *
     * a. 在编译期间生成SubscriberInfo(被@Subscribe 的方法，与所在类的关系映射缓存起来)
     * b. 在运行时使用SubscriberInfo中保存的事件处理函数处理事件
     *
     * 优点：
     *  减少了反射时需要是耗时，会有运行速度上的提升
     * 缺点：
     *  使用较麻烦
     *
     *
     * @throws Exception
     */
    @Test
    public void indexTest() throws Exception {
        EventBus eventBus = EventBus.builder().addIndex(new MyEventBusIndex()).build();
        System.out.println("eventBus = " + eventBus);
        eventBus.register(this);
        eventBus.post(new MessageEvent("hello world."));
        eventBus.unregister(this);

        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        // Now the default instance uses the given index. Use it like this:
        eventBus = EventBus.getDefault();
    }
}
