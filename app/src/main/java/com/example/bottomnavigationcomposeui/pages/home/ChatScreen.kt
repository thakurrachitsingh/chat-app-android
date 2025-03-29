package com.example.bottomnavigationcomposeui.pages.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bottomnavigationcomposeui.R
import com.example.bottomnavigationcomposeui.datamodel.Chat
import com.example.bottomnavigationcomposeui.datamodel.response.RoomResponse
import com.example.bottomnavigationcomposeui.utils.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun ChatScreen(
    args: Screens.ChatScreen,
    chatsData: MutableStateFlow<List<RoomResponse>>,
    homeViewModel: HomeViewModel
) {
    homeViewModel.setCurrentRoomId(args.roomId)
    homeViewModel.setupChatScreen()
    val textField = remember{ mutableStateOf("") }
    val chatViewModel : ChatViewModel = viewModel()
    chatViewModel.ws = homeViewModel.ws
    LaunchedEffect(key1 = Unit) {
        chatViewModel.fetchChats(args.roomId, args)
    }
//    chatViewModel.userDetails = args.userDetails
//    val homeViewModel: HomeViewModel = viewModel()
    val isSendMessage = remember { mutableStateOf(false) }
    val chatList = remember{ mutableStateOf(emptyList<Chat>()) }
    val unseenCount = remember{ mutableStateOf(0) }
    val unreceivedCount = remember { mutableStateOf(0) }
    val x = homeViewModel.getAllChatData().collectAsState()
    val chatListState = rememberLazyListState()
    x.value.map { rooms ->
        if(rooms.room.roomId==args.roomId){
            chatList.value = rooms.room.chats.filter {
                it.userName!="newChats"
            }
            rooms.room.members.map {
                if(it.userName!=args.userName){
                    unseenCount.value = it.unread!! ?:0
                    unreceivedCount.value = it.unrecieved!! ?:0
                }
            }
        }
    }
    if(isSendMessage.value) {
            LaunchedEffect(key1 = Unit) {
                chatViewModel.sendOnWebsocket(textField.value, args)
                textField.value = ""
            }
    }
    LaunchedEffect(key1 = Unit) {
        chatViewModel.initialization(args)
    }

    LaunchedEffect(chatList.value){
        chatListState.animateScrollToItem(chatListState.layoutInfo.totalItemsCount)
    }

//    AndroidLifecycleListener {
//        when (it) {
//            Lifecycle.Event.ON_RESUME -> {
//                chatViewModel.sendOnWebsocket("", args)
//                Log.e("ChatScreen", "MyEventListener: ON_RESUME")
//            }
////            Lifecycle.Event.ON_START ->{
////                chatViewModel.sendOnWebsocket("", args)
////            }
//            Lifecycle.Event.ON_PAUSE -> {
//                Log.e("ChatScreen", "HomeChatScreen: ON_PAUSE")
//            }
//            else -> {}
//        }
//    }

    Box(modifier = Modifier.fillMaxSize()){
        Column(Modifier.fillMaxSize()) {
            LazyColumn(state = chatListState,
                flingBehavior = ScrollableDefaults.flingBehavior(),
                modifier = Modifier
                    .weight(10f)
                    .fillMaxSize(), content = {
                        itemsIndexed(chatList.value){index , it ->
                            if(args.userName==it.userName){
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                                    Chat(chatList, it, RoundedCornerShape(20.dp, 20.dp, 0.dp, 20.dp), index, unseenCount, unreceivedCount)
//                                    Text(text = it.chat, Modifier.background(Color.Yellow))
                                }
                            }else {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                    Chat(chatList, it, RoundedCornerShape(20.dp, 20.dp, 20.dp, 0.dp), -1, unseenCount, unreceivedCount)
                                }
                            }
                        }
                })
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.weight(1f)){
                TextField(value = textField.value, onValueChange = { textField.value = it }, Modifier.weight(0.3f))
                Button(onClick = {
                    if(textField.value.isNotEmpty()){
                        chatViewModel.sendMessage(textField.value, args)
                    }
//                    isSendMessage.value = !isSendMessage.value
                }, Modifier.weight(0.1f)) {
                    Text(text = "Send")
                }
            }
        }
    }
    val context = LocalContext.current
    DisposableEffect(context){
        onDispose {
            homeViewModel.setCurrentRoomId("")
        }
    }
}

@Composable
fun Chat(chatList: MutableState<List<Chat>>, chat: Chat, shape: RoundedCornerShape, index: Int, unseenCount: MutableState<Int>, unreceivedCount: MutableState<Int>){
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .padding(10.dp, 5.dp)
            .clip(shape = shape)) {
        Column(
            Modifier
                .padding(10.dp)
                .widthIn(min = 125.dp, max = 250.dp)) {
            Text(text = chat.chat)
            Row(Modifier.align(Alignment.End)) {
                Text(text = SimpleDateFormat("hh:mm").format(Date(chat.time)), fontSize = 10.sp)
                if(index!=-1 && chatList.value.size-index > (unreceivedCount.value+unseenCount.value)) {
                    Image(painter = painterResource(id = R.drawable.double_tick, ), contentDescription = "Double Tick", Modifier.size(20.dp), colorFilter = ColorFilter.tint(Color.Blue))
                }else if(index!=-1 && chatList.value.size-index > unreceivedCount.value){
                    Image(painter = painterResource(id = R.drawable.double_tick), contentDescription = "Single Tick", Modifier.size(20.dp))
                }else if(index!=-1){
                    Image(painter = painterResource(id = R.drawable.single_tick), contentDescription = "Single Tick", Modifier.size(20.dp))
                }
            }
        }
    }
}