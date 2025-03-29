package com.example.bottomnavigationcomposeui.pages.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bottomnavigationcomposeui.datamodel.Chat
import com.example.bottomnavigationcomposeui.datamodel.ChatRoomBody
import com.example.bottomnavigationcomposeui.datamodel.Member
import com.example.bottomnavigationcomposeui.datamodel.MessageModal
import com.example.bottomnavigationcomposeui.datamodel.ReadReceivedResponse
import com.example.bottomnavigationcomposeui.datamodel.User
import com.example.bottomnavigationcomposeui.datamodel.response.RoomResponse
import com.example.bottomnavigationcomposeui.datamodel.response.UserDetailsResponse
import com.example.bottomnavigationcomposeui.datamodel.response.UsersListResponse
import com.example.bottomnavigationcomposeui.pages.home.util.KtorClient
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.security.MessageDigest
import java.text.SimpleDateFormat


private var chatsList = MutableStateFlow<List<RoomResponse>>(emptyList())
private lateinit var userDetailsResponse: UserDetailsResponse
var currentRoomId = ""

@HiltViewModel
class HomeViewModel: ViewModel() {

    private var client: KtorClient = KtorClient()
    var ws: WebSocket

    fun setCurrentRoomId(roomId: String) {
        currentRoomId = roomId
    }
    fun getUserDetailsResponse() = userDetailsResponse
    private val rooms = MutableStateFlow<List<String>>(emptyList())
    fun getRooms() = rooms
    private val haveChatsToLoad  = MutableStateFlow<String>("")
    fun getHaveChatsToLoad() = haveChatsToLoad
    private val mUsersList = MutableStateFlow<List<User>>(emptyList())
    private val usersList = MutableStateFlow<List<User>>(emptyList());
    fun getUsersList() = usersList
    fun getAllChatData() = chatsList.asStateFlow()
    private val chatRoomCreationState = MutableStateFlow("")
    fun getChatRoomState() = chatRoomCreationState
    fun setChatRoomState(state: String){
        chatRoomCreationState.value = state
    }
    var newRoomId = ""

    init {
        loadUsersList()
        val client = OkHttpClient()
        val request = Request.Builder().url("wss://chat-app-backend-production-7f36.up.railway.app").build()
        val listener = WebSocketListener()
        ws = client.newWebSocket(request, listener)
    }

    fun establishWebsocketConnection(){
        if(::userDetailsResponse.isInitialized){
            try {
                val roomIds = rooms.value.joinToString(",")
                val x = ws.send(
                    """{
     "user" : {
         "name" : "${userDetailsResponse.userDetails.userName}",
         "roomId" : "${rooms.value[0]}",
         "roomIds" : ["$roomIds"]
     },
     "message": "",
     "readReceivedResponse":{"read":0,"received":-1}
 }"""
                )
                Log.d("newChatViewModel", "Data sent on websocket: $x")
            }catch (e: Exception){
                Log.d("ChatViewModel", "Error: $e")
            }
        }
    }

    fun setupChatScreen(){
        chatsList.value.map { chats ->
            if(chats.room.roomId==currentRoomId){
                chats.room.members.map {
                    if(it.userName==userDetailsResponse.userDetails.userName){
                        it.unread = 0
                    }
                }
            }
        }
        updateUnreceivedMessages(userDetailsResponse.userDetails.userName, currentRoomId, 0, 0)
    }

    fun getUserDetails(userName: String){
        haveChatsToLoad.value = ""
        CoroutineScope(Dispatchers.IO).launch {
            val response = client.client.get("https://chat-app-backend-db.vercel.app/user/$userName/getUserDetails").body<UserDetailsResponse>()
            userDetailsResponse = response
            val listOfRoomIds = ArrayList<String>()
            for(i in response.userDetails.roomIds){
                listOfRoomIds.add(i.roomId)
            }
            rooms.value = listOfRoomIds
            chatsList.value = emptyList()
            if(rooms.value.isNotEmpty()){
                loadAllChats(rooms.value)
            }else{
                haveChatsToLoad.value = "noChats"
            }
            establishWebsocketConnection()
            Log.d("HomeViewModel1", response.toString())
        }
    }


    private fun loadUsersList(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = client.client.get("https://chat-app-backend-db.vercel.app/user/getUsersList").body<UsersListResponse>()
            mUsersList.value = response.usersList
            Log.d("HomeViewModel1", response.toString())
        }
    }

    fun searchFromUsersList(input: String) {
        usersList.value = mUsersList.value
        if(input==""){
            usersList.value = mUsersList.value
        }else{
            val updatedList = usersList.value.filter { it.userName.contains(input) }
            usersList.value = updatedList
        }
    }

    fun createChatRoom(userList: List<Member>){
        CoroutineScope(Dispatchers.IO).launch {
            chatRoomCreationState.value = "creating"
            val idSum = StringBuffer()
            for(i in userList){
                idSum.append(i)
            }
            idSum.append(userDetailsResponse.userDetails.userName)
            val id = generateRoomId(idSum.toString())
            newRoomId = id
            val response = client.client.post("https://chat-app-backend-db.vercel.app/user/createRoom"){
                contentType(ContentType.Application.Json)
                setBody(ChatRoomBody(id, id, userDetailsResponse.userDetails.userName, userList))
            }
            chatRoomCreationState.value = "created"
            rooms.value = rooms.value.toMutableList().apply {
                add(id)
            }.toList()
            Log.d("HomeViewModel1", response.toString())
        }
    }

    private fun loadAllChats(rooms: List<String>){
        var index = 0
        rooms.map { rooms ->
            getRoomChat(rooms, index)
            updateUnreceivedMessages(userDetailsResponse.userDetails.userName, rooms, null, 0)
            index++
        }
    }

    private fun getRoomChat(roomId: String, index: Int){
        CoroutineScope(Dispatchers.IO).launch{
            val response = client.client.get("https://chat-app-backend-db.vercel.app/user/$roomId/getRoom").body<RoomResponse>()
            val chatsToAdd = chatsList.value.toMutableList().apply {
                add(response)
            }.toList()
            if(response.room.roomId.isEmpty()){
                haveChatsToLoad.value = "noChats"
            }else{
                haveChatsToLoad.value = "chatsLoaded"
            }
            chatsList.value = chatsToAdd
//            Log.d("HomeViewModel", response)
        }
    }

    private fun updateUnreceivedMessages(userName: String, roomId: String, unread: Int?, unreceived: Int?) {
        CoroutineScope(Dispatchers.IO).launch{
            var url = "https://chat-app-backend-db.vercel.app/user/$userName/$roomId/updateReadUnreadMessages?"
            if(unread!=null){
                url+="unread=-1"
            }
            if(unreceived!=null){
                url+="&unrecieved=-1"
            }
            val response = client.client.post(url){
                contentType(ContentType.Application.Json)
            }
            Log.d("HomeViewModel1", response.toString())
        }
    }

    private fun generateRoomId(input: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hashBytes = sha256.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun performLogout(){
        chatsList = MutableStateFlow<List<RoomResponse>>(emptyList())
    }

    fun getCollectionOfRooms(){

    }
}

