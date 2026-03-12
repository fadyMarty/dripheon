package com.fadymarty.dripheon.presentation.chat

sealed interface ChatEvent {
    data object OnSendMessageClick : ChatEvent
}