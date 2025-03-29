package com.example.bottomnavigationcomposeui.pages.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bottomnavigationcomposeui.MainActivity
import com.example.bottomnavigationcomposeui.R
import com.example.bottomnavigationcomposeui.utils.screens.SplashToHomeScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun LoginScreen(
    modifier: Modifier,
    navController: NavController,
    mainActivity: MainActivity,
    mAuth: FirebaseAuth, ){
    var isLogin by remember { mutableStateOf("Login") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val name = remember{ mutableStateOf("") }
    val userName = remember{ mutableStateOf("") }
    var progressBarVisibility = remember { mutableStateOf<Boolean?>(false) }
    var response = remember { mutableStateOf<Boolean?>(null) }
    val loginViewModel: LoginViewModel = viewModel()
    loginViewModel.activityContext = mainActivity
    loginViewModel.mAuth = mAuth
    LaunchedEffect(key1 = response.value) {
        if(response.value == true){
            navController.navigate(SplashToHomeScreen.StartPoint(userName.value))
        }
    }
//    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()
//    val pattern = remember { Regex("^\\d+\$") }
    val text = buildAnnotatedString {
        append(if (isLogin=="Login") "Don't have an account? " else "Already have an account? ")
        pushStringAnnotation(tag = "click", annotation = "click")
        withStyle(
            SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = Color.Blue
            )
        ) {
            append(if (isLogin=="Login") "Create new" else "Login now")
        }
        pop()
    }
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Gray)){
        Column(modifier =  modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.product_icon) , modifier = Modifier.padding(0.dp, 100.dp).size(50.dp), contentDescription = "Product Icon")
            Text(text = isLogin , modifier = Modifier.padding(bottom = 15.dp))
            TextField(singleLine = true, value = email,
                placeholder = { Text("Email") },
                onValueChange = {
//                    if(it.isEmpty() || it.matches(emailRegex))
                        email = it
                    }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next), modifier = Modifier.padding(bottom = 15.dp))
            TextField(singleLine = true, value = userName.value, onValueChange = {
                userName.value = it
            }, placeholder = { Text("Username") }, modifier = Modifier.padding(bottom = 15.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
            TextField(singleLine = true,
                value = password,
                placeholder = { Text("Password") },
                onValueChange = {
                    password = it
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go), modifier = Modifier.padding(bottom = 15.dp))
            Button(onClick = {
                progressBarVisibility.value = true
                if(isLogin=="Login") {
                    loginViewModel.login(email, password, response, userName.value)
                } else{
                    loginViewModel.signUp(email, password, response, userName.value)
                }
//                navController.navigate(SplashToHomeScreen.StartPoint)
            }) {
                Text(text = isLogin)
            }
            ClickableText(text = text, onClick = { offset ->
                text.getStringAnnotations(tag = "click", start = offset, end = offset).firstOrNull()
                    ?.let {
                        // on click operation here
                        isLogin = if(isLogin=="Login") "Sign Up" else "Login"
                    }
            })
//            Button(onClick = { isLogin = if(isLogin=="Login") "Sign Up" else "Login" }) {
//                Text(text = isLogin)
//            }
            if(progressBarVisibility.value == true){
                CircularProgressIndicator()
            }
        }
    }
}

