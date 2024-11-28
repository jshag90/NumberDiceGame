package com.dodam.dicegame

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
    val tipFontSize = if (Build.MODEL.contains("S23", ignoreCase = true)) 14.sp else 15.sp

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단에 뒤로가기 버튼과 닉네임 표시
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 뒤로가기 버튼
            IconButton(onClick = {navController.popBackStack()}) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.Black
                )
            }
            // 닉네임 텍스트
            Text(
                text = "$userNickname 님", // 닉네임에 "님" 붙이기
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
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
                color = Color.Red,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
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
                colors = ButtonDefaults.buttonColors(Color.Red)
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

            // 목표 숫자와 주사위 합계 비교 로직
            if (parsedTargetNumber >= 1 && rollCount > 0) {
                if (parsedTargetNumber == rolledSum) {
                    Text("목표 숫자와 일치! 🎉", color = Color.Green)
                    Text("주사위를 ${rollCount}번 만에 목표 숫자와 일치했어요!")
                }

                if (parsedTargetNumber < rolledSum) {
                    Text(
                        text = "목표 숫자 맞추기에 실패하였습니다! \uD83D\uDC80",
                        color = Color(0xFFB00020)
                    )
                }

                if (parsedTargetNumber > rolledSum) {
                    Text(
                        text = "아직 목표 숫자에 도달하지 못했습니다. 😅",
                        color = Color.Blue
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(), // 가로 방향 전체를 채움
                    horizontalArrangement = Arrangement.Center // 좌우 중앙 정렬
                ) {
                    Text(
                        text = "Tip. 목표 숫자에 도달할 때까지 주사위를 굴려주세요.",
                        fontSize = tipFontSize,
                    )
                }
            }
        }
    }
}
