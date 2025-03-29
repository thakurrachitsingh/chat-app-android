package com.example.bottomnavigationcomposeui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.bottomnavigationcomposeui.ui.theme.BottomNavigationComposeUITheme
import com.example.bottomnavigationcomposeui.utils.Screens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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