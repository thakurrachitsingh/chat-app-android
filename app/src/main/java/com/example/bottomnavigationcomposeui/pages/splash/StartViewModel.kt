package com.example.bottomnavigationcomposeui.pages.splash

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.example.bottomnavigationcomposeui.datamodel.Chat
import com.example.bottomnavigationcomposeui.datamodel.ChatRoomBody
import com.example.bottomnavigationcomposeui.datamodel.MessageModal
import com.example.bottomnavigationcomposeui.datamodel.response.RoomResponse
import com.example.bottomnavigationcomposeui.datamodel.response.UserDetailsResponse
import com.example.bottomnavigationcomposeui.pages.home.util.KtorClient
import com.example.bottomnavigationcomposeui.utils.Screens
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.text.SimpleDateFormat
import java.util.Date

var chatsData = MutableStateFlow<List<RoomResponse>>(emptyList())
@HiltViewModel
class StartViewModel: ViewModel() {
    private var client: KtorClient = KtorClient()
        private var ws: WebSocket
        fun getWS() = ws
    init {
        val client = OkHttpClient()
        val request = Request.Builder().url("wss://chat-app-backend-production-7f36.up.railway.app").build()
        val listener = WebSocketListener()
        ws = client.newWebSocket(request, listener)
    }

    fun chatsData() = chatsData

    fun establishWebsocketConnection(args: Screens.ChatScreen){
        try {
//            val user = UserForWebSocket(userDetailsResponse.userDetails.userName, rooms.value[0], rooms.value)
//            val dataToBeSend = MessageModal(user, "")
            val roomIds = args.roomIds
            val x = ws.send(
                """{
     "user" : {
         "name" : "${args.userName}",
         "roomId" : "${args.roomId}",
         "roomIds" : ["$roomIds"]
     },
     "message": ""
 }"""
            )
            Log.d("ChatViewModel", "Data sent on websocket: $x")
        }catch (e: Exception){
            Log.d("ChatViewModel", "Error: $e")
        }
    }

    fun getUserDetails(userName: String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = client.client.get("https://chat-app-backend-db.vercel.app/user/$userName/getUserDetails").body<UserDetailsResponse>()
            val listOfRoomIds = ArrayList<String>()
            for(i in response.userDetails.roomIds){
                listOfRoomIds.add(i.roomId)
            }
            if(listOfRoomIds.isNotEmpty()){
                loadAllChats(listOfRoomIds)
            }
        }
    }

    private fun loadAllChats(rooms: List<String>){
        rooms.map { rooms ->
            getRoomChat(rooms)
        }
    }

    private fun getRoomChat(roomId: String){
        CoroutineScope(Dispatchers.IO).launch{
            val response = client.client.get("https://chat-app-backend-db.vercel.app/user/$roomId/getRoom").body<RoomResponse>()
            val chatsToAdd = chatsData.value.toMutableList().apply {
                add(response)
            }.toList()
            chatsData.value = chatsToAdd
//            Log.d("HomeViewModel", response)
        }
    }
}

class WebSocketListener() : okhttp3.WebSocketListener() {
    override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
        super.onMessage(webSocket, text)
        addReceivedMessage(text)
        Log.d("WebSocketListener", "Received Message: $text")
    }

    override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d("WebSocketListener", "connection closed")
    }

    override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d("WebSocketListener", "connected")
    }

    override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: okhttp3.Response?) {
        super.onFailure(webSocket, t, response)
        Log.d("WebSocketListener", "failureMessage"+t.toString()+" "+response.toString())
    }

    override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
        super.onOpen(webSocket, response)
//        webSocket.send("Hello, it's me")
        Log.d("WebSocketListener", "onOpen: ${response.body?.string()}")
    }

    private fun addReceivedMessage(text: String) {
//        val response = Gson().fromJson(text, MessageModal::class.java)
//        val simpleDateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//        var temparoryData = chatsData.value
//        for( i in 0..chatsData.value.size-1){
//            if(chatsData.value[i].room.roomId==response.user.roomId){
//                val newChats = chatsData.value.toMutableList()[i].room.chats.toMutableList().apply {
//                    add(Chat(response.user.name, response.message, simpleDateFormat.format(Date())))
//                }.toList()
////                var chatsToAdd = Gson().fromJson(i.jsonObject.values.elementAt(2).jsonObject.values.elementAt(4).jsonPrimitive.content , Array<Chat>::class.java).toList()
////                chatsToAdd = chatsToAdd.toMutableList().apply {
////                    add(Chat(response.user.name, response.message, simpleDateFormat.format(Date())))
////                }.toList()
//                temparoryData[i].room.chats = newChats
//            }
//        }
//        temparoryData = temparoryData.toMutableList().apply {
//            add(RoomResponse(0, "", ChatRoomBody("response.user.roomId", "newChats", emptyList())))
//        }
//        chatsData.value = temparoryData
////        val existingData = Gson().fromJson(Json.parseToJsonElement(chatsData.value.toString()).jsonObject.values.elementAt(2), Array<ChatRoomBody>::class.java).toList()
//
////        chatsData.value = response
////        Log.d("WebSocketListener", response)
    }
}