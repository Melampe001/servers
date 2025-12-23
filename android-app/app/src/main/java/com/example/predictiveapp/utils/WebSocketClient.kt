package com.example.predictiveapp.utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * WebSocket client with automatic reconnection
 * Optional feature for device synchronization
 */
class WebSocketClient(private val url: String) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()
    
    val messageChannel = Channel<String>(Channel.UNLIMITED)
    private var isConnected = false
    private var shouldReconnect = true
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    
    /**
     * Connect to WebSocket server
     */
    fun connect() {
        val request = Request.Builder()
            .url(url)
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                reconnectAttempts = 0
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                messageChannel.trySend(text)
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                if (shouldReconnect && reconnectAttempts < maxReconnectAttempts) {
                    scheduleReconnect()
                }
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
            }
        })
    }
    
    /**
     * Send message through WebSocket
     */
    fun send(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }
    
    /**
     * Disconnect from WebSocket
     */
    fun disconnect() {
        shouldReconnect = false
        webSocket?.close(1000, "Client disconnecting")
        webSocket = null
    }
    
    /**
     * Schedule reconnection with exponential backoff
     * Note: In production, inject a CoroutineScope for better lifecycle management
     */
    private fun scheduleReconnect() {
        reconnectAttempts++
        val delayMs = (1000L * reconnectAttempts).coerceAtMost(30000L)
        
        // TODO: Use proper CoroutineScope injection for lifecycle-aware reconnection
        Thread {
            Thread.sleep(delayMs)
            if (shouldReconnect && !isConnected) {
                connect()
            }
        }.start()
    }
    
    fun isConnected(): Boolean = isConnected
}
