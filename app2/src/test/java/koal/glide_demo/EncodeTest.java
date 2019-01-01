package koal.glide_demo;

import org.junit.Test;

public class EncodeTest {
    /**
     *
     * 如何理解java采用Unicode编码?(比较好的一个编码转换解释)
     * https://blog.csdn.net/shijinupc/article/details/7679945
     *
     * JVM和OS的交界处，也就是各种输入/输出流（或者Reader，Writer类）起作用的地方。
     * IO流的选择：
     * 1. 面向字节的IO流：
     *    原样的从文件中读入二进制到JVM内部，不做任何变换，适合诸如视频、音频文件
     * 2. 面向字符的IO流：
     *    从文件中读入的二进制与到JVM内部的不一致，需要做"编码转换"
     *
     * EF BB BF
     *  表示UTF-8
     *
     * FE FF
     *  表示UTF-16.
     *
     * Unicode(UTF-8, UTF-16)令人混淆的概念:
     * https://www.cnblogs.com/fnlingnzb-learner/p/6163205.html
     *
     * 其他：
     * Java字符和字符串存在于以下几个地方：
     * 1. Java源码文件，*.java，可以是任意字符编码，如GBK，UTF-8
     * 2. Class文件，*.class，采用的是一种改进的UTF-8编码（Modified UTF-8）
     * 3. JVM，内存中使用Unicode字符集
     *
     * Java源码文件
     * --(Java编译器需要读取源码，消除编码差异，然后编译成UTF-8编码的Class文件)-->
     * Class文件
     * --(JVM加载Class文件，把其中的字符或字符串转成UTF-16编码序列)-->
     * JVM
     *
     * @throws Exception
     */
    @Test
    public void unicode_Test() throws Exception{
        System.out.println(System.getProperty("file.encoding"));

        char han = '永';
        System.out.format("%x", (short)han);

        /*
        * 验证：
        * "在Java中字符仅以一种形式存在，那就是Unicode"
        * （不选择任何特定的编码，直接使用他们在字符集中的编号，这是统一的唯一方法）
        *
        *  这里的Java中是指在"JVM"中、在内存中、在代码里声明的每一个char、String类型的变量中。
        * */
        char han2 = 0x6c38;
        System.out.println("\nhan2 = " + han2);

        // 只要我们正确地读入了汉字“永”字，
        // 那么它在内存中的表示形式一定是0x6c38，没有其他任何值能替代这个字。

        String raw = "永";
        byte b[] = null;

        // 1. UTF-8
        b = raw.getBytes("UTF-8");
        System.out.println("UTF8编码格式下，一个汉字占" + b.length + "字节: ");
        for (byte i :
                b) {
            System.out.format("%x ", i);
        }
        // e6 b0 b8
        System.out.println();

        // 2. UTF-16
        b = raw.getBytes("UTF-16");
        System.out.println("UTF16编码格式下，一个汉字占" + (b.length - 2) + "字节: ");
        for(byte i:b) {
            System.out.format("%x ", i);
        }
        // fe ff 6c 38
        System.out.println();

        // 3. 默认编码: UTF-8
        b = raw.getBytes();
        System.out.println("默认编码" + System.getProperty("file.encoding") + "编码格式下，一个汉字占" + b.length + "字节:");
        for(byte i:b) {
            System.out.format("%x ", i);
        }

    }

    @Test
    public void encode_Test() throws Exception {
        String raw = "永";
        byte[] b = raw.getBytes("UTF-8");
        for (byte i:
             b) {
            System.out.format("%x ",i);
        }

        System.out.println();
        b = raw.getBytes("UTF-16");
        for (byte i :
                b) {
            System.out.format("%x ",i);
        }
    }
}
