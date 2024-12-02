package com.dodam.dicegame.api

import android.util.Log
import com.dodam.dicegame.vo.RoomInfoVO
import okhttp3.Request

fun createRoomWithOkHttpSync(roomInfo: RoomInfoVO): Long? {
    val url = "http://152.67.209.165:9081/dicegame/room/create"
    val jsonBody = toJson(roomInfo, RoomInfoVO::class.java)
    val requestBody = createRequestBody(jsonBody)
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    val responseBody = executeRequest(request)
    return responseBody?.toLongOrNull()?.also {
        Log.d("OkHttp", "Room created successfully with ID: $it")
    } ?: run {
        Log.e("OkHttp", "Failed to create room or parse room ID")
        null
    }
}