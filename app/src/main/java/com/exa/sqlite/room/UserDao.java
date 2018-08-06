package com.exa.sqlite.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 *
 * Data Access Objects (DAOs)
 *
 * DAO以干净的方式抽象地访问数据库。
 *
 * Created by user on 2018/7/28.
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_tab")
    List<User> getAll();

    @Query("SELECT * FROM user_tab WHERE uid = :iuid")
    User getUserById(int iuid);

    @Query("SELECT * FROM user_tab WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user_tab WHERE " +
            "first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);


    // <-------  索引测试方法 开始 --------- >
    @Query("SELECT * FROM user_tab WHERE first_name = :first")
//   错误！LIKE 索引无效！ @Query("SELECT * FROM user_tab WHERE first_name LIKE :first LIMIT 1")
    User findByfirstName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertByfirstName(User user);

    @Update
    void updateByfirstName(User user);

    @Delete
    void deleteByfirstName(User user);
    // <-------  索引测试方法 结束 --------- >

    /*
    *
    *   查询记录只返回指定的列：
    *
    *     @Query("SELECT first_name, last_name FROM user")
          public List<NameTuple> loadFullName();

          这个自定义的POJO也可以使用@Embeded注解。
    *
    * */


    /*
    *
    * 多个参数的同名匹配：
    *
    *   WHERE 数据列名 > :参数名 大于
        WHERE 数据列名 < :参数名 小于
        WHERE 数据列名 BETWEEN :参数名 AND :参数名2 这个区间
        WHERE 数据列名 LIKE :参数名 等于
        WHERE 数据列名 IN (:集合参数名) 查询符合集合内指定字段值的记录
    *
    * */

    @Update
    void updateUsers(User user);

    /*
    *
    * 设置冲突策略为"替换"，否则会当主键相同时，会插入失败
    *
    * 报错：
    *   android.database.sqlite.SQLiteConstraintException:
    *       UNIQUE constraint failed: user_tab.uid (code 1555)
    * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<User> userList);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM user_tab WHERE birthday BETWEEN :from AND :to")
    List<User> findUsersBornBetweenDates(Date from, Date to);
}
