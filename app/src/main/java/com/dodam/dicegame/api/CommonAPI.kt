package com.dodam.dicegame.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException


private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client: OkHttpClient = OkHttpClient()

fun createRequestBody(json: String, mediaType: String = "application/json; charset=utf-8"): RequestBody {
    return json.toRequestBody(mediaType.toMediaType())
}

// 공통 요청 실행 함수
fun executeRequest(request: Request): String? {
    return try {
        val response: Response = client.newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.string()
        } else {
            Log.e("OkHttp", "Error: ${response.code} - ${response.message}")
            null
        }
    } catch (e: IOException) {
        Log.e("OkHttp", "Request failed: ${e.message}")
        e.printStackTrace()
        null
    }
}

// 공통 JSON 변환 함수
fun <T> toJson(obj: T, clazz: Class<T>): String {
    val adapter = moshi.adapter(clazz)
    return adapter.toJson(obj)
}

// 공통 JSON 파싱 함수
fun <T> fromJson(json: String?, clazz: Class<T>): T? {
    return try {
        val adapter = moshi.adapter(clazz)
        adapter.fromJson(json ?: "")
    } catch (e: Exception) {
        Log.e("OkHttp", "Failed to parse JSON: ${e.message}")
        null
    }
}