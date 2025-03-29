package com.example.bottomnavigationcomposeui.pages.splash

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.bottomnavigationcomposeui.utils.LifeCycleObserver
import com.google.firebase.auth.FirebaseAuth

open class ScreenLifecycleObserver(val mAuth: FirebaseAuth, val sharedPreferences: SharedPreferences) :
    LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(): Boolean {
        // Handle onResume
        if(sharedPreferences.getBoolean("userLoggedIn", false)){
            Log.d("Splash", "User is Resumed")
            return true
        }
        return false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
        // Handle onStart (useful for onRestart equivalent)
        Log.d("Splash", "Activity started")
    }
}