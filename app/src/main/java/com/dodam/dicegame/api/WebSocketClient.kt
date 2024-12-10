package com.dodam.dicegame.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.dodam.dicegame.vo.ResponseMessageVO
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebSocketClient(private val context: Context) {

    private var webSocket: WebSocket? = null
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    fun connect(url: String, onRoomCountReceived: (Int) -> Unit) {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, WebSocketListenerImpl(context, onRoomCountReceived))
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun closeConnection() {
        webSocket?.close(1000, "Closing connection")
    }

    private class WebSocketListenerImpl(private val context: Context,
                                        private val onRoomCountReceived: (Int) -> Unit // room count를 업데이트하는 콜백 추가
    ) : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            showToast("WebSocket 연결 성공")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d("WebSocket", "Received text: $text")

            val responseMessageVO = Gson().fromJson(text, ResponseMessageVO::class.java)
            when (responseMessageVO.action) {
                "joinRoom" -> showToast(responseMessageVO.message)
                "getRoomsCount" -> onRoomCountReceived(responseMessageVO.message.toInt())
                "startGame" ->  showToast(responseMessageVO.message)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            showToast("수신 데이터 (ByteString): $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            showToast("WebSocket 닫는 중: $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            showToast("WebSocket 연결 종료: $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            showToast("WebSocket 오류: ${t.message}")
        }

        private var toast: Toast? = null

        private fun showToast(message: String) {
            CoroutineScope(Dispatchers.Main).launch {
                toast?.cancel()
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT).apply { show() }
            }
        }

    }
}
