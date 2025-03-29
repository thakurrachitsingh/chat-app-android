package com.example.bottomnavigationcomposeui.datamodel.response

import com.example.bottomnavigationcomposeui.datamodel.ChatRoomBody
import kotlinx.serialization.Serializable

@Serializable
data class RoomResponse(
    @Serializable
    val code: Int,
    @Serializable
    val message: String,
    @Serializable
    val room: ChatRoomBody
)

//@Serializable
//data class Room(
//    val _id: String,
//    val roomId: String,
//    val admin: String,
//    val members:
//)
