package com.example.user.ndkdebug;

import com.exa.sqlite.room.Book;
import com.exa.sqlite.room.User;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 2018/7/29.
 */

public class TestData {

    static final User USER1 = new User(1,"firstName1","lastName1");
    static final User USER2 = new User(2,"firstName2","lastName2");
    static final User USER3 = new User(3,"firstName3","lastName3");

    static final List<User> USERLIST = Arrays.asList(USER1, USER2, USER3);

    private static Random ran = new Random();

    static final Book BOOK1 = new Book(ran.nextInt(100),"bookName1", USER1.getUid());
    static final Book BOOK2 = new Book(ran.nextInt(100),"bookName2", USER2.getUid());
    static final Book BOOK3 = new Book(ran.nextInt(100),"bookName3", USER3.getUid());

    static final List<Book> BOOKLIST = Arrays.asList(BOOK1, BOOK2, BOOK3);

}
