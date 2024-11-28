package com.dodam.dicegame.component

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun displayDiceRollResult(targetNumber: Int, rolledSum: Int, rollCount: Int) {
    // 목표 숫자 비교 로직
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
}

@Composable
fun displayDiceBlackJackTip(){

    val tipFontSize = if (Build.MODEL.contains("S23", ignoreCase = true)) 14.sp else 15.sp

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