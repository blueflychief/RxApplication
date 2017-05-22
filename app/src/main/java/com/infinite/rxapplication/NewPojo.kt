package com.infinite.rxapplication

/**
 * Created by lsq on 5/22/2017.
 */
class NewPojo(args1: Int, args2: Int) : OldPojo(args1, args2) {
    fun getNewResult(): Int {
        return result + 5
    }
}
