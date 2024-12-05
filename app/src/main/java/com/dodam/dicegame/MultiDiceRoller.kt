package com.dodam.dicegame

import android.media.MediaPlayer
import android.os.Build
import android.util.Log
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
import com.dodam.dicegame.component.displayDiceBlackJackTip
import com.dodam.dicegame.component.displayDiceRollResult
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
    roomId : String,
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

    val isRollingButtonEnabled = rolledSum < parsedTargetNumber


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
                Log.d("TEST_isPublic:", isPublic)
                Log.d("entryCode:", entryCode)
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
                enabled = isRollingButtonEnabled
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
                colors = ButtonDefaults.buttonColors(Color.Blue)
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
