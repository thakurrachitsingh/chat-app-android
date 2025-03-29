package com.example.bottomnavigationcomposeui.datamodel

import kotlinx.serialization.Serializable

@Serializable
data class User(
    @Serializable
    val _id: String = "",
    @Serializable
    val name: String,
    @Serializable
    val email: String,
    @Serializable
    val userName: String,
    @Serializable
    val roomIds: List<RoomId> = emptyList()
)

@Serializable
data class RoomId(
    @Serializable
    val roomId: String
)
