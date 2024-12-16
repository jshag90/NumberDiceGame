package com.dodam.dicegame.api

import android.content.Context
import android.widget.Toast
import com.dodam.dicegame.vo.ReturnCodeVO
import com.dodam.dicegame.vo.SaveScoreVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*

fun saveScoreWithOkHttpAsync(saveScoreVO: SaveScoreVO, context: Context, onComplete: (Long?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val url = "$serverUrl/score/save"
        val jsonBody = toJson(saveScoreVO, SaveScoreVO::class.java)
        val requestBody = createRequestBody(jsonBody)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val responseBody = executeRequest(request)
            val returnCodeVO: ReturnCodeVO<Long>? = fromJson(responseBody)

            // UI 업데이트는 Main 스레드에서 처리
            withContext(Dispatchers.Main) {
                if (returnCodeVO?.returnCode != 0) {
                    Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
                    onComplete(null)
                } else {
                    onComplete(returnCodeVO.data)
                }
            }
        } catch (e: Exception) {
            // 에러 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "요청 처리 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                onComplete(null)
            }
        }
    }
}

