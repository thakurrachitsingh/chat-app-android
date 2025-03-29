package com.example.bottomnavigationcomposeui.pages.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bottomnavigationcomposeui.datamodel.Chat
import com.example.bottomnavigationcomposeui.datamodel.User
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.WebSocket
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.runtime.toMutableStateList
import com.example.bottomnavigationcomposeui.datamodel.MessageToDB
import com.example.bottomnavigationcomposeui.datamodel.RoomId
import com.example.bottomnavigationcomposeui.pages.home.util.KtorClient
import com.example.bottomnavigationcomposeui.utils.Screens
import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.util.UUID


@HiltViewModel
class ChatViewModel: ViewModel() {
    private val fireStore = FirebaseFirestore.getInstance()
    private var client: KtorClient = KtorClient()
    lateinit var ws: WebSocket
    var userDetails: User = User("", "Rachit Singh", "meraEmail", "thakurrachitsingh", listOf(RoomId("dummyRoom1"), RoomId("dummyRoom2")))
    private val chats = emptyList<Chat>()
    private val chatList = MutableStateFlow(chats)
    fun getChatList() = chatList.asStateFlow()

    fun initialization(args: Screens.ChatScreen){
        val roomIds = args.roomIds.joinToString("\",\"")
        ws.send(
            """{
     "user" : {
         "name" : "${args.userName}",
         "roomId" : "${args.roomId}",
         "roomIds" : ["$roomIds"]
     },
     "message": "",
     "readReceivedResponse":{"read":-1,"received":-1}
 }"""
        )
    }

    fun fetchChats(roomId: String, args: Screens.ChatScreen){
        CoroutineScope(Dispatchers.IO).launch{
            val url = "https://chat-app-backend-db.vercel.app/user/$roomId/getRoom"
            val response = client.client.get(url).body<String>()
            Log.d("chatViewModel", response)
            val messagesToSet = Gson().fromJson(Json.parseToJsonElement(response).jsonObject.values.elementAt(2).jsonObject.values.elementAt(4).toString(), Array<Chat>::class.java).toList()
            updateChatList(messagesToSet)
        }
        updateUnreadCount(roomId, args)
    }

    private fun updateUnreadCount(roomId: String, args: Screens.ChatScreen){
        CoroutineScope(Dispatchers.IO).launch {
            val response = client.client.post("https://chat-app-backend-db.vercel.app/user/${args.userName}/$roomId/updateReadUnreadMessages?unread=-1"){
                contentType(ContentType.Application.Json)
            }
            Log.d("chatViewModel", response.body<String>())
        }
    }

    private fun updateChatList(chat: List<Chat>){
        chatList.value = chat as List<Chat>
    }
    fun sendMessage(message: String, args: Screens.ChatScreen) {
//        Log.d("ChatViewModel", "Message: $message")
//        val dataToBeSent = HashMap<String, String>()
//        dataToBeSent["message"] = message
//        fireStore.collection("Testing").add(dataToBeSent).addOnSuccessListener {
//            Log.d("ChatViewModel", "Message sent successfully $it")
//        }.addOnFailureListener {
//            Log.d("ChatViewModel", "Message sending failed $it")
//        }
        val dateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val messageToAdd = Chat(UUID.randomUUID().toString(), args.userName, message, dateFormat.format(Date()))
        val newChats = chatList.value.toMutableStateList().apply {
            add(messageToAdd)
        }.toList()
        chatList.value = newChats
        sendOnWebsocket(message, args)
        writeMessageOnDB(args, messageToAdd)
    }

    private fun writeMessageOnDB(args: Screens.ChatScreen, messageToAdd: Chat) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = client.client.post("https://chat-app-backend-db.vercel.app/user/addChat"){
                contentType(ContentType.Application.Json)
                setBody(MessageToDB(args.roomId, messageToAdd))
            }
            Log.d("chatViewModel", response.body<String>())
        }
    }

    fun sendOnWebsocket(message: String, args: Screens.ChatScreen){
        try {
//            val user = UserForWebSocket(args.userName, args.roomId, args.roomIds)
//            val dataToBeSend = MessageModal(user, message)
            val roomIds = args.roomIds.joinToString("\",\"")
            val chatId = UUID.randomUUID().toString()
            val res = Json.parseToJsonElement("""{
     "_id": "$chatId",
     "user" : {
         "name" : "${args.userName}",
         "roomId" : "${args.roomId}",
         "roomIds" : ["$roomIds"]
     },
     "message": "$message",
     "time": "${SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date())}"
 }""")
            val x = ws.send(
                res.toString()
            )
            Log.d("ChatViewModel", "Data sent on websocket: $x")
        }catch (e: Exception){
            Log.d("ChatViewModel", "Error: $e")
        }
    }

}