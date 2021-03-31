package com.example.kotlinlib

/**
 *
 * Created by wenbin.liu on 2021/3/27
 *
 * @author wenbin.liu
 */

class Person{
    // 延迟初始化，可以暂时为空
    lateinit var lastValue:String

    var lastName:String = "zhang"
    get() = field.toUpperCase()
    set

    var no:Int = 100
    get() = field  // 后端变量
    set(value){
        if (value < 10) {
            field = value
        } else {
            field = -1
        }
    }

    var height = 145.6f
    private set
}

// lateinit 只用于变量 var，而 lazy 只用于常量 val
val lazyValue:String by lazy {
    println("lazyValue call!!")
    "Hello"
}

lateinit var lateValue: String

fun main(args:Array<String>) {

    // Kotlin 中没有 new 关键字
    var person:Person = Person()
    person.lastName = "wang"
    println("lastName:${person.lastName}")

    person.no = 5
    println("no:${person.no}")

    person.no = 100
    println("no:${person.no}")

    // person.height = 200.0f
    println("1111 lazyValue:$lazyValue")
    println("2222 lazyValue:$lazyValue")

    println("lateValue:$lateValue")
}