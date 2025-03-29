package com.example.bottomnavigationcomposeui.utils.screens

import kotlinx.serialization.Serializable

@Serializable
class SplashToHomeScreen{
    @Serializable
    object Splash
    @Serializable
    object Login
    @Serializable
    data class StartPoint(val userName: String)
    @Serializable
    data class OTPScreen(val phoneNumber: String)
}