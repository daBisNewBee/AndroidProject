package com.example.kotlinlib

import java.lang.IllegalArgumentException

/**
 *
 * Created by wenbin.liu on 2021/3/23
 *
 * 基本数据类型
 *
 * 几个注意点：
 * 1. 下划线分隔符，数字较长时，比较方便: xxx_xxx
 * 2. === 对象地址比较，== 对象值比较
 * 3. kt没有npe，是因为全是对象！没有基本数据类型，其已被封装为对象！
 * 4. 数组的两种创建方式：arrayOf、Array
 * 5. 位操作符shl shr xor or and inv
 * 6. Kotlin 的基本数值类型包括 Byte、Short、Int、Long、Float、Double 等。
 *    不同于 Java 的是，字符不属于数值类型，是一个独立的数据类型
 *
 * TODO:
 * Kotlin 中数组是不协变的
 *
 * @author wenbin.liu
 */

fun main(args:Array<String>) {

    var million = 1_000_000
    var millions = 1000000
    println(million)
    println(millions)
    var creditCardNumber = 1_234_5678_910L
    val hexBytes = 0xEE_12_D_1
    val bytes = 0b11_0101_111_000

    // 比较两个数字
    // "Kotlin 中没有基础数据类型，只有封装的数字类型，你每定义的一个变量，其实 Kotlin 帮你封装了一个对象，这样可以保证不会出现空指针。数字类型也一样，所以在比较两个数字的时候，就有比较数据大小和比较两个对象是否相同的区别了。"
    val a:Int = 1000
    println(a === a)

    val boxedA:Int? = a
    val anotherBoxedA:Int? = a
    println("对象地址比较 === " + (boxedA === anotherBoxedA))
    println("值比较 == " + (boxedA == anotherBoxedA))

    var b:Byte = 1
//    var i:Int = b // 错误。较小的类型不能隐式转换为较大的类型
    var i:Int = b.toInt() // 正确

    val l = 1L + 3 // 此时可以自动类型转化：Long + Int => Long

    // 位操作
    1 shl 3 // 左移位 <<
    2 shr 2 // 右移位 >>
    // 与
    1 and 2
    // 或
    2 or 1
    // 异或
    1 xor 2
    // 反向
    3.inv()

    println("字符字面值用单引号括起来: '1'")
    try {
        for (c in "56789abc") print(decimalDigitValue(c))
    } catch (e:Exception) {
        println(e)
    }

    arrayTest()

    for (c in "abcdef") print(c)

    var text = """
        1111
        2222
        3333
        """ // 输出有一些前置空格
    println(text)

    val textx = """
    |多行字符串
    |菜鸟教程
    |多行字符串
    |Runoob
    """.trimMargin() // 默认 | 用作边界前缀，也可以选择其他字符并作为参数传入，比如 trimMargin(">")。
    println(textx)

    val s = "runnable"
    var str = "$s length is ${s.length}"
    println(str)

    var price = "${'$'}:9.99"
    println(price)
}

fun arrayTest() {
    // 数组的创建两种方式：
    // 1. 一种是使用函数arrayOf()；
    val a = arrayOf(1,2,3)
    // 2. 另外一种是使用工厂函数
    val b = Array(size = 3, init = {i -> (i * 2)})
    val c = Array(3) { i -> (i * 2)}

    for (i in a) print(i)
    println()
    b[1] = 100
    for (i in b) print(i)
    println()
    for (i in c) print(i)

    println()
    // "Kotlin 中数组是不协变的（invariant）"
    val x:IntArray = intArrayOf(1,2,3)
    // [] 运算符代表调用成员函数 get() 和 set()。
    x[0] = x[1] + x[2]
    println(x[0])
}

fun decimalDigitValue(c:Char):Int {
    if (c !in '0'..'9') {
        throw IllegalArgumentException("不是有效的数字字符")
    }
    return c.toInt() - '0'.toInt(); // 显式转换为数字

}