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

    fun connect(url: String, onRoomCountReceived: (Int) -> Unit
                , onIsGameStartReceived: (Boolean) -> Unit
                , onIsAllDoneRoundPlay: (String) -> Unit
                , onIsChangeRoomMaster: (ResponseMessageVO) -> Unit
    ) {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, WebSocketListenerImpl(context, onRoomCountReceived, onIsGameStartReceived, onIsAllDoneRoundPlay, onIsChangeRoomMaster))
    }

    fun sendMessage(message: String) {
        Log.d("WebSocket", "sendMessage: $message")
        webSocket?.send(message)
    }

    fun closeConnection() {
        webSocket?.close(1000, "Closing connection")
    }

    private class WebSocketListenerImpl(private val context: Context,
                                        private val onRoomCountReceived: (Int) -> Unit, // room count를 업데이트하는 콜백 추가
        private val onIsGameStartReceived: (Boolean) -> Unit,
        private val onIsAllDoneRoundPlay: (String) -> Unit,
        private val onIsChangeRoomMaster: (ResponseMessageVO) -> Unit
    ) : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d("WebSocket", "Received text: $text")

            val responseMessageVO = Gson().fromJson(text, ResponseMessageVO::class.java)
            when (responseMessageVO.action) {
                "joinRoom" -> showToast(responseMessageVO.message)
                "createGameRoom" -> showToast(responseMessageVO.message)
                "getRoomsCount" -> onRoomCountReceived(responseMessageVO.message.toInt())
                "startGame" -> {
                    onIsGameStartReceived(true)
                    showToast(responseMessageVO.message)
                }
                "playGame" -> {
                    val message = responseMessageVO.message
                    onIsAllDoneRoundPlay(message)
                    if(message == "end"){
                        showToast("게임을 종료합니다.")
                    }

                }
                "leaveRoom" ->{
                    if(responseMessageVO.subMessage=="changeRoomMaster"){
                        showToast(responseMessageVO.message.substring(0,8)+"님으로 방장이 변경되었습니다.")
                    }else{
                        showToast(responseMessageVO.message.substring(0,8)+"님이 퇴장하였습니다.")
                    }
                    onIsChangeRoomMaster(responseMessageVO)
                }

            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            //showToast("수신 데이터 (ByteString): $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            //showToast("WebSocket 닫는 중: $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            //showToast("WebSocket 오류: ${t.message}")
            Log.d("WebSocket", "sendMessage: $t.message")

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
