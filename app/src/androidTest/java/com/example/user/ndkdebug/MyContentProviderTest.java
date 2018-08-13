package com.example.user.ndkdebug;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.test.InstrumentationRegistry;

import com.exa.contProd.MyContentProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by user on 2018/8/13.
 */

public class MyContentProviderTest {

    private ContentResolver mContentResolver;

    private ContentObserver mContentObserver;

    /*
    * 使用ContentObserver的情况主要有一下两者情况：

      1、需要频繁检测的数据库或者某个数据是否发生改变，如果使用线程去操作，很不经济而且很耗时 ；

      2、在用户不知晓的情况下对数据库做一些事件，比如：悄悄发送信息、拒绝接受短信黑名单等；
    * */
    static class DataContentObserver extends ContentObserver{
        private static int count = 0;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DataContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            mHandler.sendEmptyMessage(count++);
            System.out.println("onChange uri = " + uri + "----->"+selfChange);
        }
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("msg = " + msg.what+"\n");
        }
    };

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        mContentResolver = context.getContentResolver();
        mContentObserver = new DataContentObserver(mHandler);
        mContentResolver.registerContentObserver(MyContentProvider.URI_MYPROVIDER, true, mContentObserver);
    }

    @After
    public void tearDown() throws Exception {
        mContentResolver.unregisterContentObserver(mContentObserver);
    }

    @Test
    public void QUERY() throws Exception {
        String[] queryTargets = new String[]{
                MyContentProvider.TABLE_SSO,
                MyContentProvider.TABLE_TICKET,
                MyContentProvider.TABLE_TICKET + "/1",
                MyContentProvider.TABLE_TICKET + "/2",
                MyContentProvider.TABLE_TICKET + "/3",
                MyContentProvider.TABLE_TICKET + "/4",
                MyContentProvider.TABLE_ALL};
        for (String queryTarget : queryTargets) {
//            System.out.println(" \n-----------> query:"+queryTarget);

            Cursor cursor = mContentResolver.query(
                    Uri.parse(MyContentProvider.URI_MYPROVIDER + queryTarget),
                    new String[]{MyContentProvider.CURSOR_COLUMN_NAME, MyContentProvider.CURSOR_COLUMN_VALUE},
                    null, new String[]{}, null);
            while (cursor!=null && cursor.moveToNext()){
                int nameIndex = cursor.getColumnIndex(MyContentProvider.CURSOR_COLUMN_NAME);
                int valueIndex = cursor.getColumnIndex(MyContentProvider.CURSOR_COLUMN_VALUE);
                String name = cursor.getString(nameIndex);
                String value = cursor.getString(valueIndex);
                System.out.println("name = " + name+" value:"+value);
            }
            cursor.close();
        }
    }

    @Test
    public void INSERT() throws Exception {
        ContentValues values = new ContentValues();
        values.put("alice-key","alice-value");
        /*
        * 注意"notifyForDescendants"不同，notifyChange 是否回调的变化：
        * true：
        *   I/System.out(10840): insert -----> content://com.exa.MyContentProvider/ticket/2
            I/System.out(10840): onChange uri = content://com.exa.MyContentProvider/ticket/2----->false
            I/System.out(10840): msg = 0

          false:(无"onChange"回调！！监听在父URL，子URL的变更不会通知)
            I/System.out(10945): insert -----> content://com.exa.MyContentProvider/ticket/2
        *
        * */
        mContentResolver.insert(Uri.parse(MyContentProvider.URI_MYPROVIDER + MyContentProvider.TABLE_TICKET+"/2"),values);

    }

    @Test
    public void getType() throws Exception {

        Uri uri = Uri.parse(MyContentProvider.URI_MYPROVIDER + MyContentProvider.TABLE_SSO);
        String type_TABLE_SSO = mContentResolver.getType(uri);
        System.out.println("type_TABLE_SSO = " + type_TABLE_SSO);

        uri = Uri.parse(MyContentProvider.URI_MYPROVIDER + MyContentProvider.TABLE_TICKET);
        String type_TABLE_TICKET = mContentResolver.getType(uri);
        System.out.println("type_TABLE_TICKET = " + type_TABLE_TICKET);

    }
}
