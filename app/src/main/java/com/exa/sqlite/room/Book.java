package com.exa.sqlite.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by user on 2018/7/29.
 */

@Entity(tableName = "book_tab", foreignKeys = @ForeignKey(entity = User.class,
                                    parentColumns = "uid",
                                    childColumns = "user_id",
                                    onDelete = ForeignKey.CASCADE))
public class Book {

    @PrimaryKey
    private int bookId;

    private String title;

    @ColumnInfo(name = "user_id")
    private int userId;

    public Book(int bookId, String title, int userId) {
        this.bookId = bookId;
        this.title = title;
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                '}';
    }
}
