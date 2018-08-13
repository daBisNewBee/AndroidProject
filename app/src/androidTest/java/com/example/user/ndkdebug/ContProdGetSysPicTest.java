package com.example.user.ndkdebug;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by user on 2018/8/9.
 */

public class ContProdGetSysPicTest {
    Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
    }

    class Pic{
        String name;
        String desc;
        String data;

        public Pic(String name, String desc, String data) {
            this.name = name;
            this.desc = desc;
            this.data = data;
        }

        @Override
        public String toString() {
            return "Pic{" +
                    "name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    class Contacts{
        String contactId;
        String name;
        String phoneNum;

        public Contacts(String contactId, String name, String phoneNum) {
            this.contactId = contactId;
            this.name = name;
            this.phoneNum = phoneNum;
        }

        @Override
        public String toString() {
            return "Contacts{" +
                    "contactId='" + contactId + '\'' +
                    ", name='" + name + '\'' +
                    ", phoneNum='" + phoneNum + '\'' +
                    '}';
        }
    }

    @Test
    public void INSERT_PHONE() throws Exception {
        ContentValues values = new ContentValues();
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI,values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        String userName = "Alice";
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, userName);
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();
        String phoneNum = "13913716111";
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,values);
    }

    @Test
    public void Get_PHONE_CONTACTS() throws Exception {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,null,null,null
        );
        while (cursor.moveToNext()){
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneC = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId, null,null);
            phoneC.moveToNext();
            String phoneNum = phoneC.getString(phoneC.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneC.close();
            Contacts people = new Contacts(contactId,name,phoneNum);
            System.out.println("people = " + people);
        }
        cursor.close();
        /*
I/System.out(13058): people = Contacts{contactId='1', name='刘大帅', phoneNum='188 1758 1870'}
I/System.out(13058): people = Contacts{contactId='2', name='刘二帅', phoneNum='158 5061 7677'}
I/System.out(13058): people = Contacts{contactId='3', name='刘特别帅', phoneNum='6531 8814'
        * */
    }

    @Test
    public void Get_SYS_PIC() throws Exception {
        /*
        * MediaStore.Images.Media.INTERNAL_CONTENT_URI
        * */
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null
                );

        while (cursor.moveToNext()){
            String name = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String desc = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
            String data = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Pic pic = new Pic(name,desc,data);
            System.out.println("pic = " + pic);

        }
        cursor.close();
    }

    /**
     *
     *
     * ContentProvider数据库共享之——读写权限与数据监听:
     *
     *  https://blog.csdn.net/u010952965/article/details/51943086
     *
     * @throws Exception
     */
    @Test
    public void Get_GOOGLE_SAMPLE() throws Exception {
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://com.example.android.contentprovidersample.provider/cheeses"),
                null,null,null,null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(
                    cursor.getColumnIndex("_id"));
            String name = cursor.getString(
                    cursor.getColumnIndex("name"));
            System.out.println("id = " + id + " name:" + name);
        };
    }
}
