package com.dodam.dicegame.api

import android.util.Log
import com.dodam.dicegame.vo.RoomInfoVO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

fun createRoomWithOkHttpSync(roomInfo: RoomInfoVO): Long? {

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
    val url = "http://152.67.209.165:9081/dicegame/room/create"
    val request = Request.Builder()
        .url(url) // API 엔드포인트
        .post(requestBody)
        .build()

    return try {
        // 동기 요청 실행
        val response: Response = client.newCall(request).execute()
        if (response.isSuccessful) {
            // 방 ID를 Long 타입으로 반환
            val responseBody = response.body?.string()
            val roomId = responseBody?.toLongOrNull()

            if (roomId != null) {
                Log.d("OkHttp", "Room created successfully with ID: $roomId") // 성공적인 roomId 로그
            } else {
                Log.e("OkHttp", "Failed to convert response body to roomId")
            }

            roomId // roomId 반환
        } else {
            Log.e("OkHttp", "Error: ${response.code} - ${response.message}")
            null
        }
    } catch (e: IOException) {
        Log.e("OkHttp", "Failed to create room: ${e.message}")
        e.printStackTrace() // 스택 트레이스 출력
        null
    }
}
