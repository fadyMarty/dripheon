package com.fadymarty.dripheon.presentation.chat

import com.fadymarty.dripheon.domain.model.Message

data class ChatState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val messages: List<Message> = emptyList(),
    val message: String = "",
)
