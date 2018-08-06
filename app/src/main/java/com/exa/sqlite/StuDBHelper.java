package com.exa.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.exa.MyApplication;

/**
 * Created by user on 2018/7/27.
 */

public class StuDBHelper extends SQLiteOpenHelper {

    public StuDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists stu_table(id int,sname varchar(20),sage int,ssex varchar(10))";
        //输出创建数据库的日志信息
        System.out.println("StuDBHelper.onCreate ------------>");
        //execSQL函数用于执行SQL语句
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("db = [" + db + "], oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");
    }
}
