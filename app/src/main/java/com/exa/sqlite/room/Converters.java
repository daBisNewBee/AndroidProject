package com.exa.sqlite.room;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by user on 2018/7/29.
 */

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value){
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }

}
