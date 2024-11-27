package com.dodam.dicegame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MultiDiceRoller(
    targetNumber: String,
    numDice: String,
    isPublic: String,
    entryCode: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("목표 숫자: $targetNumber", style = MaterialTheme.typography.headlineMedium)
        Text("주사위 개수: $numDice", style = MaterialTheme.typography.headlineMedium)
        Text("방 공개 여부: $isPublic", style = MaterialTheme.typography.headlineMedium)
        Text("입장 코드: $entryCode", style = MaterialTheme.typography.headlineMedium)
    }
}