package com.exa.sqlite.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by user on 2018/7/29.
 */

@Dao
public interface BookDao {

    @Query("SELECT * FROM book_tab")
    List<Book> getAll();

    @Query("SELECT * FROM book_tab WHERE bookId IN (:bookIds)")
    List<Book> loadAllByBookIds(int[] bookIds);

    @Query("SELECT * FROM book_tab WHERE title = (:title)")
    Book findByName(String title);

    @Insert
    void insertAll(List<Book> bookList);

    @Delete
    void delete(Book book);

}
