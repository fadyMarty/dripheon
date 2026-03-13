package com.fadymarty.dripheon.common.util

sealed interface WebSocketEvent<out T> {
    data class Message<out T>(val data: T) : WebSocketEvent<T>
    data class Failure(val exception: Throwable) : WebSocketEvent<Nothing>
    data object Open : WebSocketEvent<Nothing>
    data object Closing : WebSocketEvent<Nothing>
    data object Closed : WebSocketEvent<Nothing>
}

inline fun <T, R> WebSocketEvent<T>.map(map: (T) -> R): WebSocketEvent<R> {
    return when (this) {
        is WebSocketEvent.Message -> WebSocketEvent.Message(map(data))
        is WebSocketEvent.Failure -> WebSocketEvent.Failure(exception)
        WebSocketEvent.Open -> WebSocketEvent.Open
        WebSocketEvent.Closing -> WebSocketEvent.Closing
        WebSocketEvent.Closed -> WebSocketEvent.Closed
    }
}