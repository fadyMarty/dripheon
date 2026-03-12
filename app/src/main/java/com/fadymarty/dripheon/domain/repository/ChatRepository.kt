package com.fadymarty.dripheon.domain.repository

import com.fadymarty.dripheon.common.util.WebSocketEvent
import com.fadymarty.dripheon.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessageEvents(): Flow<WebSocketEvent<Message>>
    fun connect()
    fun disconnect()
    fun sendMessage(content: String): Boolean
}