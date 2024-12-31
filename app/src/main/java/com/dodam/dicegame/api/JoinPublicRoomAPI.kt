package com.dodam.dicegame.api

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.dodam.dicegame.component.showNicknameChangeModal
import com.dodam.dicegame.dto.RoomPlayerDto
import com.dodam.dicegame.vo.ReturnCodeVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

fun joinPublicRoomWithOkHttpSync(
    uuid: String,
    context: Context,
    navController: NavController
): RoomPlayerDto? {
    val url = "$serverUrl/room/public/join/uuid=${uuid}"

    val request = Request.Builder().url(url).get()
        .addHeader(HttpHeaders.ACCEPT, HttpHeadersValue.ACCEPT_VALUE).build()

    val responseBody = executeRequest(request)
    if (responseBody == null) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "서버 응답이 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    val returnCodeVO: ReturnCodeVO<RoomPlayerDto>? = fromJson(responseBody)

    if (returnCodeVO == null) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    if (returnCodeVO.returnCode == -2) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "공개방이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    if (returnCodeVO.returnCode == -5) {
        CoroutineScope(Dispatchers.Main).launch {
            returnCodeVO.data?.let {
                showNicknameChangeModal(
                    context,
                    it.playerId,
                    navController,
                    returnCodeVO.data
                )
            }
        }
        return returnCodeVO.data
    }

    return returnCodeVO.data
}



