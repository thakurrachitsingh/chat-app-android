package com.example.bottomnavigationcomposeui.pages.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.example.bottomnavigationcomposeui.R
import com.example.bottomnavigationcomposeui.datamodel.Member
import com.example.bottomnavigationcomposeui.datamodel.response.RoomResponse
import com.example.bottomnavigationcomposeui.utils.AndroidLifecycleListener
import com.example.bottomnavigationcomposeui.utils.Screens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    userName: String,
    topBarVisibility: MutableState<Boolean>,
    bottomNavVisibility: MutableState<Boolean>,
    navController: NavHostController,
    homeViewModel: HomeViewModel
){


//    homeViewModel.ws = ws
    if(homeViewModel.getAllChatData().collectAsState().value.isEmpty()){
        LaunchedEffect(key1 = Unit) {
            homeViewModel.getUserDetails(userName)
        }
    }
    val roomLists = homeViewModel.getRooms().collectAsState()
    val haveChatsToLoad = homeViewModel.getHaveChatsToLoad().collectAsState()
    val usersList = homeViewModel.getUsersList().collectAsState()
    val allChatData = homeViewModel.getAllChatData().collectAsState()
    val chatRoomCreationState = homeViewModel.getChatRoomState().collectAsState()
    val newChatTextField = remember{ mutableStateOf("") }
    val screenState = remember { mutableStateOf("home") }
//    val chatListState = rememberLazyListState()

    AndroidLifecycleListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                homeViewModel.establishWebsocketConnection()
                Log.e("ChatScreen", "MyEventListener: ON_RESUME")
            }
//            Lifecycle.Event.ON_START ->{
//                chatViewModel.sendOnWebsocket("", args)
//            }
            Lifecycle.Event.ON_PAUSE -> {
                Log.e("ChatScreen", "HomeChatScreen: ON_PAUSE")
            }
            else -> {}
        }
    }

