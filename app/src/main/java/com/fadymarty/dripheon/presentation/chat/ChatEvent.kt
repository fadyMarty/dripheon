package com.fadymarty.dripheon.presentation.chat

sealed interface ChatEvent {
    data class OnMessageChange(val message: String) : ChatEvent
    data object OnSendMessage : ChatEvent
}