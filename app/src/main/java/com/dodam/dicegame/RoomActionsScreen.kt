package com.dodam.dicegame

import com.dodam.dicegame.dto.RoomPlayerDto
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dodam.dicegame.api.createRoomWithOkHttpSync
import com.dodam.dicegame.vo.RoomInfoVO
import com.dodam.dicegame.vo.RoomJoinVO
import com.dodam.dicegame.vo.RoomType
import com.dodam.dicegame.api.joinPublicRoomWithOkHttpSync
import joinSecretRoomWithOkHttpSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RoomActionsScreen(
    navController: NavHostController,
    onCreateRoomClick: () -> Unit,
    onPrivateRoomClick: () -> Unit,
    onPublicRoomClick: () -> Unit
) {

    val context = LocalContext.current
    var showCreateRoomModal by remember { mutableStateOf(false) }
    var showSecretRoomModal by remember { mutableStateOf(false) }
    var showPublicRoomModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp), // 전체 화면 패딩 설정
        verticalArrangement = Arrangement.spacedBy(30.dp) // 버튼 간 마진 10dp
    ) {
        Button(
            onClick = { showCreateRoomModal = true }, // Show modal on button click
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(40.dp),
            /*  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),*/
            shape = RectangleShape
        ) {
            Icon(Icons.Filled.Add, contentDescription = "방 만들기", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "방만들기",
                color = Color.White,
                fontSize = 34.sp
            )
        }
        Button(
            onClick = { showSecretRoomModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(40.dp),
            /*   colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),*/
            shape = RectangleShape // 각진 모양으로 설정
        ) {
            Icon(Icons.Filled.Lock, contentDescription = "비공개 방 입장", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "비공개방",
                color = Color.White,
                fontSize = 34.sp
            )
        }
        Button(
            onClick = { showPublicRoomModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(40.dp),
            /*  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),*/
            shape = RectangleShape
        ) {
            Icon(Icons.Filled.Face, contentDescription = "공개 방 입장", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "공개방",
                color = Color.White,
                fontSize = 34.sp
            )
        }
    }

    if (showCreateRoomModal) {
        CreateRoomModal(
            onDismiss = { showCreateRoomModal = false },
            onConfirm = { targetNumber, numDice, isPublic, entryCode, userNickname, maxPlayers ->
                showCreateRoomModal = false
                val isPublicText = if (isPublic) "true" else "false"
                val entryCodeText = entryCode.ifBlank { "-1" }

                val roomInfo = RoomInfoVO(
                    maxPlayers = maxPlayers,
                    targetNumber = targetNumber,
                    diceCount = numDice,
                    roomType = if (isPublic) RoomType.PUBLIC else RoomType.SECRET,
                    entryCode = if (isPublic) "" else entryCode,
                    nickName = userNickname
                )

                // 비동기 방식으로 roomId를 받아오고 나서 navigate 호출
                CoroutineScope(Dispatchers.IO).launch {
                    val roomId = createRoomWithOkHttpSync(roomInfo, context)
                    withContext(Dispatchers.Main) {
                        if (roomId != null) {
                            navController.navigate(
                                "game_room/$targetNumber/$numDice/$isPublicText/$entryCodeText/$userNickname/$maxPlayers/${roomId}/true"
                            )
                        } else {
                            Log.e("방만들기", "Failed to 방만들기. roomId is null.")
                        }
                    }
                }

            }
        )
    }

    if (showSecretRoomModal) {
        SecretRoomModal(
            onDismiss = { showSecretRoomModal = false },
            onConfirm = { roomId, entryCode, nickName ->
                if (roomId > 0L && entryCode.isNotBlank() && nickName.isNotBlank()) {
                    showSecretRoomModal = false

                    CoroutineScope(Dispatchers.IO).launch {
                        val roomPlayerDto = joinSecretRoomWithOkHttpSync(
                            RoomJoinVO(roomId, entryCode, nickName),
                            context,
                            navController,
                            RoomPlayerDto(0, 0, 0, 0, 0, "", "")
                        )

                        withContext(Dispatchers.Main) {
                            if (roomPlayerDto != null) {
                                navController.navigate(
                                    "game_room/${roomPlayerDto.targetNumber}" +
                                            "/${roomPlayerDto.diceCount}/false/${roomPlayerDto.entryCode}" +
                                            "/${roomPlayerDto.nickName}" +
                                            "/${roomPlayerDto.maxPlayer}" +
                                            "/${roomPlayerDto.roomId}"+
                                            "/false"
                                )
                            } else {
                                Log.e("비밀방 입장", "Failed to 비밀방 입장.")
                            }
                        }
                    }


                    onPrivateRoomClick()
                }
            }
        )
    }

    if (showPublicRoomModal) {
        PublicRoomModal(
            onDismiss = { showPublicRoomModal = false },
            onConfirm = { nickName ->
                if (nickName.isNotBlank()) {
                    showPublicRoomModal = false

                    CoroutineScope(Dispatchers.IO).launch {
                        val roomPlayerDto = joinPublicRoomWithOkHttpSync(
                            nickName,
                            context,
                            navController
                        )

                        withContext(Dispatchers.Main) {
                            if (roomPlayerDto != null) {
                                navController.navigate(
                                    "game_room/${roomPlayerDto.targetNumber}" +
                                                  "/${roomPlayerDto.diceCount}/true/-1" +
                                                  "/${roomPlayerDto.nickName}" +
                                                  "/${roomPlayerDto.maxPlayer}" +
                                                  "/${roomPlayerDto.roomId}"+
                                                  "/false"
                                )
                            } else {
                                Log.e("공개방 입장", "Failed to 공개방 입장. roomId is null.")
                            }
                        }
                    }

                    onPrivateRoomClick()
                }
            }
        )
    }


}