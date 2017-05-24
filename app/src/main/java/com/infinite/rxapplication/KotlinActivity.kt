package com.infinite.rxapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_kotlin.*

class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
//        tvResult.text = NewPojo(1, 9).getNewResult().toString()
        tvResult.text = test(2).toString()
        loopTest()


        NewPojo(1, 3).swap(1, 3)

        step(arrayOf("A", "B", "C"))
    }

    fun step(args: Array<String>) {
        for (i in 1..100 step 20) {
            print("$i ")
        }
    }

    fun NewPojo.swap(num1: Int, num2: Int) {

    }


    fun test(num1: Int, num2: Int = 20): Int {
        return num1 + num2
    }


    fun loopTest() {
        var array1: IntArray = intArrayOf(12, 13, 14, 15, 16)
        for (i in array1) {
            println(i)
        }

        var charArray: CharArray = charArrayOf('A', 'B')
        var result: Int
        for (c in charArray) {
            result = c.toInt()
            println("result is " + result)
        }
    }

    fun <T> asList(vararg ts: T): List<T> {
        val result = ArrayList<T>()
        for (t in ts) // ts 是一个 Array
            result.add(t)
        return result
    }
}
