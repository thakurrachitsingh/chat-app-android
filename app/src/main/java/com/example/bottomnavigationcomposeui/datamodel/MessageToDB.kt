package com.example.bottomnavigationcomposeui.datamodel

import kotlinx.serialization.Serializable

@Serializable
data class MessageToDB(
    @Serializable
    val roomId: String,
    @Serializable
    val chat: Chat
)

//@Serializable
//data class Message(
//    val userName: String,
//    val message: String,
//    val time: String
//)
