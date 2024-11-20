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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DiceRoller() {
    var diceValues by remember { mutableStateOf(List(1) { 1 }) }
    var numDice by remember { mutableStateOf(1) }
    var rolledText by remember { mutableStateOf("") }
    var isRolling by remember { mutableStateOf(false) }
    var showGifList by remember { mutableStateOf(List(1) { false }) }
    var rolledSum by remember { mutableStateOf(0) }
    var targetNumber by remember { mutableStateOf(21) }
    var rollCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 주사위 UI를 스크롤 가능하게 설정
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
                        delay(1000)
                        diceValues = List(numDice) { Random.nextInt(1, 7) }
                        rolledText = diceValues.joinToString(", ")
                        rolledSum += diceValues.sum()
                        showGifList = List(numDice) { false }
                        isRolling = false
                    }
                    showGifList = List(numDice) { true }
                    GifImageList(showGifList)
                } else {
                    RollMultipleDice(diceValues)
                }
            }
        }

        // 고정된 하단 UI
        Column(
            modifier = Modifier
                .padding(13.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$rolledSum",
                    color = Color(0xFFD32F2F), // 강조를 위한 강렬한 빨간색 (Material Design Red 700)
                    fontSize = 42.sp, // 글씨 크기 크게 설정
                    fontWeight = FontWeight.Bold, // 굵게 설정
                    style = MaterialTheme.typography.bodyLarge // 기본 스타일도 유지
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("   목표 숫자  ")
                Button(
                    onClick = {
                        if (targetNumber > 0) targetNumber--
                    },
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                    enabled = rolledSum < targetNumber // 조건 추가
                ) {

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("$targetNumber")
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { targetNumber++ },
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                    enabled = rolledSum < targetNumber // 조건 추가
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("주사위 개수 ")
                Button(
                    onClick = {
                        if (numDice > 1) {
                            numDice--
                            showGifList = List(numDice) { false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                    enabled = rolledSum < targetNumber // 조건 추가
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(numDice.toString())
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        numDice++
                        showGifList = List(numDice) { false }
                    },
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                    enabled = rolledSum < targetNumber // 조건 추가
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Spacer(modifier = Modifier.height(13.dp))

            Button(
                onClick = {
                    if (!isRolling) {
                        isRolling = true
                        rollCount++
                    }
                },
                enabled = !isRolling && rolledSum < targetNumber, // 조건 추가
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Refresh Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("굴리기(${rollCount}회)")
            }

            Spacer(modifier = Modifier.height(5.dp))

            Button(
                onClick = {
                    // 모든 상태 초기화
                    //  diceValues = List(1) { 1 }
                    numDice = 1
                    rolledText = ""
                    isRolling = false
                    showGifList = List(1) { false }
                    rolledSum = 0
                    //  targetNumber = 0
                    rollCount = 0
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("다시하기")
            }

            Spacer(modifier = Modifier.height(13.dp))


            if (targetNumber >= 1 && rollCount > 0) {

                if (targetNumber == rolledSum) {
                    Text("목표 숫자와 일치! 🎉", color = Color.Green)
                    Text("주사위를 ${rollCount}번 만에 목표 숫자와 일치했어요!")
                }

                if (targetNumber < rolledSum) {
                    Text(
                        text = "목표 숫자 맞추기에 실패하였습니다! \uD83D\uDC80",
                        color = Color(0xFFB00020)
                    )
                }

                if (targetNumber > rolledSum) {
                    Text(
                        text = "아직 목표 숫자에 도달하지 못했습니다. 😅",
                        color = Color.Blue
                    )
                }

            }else{
                Text("Tip. 목표 숫자에 도달할 때까지 주사위를 굴려주세요.")
            }
        }
    }
}