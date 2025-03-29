package com.example.bottomnavigationcomposeui.pages.home.util

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClient() {
     val client = HttpClient(){
         //         Install JSON feature with KotlinxSerializer for serialization and deserialization
         install(ContentNegotiation) {
             json(Json {
                 ignoreUnknownKeys = true
             })
         }

         // Install HttpTimeout feature to set request and connect timeouts
         install(HttpTimeout) {
             requestTimeoutMillis = 15000
             connectTimeoutMillis = 20000
         }

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
         install(Logging) {
             logger = object : Logger {
                 override fun log(message: String) {
                     Log.d("KtorClient",
                         message
                     )
                 }
             }
             level = LogLevel.ALL
         }

    }
}