class WebSocketListener() : okhttp3.WebSocketListener() {
    override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
        super.onMessage(webSocket, text)
        addReceivedMessage(text)
        notifyReadReceivedStatus(webSocket, text)
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

    private fun  notifyReadReceivedStatus(webSocket: WebSocket, text: String) {
        val response = Gson().fromJson(text, MessageModal::class.java)
        if(response.readReceivedResponse==null){
            response.receiver = response.user.name
            response.user.name = userDetailsResponse.userDetails.userName
            if(response.user.roomId== currentRoomId){
                response.readReceivedResponse = ReadReceivedResponse(0,0)
            }else{
                response.readReceivedResponse = ReadReceivedResponse(1,0)
            }
            val dataToSend = Gson().toJson(response)
            webSocket.send(dataToSend)
        }
    }

    private fun addReceivedMessage(text: String) {
        val response = Gson().fromJson(text, MessageModal::class.java)
        val simpleDateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
         var temparoryData = chatsList.value
        var chatExists = false
        for( i in 0..chatsList.value.size-1){
            if(chatsList.value[i].room.roomId==response.user.roomId){
                chatExists = true
                if(response.readReceivedResponse!=null){
                    updateReadReceivedStatus(response, i)
                    if(response.receiver==null){
                        temparoryData = temparoryData.toMutableList().apply {
                            add(RoomResponse(0, "", ChatRoomBody("id","response.user.roomId", "newChats", emptyList())))
                        }
                        chatsList.value = temparoryData
                        return
                    }
                    val sender = response.user.name
                    response.user.name = response.receiver!!
                    response.receiver = sender
                }
                val newChats = chatsList.value.toMutableList()[i].room.chats.toMutableList().apply {
                    add(Chat(response._id, response.user.name, response.message, response.time))
                }.toList()
                if(response.readReceivedResponse==null){
                    val updatedMembers = chatsList.value.toMutableList()[i].room.members.toMutableList().apply {
                        for(j in 0..<size){
                            if(get(j).userName!=response.user.name){
                                val unread: Int? = if(currentRoomId==response.user.roomId){
                                    0
                                }else{
                                    get(j).unread?.plus(1)
                                }
                                val newMember = Member(get(j)._id, get(j).userName, unread, 0)
                                set(j, newMember)
                            }
                        }
                    }.toList()
                    temparoryData[i].room.members = updatedMembers
                }
                temparoryData[i].room.chats = newChats
            }
        }
        if(!chatExists){
            val newChats = temparoryData.toMutableList().apply {
                add(RoomResponse(0, "success", ChatRoomBody(response._id, response.user.roomId, response.user.name, listOf(Member("randomId", response.user.name)), listOf(Chat(response._id, response.user.name, response.message, response.time)))))
            }
            temparoryData = newChats
        }
        temparoryData = temparoryData.toMutableList().apply {
            add(RoomResponse(0, "", ChatRoomBody("id","response.user.roomId", "newChats", emptyList())))
        }
        chatsList.value = temparoryData
    }

    private fun updateReadReceivedStatus(response: MessageModal, index: Int) {
        val read = response.readReceivedResponse?.read
        val received = response.readReceivedResponse?.received
        var temparoryData = chatsList.value
        val updatedMembers = chatsList.value.toMutableList()[index].room.members.toMutableList().apply {
            for(j in 0..size-1){
                if(get(j).userName==response.user.name){
                    if(received==-1 && read==-1){
                        val newMember = Member(get(j)._id, get(j).userName, 0, 0)
                        set(j, newMember)
                    }else if(received==-1){
                        val newMember = Member(get(j)._id, get(j).userName, get(j).unread?.plus(read ?: 0), 0)
                        set(j, newMember)
                    }else if(read==-1){
                        val newMember = Member(get(j)._id, get(j).userName, 0, get(j).unrecieved?.plus(received?:0))
                        set(j, newMember)
                    }else{
                        val newMember = Member(get(j)._id, get(j).userName, get(j).unread?.plus(read ?: 0), get(j).unrecieved?.plus(received?:0))
                        set(j, newMember)
                    }
                }
            }
        }.toList()
        temparoryData[index].room.members = updatedMembers
        chatsList.value = temparoryData
    }
}