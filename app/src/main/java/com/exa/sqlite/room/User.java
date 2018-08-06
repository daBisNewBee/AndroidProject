package com.exa.sqlite.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 *
 * sqlite> .schema
 CREATE TABLE android_metadata (locale TEXT);
 CREATE TABLE room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT);
 CREATE TABLE `user_tab` (`uid` INTEGER NOT NULL, `first_name` TEXT NOT NULL, `last_name` TEXT NOT NULL, `birthday` INTEGER, `street` TEXT, `state` TEXT, `city` TEXT, `postCode` INTEGER, PRIMARY KEY(`uid`));
 CREATE INDEX `index_user_tab_uid` ON `user_tab` (`uid`);
 CREATE INDEX `index_user_tab_first_name_last_name` ON `user_tab` (`first_name`, `last_name`);
 CREATE TABLE `book_tab` (`bookId` INTEGER NOT NULL, `title` TEXT, `user_id` INTEGER NOT NULL, PRIMARY KEY(`bookId`), FOREIGN KEY(`user_id`) REFERENCES `user_tab`(`uid`) ON UPDATE NO ACTION ON DELETE CASCADE );
 CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, PRIMARY KEY(`id`));
 *
 *
 * Created by user on 2018/7/28.
 */

// 默认表名使用类名，若要求表名不一样，此处声明
/*
*
* TODO:谨慎使用索引：提速查找、减速插入更新操作
* indices = {@Index("name"), @Index("last_name", "address")}
* */
@Entity(tableName = "user_tab"
        ,indices = @Index(value = "first_name", unique = true))
//        , indices = {@Index(value = "uid"), @Index(value = {"first_name","last_name"})})
//@Entity(primaryKeys = {"first_name", "last_name"}, tableName = "user_tab" )
public class User {

    @PrimaryKey
    private int uid;

    @NonNull
    // 若希望列有不同名称，此处声明
    @ColumnInfo(name = "first_name")
    // 设置索引的几种方式：此处设置 index；或者在Entity中声明 indices ，多个index
//    @ColumnInfo(name = "first_name", index = true)
    private String firstName;

    @NonNull
    @ColumnInfo(name = "last_name")
    private String lastName;

    private Date birthday;

    @Embedded
    private Address address;
    /*
    *
    * sqlite> select * from user_tab;
       uid = 1
first_name = firstName1
 last_name = lastName1
    street =
     state =
      city =
  postCode =
    * */

    // 不想持久化的字段
    @Ignore
    Bitmap picture;

    public User(int uid, String firstName, String lastName) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    static class Address {
        public String street;
        public String state;
        public String city;

//        @ColumnInfo(name = "post_code")
        public int postCode;
    }

}
