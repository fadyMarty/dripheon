package com.fadymarty.dripheon.data.remote

import com.fadymarty.dripheon.common.util.WebSocketEvent
import kotlinx.coroutines.flow.SharedFlow

interface ChatRemoteDataSource {
    val events: SharedFlow<WebSocketEvent<String>>
    fun connect()
    fun disconnect()
    fun sendMessage(content: String): Boolean
}