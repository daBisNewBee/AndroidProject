package com.exa.sqlite.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by user on 2018/8/6.
 */

@Dao
public interface PersonDao {

    @Query("SELECT * FROM per_tab")
    List<Person> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Person> personList);
}
