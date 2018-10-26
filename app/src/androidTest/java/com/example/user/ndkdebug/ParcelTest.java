package com.example.user.ndkdebug;

import android.os.Parcel;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * 在"内存"操作时，使用"Parcelable"！其性能高于Serializable！速度高十倍以上
 * 因为Serializable使用了反射，在序列化的时候会产生大量的临时变量，从而引起频繁的GC。
 * 而 Parcelable 原理是将一个完整的对象进行分解
 *
 * 在"磁盘"操作时，使用"Serializable"！
 * 因为Parcelable不能很好的保证数据的持续性在外界有变化的情况下
 *
 * Created by user on 2018/8/28.
 */

public class ParcelTest {

    /*
    * 使用非 static 时，会报错：
        *
java.io.NotSerializableException: com.example.user.ndkdebug.ParcelTest
at java.io.ObjectOutputStream.writeNewObject(ObjectOutputStream.java:1344)
        *
        * 原因：
        * 假设一个可序列化的对象包括对某个不可序列化的对象的引用，
        * 那么整个序列化操作将会失败，而且会抛出一个NotSerializableException.

        非静态内部类拥有对外部类的全部成员的全然訪问权限，包含实例字段和方法。
        为实现这一行为，非静态内部类存储着对外部类的实例的一个隐式引用。序列
        化时要求全部的成员变量是Serializable,如今外部的类并没有implements Serializable,
        所以就抛出java.io.NotSerializableException异常。
        此处表现为：
            User类包括ParcelTest对象的引用，而ParcelTest对象是非序列化的。
        *
    * */
//    class User implements Serializable{
    static class User implements Serializable{
        private static final long serialVersionUID = 1L;

        int id;
        String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    /**
     *
     * shell@latte:/sdcard $ busybox hexdump -C obj.der
     00000000  ac ed 00 05 73 72 00 29  63 6f 6d 2e 65 78 61 6d  |....sr.)com.exam|
     00000010  70 6c 65 2e 75 73 65 72  2e 6e 64 6b 64 65 62 75  |ple.user.ndkdebu|
     00000020  67 2e 50 61 72 63 65 6c  54 65 73 74 24 55 73 65  |g.ParcelTest$Use|
     00000030  72 00 00 00 00 00 00 00  01 02 00 02 49 00 02 69  |r...........I..i|
     00000040  64 4c 00 04 6e 61 6d 65  74 00 12 4c 6a 61 76 61  |dL..namet..Ljava|
     00000050  2f 6c 61 6e 67 2f 53 74  72 69 6e 67 3b 78 70 00  |/lang/String;xp.|
     00000060  00 00 0a 74 00 05 61 6c  69 63 65                 |...t..alice|
     *
     * 规则见：
     * https://www.cnblogs.com/senlinyang/p/8204752.html
     *
     * @throws Exception
     */
    @Test
    public void serializable_Test() throws Exception {
        User user = new User(10, "alice");

        String objectPath = "/sdcard/obj.der";
        FileOutputStream fos = new FileOutputStream(objectPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(user);
        oos.flush();
        oos.close();

        FileInputStream fis = new FileInputStream(objectPath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        User readUser = (User)ois.readObject();
        System.out.println("readUser = " + readUser);
    }

    private Parcel parcel;

    private void getParcelInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("dataSize:"+parcel.dataSize()
                + ", dataCapacity:"+parcel.dataCapacity()
                + ", dataPositon:"+parcel.dataPosition());
        System.out.println(sb.toString());
    }

    /**
     *
     * 对parcel 操作的几个注意点：
     *
     * 1. 对小于32位的数据类型操作时，实际按照最小"32位"保存。
     * 比如 char、byte、boolean
     *
     * 2. readXXX()、writeXXX()会对对象产生偏移。偏移量会读取对象的大小
     *
     * 3. 无论他存储的是基本数据类型或引用数据类型的变量，都是以32bit基本单位作为偏移量
     *
     * @throws Exception
     */
    @Test
    public void parcel_Test() throws Exception {
        parcel = Parcel.obtain();
        getParcelInfo();
        for (int i = 0; i < 10; i++) {
            parcel.writeDouble(i);
            System.out.println("writeDouble:");
            getParcelInfo();
        }

        System.out.println("\nstart read -----> ");

        // //方法一 ，显示设置偏移量
        int i = 0;
        int dataSize = parcel.dataSize();
        parcel.setDataPosition(i);
        while (i < dataSize){
            parcel.setDataPosition(i);// 该步实际可省略！readXXX操作 自己会产生偏移
            double value = parcel.readDouble();
            System.out.println("\nvalue = " + value);
            getParcelInfo();
            i += 8;
        }

        // 方法二, 我们可以直接利用readXXX()读取值会产生偏移量。前提：对象的类型一致
        parcel.setDataPosition(0);
        while (parcel.dataPosition() < parcel.dataSize()){
            double valueAutoMove = parcel.readDouble();
            System.out.println("valueAutoMove = " + valueAutoMove);
            getParcelInfo();
        }

        System.out.println("do recycle -----> ");
        parcel.recycle();

        /*
            验证 基本数据类型占用字节数：
        *          boolean     1bit          1字节（无法验证）

                   char          16bit         2字节（无法验证）

                   int             32bit        4字节

                   short         16bit          2字节

                   long          64bit        8字节

                   float          32bit        4字节

                  double       64bit         8字节

                  byte          8bit         1字节
        *
        * */


        /*
I/System.out(10581): dataSize:0, dataCapacity:0, dataPositon:0
// writeInt:
I/System.out(10581): dataSize:4, dataCapacity:6, dataPositon:4

// writeLong:
I/System.out(10581): dataSize:12, dataCapacity:18, dataPositon:12

// writeCharArray:（想想为什么size是增加了8，不是4 ？）
I/System.out(10581): dataSize:20, dataCapacity:30, dataPositon:20

// writeFloat:
I/System.out(10581): dataSize:24, dataCapacity:30, dataPositon:24

// writeDouble:
I/System.out(10581): dataSize:32, dataCapacity:48, dataPositon:32

// writeBooleanArray:（想想为什么size是增加了8，不是4 ？）
I/System.out(10581): dataSize:40, dataCapacity:48, dataPositon:40

// writeByte:（想想为什么size是增加了4，不是2 ？）
I/System.out(10581): dataSize:44, dataCapacity:48, dataPositon:44
* */
        int in = 6;
        short sh = 80;
        long lo = 10;
        char ch = 'b';
        float fl = 100.0f;
        double dou = 200.0;
        boolean bool = true;
        byte by = (byte)1;
        getParcelInfo();
        parcel.writeInt(in);
        getParcelInfo();
        parcel.writeLong(lo);
        getParcelInfo();
        parcel.writeCharArray(new char[]{ch}); // 实际走的是 "writeInt"
        getParcelInfo();
        parcel.writeFloat(fl);
        getParcelInfo();
        parcel.writeDouble(dou);
        getParcelInfo();
        parcel.writeBooleanArray(new boolean[]{bool}); // 实际走的是 "writeInt"
        getParcelInfo();
        parcel.writeByte(by);
        getParcelInfo();
        parcel.recycle();
/*
*
* 不执行 recycle，会报错：
* 08-30 21:35:20.443 11511-11542/? I/TestRunner: ----- begin exception -----
08-30 21:35:20.443 11511-11542/? I/TestRunner: java.lang.SecurityException: Binder invocation to an incorrect interface
                                                   at android.os.Parcel.readException(Parcel.java:1546)
* */
    }
}
