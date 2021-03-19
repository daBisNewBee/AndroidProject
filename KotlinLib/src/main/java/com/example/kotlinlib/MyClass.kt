package com.example.kotlinlib

/*
*
* Kotlin 基础语法
* 
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
    }
//}

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

