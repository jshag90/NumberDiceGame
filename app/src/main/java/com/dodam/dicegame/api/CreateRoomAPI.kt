package com.dodam.dicegame.api

import android.util.Log
import com.dodam.dicegame.vo.RoomInfoVO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

fun createRoomWithOkHttp(roomInfo: RoomInfoVO, onResult: (Long?) -> Unit) {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val roomInfoAdapter = moshi.adapter(RoomInfoVO::class.java)

    val client = OkHttpClient()

    // RoomInfoVO 객체를 JSON으로 변환
    val jsonBody = roomInfoAdapter.toJson(roomInfo)

    // 요청 본문 설정
    val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

    // 요청 객체 생성
    val request = Request.Builder()
        .url("http://192.168.0.20:8080/room/create") // API 엔드포인트
        .post(requestBody)
        .build()

    // 비동기 요청 실행
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("OkHttp", "Failed to create room: ${e.message}")
            e.printStackTrace() // 스택 트레이스 출력
            onResult(null)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                // 방 ID를 Long 타입으로 반환
                val responseBody = response.body?.string()
                val roomId = responseBody?.toLongOrNull()
                onResult(roomId)
            } else {
                Log.e("OkHttp", "Error: ${response.code} - ${response.message}")
                onResult(null)
            }
        }
    })
}
