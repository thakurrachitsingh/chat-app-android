package com.example.bottomnavigationcomposeui.datamodel

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomBody(
    @Serializable
    val _id: String,
    @Serializable
    val roomId: String,
    @Serializable
    val admin: String,
    @Serializable
    var members: List<Member>,
    @Serializable
    var chats: List<Chat> = emptyList()
)


@Serializable
data class Member(
    @Serializable
    val _id: String,
    @Serializable
    val userName: String,
    @Serializable
    var unread : Int? = null,
    @Serializable
    var unrecieved : Int? = null
)