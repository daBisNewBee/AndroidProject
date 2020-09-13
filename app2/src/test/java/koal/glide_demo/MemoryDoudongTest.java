package koal.glide_demo;


import org.junit.Test;

/**
 * Created by wenbin.liu on 2020-09-13
 *
 * @author wenbin.liu
 */
public class MemoryDoudongTest {
    @Test
    public void main() {
        System.out.println("1111111 " + "2222222"); // 1. 静态字符，编译阶段直接拼接，不会用到SB

        System.out.println("==========");

        // 2. 反编译后实际是StringBuilder
        // 3. 反编译 javap -s -v .//app2/build/intermediates/classes/test/debug/koal/glide_demo/MemoryDoudongTest.class
        System.out.println("aaaaa" + System.currentTimeMillis());
        /*
        *   class java/lang/StringBuilder
            Method java/lang/StringBuilder."<init>":()V
            String aaaaa
            Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/St
            Method java/lang/System.currentTimeMillis:()J
            Method java/lang/StringBuilder.append:(J)Ljava/lang/StringBuilder;
            Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        * */

        System.out.println("==========");
        StringBuffer stringBuffer = new StringBuffer();  // 4.StringBuffer = StringBuilder + "synchronized"
        stringBuffer.append("");

        StringBuilder sb = new StringBuilder();
        sb.append("aaaaa");
        sb.append(System.currentTimeMillis());
        String str = sb.toString();
        System.out.println(str);
    }
}
