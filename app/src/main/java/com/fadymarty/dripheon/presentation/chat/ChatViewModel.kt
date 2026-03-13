package com.fadymarty.dripheon.presentation.chat

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadymarty.dripheon.common.util.WebSocketEvent
import com.fadymarty.dripheon.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    init {
        chatRepository.connect()
        observeMessages()
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            ChatEvent.OnSendMessageClick -> {
                viewModelScope.launch {
                    val message = chatRepository.sendMessage(
                        _state.value.messageState.text.toString()
                    )
                    message?.let {
                        _state.update {
                            it.copy(
                                messages = it.messages + message
                            )
                        }
                        _state.value.messageState.clearText()
                    }
                }
            }
        }
    }

    private fun observeMessages() {
        _state.update {
            it.copy(isLoading = true)
        }
        chatRepository.observeMessageEvents().onEach { event ->
            when (event) {
                is WebSocketEvent.Message -> {
                    _state.update {
                        it.copy(
                            messages = it.messages + event.data
                        )
                    }
                }
                is WebSocketEvent.Failure -> {
                    if (!_state.value.isLoading) {
                        _state.update {
                            it.copy(isRefreshing = true)
                        }
                    }
                }
                WebSocketEvent.Open -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnect()
    }
}