package com.dodam.dicegame.api

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.dodam.dicegame.component.showNicknameChangeModal
import com.dodam.dicegame.dto.RoomPlayerDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType

fun updateNickNameWithOkHttpAsync(
    playerId: Int,
    nickName: String,
    context: Context,
    navController: NavController,
    roomPlayerDto: RoomPlayerDto
) {
    val url = "$serverUrl/player/update/nick-name/$playerId/$nickName"

    val requestBody = RequestBody.create("application/json".toMediaType(), "")
    val request = Request.Builder()
        .url(url)
        .put(requestBody)
        .addHeader("accept", "*/*")
        .addHeader("Content-Type", "application/json")
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val responseBody = executeRequest(request)

            if (responseBody != null) {
                val returnCodeVO = fromJson<Void>(responseBody)
                when (returnCodeVO?.returnCode) {
                    0 -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "닉네임 변경에 성공했습니다.", Toast.LENGTH_SHORT).show()

                            //닉네임 변경에 성공하면 게임화면으로 재진입
                            navController.navigate(
                                "game_room/${roomPlayerDto.targetNumber}" +
                                        "/${roomPlayerDto.diceCount}" +
                                        "/${roomPlayerDto.isPublic}" +
                                        "/${roomPlayerDto.entryCode}" +
                                        "/${nickName}" + // Updated nickname
                                        "/${roomPlayerDto.maxPlayer}" +
                                        "/${roomPlayerDto.roomId}"+
                                        "/${roomPlayerDto.isRoomMaster}"
                            ) {
                                popUpTo("previous_screen_route") { inclusive = true }
                                launchSingleTop = true
                            }

                        }
                    }

                    -5 -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "다른 플레이어가 사용하는 닉네임입니다.", Toast.LENGTH_SHORT)
                                .show()
                            showNicknameChangeModal(
                                context,
                                playerId,
                                navController,
                                roomPlayerDto
                            )
                        }
                    }

                    -6 -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "동일한 닉네임으로 변경할 수 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                            showNicknameChangeModal(
                                context,
                                playerId,
                                navController,
                                roomPlayerDto
                            )
                        }
                    }

                    else -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "닉네임 변경 실패: 코드 ${returnCodeVO?.returnCode}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
