package com.dodam.dicegame.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.dodam.dicegame.dto.RankingDto
import com.dodam.dicegame.vo.ReturnCodeVO
import kotlinx.coroutines.*
import okhttp3.Request

fun getRankingWithOkHttpAsync(
    context: Context,
    onResult: (List<RankingDto>?) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = "$serverUrl/score/ranking"
            val request = Request.Builder().url(url).get()
                .addHeader(HttpHeaders.ACCEPT, HttpHeadersValue.ACCEPT_VALUE).build()

            val responseBody = executeRequest(request)

            if (responseBody == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "서버 응답이 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
                onResult(null)
                return@launch
            }

            val returnCodeVO: ReturnCodeVO<List<RankingDto>>? = fromJson(responseBody)

            if (returnCodeVO == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
                }
                onResult(null)
                return@launch
            }

            withContext(Dispatchers.Main) {
                onResult(returnCodeVO.data)
            }
        } catch (e: Exception) {
            Log.e("ERROR : ", "Network error", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "예기치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
            onResult(null)
        }
    }
}



