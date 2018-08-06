package com.example.user.ndkdebug;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.exa.MyApplication;
import com.exa.sqlite.StuDBHelper;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by user on 2018/7/27.
 */

public class SQLiteTest {

    SQLiteDatabase db = null;

    StuDBHelper dbHelper = null;

    @Before
    public void setUp() throws Exception {
        dbHelper = new StuDBHelper(MyApplication.getContext()
                , "stu_db", null, 1);
//        db = dbHelper.getReadableDatabase();
    }

    @Test
    public void EXEC_SQL() throws Exception {
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        String sql = "insert into stu_table values(1,\"liuwb\",28, \"male\")";
        db.execSQL(sql);

        queryDb("1");

        sql = "update stu_table set sage=18 where id=1 and ssex=\"male\"";
        db.execSQL(sql);

        queryDb("1");

        sql = "delete from stu_table where id=1";
        db.execSQL(sql);

        queryDb("1");

        db.setTransactionSuccessful();
    }

    @Test
    public void MAIN() throws Exception {
        // 1. 数据库版本的更新,由原来的1变为2
//        dbHelper = new StuDBHelper(MyApplication.getContext()
//                , "stu_db", null, 2);

        // 2. 插入数据的方法
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", 1);
        cv.put("sname","xiaoming");
        cv.put("sage", 21);
        cv.put("ssex", "male");
        db.insert("stu_table", null, cv);
        db.close();

        // 3. 查询数据的方法
        queryDb("1");

        // 4. 修改数据的方法
        db = dbHelper.getWritableDatabase();
        cv.clear();
        cv.put("sage",23);
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(1)};
        db.update("stu_table", cv, whereClause, whereArgs);
        db.close();

        queryDb("1");

        // 5. 删除数据的方法
        db = dbHelper.getReadableDatabase();
        whereArgs = new String[]{String.valueOf(2)};
        db.delete("stu_table", whereClause, whereArgs);
        db.close();

    }

    void queryDb(String id){
        db = dbHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        Cursor cursor = db.query("stu_table", new String[]{"id","sname","sage","ssex"}
                ,"id=?", new String[]{id}, null, null, null);

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("sname"));
            String age = cursor.getString(cursor.getColumnIndex("sage"));
            String sex = cursor.getString(cursor.getColumnIndex("ssex"));
            System.out.println("query------->" + "姓名："+name+" "+"年龄："+age+" "+"性别："+sex);
        }
//        db.close();
    }
}
