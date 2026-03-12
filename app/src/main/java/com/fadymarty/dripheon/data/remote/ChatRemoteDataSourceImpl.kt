package com.fadymarty.dripheon.data.remote

import com.fadymarty.dripheon.BuildConfig
import com.fadymarty.dripheon.common.util.WebSocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlin.math.pow

class ChatRemoteDataSourceImpl(
    private val okHttpClient: OkHttpClient,
) : ChatRemoteDataSource {

    private var webSocket: WebSocket? = null

    private val _events = MutableSharedFlow<WebSocketEvent<String>>()
    override val events: SharedFlow<WebSocketEvent<String>>
        get() = _events.asSharedFlow()

    private var reconnectJob: Job? = null
    private var retryCount = 0

    private val scope = CoroutineScope(Dispatchers.IO)

    private val listener = object : WebSocketListener() {

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            scope.launch {
                _events.emit(WebSocketEvent.Message(text))
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            scope.launch {
                _events.emit(WebSocketEvent.Failure(t))
            }
            reconnectWithBackoff()
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            retryCount = 0
            scope.launch {
                _events.emit(WebSocketEvent.Open)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            scope.launch {
                _events.emit(WebSocketEvent.Closing)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            scope.launch {
                _events.emit(WebSocketEvent.Closed)
            }
        }
    }

    override fun connect() {
        if (webSocket != null) disconnect()
        val request = Request.Builder()
            .url("${BASE_URL}chat?api_key=${BuildConfig.API_KEY}")
            .build()
        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    override fun disconnect() {
        webSocket?.close(1000, null)
        webSocket = null
    }

    override fun sendMessage(content: String): Boolean {
        return webSocket?.send(content) ?: false
    }

    private fun reconnectWithBackoff() {
        retryCount++
        scope.launch {
            _events.emit(WebSocketEvent.Loading)
        }
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val delayTime = 1000L * 2.0.pow(retryCount).toLong()
            val delay = minOf(delayTime, MAX_RETRY_DELAY)
            delay(delay)
            connect()
        }
    }

    companion object {
        const val BASE_URL = "wss://s16232.ams1.piesocket.com/v3/"
        private const val MAX_RETRY_DELAY = 5000L
    }
}