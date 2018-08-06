package com.exa.sqlite.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by user on 2018/8/6.
 */

@Entity(tableName = "per_tab")
public class Person {

    @PrimaryKey
    private int uid;

    private String age;

    private String sex;

//    private String lang;
//    private String hobby;

    public Person(int uid, String age, String sex) {
        this.uid = uid;
        this.age = age;
        this.sex = sex;
    }

//    public String getHobby() {
//        return hobby;
//    }
//
//    public void setHobby(String hobby) {
//        this.hobby = hobby;
//    }
//
//    public String getLang() {
//        return lang;
//    }
//
//    public void setLang(String lang) {
//        this.lang = lang;
//    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
