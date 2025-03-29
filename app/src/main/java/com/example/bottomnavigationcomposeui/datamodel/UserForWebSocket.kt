package com.example.bottomnavigationcomposeui.datamodel

import kotlinx.serialization.Serializable

data class UserForWebSocket(
    @Serializable
    var name: String,
    @Serializable
    val roomId: String,
    @Serializable
    val roomIds: List<String>
)