//    LaunchedEffect(allChatData.value){
//        chatListState.animateScrollToItem(chatListState.layoutInfo.totalItemsCount)
//    }

    Box(Modifier.fillMaxSize()){
        LazyColumn(
            content = {
                items(allChatData.value){ chatRoom ->
                    if(chatRoom.room.admin!="newChats"){
                        Rooms(chatRoom, userName, homeViewModel, navController)
                    }
//                    val roomId = chatRoom.room.roomId
//                    Row(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(0.dp, 10.dp)
//                            .clickable {
//                                homeViewModel.getAllChatData()
//                                navController.navigate(
//                                    Screens.ChatScreen(
//                                        homeViewModel.getUserDetailsResponse().userDetails.userName,
//                                        homeViewModel.getRooms().value,
//                                        roomId,
//                                        chatRoom.room.chats.toString()
//                                    )
//                                )
//                            }
//                        , verticalAlignment = Alignment.CenterVertically) {
//                        Image(painter = painterResource(id = R.drawable.profile_icon), contentDescription = "profileIcon", Modifier.size(50.dp))
//                        Text(text = if (chatRoom.room.admin==userName) chatRoom.room.members[0].userName else chatRoom.room.admin)
//                        chatRoom.room.members.map {
//                            if(it.userName==userName){
//                                Badge {
//                                    Text(text = it.unRead.toString())
//                                }
//                            }
//                        }
//                    }
                }
            }
        )
        FloatingActionButton(onClick = {
            topBarVisibility.value = false
            bottomNavVisibility.value = false
            screenState.value = "search"
        }, modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(30.dp)) {
            Image(painter = painterResource(id = R.drawable.chat_icon), contentDescription = "chatIcon")
        }
        if(haveChatsToLoad.value==""){
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }
        if(haveChatsToLoad.value=="noChats" && allChatData.value.isEmpty()){
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(text = "No Chats to Load")
            }
        }
    }
    AnimatedVisibility(visible = screenState.value =="search",
        enter = scaleIn(animationSpec = tween(1000), initialScale = 0f, transformOrigin = TransformOrigin(0.9f, 0.9f)) + fadeIn(),
        exit = scaleOut(animationSpec = tween(1000), targetScale = 0f, transformOrigin = TransformOrigin(0.9f, 0.9f)),
    ){

        LaunchedEffect(key1 = newChatTextField.value) {
            homeViewModel.searchFromUsersList(newChatTextField.value)
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(10.dp, 0.dp, 10.dp, 0.dp)){
            Column(
                Modifier
                    .fillMaxSize()) {
                Card( elevation = CardDefaults.cardElevation(20.dp, 20.dp, 20.dp, 20.dp, 20.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .padding(10.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.left_arrow), contentDescription = "backArrow",
                            modifier = Modifier.clickable {
                                screenState.value = "home"
                                topBarVisibility.value = true
                                bottomNavVisibility.value = true
                            })
                        val interactionSource = remember {
                            MutableInteractionSource()
                        }
                        Box {
                            BasicTextField(value = newChatTextField.value, onValueChange = {newChatTextField.value = it},
                                textStyle = TextStyle(fontSize = 22.sp),
                                decorationBox = @Composable { innerTextField ->
                                    TextFieldDefaults.DecorationBox(
                                        value = newChatTextField.value,
                                        innerTextField = innerTextField,
                                        enabled = false,
                                        singleLine = false,
                                        visualTransformation = VisualTransformation.None,
                                        interactionSource = interactionSource,
                                        placeholder =  { Text("Enter username") },
                                        colors = TextFieldDefaults.textFieldColors(
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                            errorIndicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                LazyColumn(content = {
                    items(usersList.value){
//                        Card(Modifier.padding(0.dp, 10.dp)) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 10.dp)
                                    .clickable {
                                        homeViewModel.createChatRoom(
                                            listOf(
                                                Member(
                                                    UUID
                                                        .randomUUID()
                                                        .toString(), it.userName
                                                ),
                                                Member(
                                                    UUID
                                                        .randomUUID()
                                                        .toString(),
                                                    homeViewModel.getUserDetailsResponse().userDetails.userName
                                                )
                                            )
                                        )
                                    }
                                , verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(id = R.drawable.profile_icon), contentDescription = "profileIcon", Modifier.size(50.dp))
                                Text(text = it.userName)
                            }
//                        }
                    }
                })
            }
            if(chatRoomCreationState.value=="created"){
                topBarVisibility.value = true
                navController.navigate(Screens.ChatScreen(
                    homeViewModel.getUserDetailsResponse().userDetails.userName,
                    roomLists.value,
                    homeViewModel.newRoomId,
                ))
                homeViewModel.setChatRoomState("")
            }
            if(chatRoomCreationState.value=="creating"){
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
//    else{
//        topBarVisibility.value = true
//        bottomNavVisibility.value = true
//    }
    BackHandler(enabled = true) {
        // Do stuff..

        if(screenState.value=="search"){
            screenState.value = "home"
            topBarVisibility.value = true
            Log.d("HomeScreen", "Back Pressed")
            bottomNavVisibility.value = true
        }
//        topBarVisibility.value = true
//        Log.d("HomeScreen", "Back Pressed")
//        bottomNavVisibility.value = true
    }
}


@Composable
fun Rooms(
    chatRoom: RoomResponse,
    userName: String,
    homeViewModel: HomeViewModel,
    navController: NavHostController
) {
    val lastIndex = chatRoom.room.chats.lastIndex
        Row(
            Modifier.clickable {
                homeViewModel.getAllChatData()
                navController.navigate(
                    Screens.ChatScreen(
                        homeViewModel.getUserDetailsResponse().userDetails.userName,
                        homeViewModel.getRooms().value,
                        chatRoom.room.roomId,
                        chatRoom.room.chats.toString()
                    )
                )
            }
        ) {
            Image(painter = painterResource(id = R.drawable.profile_icon), contentDescription = "profileIcon", Modifier.size(50.dp))
            Column() {
                Box(Modifier.fillMaxWidth()) {
                    Text(text = if (chatRoom.room.admin==userName) chatRoom.room.members[0].userName else chatRoom.room.admin, modifier = Modifier.align(Alignment.TopStart))
                    if(lastIndex!=null  && lastIndex>=0){
                        Text(text = SimpleDateFormat("hh:mm").format(Date(chatRoom.room.chats[lastIndex].time)), Modifier.align(Alignment.TopEnd))
                    }
                }
                Box(Modifier.fillMaxWidth()) {
                    if(lastIndex!=null  && lastIndex>=0) {
                        Text(text = chatRoom.room.chats[lastIndex].chat, modifier = Modifier.align(Alignment.TopStart))
                    }
                    chatRoom.room.members.map {
                        if(it.userName==userName && it.unread!=null && it.unread!! >0){
                            Badge(Modifier.align(Alignment.TopEnd)) {
                                Text(text = it.unread.toString())
                            }
                        }
                    }
                }
            }
        }
}