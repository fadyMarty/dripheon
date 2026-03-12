package com.fadymarty.dripheon.data.repository

import com.fadymarty.dripheon.common.util.WebSocketEvent
import com.fadymarty.dripheon.common.util.map
import com.fadymarty.dripheon.data.mapper.toMessage
import com.fadymarty.dripheon.data.remote.ChatRemoteDataSource
import com.fadymarty.dripheon.domain.model.Message
import com.fadymarty.dripheon.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val chatRemoteDataSource: ChatRemoteDataSource,
) : ChatRepository {

    override fun observeMessageEvents(): Flow<WebSocketEvent<Message>> {
        return chatRemoteDataSource.events.map { event ->
            event.map { content ->
                content.toMessage(isFromLocalUser = false)
            }
        }
    }

    override fun connect() {
        chatRemoteDataSource.connect()
    }

    override fun disconnect() {
        chatRemoteDataSource.disconnect()
    }

    override fun sendMessage(content: String): Boolean {
        return chatRemoteDataSource.sendMessage(content)
    }
}