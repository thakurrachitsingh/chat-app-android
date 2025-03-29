package com.example.bottomnavigationcomposeui.pages.splash

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bottomnavigationcomposeui.datamodel.NavigationBarItem
import com.example.bottomnavigationcomposeui.pages.home.ChatScreen
import com.example.bottomnavigationcomposeui.pages.home.Home
import com.example.bottomnavigationcomposeui.pages.home.HomeViewModel
import com.example.bottomnavigationcomposeui.pages.more.More
import com.example.bottomnavigationcomposeui.pages.mycloset.MyCloset
import com.example.bottomnavigationcomposeui.pages.referafriend.ReferAFriend
import com.example.bottomnavigationcomposeui.utils.Screens

//lateinit var ws: WebSocket
//lateinit var startViewModel: StartViewModel
lateinit var homeViewModel: HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartPoint(userName: String, controller: NavHostController) {
//    startViewModel = viewModel()
    homeViewModel = viewModel()
//    LaunchedEffect(key1 = Unit) {
//        startViewModel.getUserDetails(userName)
//    }
//    val tempChatData = startViewModel.chatsData().collectAsState()
//    chatsData = MutableStateFlow(tempChatData.value)
    val selectedScreenIndex = rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()
    val listOfScreens = listOf(
        NavigationBarItem(title = "Home", Screens.Home, icon = Icons.Default.Home, selected = true, hasContent = false),
        NavigationBarItem(title = "My Account", Screens.MyCloset, icon = Icons.Default.FavoriteBorder, badgeCount = 5, selected = false, hasContent = false)
    )
    val topBarVisibility = remember { mutableStateOf(true) }
    val bottomNavVisibility = rememberSaveable { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AnimatedVisibility(visible = topBarVisibility.value,
                exit = slideOutVertically(animationSpec = tween(700)) + fadeOut(),
                enter = slideInVertically(animationSpec = tween(700)) + fadeIn()
            ) {
                TopAppBar(title = { Text(text = "Hello Android") },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = bottomNavVisibility.value,
                exit = slideOutVertically(animationSpec = tween(400), targetOffsetY = { 800 }) + fadeOut(),
                enter = slideInVertically(animationSpec = tween(400)) + fadeIn(),
            ){
                NavigationBar {
                    listOfScreens.forEachIndexed { index, navigationBarItem ->
                        NavigationBarItem(selected = selectedScreenIndex.intValue == index,
                            onClick = { selectedScreenIndex.intValue = index
                                if(navigationBarItem.screenName==Screens.MyCloset){
                                    homeViewModel.performLogout()
                                    mAuth.signOut()
                                    controller.popBackStack()
                                }else{
                                    navController.navigate(navigationBarItem.screenName)
                                    {
                                        popUpTo(navController.graph.findStartDestination().id){
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                      },
                            icon = {
                                BadgedBox(badge = {
                                    if(navigationBarItem.badgeCount != null) {
                                        Badge(){
                                            Text(text = navigationBarItem.badgeCount.toString())
                                        }
                                    }else if(navigationBarItem.hasContent) {
                                        Badge()
                                    }
                                }) {
                                    Icon(imageVector = navigationBarItem.icon, contentDescription = navigationBarItem.title)
                                }
                            },
                            label = { Text(text = navigationBarItem.title) }
                        )
                    }
                }
            }
        }
    ) {padding ->
        Log.d("padding", "Greeting: $padding")
        val paddingForTopAppBar = animateDpAsState(targetValue = if (!topBarVisibility.value) 41.5.dp else padding.calculateTopPadding(), animationSpec = tween(1000))
        val paddingForTopBottomBar = animateDpAsState(targetValue = if (!topBarVisibility.value) 24.dp else padding.calculateBottomPadding(), animationSpec = tween(1000))
        NavHost(navController = navController, startDestination = Screens.Home, modifier = Modifier.padding(top = paddingForTopAppBar.value, bottom = paddingForTopBottomBar.value)) {
            composable<Screens.Home>(enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, spring())
            },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, spring(2f))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, spring(2f))
                }) {
                Home(userName, topBarVisibility, bottomNavVisibility, navController, homeViewModel)
            }
            composable<Screens.MyCloset>(){
                MyCloset()
            }
            composable<Screens.ReferFriend>() {
                ReferAFriend()
            }
            composable<Screens.More>() {
                More()
            }
            composable<Screens.ChatScreen>(
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, spring(2f)) + fadeIn()
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, spring(2f))
                }
            ){
                val args = it.toRoute<Screens.ChatScreen>()
                ChatScreen(args, chatsData, homeViewModel)
            }
        }
    }
}