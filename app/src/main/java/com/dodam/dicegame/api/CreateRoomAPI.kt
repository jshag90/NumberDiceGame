package com.dodam.dicegame.api

import android.content.Context
import android.widget.Toast
import com.dodam.dicegame.vo.ReturnCodeVO
import com.dodam.dicegame.vo.RoomInfoVO
import okhttp3.Request

fun createRoomWithOkHttpSync(roomInfo: RoomInfoVO, context: Context): Long? {
    val url = "$serverUrl/room/create"
    val jsonBody = toJson(roomInfo, RoomInfoVO::class.java)
    val requestBody = createRequestBody(jsonBody)
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    val responseBody = executeRequest(request)
    val returnCodeVO: ReturnCodeVO<Long>? = fromJson(responseBody)
    if (returnCodeVO?.returnCode != 0) {
        Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
        return null
    }

    return returnCodeVO.data //방번호

}

