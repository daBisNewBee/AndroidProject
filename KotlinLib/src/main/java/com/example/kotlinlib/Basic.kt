package com.example.kotlinlib

/*
*
* Kotlin 基础语法
* https://www.runoob.com/kotlin/kotlin-basic-syntax.html
*
* */
//class MyClass {
    fun main(args: Array<String>){
        println("FUCK")
        var ret = sum(1,2)
        println("ret: $ret")
        ret = sum1(1,2)
        println("ret: $ret")
        ret = sum2(1,2)
        println("ret: $ret")
        vars(1,2,3,4,5)
        // lambda(匿名函数)
        var sumLambda:(Int, Int)->Int = {x,y -> x+y}
        println(sumLambda(3,7))

        // "val" 和 "var" 的区别
        // var <标识符> : <类型> = <初始化值>
        // var cc:Int = 2
        val c = 1 // 自动推断类型为Int
        println("这是val类型不可变变量:$c");
        // c = 2 // 赋值失败

        var b = 2
        b = 3; // 可变变量，可多次赋值

        var a = 1
        val s1 = "a is $a"
        a = 2
        // 模板中的任意表达式
        val s2 = "${s1.replace("is", "was")}, but now is $a"
        println(s2)

        // NULL检查机制
//        var age:String=null
        //类型后面加?表示可为空
        var age: String?="23"
        println(message = age)
        //抛出空指针异常
        val ages = age!!.toInt()
        println(ages)
        //不做处理返回 null
        val ages1 = age?.toInt()
        //age为空返回-1
        val ages2 = age?.toInt() ?: -1

        randExpressionTest()
}
//}

fun sum(args:Array<String>) {
    if (args.size < 2) {
        return
    }
    var arg1 = parseInt(args[0])
    var arg2 = parseInt(args[1])
        // println(arg1 * 2) // 未做null检查，会报错
    if (arg1 != null && arg2 != null) {
        // 在进行过 null 值检查之后, x 和 y 的类型会被自动转换为非 null 变量
        println(arg1 * arg2)
    }
}

// "当一个引用可能为 null 值时, 对应的类型声明必须明确地标记为可为 null"
fun parseInt(str:String): Int? {
    return null
}

// 区间
fun randExpressionTest() {
    println("==== 遍历")
    for (i in 1..4) print(i)
    println("==== 无输出")
    for (i in 4..1) print(i)
    println("==== 遍历 ")
    for (i in 1..4) {
        println(i)
    }
    println("==== 步长2")
    for (i in 1..4 step 2) print(i)
    println("==== 逆序")
    for (i in 4 downTo 1 step 2) print(i)
    println("排除 10 ====")
    for (i in 1 until 10) print(i)
}

// 类型检测及自动类型转换
fun getStringLength(obj:Any):Int? {
    // 类似于 instanceof
    if (obj is String) {
        return obj.length
    }
    // `obj` 的类型会被自动转换为 `String`
    if (obj is String && obj.length > 0) {
    }
    if (obj !is String) {
        //
    }
    return null
}

public fun sum2(a: Int, b:Int):Int = a + b

fun sum1(a: Int, b: Int) = a + b

fun sum(a:Int, b:Int): Int{
    return a + b
}

// 可变长参数函数
fun vars(vararg v:Int) {
    for (i in v) {
        print(i)
    }
    println()
}

