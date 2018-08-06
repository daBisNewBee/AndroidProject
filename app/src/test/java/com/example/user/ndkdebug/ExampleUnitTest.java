package com.example.user.ndkdebug;

import android.provider.Settings;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Vector;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleUnitTest {

    public static Vector<CallBack> callBacks = new Vector<>();

    static {
        callBacks.add(new CallBack() {
            @Override
            public void update(String msg) {
                System.out.println("msg = " + msg);
            }
        });
    }

    public interface CallBack{
        void update(String msg);
    }

    private static void log(final String msg){
        callBacks.forEach(new Consumer<CallBack>() {
            @Override
            public void accept(CallBack callBack) {
                callBack.update(msg);
            }
        });
    }

    @BeforeClass
    public static void beforClass() throws Exception {
        log("beforClass");
    }

    @Before
    public void setUp() throws Exception {
//        log("setUp");
    }

    @After
    public void tearDown() throws Exception {
//        log("tearDown");
    }

    @Test
    public void T002_test2() throws Exception {
        log("test2:"+ System.currentTimeMillis());
    }

    @Test
    public void T001_test1() throws Exception {
        log("test1:"+ System.currentTimeMillis());
    }

    @Test
    public void T003_test3() throws Exception {
        log("test3:"+ System.currentTimeMillis());
    }

    @Test
    public void T004_test4() throws Exception {
        log("test4:"+ System.currentTimeMillis());
    }
}