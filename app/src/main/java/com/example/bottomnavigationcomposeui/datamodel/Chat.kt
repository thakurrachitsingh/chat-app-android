package com.example.bottomnavigationcomposeui.datamodel

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    @Serializable
    val _id: String,
    @Serializable
    val userName: String,
    @Serializable
    val chat: String,
    @Serializable
    val time: String
)
