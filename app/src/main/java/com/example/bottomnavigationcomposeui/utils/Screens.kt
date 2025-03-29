package com.example.bottomnavigationcomposeui.utils

import com.example.bottomnavigationcomposeui.datamodel.RoomId
import com.example.bottomnavigationcomposeui.datamodel.User
import kotlinx.serialization.Serializable
import okhttp3.WebSocket

@Serializable
class Screens {
    @Serializable
    object Home
    @Serializable
    object MyCloset
    @Serializable
    object ReferFriend
    @Serializable
    object More
    @Serializable
    data class ChatScreen(
        @Serializable
        val userName: String,
        @Serializable
        val roomIds: List<String>,
        @Serializable
        val roomId: String,
        @Serializable
        val roomData: String? = null
    )
}