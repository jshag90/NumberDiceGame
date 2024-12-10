package com.dodam.dicegame

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dodam.dicegame.api.WebSocketClient
import com.dodam.dicegame.api.socketServerUrl
import com.dodam.dicegame.component.displayDiceBlackJackTip
import com.dodam.dicegame.component.displayDiceRollResult
import com.dodam.dicegame.vo.GetRoomsCountMessageVO
import com.dodam.dicegame.vo.JoinRoomMessageVO
import com.dodam.dicegame.vo.StartGameMessageVO
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MultiDiceRoller(
    targetNumber: String,
    numDice: String,
    isPublic: String,
    entryCode: String,
    userNickname: String,
    maxPlayer: String,
    roomId: String,
    isRoomMaster: String,
    navController: NavController
) {
    var diceValues by remember { mutableStateOf(List(numDice.toIntOrNull() ?: 1) { 1 }) }
    var rolledSum by remember { mutableStateOf(0) }
    var rollCount by remember { mutableStateOf(0) }
    var rolledText by remember { mutableStateOf("") }
    val parsedTargetNumber = targetNumber.toIntOrNull() ?: 21 // Default value
    val parsedNumDice = numDice.toIntOrNull() ?: 1 // Default value

    var showGifList by remember { mutableStateOf(List(parsedNumDice) { false }) }
    var isRolling by remember { mutableStateOf(false) }

    var isGameStarted by remember { mutableStateOf(false) }

    //주사위 소리 재생
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    LaunchedEffect(isRolling) {
        if (isRolling) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.dice_sound)
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener {
                it.release()
            }
        }
    }

    //현재 입장인원
    var memberCount by remember { mutableStateOf(0) }

    var webSocketClient by remember { mutableStateOf<WebSocketClient?>(null) }
    LaunchedEffect(roomId) {
        if (webSocketClient == null) {
            val client = WebSocketClient(context)
            client.connect(socketServerUrl) { roomCount -> memberCount = roomCount }

            val joinRoomMessageVO = JoinRoomMessageVO(roomId, userNickname, "joinRoom")
            client.sendMessage(Gson().toJson(joinRoomMessageVO))

            val getRoomsCountMessageVO = GetRoomsCountMessageVO(roomId, "getRoomsCount")
            client.sendMessage(Gson().toJson(getRoomsCountMessageVO))

            webSocketClient = client
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 뒤로가기 버튼
            IconButton(onClick = { navController.popBackStack() }) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // 뒤로가기 버튼과 닉네임 사이의 간격

            // 닉네임 텍스트
            Text(
                text = "$userNickname 님",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f)) // 닉네임과 방번호 사이의 간격을 자동으로 채움

            Column(
                horizontalAlignment = Alignment.End // Align text to the end
            ) {
                // Room number text
                Text(
                    text = "방번호 : $roomId",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (isPublic == "false") {
                    Text(
                        text = "입장코드 : $entryCode",
                        fontSize = 16.sp, // Smaller font size for entry code
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray // Slightly lighter color for distinction
                    )
                }

                Text(
                    text = "최대인원 : $maxPlayer",
                    fontSize = 16.sp, // Smaller font size for entry code
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray // Slightly lighter color for distinction
                )

                Text(
                    text = "현재입장인원 : $memberCount",
                    fontSize = 16.sp, // Smaller font size for entry code
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray // Slightly lighter color for distinction
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f) // 상단 영역을 스크롤 가능한 공간으로 설정
                .padding(13.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(13.dp))
                if (isRolling) {
                    LaunchedEffect(Unit) {
                        delay(1000) // 1초 후 결과 업데이트
                        diceValues = List(parsedNumDice) { Random.nextInt(1, 7) }
                        rolledText = diceValues.joinToString(", ")
                        rolledSum += diceValues.sum()
                        showGifList = List(parsedNumDice) { false }
                        isRolling = false
                    }
                    showGifList = List(parsedNumDice) { true }
                    GifImageList(showGifList)
                } else {
                    RollMultipleDice(diceValues)
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(13.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 게임시작 버튼
            // 게임시작 버튼
            if (isRoomMaster == "true" && !isGameStarted) {
                Button(
                    onClick = {
                        isGameStarted = true
                        webSocketClient?.let { client ->
                            val startGameMessageVO = StartGameMessageVO(roomId, "startGame")
                            client.sendMessage(Gson().toJson(startGameMessageVO))
                        }
                    },
                    modifier = Modifier
                        .width(200.dp) // 크기 절반으로 설정
                        .padding(horizontal = 13.dp),
                ) {
                    Text("게임시작")
                }

                Spacer(modifier = Modifier.height(13.dp))
            }


            // 게임시작 버튼이 클릭되면 숨겨짐
            if (isGameStarted) {


            }

            // 게임 시작 버튼을 누른 후에는 굴리기 버튼과 STOP 버튼을 활성화시킴
            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "$rolledSum",
                color = Color(0xFFD32F2F), // 강조를 위한 강렬한 빨간색 (Material Design Red 700)
                fontSize = 42.sp, // 글씨 크기 크게 설정
                fontWeight = FontWeight.Bold, // 굵게 설정
                style = MaterialTheme.typography.bodyLarge // 기본 스타일도 유지
            )

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("목표 숫자: $parsedTargetNumber")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (!isRolling) {
                        isRolling = true
                        rollCount++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                enabled = isRoomMaster=="true" && isGameStarted && !isRolling // 게임 시작 후에만 활성화, 굴릴 때는 비활성화
            ) {
                Text("굴리기(${rollCount}회)")
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Stop 버튼 추가
            Button(
                onClick = { /* 아무 동작도 수행하지 않음 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp),
                colors = ButtonDefaults.buttonColors(Color.Blue),
                enabled = isRoomMaster=="true" && isGameStarted && !isRolling // 게임 시작 후에만 활성화, 굴릴 때는 비활성화
            ) {
                Text("STOP")
            }

            Spacer(modifier = Modifier.height(13.dp))

            if (parsedTargetNumber >= 1 && rollCount > 0)
                displayDiceRollResult(parsedTargetNumber, rolledSum, rollCount)
            if (rollCount < 1)
                displayDiceBlackJackTip()
        }
    }
}


