package com.dodam.dicegame

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dodam.dicegame.api.createRoomWithOkHttpSync
import com.dodam.dicegame.api.getPlayerUuidWithOkHttpAsync
import com.dodam.dicegame.api.getRankingUuidWithOkHttpAsync
import com.dodam.dicegame.api.getRankingWithOkHttpAsync
import com.dodam.dicegame.vo.RoomInfoVO
import com.dodam.dicegame.vo.RoomJoinVO
import com.dodam.dicegame.vo.RoomType
import com.dodam.dicegame.api.joinPublicRoomWithOkHttpSync
import com.dodam.dicegame.dto.PlayerDto
import com.dodam.dicegame.dto.RankingDto
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
    var showTotalRankingModal by remember { mutableStateOf(false) }
    var rankingDtoList: List<RankingDto>? by remember { mutableStateOf(null) }
    var totalScore by remember { mutableStateOf(0) } // totalScore 변수 추가
    val uuid = UUIDManager.getOrCreateUUID(context)

    LaunchedEffect(showCreateRoomModal, showSecretRoomModal, showTotalRankingModal, rankingDtoList) {
        CoroutineScope(Dispatchers.IO).launch {
            getPlayerUuidWithOkHttpAsync(context){playerDto: PlayerDto? ->
                if (playerDto != null) {
                    totalScore = playerDto.totalScore
                }
            }
        }
    }

    Text(
        text = "나의 ID : ${uuid.substring(0, 8)}, 승점 : $totalScore",
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 17.dp),
        textAlign = TextAlign.End
    )

    Text(
        text = "uuid : $uuid",
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        color = Color.LightGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 17.dp),
        textAlign = TextAlign.End
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(17.dp), // 전체 화면 패딩 설정
        verticalArrangement = Arrangement.spacedBy(12.dp) // 버튼 간 마진 10dp
    ) {

        Button(
            onClick = {
                getRankingWithOkHttpAsync(context) { rankingList -> rankingDtoList = rankingList }
                showTotalRankingModal = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C25)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "랭킹보기",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "랭킹보기",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = { showCreateRoomModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C25)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "방 만들기",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "방만들기",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = { showSecretRoomModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C25)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "비공개 방 입장",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "비공개방",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    getPlayerUuidWithOkHttpAsync(context) { playerDto: PlayerDto? ->
                        if (playerDto != null) {
                            totalScore = playerDto.totalScore
                        }
                    }

                    val roomPlayerDto = joinPublicRoomWithOkHttpSync(
                        uuid,
                        context,
                        navController
                    )

                    withContext(Dispatchers.Main) {
                        if (roomPlayerDto != null) {
                            navController.navigate(
                                "game_room/${roomPlayerDto.targetNumber}" +
                                        "/${roomPlayerDto.diceCount}/true/-1" +
                                        "/${roomPlayerDto.maxPlayer}" +
                                        "/${roomPlayerDto.roomId}" +
                                        "/false"
                            )

                        } else {
                            Log.e("공개방 입장", "Failed to 공개방 입장. roomId is null.")
                        }
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(30.dp), // 버튼 높이를 키워서 아이콘을 크게 보여줄 수 있는 공간 확보
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C25)),
            shape = RoundedCornerShape(24.dp) // 버튼 모서리를 둥글게 설정
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "공개 방 입장",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "공개방",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }

    if(showTotalRankingModal){
        rankingDtoList?.let {
            TotalRankingModal(onDismiss = {showTotalRankingModal = false},
                onConfirm = {},
                rankingList = it
            )
        }
    }


    if (showCreateRoomModal) {
        CreateRoomModal(
            onDismiss = { showCreateRoomModal = false },
            onConfirm = { targetNumber, numDice, isPublic, entryCode, maxPlayers ->
                showCreateRoomModal = false
                val isPublicText = if (isPublic) "true" else "false"
                val entryCodeText = entryCode.ifBlank { "-1" }

                val roomInfo = RoomInfoVO(
                    maxPlayers = maxPlayers,
                    targetNumber = targetNumber,
                    diceCount = numDice,
                    roomType = if (isPublic) RoomType.PUBLIC else RoomType.SECRET,
                    entryCode = if (isPublic) "" else entryCode,
                    uuid =  UUIDManager.getOrCreateUUID(context)
                )

                // 비동기 방식으로 roomId를 받아오고 나서 navigate 호출
                CoroutineScope(Dispatchers.IO).launch {
                    val roomId = createRoomWithOkHttpSync(roomInfo, context)
                    withContext(Dispatchers.Main) {
                        if (roomId != null) {
                            navController.navigate(
                                "game_room/$targetNumber/$numDice/$isPublicText/$entryCodeText/$maxPlayers/${roomId}/true"
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
            onConfirm = { roomId, entryCode ->
                if (roomId > 0L && entryCode.isNotBlank()) {
                    showSecretRoomModal = false

                    CoroutineScope(Dispatchers.IO).launch {
                        val roomPlayerDto = joinSecretRoomWithOkHttpSync(
                            RoomJoinVO(roomId, entryCode, uuid),
                            context,
                            navController
                        )

                        withContext(Dispatchers.Main) {
                            if (roomPlayerDto != null) {
                                navController.navigate(
                                    "game_room/${roomPlayerDto.targetNumber}" +
                                            "/${roomPlayerDto.diceCount}/false/${roomPlayerDto.entryCode}" +
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

}