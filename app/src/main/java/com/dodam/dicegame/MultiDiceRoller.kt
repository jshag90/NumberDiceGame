package com.dodam.dicegame

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.dodam.dicegame.api.getScoreResultsOkHttpSync
import com.dodam.dicegame.api.saveScoreWithOkHttpAsync
import com.dodam.dicegame.api.socketServerUrl
import com.dodam.dicegame.component.displayDiceBlackJackTip
import com.dodam.dicegame.component.displayDiceRollResult
import com.dodam.dicegame.dto.ScoreResultsDto
import com.dodam.dicegame.vo.CreateGameRoomMessageVO
import com.dodam.dicegame.vo.GetRoomsCountMessageVO
import com.dodam.dicegame.vo.JoinRoomMessageVO
import com.dodam.dicegame.vo.LeaveRoomMessageVO
import com.dodam.dicegame.vo.PlayGameMessageVO
import com.dodam.dicegame.vo.ResponseMessageVO
import com.dodam.dicegame.vo.SaveScoreVO
import com.dodam.dicegame.vo.StartGameMessageVO
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MultiDiceRoller(
    targetNumber: String,
    numDice: String,
    isPublic: String,
    entryCode: String,
    maxPlayer: String,
    roomId: String,
    isRoomMaster: String,
    navController: NavController
) {
    val GAME_START_TIMEOUT = 600 //10분
    val GAME_PLAY_TIMEOUT = 30 //30초

    val context = LocalContext.current
    val uuid = UUIDManager.getOrCreateUUID(context)

    var diceValues by remember { mutableStateOf(List(numDice.toIntOrNull() ?: 1) { 1 }) }
    var rolledSum by remember { mutableStateOf(0) }
    var rollCount by remember { mutableStateOf(0) }
    var rolledText by remember { mutableStateOf("") }
    val parsedTargetNumber = targetNumber.toIntOrNull() ?: 21
    val parsedNumDice = numDice.toIntOrNull() ?: 1

    var showGifList by remember { mutableStateOf(List(parsedNumDice) { false }) }
    var tipMessage by remember { mutableStateOf("Tip. 목표 숫자에 도달할 때까지 주사위를 굴려주세요.") }

    //현재 입장인원
    var memberCount by remember { mutableStateOf(0) }
    var isAllDoneRoundPlay by remember { mutableStateOf(true) }
    var isGameEnd by remember { mutableStateOf(false) }
    var webSocketClient by remember { mutableStateOf<WebSocketClient?>(null) }
    var isSelfStop by remember { mutableStateOf(false) }
    var isRoomMasterFlag by remember { mutableStateOf(isRoomMaster) }

    var isGameStarted by remember { mutableStateOf(false) }
    var isGameTimeout by remember { mutableStateOf(false) }
    var timerValue by remember { mutableStateOf(GAME_PLAY_TIMEOUT) }

    var isRolling by remember { mutableStateOf(false) }
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

    fun startNewRound() {
        timerValue = GAME_PLAY_TIMEOUT
        isGameTimeout = false
    }

    LaunchedEffect(isGameStarted) {
        if (isGameStarted && isAllDoneRoundPlay && !isRolling && !isSelfStop) {
            while (timerValue > 0) {
                delay(1000) // 1초씩 대기
                timerValue -= 1 // 1초씩 감소
            }
            if (timerValue == 0) {
                isGameTimeout = true
                isGameStarted = false
                showToast(context, "${GAME_PLAY_TIMEOUT}초 타임아웃! 게임 종료.")
            }
        }
    }

    LaunchedEffect(roomId) {
        if (webSocketClient == null) {
            val client = WebSocketClient(context)
            val createGameRoomMessageVO = CreateGameRoomMessageVO(roomId, uuid, "createGameRoom")
            val getRoomsCountMessageVO = GetRoomsCountMessageVO(roomId, "getRoomsCount") //입장 인원
            val joinRoomMessageVO = JoinRoomMessageVO(roomId, uuid, "joinRoom") //방 입장
            client.connect(socketServerUrl,
                { roomCount: Int ->
                    memberCount = roomCount
                },
                { gameStarted: Boolean ->
                    isGameStarted = gameStarted
                },
                { allDoneRoundPlay: String ->
                    when (allDoneRoundPlay) {
                        "done" -> {
                            isAllDoneRoundPlay = true
                            startNewRound()
                        }

                        "end" -> {
                            isAllDoneRoundPlay = false
                            isGameEnd = true
                        }
                    }
                },
                { isChangeRoomMaster: ResponseMessageVO ->
                    if (isChangeRoomMaster.subMessage == "changeRoomMaster" && isChangeRoomMaster.message == uuid) {
                        isRoomMasterFlag = "true"
                        isGameStarted = false
                    }
                    client.sendMessage(Gson().toJson(getRoomsCountMessageVO)) //입장 인원 갱신
                }
            )

            client.sendMessage(Gson().toJson(joinRoomMessageVO))
            client.sendMessage(Gson().toJson(getRoomsCountMessageVO))
            if (isRoomMaster == "true" && memberCount < 2) {
                isGameStarted = false
                client.sendMessage(Gson().toJson(createGameRoomMessageVO))
            }

            webSocketClient = client
        }
    }

    val showGameScoreResultsModal = remember { mutableStateOf(false) }
    val scoreResultsDtoListState = remember { mutableStateOf<List<ScoreResultsDto>>(emptyList()) }

    LaunchedEffect(isGameEnd) {
        if (isGameEnd&&isGameStarted) {
            val scoreResultsDtoList =
                withContext(Dispatchers.IO) { getScoreResultsOkHttpSync(roomId, uuid, context) }

            scoreResultsDtoList?.let {
                scoreResultsDtoListState.value = it
                showGameScoreResultsModal.value = true
            }
        }

    }

    if (isGameStarted && isGameEnd && memberCount < 2) {
        sendPlayGameMessageWebSocket(webSocketClient, roomId, "N")
        saveScoreWithOkHttpAsync(
            SaveScoreVO(roomId.toLong(), uuid, rollCount, rolledSum),
            context
        ) {}

        isSelfStop = true
        isGameStarted = false

        showToast(context, "더 이상 게임을 진행할 플레이어가 존재하지 않습니다.")
    }

    // 타임아웃이 발생했을 때 처리할 로직 추가
    LaunchedEffect(isGameTimeout) {
        if (isGameTimeout) {
            isSelfStop = true
            startNewRound()
            sendPlayGameMessageWebSocket(webSocketClient, roomId, "N")
            saveScoreWithOkHttpAsync(
                SaveScoreVO(roomId.toLong(), uuid, rollCount, rolledSum),
                context
            ) {}
        }
    }

    var gameStartTimeout by remember { mutableStateOf(GAME_START_TIMEOUT) }
    var isRoomTimeout by remember { mutableStateOf(false) }

    LaunchedEffect(isGameStarted, gameStartTimeout) {
        if (!isGameStarted) {
            while (gameStartTimeout > 0) {
                delay(1000)
                gameStartTimeout -= 1
            }
            if (gameStartTimeout == 0 && !isGameStarted) {
                isRoomTimeout = true
            }
        } else {
            gameStartTimeout = GAME_START_TIMEOUT
        }
    }

    LaunchedEffect(isRoomTimeout) {
        if (isRoomTimeout) {
            sendLeaveRoomMessageWebSocket(webSocketClient, roomId, uuid)
            webSocketClient?.closeConnection()
            showToast(context, "게임이 5분 동안 시작되지 않아 방을 나갑니다.")
            navController.popBackStack()
        }
    }


    BackHandler {
        sendPlayGameMessageWebSocket(webSocketClient, roomId, "N")
        saveScoreWithOkHttpAsync(
            SaveScoreVO(roomId.toLong(), uuid, rollCount, rolledSum),
            context
        ) {
            sendLeaveRoomMessageWebSocket(webSocketClient, roomId, uuid)
            webSocketClient?.closeConnection()
        }
        navController.popBackStack()
    }

    Column(
        modifier = Modifier.fillMaxSize(), // 배경색 설정,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {
                sendPlayGameMessageWebSocket(webSocketClient, roomId, "N")
                saveScoreWithOkHttpAsync(
                    SaveScoreVO(roomId.toLong(), uuid, rollCount, rolledSum),
                    context
                ) {
                    sendLeaveRoomMessageWebSocket(webSocketClient, roomId, uuid)
                    webSocketClient?.closeConnection()
                }
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${uuid.substring(0, 8)} 님",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = "- 방번호 : $roomId",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                if (isPublic == "false") {
                    Text(
                        text = "- 입장코드 : $entryCode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }

                Text(
                    text = "- 최대인원 : $maxPlayer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Text(
                    text = "- 입장인원 : $memberCount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Text(
                    text = "- 라운드 : ${rollCount + 1}",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge
                )

            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(13.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(13.dp))

                if (isRolling) {
                    LaunchedEffect(Unit) {
                        delay(900)
                        diceValues = List(parsedNumDice) { Random.nextInt(1, 7) }
                        rolledText = diceValues.joinToString(", ")
                        rolledSum += diceValues.sum()
                        showGifList = List(parsedNumDice) { false }
                        isRolling = false

                        saveScoreWithOkHttpAsync(
                            SaveScoreVO(roomId.toLong(), uuid, rollCount, rolledSum),
                            context
                        ) {}

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
            if (isRoomMasterFlag == "true" && !isGameStarted) {

                Button(
                    onClick = {
                        if (memberCount < 2) {
                            showToast(context, "참가 인원이 최소 2명이 되어야 합니다.")
                            return@Button
                        }

                        webSocketClient?.let { client ->
                            val startGameMessageVO = StartGameMessageVO(roomId, "startGame")
                            client.sendMessage(Gson().toJson(startGameMessageVO))
                        }
                        isRoomMasterFlag = "false"
                        startNewRound()
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(horizontal = 13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C25)),
                ) {
                    Text("게임시작")
                }

                Spacer(modifier = Modifier.height(13.dp))
            }


            if (isGameStarted) {
                tipMessage = "모든 플레이어가 굴리기|STOP을 결정해야지 버튼이 활성화됩니다."
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "$rolledSum",
                color = Color(0xFFD32F2F),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isGameStarted && !isGameTimeout && !isGameEnd && !isSelfStop && isAllDoneRoundPlay) {
                    Text(
                        text = "남은 시간: ${timerValue}초",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (isGameTimeout && !isGameEnd && !isSelfStop && isAllDoneRoundPlay) {
                    Text(
                        text = "시간 초과!",
                        color = Color.Red,
                        fontSize = 20.sp
                    )
                }

            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "목표숫자 : $parsedTargetNumber",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )

            }

            Spacer(modifier = Modifier.height(20.dp))


            Button(
                onClick = {

                    isAllDoneRoundPlay = false
                    if (!isRolling) {
                        isRolling = true
                        rollCount++
                    }
                    sendPlayGameMessageWebSocket(webSocketClient, roomId, "Y")

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141C25)),
                enabled = isGameStarted && !isRolling && isAllDoneRoundPlay && !isSelfStop// 게임 시작 후에만 활성화, 굴릴 때는 비활성화
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "PlayArrow Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("굴리기")
            }


            Spacer(modifier = Modifier.height(5.dp))

            Button(
                onClick = {

                    isSelfStop = true
                    sendPlayGameMessageWebSocket(webSocketClient, roomId, "N")

                    saveScoreWithOkHttpAsync(
                        SaveScoreVO(roomId.toLong(), uuid, rollCount, rolledSum),
                        context
                    ) {}

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                enabled = (isGameStarted && !isRolling && isAllDoneRoundPlay && !isSelfStop)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("STOP")
            }

            Spacer(modifier = Modifier.height(13.dp))

            if (parsedTargetNumber >= 1 && rollCount > 0) {

                if (parsedTargetNumber < rolledSum) {

                    isSelfStop = true

                    sendPlayGameMessageWebSocket(webSocketClient, roomId, "N")

                    saveScoreWithOkHttpAsync(
                        SaveScoreVO(roomId.toLong(), uuid, rollCount, rolledSum),
                        context
                    ) {}

                }

            }

            if (isGameStarted && rollCount > 0) {
                displayDiceRollResult(parsedTargetNumber, rolledSum, rollCount)
            }

            if (rollCount < 1) {
                displayDiceBlackJackTip(tipMessage)
            }

            if (showGameScoreResultsModal.value) {
                webSocketClient?.let {
                    GameScoreResultsModal(
                        navController,
                        onConfirm = { showGameScoreResultsModal.value = false},
                        scoreResultsDtoList = scoreResultsDtoListState.value,
                        currentUuid = uuid,
                        webSocketClient = it,
                        roomId = roomId,
                        uuid = uuid,
                        context = context
                    )
                }
            }


        }
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/**
 * websocket으로 게임 진행 여부 메시지를 보냄.
 */
private fun sendPlayGameMessageWebSocket(
    webSocketClient: WebSocketClient?,
    roomId: String,
    isGo: String
) {
    webSocketClient?.let { client ->
        val playGameMessageVO = PlayGameMessageVO(roomId, isGo, "playGame")
        client.sendMessage(Gson().toJson(playGameMessageVO))
    }
}

private fun sendLeaveRoomMessageWebSocket(
    webSocketClient: WebSocketClient?,
    roomId: String,
    uuid: String
) {
    webSocketClient?.let { client ->
        val playGameMessageVO = LeaveRoomMessageVO(roomId, uuid, "leaveRoom")
        client.sendMessage(Gson().toJson(playGameMessageVO))
    }
}


