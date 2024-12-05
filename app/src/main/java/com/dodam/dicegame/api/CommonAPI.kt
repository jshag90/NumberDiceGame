package com.dodam.dicegame.api

import android.util.Log
import com.dodam.dicegame.vo.ReturnCodeVO
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import com.google.gson.reflect.TypeToken


private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client: OkHttpClient = OkHttpClient()

/*val serverUrl = "http://152.67.209.165:9081/dicegame"*/
val serverUrl = "http://192.168.0.20:8080"

object HttpHeaders {
    const val ACCEPT = "Accept"
    const val CONTENT_TYPE = "Content-Type"
}

object HttpHeadersValue {
    const val ACCEPT_VALUE = "*/*"
    const val CONTENT_TYPE_VALUE = "application/json"
}

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


inline fun <reified T> fromJson(json: String?): ReturnCodeVO<T>? {
    return try {
        val type = object : TypeToken<ReturnCodeVO<T>>() {}.type
        Gson().fromJson<ReturnCodeVO<T>>(json, type)
    } catch (e: Exception) {
        Log.e("Gson", "Failed to parse JSON: ${e.message}, JSON: $json")
        null
    }
}





