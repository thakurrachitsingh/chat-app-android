package com.example.bottomnavigationcomposeui.utils

import android.util.Log
import com.example.bottomnavigationcomposeui.datamodel.Chat
import com.example.bottomnavigationcomposeui.datamodel.ChatRoomBody
import com.example.bottomnavigationcomposeui.datamodel.MessageModal
import com.example.bottomnavigationcomposeui.datamodel.response.RoomResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.util.Date

class WebSocketListener() : WebSocketListener() {
    lateinit var chatsData: MutableStateFlow<List<RoomResponse>>
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
//        val temparoryData = chatsData.value
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
//        chatsData.value = temparoryData
////        val existingData = Gson().fromJson(Json.parseToJsonElement(chatsData.value.toString()).jsonObject.values.elementAt(2), Array<ChatRoomBody>::class.java).toList()
//
////        chatsData.value = response
////        Log.d("WebSocketListener", response)
    }
}