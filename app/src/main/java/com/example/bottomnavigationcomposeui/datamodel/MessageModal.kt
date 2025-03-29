package com.example.bottomnavigationcomposeui.datamodel

import kotlinx.serialization.Serializable

data class MessageModal(
    @Serializable
    val _id: String,
    @Serializable
    val user: UserForWebSocket,
    @Serializable
    var receiver: String? = null,
    @Serializable
    val message: String,
    @Serializable
    var readReceivedResponse : ReadReceivedResponse? = null,
    @Serializable
    val time: String
)

data class ReadReceivedResponse(
    @Serializable
    var read: Int? = null,
    @Serializable
    var received: Int? = null
)