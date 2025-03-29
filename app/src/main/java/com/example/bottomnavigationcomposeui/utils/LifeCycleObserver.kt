package com.example.bottomnavigationcomposeui.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent


open class LifeCycleObserver : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(): Boolean {
        // Handle onResume
        println("Activity resumed")
        return false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
        // Handle onStart (useful for onRestart equivalent)
        println("Activity started")
    }
}