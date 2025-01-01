package com.dodam.dicegame.api

import android.content.Context
import android.widget.Toast
import com.dodam.dicegame.vo.ReturnCodeVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

fun deletePlayerOkHttpSync(
    roomId: String,
    uuid: String,
    context: Context
) {
    CoroutineScope(Dispatchers.IO).launch {
        val url = "$serverUrl/player/delete/in-room/${roomId}/${uuid}"

        val request = Request.Builder().url(url).delete()
            .addHeader(HttpHeaders.ACCEPT, HttpHeadersValue.ACCEPT_VALUE).build()

        val responseBody = executeRequest(request)
        if (responseBody == null) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "서버 응답이 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        val returnCodeVO: ReturnCodeVO<Void>? = fromJson(responseBody)

        if (returnCodeVO == null) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}



