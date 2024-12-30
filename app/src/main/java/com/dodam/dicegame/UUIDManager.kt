package com.dodam.dicegame

import android.content.Context
import com.dodam.dicegame.api.serverUrl
import okhttp3.*
import java.io.IOException
import java.util.UUID

object UUIDManager {
    private const val PREFS_NAME = "app_uuid_prefs" // SharedPreferences 파일 이름
    private const val KEY_UUID = "app_uuid" // UUID 저장 키

    // OkHttpClient 객체 생성
    private val client = OkHttpClient()

    // UUID 생성 또는 가져오기
    fun getOrCreateUUID(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var uuid = sharedPreferences.getString(KEY_UUID, null)

        if (uuid == null) {
            // UUID 생성
            uuid = UUID.randomUUID().toString()
            // SharedPreferences에 저장
            sharedPreferences.edit().putString(KEY_UUID, uuid).apply()
            // 서버에 업로드
            uploadUUIDToServer(uuid)
        }

        return uuid
    }

    fun uploadUUIDToServer(uuid: String) {
        val url = "$serverUrl/player/save/uuid=$uuid" // API 엔드포인트

        val request = Request.Builder()
            .url(url) // GET 요청 URL
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("UUID 저장 성공: $uuid")
                } else {
                    println("UUID 저장 실패: ${response.code}")
                }
                response.close()
            }

            override fun onFailure(call: Call, e: IOException) {
                println("UUID 저장 중 오류 발생: ${e.message}")
            }
        })
    }
}
