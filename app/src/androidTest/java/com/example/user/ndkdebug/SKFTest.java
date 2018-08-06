package com.example.user.ndkdebug;

import android.support.test.runner.AndroidJUnit4;

import com.exa.CertManager;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Vector;

/**
 * Created by user on 2018/7/20.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SKFTest implements CertManager {
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
        for (CallBack one :
                callBacks) {
            one.update(msg);
        }
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
    public void T001_SKF_EnumDev() throws Exception {
        log("T001_SKF_EnumDev:"+ System.currentTimeMillis());
    }

    @Test
    public void T002_SKF_ConnectDev() throws Exception {
        log("T002_SKF_ConnectDev:"+ System.currentTimeMillis());
    }

    @Test
    public void T003_SKF_DisConnectDev() throws Exception {
        log("T003_SKF_DisConnectDev:"+ System.currentTimeMillis());
    }

    @Test
    public void T004_SKF_DevAuth() throws Exception {
        log("T004_SKF_DevAuth:"+ System.currentTimeMillis());
    }

}
