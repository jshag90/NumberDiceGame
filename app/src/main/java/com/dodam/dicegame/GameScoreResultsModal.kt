package com.dodam.dicegame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dodam.dicegame.api.WebSocketClient
import com.dodam.dicegame.dto.ScoreResultsDto

@Composable
fun GameScoreResultsModal(
    navController: NavController,
    onConfirm: (scoreResultsDtoList: List<ScoreResultsDto>) -> Unit,
    scoreResultsDtoList: List<ScoreResultsDto>,
    currentUserNickName: String,
    webSocketClient: WebSocketClient
) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(scoreResultsDtoList)
                    webSocketClient?.closeConnection()
                    navController.popBackStack()
                }
            ) {
                Text("확인")
            }
        },
        title = { Text("게임 결과") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 테이블 헤더
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "순위",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center // 가운데 정렬
                    )
                    Text(
                        text = "닉네임",
                        modifier = Modifier.weight(2f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center // 가운데 정렬
                    )
                    Text(
                        text = "점수",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center // 가운데 정렬
                    )
                }
                // 결과 리스트
                scoreResultsDtoList.forEach { scoreResult ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = scoreResult.rank.toString(),
                            modifier = Modifier.weight(1f),
                            fontWeight = if (scoreResult.nickName == currentUserNickName) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center // 가운데 정렬
                        )
                        Text(
                            text = scoreResult.nickName,
                            modifier = Modifier.weight(2f),
                            fontWeight = if (scoreResult.nickName == currentUserNickName) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center // 가운데 정렬
                        )
                        Text(
                            text = scoreResult.score.toString(),
                            modifier = Modifier.weight(1f),
                            fontWeight = if (scoreResult.nickName == currentUserNickName) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center // 가운데 정렬
                        )
                    }
                }
            }
        }
    )
}


