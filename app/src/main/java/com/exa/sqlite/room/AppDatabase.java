package com.exa.sqlite.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * Created by user on 2018/7/28.
 */

// 导出表的schema信息：到“room.schemaLocation”指定的目录
@Database(entities = {User.class, Book.class, Person.class}, version = 1, exportSchema = true)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    // 带有@Database注解的类必须包括一个没有参数的抽象方法，该方法返回以@DAO注解的类
    public abstract UserDao userDao();

    public abstract BookDao bookDao();

    public abstract PersonDao personDao();

}
