package com.dodam.dicegame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun RollMultipleDice(diceValues: List<Int>) {
    if (diceValues.size == 1) {
        // 주사위 개수가 1개일 때 중앙 배치
        Box(
            modifier = Modifier
                .fillMaxSize(), // 화면 전체를 채움
            contentAlignment = Alignment.Center // 수평, 수직 중앙 정렬
        ) {
            Dice(diceValues.first()) // 주사위 1개 표시
        }
    } else if (diceValues.size == 2) {
        // 주사위 개수가 2개일 때 좌우 중간 배치
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center, // 좌우 중앙 정렬
            verticalAlignment = Alignment.CenterVertically // 상하 중앙 정렬
        ) {
            Dice(
                value = diceValues.first(),
                modifier = Modifier.padding(start = 16.dp) // 첫 번째 주사위에 왼쪽 마진 추가
            )
            Spacer(modifier = Modifier.width(16.dp)) // 주사위 간 간격 설정
            Dice(value = diceValues.last()) // 두 번째 주사위 표시
        }

    } else {
        // 주사위 개수가 3개 이상일 때는 기존 방식대로 세로로 배치
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // 여백 추가
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(diceValues.size) { index ->
                Dice(diceValues[index]) // 각 주사위 표시
            }
        }
    }
}



@Composable
fun Dice(value: Int) {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        DiceFace(value)
    }
}

@Composable
fun Dice(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        DiceFace(value)
    }
}

@Composable
fun DiceFace(value: Int) {
    val imageResId = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Dice Face",
        modifier = Modifier.size(200.dp)
    )
}