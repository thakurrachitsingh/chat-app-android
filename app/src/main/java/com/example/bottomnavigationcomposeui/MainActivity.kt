package com.example.bottomnavigationcomposeui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bottomnavigationcomposeui.datamodel.NavigationBarItem
import com.example.bottomnavigationcomposeui.pages.home.ChatScreen
import com.example.bottomnavigationcomposeui.pages.home.Home
import com.example.bottomnavigationcomposeui.pages.more.More
import com.example.bottomnavigationcomposeui.pages.mycloset.MyCloset
import com.example.bottomnavigationcomposeui.pages.referafriend.ReferAFriend
import com.example.bottomnavigationcomposeui.pages.splash.SplashScreen
import com.example.bottomnavigationcomposeui.pages.splash.StartViewModel
import com.example.bottomnavigationcomposeui.ui.theme.BottomNavigationComposeUITheme
import com.example.bottomnavigationcomposeui.utils.Screens
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class MainActivity : ComponentActivity() {
    lateinit var job1: Deferred<Unit>
    lateinit var job2: Job
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),0)
        val flow1 = flowOf(1, 2, 3, 4, 5).onEach { delay(600) }
        val flow2 = flowOf("a", "b", "c", "d", "e").onEach { delay(800) }
        val state = MutableStateFlow("")




//        val parentJob = CoroutineScope(Dispatchers.IO).launch{
//            job1 = async{ flow1.collectLatest {
//                Log.d("mainActivity1", it.toString())
//
//                }
//                "sv"
//            }
//            job2 = launch { flow2.collect{
//                Log.d("mainActivity2", it)
//            } }
//            delay(2000)
//            job1.await()
//            Log.d("mainActivity1", "job1 done")
//            job2.join()
//            Log.d("mainActivity2", "job2 done")
//
//        }
//
//        MainScope().launch(){
//
//        }
//        GlobalScope.launch {
//            delay(2500)
//            parentJob.cancel()
//
//        }
//        lifecycleScope.launch(Dispatchers.Main) {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                Log.d("mainActivity", currentCoroutineContext().toString())
//            }
//        }


//
//        runBlocking {
//            delay(5000)
//            Log.d("mainActivity", "running blocking ${currentCoroutineContext()}")
//        }
//        Log.d("mainActivity", "after blocking")


//        GlobalScope.launch {
//            flow{
//                for(i in 1..5){
//                    delay(200)
//                    emit(i)
//                }
//            }.retryWhen {cause, attempt ->
//                 attempt<3
////                .conflate()
////                .collect {
////                delay(500)
////                Log.d("flow", "collectLatest: $it")
//            }
//        }

        val handler = CoroutineExceptionHandler{ _, expection ->
            Log.d("mainActivity", "error: ${expection.message}")
        }
        val superVisorJob = SupervisorJob()
        val flow = MutableSharedFlow<Int>(5)
        val floww = flow.asSharedFlow()


//        GlobalScope.launch {
//            flow.emit(1)
//            delay(1000)
//            flow.emit(2)
//            delay(1000)
//            flow.emit(3)
//            delay(1000)
//            flow.emit(4)
//            delay(800)
//            flow.emit(5)
//        }

        val supervisor = GlobalScope.launch(Dispatchers.IO + superVisorJob) {
//            delay(2000)
//            flow.collect{
//                Log.d("mainActivity", "collect: $it")
//            }

//        supervisorScope {
//            val job1 = async{
//                delay(200)
//                Log.d("mainActivity", "job1 done")
//            }
//            val job2 = async(handler){
//                delay(800)
////                throw Exception("error")
//                Log.d("mainActivity", "job2 done")
//            }
//            val job3 = async{
//                delay(1000)
//                Log.d("mainActivity", "job3 done")
//            }
//            Log.d("mainActivity", "supervisor done")
//        }

        //withTimeout
//        try {
//            val response = withTimeout(2000){
//                delay(2900)
//                "answer"
//            }
//        }catch (e: Exception){
//            Log.d("mainActivity", "error:${e}")
//        }
//        Log.d("mainActivity", "response:")
////        Log.d("mainActivity", "response: $response")
    }

//        GlobalScope.launch {
//            delay(600)
//            supervisor.cancel()
//        }
        enableEdgeToEdge()
        setContent {
            BottomNavigationComposeUITheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    SplashScreen(
                        modifier = Modifier.padding(innerPadding),
                        this
                    )
                }
            }
        }
    }
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    BottomNavigationComposeUITheme {
//        Greeting("Android")
//    }
//}

suspend fun extra(){
    withContext(Dispatchers.IO){
        delay(1000)
        Log.d("mainActivity", "extra: $coroutineContext")
    }
}

