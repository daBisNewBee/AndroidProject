package com.example.user.ndkdebug;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;

import com.exa.sqlite.room.AppDatabase;
import com.exa.sqlite.room.Book;
import com.exa.sqlite.room.BookDao;
import com.exa.sqlite.room.Person;
import com.exa.sqlite.room.PersonDao;
import com.exa.sqlite.room.User;
import com.exa.sqlite.room.UserDao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.user.ndkdebug.TestData.BOOK1;
import static com.example.user.ndkdebug.TestData.BOOK2;
import static com.example.user.ndkdebug.TestData.BOOKLIST;
import static com.example.user.ndkdebug.TestData.USER1;
import static com.example.user.ndkdebug.TestData.USER2;
import static com.example.user.ndkdebug.TestData.USER3;
import static com.example.user.ndkdebug.TestData.USERLIST;
import static org.hamcrest.Matchers.is;

/**
 *
 * 参考：
 * Room——Google组件开发
 *
 * https://www.jianshu.com/p/4006cc7307f0
 *
 * Created by user on 2018/7/28.
 */

public class RoomTest {

    private AppDatabase db = null;
    boolean isTmpDb = false;

    @Before
    public void setUp() throws Exception {
        if (isTmpDb){
            db = Room.inMemoryDatabaseBuilder(
                    InstrumentationRegistry.getTargetContext(),AppDatabase.class)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();
        }else {
            db = Room.databaseBuilder(
                    InstrumentationRegistry.getTargetContext(), AppDatabase.class, "room.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();
//            db.clearAllTables();
        }
        db.getOpenHelper().getWritableDatabase().execSQL("PRAGMA foreign_keys=ON");

    }

    @After
    public void tearDown() throws Exception {
        db.close();
    }

    /**
     *
     * 升级DB的几个步骤：
     *
     * 1. 需求：
     * 假设version 从 1 升级到 3；
     * 在1 -》 2 时，新增 "lang" 的列
     * 在2 -》 3 时，新增 "hobby" 的列
     *
     * 2. 定义：MIGRATION_1_2、MIGRATION_2_3（注意：对老数据的备份在此处进行）
     *  （也可一次定义 MIGRATION_1_3）
     *
     * 3. 在"per_tab"表中新增列：lang、hobby
     *
     * 4. 更新"AppDatabase"的version 为 3
     *
     * 5. 升级。
     *
     *
     I/System.out( 6747): MyApplication.onCreate ==========
     I/System.out( 6747): version = 1
     I/System.out( 6747): person1 = com.exa.sqlite.room.Person@1b6174f8
     I/System.out( 6747): person1 = com.exa.sqlite.room.Person@9f28dd1
     I/System.out( 6747): person1 = com.exa.sqlite.room.Person@4224036

     I/System.out( 6930): RoomTest.MIGRATION_1_2
     I/System.out( 6930): RoomTest.MIGRATION_2_3
     I/System.out( 6930): version = 3
     I/System.out( 6930): person1 = com.exa.sqlite.room.Person@11e9fb37
     I/System.out( 6930): person1 = com.exa.sqlite.room.Person@eb4e8a4
     I/System.out( 6930): person1 = com.exa.sqlite.room.Person@14c1360d
     *
     *
     * 参考：
     *  https://stackoverflow.com/questions/50082785/room-database-migration-didnt-properly-handle-conversion
     *
     *
     * @throws Exception
     */
    @Test
    public void upgradeTest() throws Exception {

        int version = db.getOpenHelper().getWritableDatabase().getVersion();
        System.out.println("version = " + version);

        Person person = new Person(1,"28","male");
        Person person2 = new Person(2,"29","female");
        Person person3 = new Person(3,"30","female");

        List<Person> personList = new ArrayList<>();
        personList.add(person);
        personList.add(person2);
        personList.add(person3);

        PersonDao personDao = db.personDao();
        personDao.insertAll(personList);

        personList.clear();
        personList = personDao.getAll();
        for (Person person1 : personList) {
            System.out.println("person1 = " + person1);
        }

    }

    /**
     *
     * 索引测试
     *
     * 50* 10000 数据
     *
     * 查询 id = 499999的记录，
     *
     *          索引      无索引
     * 查询     1~4 ms    138 ms
     * 更新     10 ms     13 ms
     * 删除     3 ms      1 ms
     * 插入     0 ms      1ms
     *
     *
     I/System.out(12011): insertAll cost:23810ms
     I/System.out(12011): findByName 499999 cost:5ms
     I/System.out(12011): user = User{uid=499999, firstName='499999', lastName='499999'}
     I/System.out(12011): updateByfirstName 499999 cost:31ms
     I/System.out(12011): insertByfirstName 499999 cost:1ms
     I/System.out(12011): deleteByfirstName 499999 cost:1ms
     *
     * @throws Exception
     */
    @Test
    public void INDEX_TEST() throws Exception {

        final int TIMES = 50 * 10000;
        UserDao userDao = db.userDao();
        User user = null;
        long start, end;

        List<User> userList = userDao.getAll();
        if (userList.size() == 0){
            for (int i = 0; i < TIMES; i++) {
                userList.add(new User(i,i+"",i+""));
            }
            // 1. 插入数据测试
            start = System.currentTimeMillis();
            userDao.insertAll(userList);
            end = System.currentTimeMillis();
            System.out.println("insertAll cost:"+(end-start)+"ms");
        }

        // 2. 查询数据测试。重要：索引的性能改善体现
        start = System.currentTimeMillis();
        user = userDao.findByfirstName("499999");
        end = System.currentTimeMillis();
        System.out.println("findByName 499999 cost:"+(end-start)+"ms");
        System.out.println("user = " + user);

        // 3. 更新数据测试
        user.setLastName("newLastName");
        start = System.currentTimeMillis();
        userDao.updateByfirstName(user);
        end = System.currentTimeMillis();
        System.out.println("updateByfirstName 499999 cost:"+(end-start)+"ms");

        // 4. 插入数据测试
        start = System.currentTimeMillis();
        userDao.insertByfirstName(user);
        end = System.currentTimeMillis();
        System.out.println("insertByfirstName 499999 cost:"+(end-start)+"ms");

        // 5. 删除数据测试
        start = System.currentTimeMillis();
        userDao.deleteByfirstName(user);
        end = System.currentTimeMillis();
        System.out.println("deleteByfirstName 499999 cost:"+(end-start)+"ms");

        user = new User(499999,"499999","499999");
        userDao.insertByfirstName(user);
    }

    /**
     *
     * 验证 作为 PrimaryKey 的 uid 字段 会 自建索引！
     *  和主动设置索引的字段（firstName）有一样的效果！
     *
     I/System.out(10555): insertAll cost:26005ms
     I/System.out(10555): getUserById 499999 cost:3ms
     I/System.out(10555): user = User{uid=499999, firstName='newFirstName', lastName='499999'}
     I/System.out(10555): updateUsers cost:53ms
     I/System.out(10555): findByName cost:174ms
     * @throws Exception
     */
    @Test
    public void SPEED_TEST() throws Exception {
        final int TIMES = 50 * 10000;
        UserDao userDao = db.userDao();
        User user = null;
        long start,end;

        start = System.currentTimeMillis();
        List<User> userList = userDao.getAll();
        end = System.currentTimeMillis();
        System.out.println("getAll cost:"+(end-start)+"ms");
        if (userList.size() == 0){
            for (int i = 0; i < TIMES; i++) {
                userList.add(new User(i,i+"",i+""));
            }
            start = System.currentTimeMillis();
            userDao.insertAll(userList);
            end = System.currentTimeMillis();
            System.out.println("insertAll cost:"+(end-start)+"ms");
        }

        start = System.currentTimeMillis();
        user = userDao.getUserById(499999);
        end = System.currentTimeMillis();
        System.out.println("getUserById 499999 cost:"+(end-start)+"ms");
        System.out.println("user = " + user);

        start = System.currentTimeMillis();
        user.setFirstName("newFirstName");
        userDao.updateUsers(user);
        end = System.currentTimeMillis();
        System.out.println("updateUsers cost:"+(end-start)+"ms");

        start = System.currentTimeMillis();
        user = userDao.findByName("newFirstName","499999");
        Assert.assertNotNull(user);
        end = System.currentTimeMillis();
        System.out.println("findByName cost:"+(end-start)+"ms");
    }

    @Test
    public void bookTest() throws Exception {
        BookDao bookDao = db.bookDao();

        // 1. 验证 外键"ForeignKey" 所在的表user_tab 未初始化，其主键所在的表 book_tab 也无法执行插入操作
        try {
            bookDao.insertAll(BOOKLIST);
            Assert.fail("SQLiteConstraintException expected");
        }catch (SQLiteConstraintException ignore){
            // android.database.sqlite.SQLiteConstraintException: FOREIGN KEY constraint failed (code 787)
            System.out.println("ignore = " + ignore);
        }
        UserDao userDao = db.userDao();
        userDao.insertAll(USERLIST);

        bookDao.insertAll(BOOKLIST);

        List<Book> bookList = bookDao.getAll();
        for (Book book : bookList) {
            System.out.println("book = " + book);
        }

        Book book = bookDao.findByName(BOOK2.getTitle());
        Assert.assertThat(book.getBookId(), is(BOOK2.getBookId()));
        Assert.assertThat(book.getTitle(), is(BOOK2.getTitle()));
        Assert.assertThat(book.getUserId(), is(BOOK2.getUserId()));

        System.out.println("findByName success ----->"+BOOK2);

        bookDao.delete(BOOK1);
        book = bookDao.findByName(BOOK1.getTitle());
        System.out.println("after delete. book = " + book);
        Assert.assertNull(book);
        System.out.println("delete success ----->"+BOOK1);

        bookList = bookDao.getAll();
        for (Book one : bookList) {
            System.out.println("one = " + one);
        }
        /*
        * 重要：
        *   验证 "onDelete = ForeignKey.CASCADE"的作用！
        *
        *   即：
        *     删除 user_tab 中的实例，其对应在book_tab中的实例也会删除。
        *
        *   "Room仍允许您在实体之间定义Foreign Key约束。"
        * */
        userDao.delete(USER2);

        System.out.println("after delete user2 --->");
        bookList = bookDao.getAll();
        for (Book one : bookList) {
            System.out.println("one = " + one);
        }


        List<User> userList = userDao.getAll();
        for (User user : userList) {
            System.out.println("user = " + user);
        }

    }

    void queryAll(List<?> list){
        for (Object t : list) {
            System.out.println("one = " + t);
        }
    }

    @Test
    public void basicOper() throws Exception {

        // 1. 通过@Database类获取@DAO对象
        UserDao dao = db.userDao();

        // 2. 调用@DAO对象的方法去操作@Entity的数据
        // 2.1 插入数据
        dao.insertAll(USERLIST);

        // 2.2 获取数据，所有
        System.out.println("start to getAll ------> ");
        List<User> userList = dao.getAll();
        for (User one : userList) {
            System.out.println("one = " + one);
        }

        // 2.3 查找数据
        User res = dao.findByName("wrongFirst","wrongLast");
        System.out.println("expect not exist. res = " + res);
        Assert.assertNull(res);

        res = dao.findByName(USER1.getFirstName(), USER1.getLastName());
        Assert.assertThat(res.getFirstName(), is(USER1.getFirstName()));
        Assert.assertThat(res.getLastName(), is(USER1.getLastName()));
        System.out.println("expect exist. res = " + res);

        // 2.4 获取数据，根据条件
        System.out.println("start to loadAllByIds ------> ");
        userList = dao.loadAllByIds(new int[]{2,3});
        for (User one : userList) {
            System.out.println("one = " + one);
        }

        // 2.5 删除数据
        System.out.println("start to delete -----> "+USER3);
        dao.delete(USER3);
        res = dao.findByName(USER3.getFirstName(),USER3.getLastName());
        System.out.println("expect not exist. res = " + res);
        Assert.assertNull(res);

        System.out.println("start to getAll ------> ");
        userList = dao.getAll();
        for (User one : userList) {
            System.out.println("one = " + one);
        }

        // 2.6 更新数据
        final String targetNewFirstName = "newFirstName";
        USER1.setFirstName(targetNewFirstName);
        dao.updateUsers(USER1);

        userList = dao.loadAllByIds(new int[]{USER1.getUid()});
        System.out.println("userList = " + userList.get(0));
        Assert.assertThat(userList.get(0).getFirstName(),is(targetNewFirstName));

        // 2.7 插入数据（冲突的uid）
        final String targetNewFirstNameEx = "newFirstNameTwice";
        userList.get(0).setFirstName(targetNewFirstNameEx);
        dao.insertAll(userList);

        userList = dao.loadAllByIds(new int[]{USER1.getUid()});
        System.out.println("userList = " + userList.get(0));
        Assert.assertThat(userList.get(0).getFirstName(),is(targetNewFirstNameEx));
    }

    /**
     *
     * 类型转换测试
     *
     * 支持在db中存储自定义的数据类型。
     *
     * 需要定义一个TypeConverter，在其中将自定义类型转化为ROOM已知的类型，
     *
     * 比如 Date 转换为 long。
     *
     * @throws Exception
     */
    @Test
    public void convert_test() throws Exception {

        long cur = System.currentTimeMillis();

        USER1.setBirthday(new Date(cur + 1000000));
        USER2.setBirthday(new Date(cur + 2000000));
        USER3.setBirthday(new Date(cur + 3000000));
        for (User user : USERLIST) {
            System.out.println("user = " + user.getBirthday());
        }

        UserDao userDao = db.userDao();

        userDao.insertAll(USERLIST);

        Date from = new Date(cur + 1000001);
        Date to = new Date(cur + 2000001);
        List<User> res = userDao.findUsersBornBetweenDates(from,to);

        for (User re : res) {
            System.out.println("re = " + re);
        }
        Assert.assertThat(res.get(0).getUid(), is(USER2.getUid()));
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            System.out.println("RoomTest.MIGRATION_1_2");
            // 1. 创建临时婊（根据原表的schema）
            database.execSQL("CREATE TABLE IF NOT EXISTS `per_tab_tmp` (`uid` INTEGER NOT NULL, `age` TEXT, `sex` TEXT, PRIMARY KEY(`uid`))");
            // 2. 备份老婊的数据到临时婊
            database.execSQL("INSERT INTO per_tab_tmp SELECT * FROM per_tab");
            // 3. 删除老婊
            database.execSQL("DROP TABLE per_tab");
            // 4. 重命名临时表 为 老表名称
            database.execSQL("ALTER TABLE per_tab_tmp RENAME TO per_tab");
            // 5. 新增列
            database.execSQL("alter table per_tab add column lang TEXT");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            System.out.println("RoomTest.MIGRATION_2_3");
            database.execSQL("CREATE TABLE IF NOT EXISTS `per_tab_tmp` (`uid` INTEGER NOT NULL, `age` TEXT, `sex` TEXT, `lang` TEXT, PRIMARY KEY(`uid`))");
            database.execSQL("INSERT INTO per_tab_tmp SELECT * FROM per_tab");
            database.execSQL("DROP TABLE per_tab");
            database.execSQL("ALTER TABLE per_tab_tmp RENAME TO per_tab");

            database.execSQL("alter table per_tab add column hobby TEXT");
        }
    };

}

