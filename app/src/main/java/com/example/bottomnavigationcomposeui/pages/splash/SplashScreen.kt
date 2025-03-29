package com.example.bottomnavigationcomposeui.pages.splash

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bottomnavigationcomposeui.MainActivity
import com.example.bottomnavigationcomposeui.pages.login.LoginScreen
import com.example.bottomnavigationcomposeui.utils.screens.SplashToHomeScreen
import com.example.bottomnavigationcomposeui.R
import com.example.bottomnavigationcomposeui.pages.login.LoginViewModel
import com.example.bottomnavigationcomposeui.pages.login.OTPScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
@Composable
fun SplashScreen(modifier: Modifier, mainActivity: MainActivity) {
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = SplashToHomeScreen.Splash){
        composable<SplashToHomeScreen.Splash> {
            Splash(controller, mainActivity)
        }
        composable<SplashToHomeScreen.Login> {
            LoginScreen(modifier = modifier, controller, mainActivity, mAuth)
        }
        composable<SplashToHomeScreen.OTPScreen> {
            val args = it.toRoute<SplashToHomeScreen.OTPScreen>()
            OTPScreen(modifier = modifier, args.phoneNumber)
        }
        composable<SplashToHomeScreen.StartPoint>{
            val args = it.toRoute<SplashToHomeScreen.StartPoint>()
            StartPoint(args.userName, controller)
        }
    }
}

@Composable
fun Splash(navController: NavController, mainActivity: MainActivity){
    val counter = remember{ mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    val sharedPreferences = mainActivity.getSharedPreferences("userSharedPref", Context.MODE_PRIVATE)
    val observer = ScreenLifecycleObserver(mAuth, sharedPreferences)
    val loginViewModel: LoginViewModel = viewModel()
    val response = remember{ mutableStateOf<Boolean?>(null) }
    LaunchedEffect(key1 = true) {
        loginViewModel.mAuth = mAuth
        loginViewModel.activityContext = mainActivity
//        if(sharedPreferences.contains("userLoggedIn")){
//            val email = sharedPreferences.getString("email", "null")
//            val password = sharedPreferences.getString("password", "null")
//            if (email != null && password!=null) {
//                loginViewModel.login(email, password, response)
//            }
//        }else{
//            response.value = false
//        }
        if(mAuth.currentUser!=null){
            Toast.makeText(mainActivity, "User Logged In", Toast.LENGTH_LONG).show()
            val userName = sharedPreferences.getString("userName", "null")
            if(userName!=null && userName!="null"){
                navController.navigate(SplashToHomeScreen.StartPoint(userName))
            }
        }else{
            navController.navigate(SplashToHomeScreen.Login)
        }
    }
//    LaunchedEffect(key1 = response.value) {
//        if(response.value == true){
//            Toast.makeText(mainActivity, "User Logged In", Toast.LENGTH_LONG).show()
//            navController.navigate(SplashToHomeScreen.StartPoint)
//        }else if(response.value == false){
//            navController.navigate(SplashToHomeScreen.Login)
//        }
//    }
    val producedState = produceState(initialValue = 0f ) {
        while(true){
            delay(30)
            value = (value +30) %360
            if(value==0f){
                counter.intValue+=1;
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.product_icon), contentDescription = "Product Icon", modifier = Modifier.size(100.dp))
        Image(painter = painterResource(id = R.drawable.ic_loading), contentDescription = "Loading Icon", modifier = Modifier
            .padding(0.dp, 100.dp, 0.dp, 0.dp)
            .size(50.dp)
            .rotate(producedState.value))
    }
}