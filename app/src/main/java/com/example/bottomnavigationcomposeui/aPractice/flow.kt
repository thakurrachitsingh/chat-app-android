package com.example.bottomnavigationcomposeui.aPractice

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


fun main(args: Array<String>){
    val flow1 = flowOf(1, 2, 3, 4, 5)
    val flow2 = flowOf("a", "b", "c", "d", "e")

    GlobalScope.launch {
        flow<Int>{
            for(i in 1..5){
                delay(1000)
                emit(i)
            }
        }.collectLatest {
            Log.d("flow", "collectLatest: $it")
        }
    }


//    GlobalScope.launch {
//        flow1
//            .collectLatest{
//                println(it)
//            }
//    }
}