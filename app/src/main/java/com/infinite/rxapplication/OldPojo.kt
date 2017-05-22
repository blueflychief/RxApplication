package com.infinite.rxapplication

/**
 * Created by lsq on 5/22/2017.
 */
open class OldPojo(args1: Int, args2: Int) {
    var result: Int = 10

    init {
        result = add(args1, args2)
    }


    fun parseResult(): Int {
        return result
    }

    fun add(num1: Int, num2: Int): Int {
        return num1 + num2
    }
}
