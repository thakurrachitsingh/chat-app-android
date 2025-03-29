package com.example.bottomnavigationcomposeui.datamodel.response

import com.example.bottomnavigationcomposeui.datamodel.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsResponse(
    @Serializable
    val code: Int,
    @Serializable
    val message: String,
    @Serializable
    val userDetails: User
)