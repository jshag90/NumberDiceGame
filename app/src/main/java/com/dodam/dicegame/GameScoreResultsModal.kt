package com.dodam.dicegame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(), // Ensure the Box takes the full width of the screen
                contentAlignment = Alignment.Center // Center the content inside the Box
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "최종 결과",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = scoreResultsDtoList.get(0).targetNumber.toString(),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 8.dp) // Add some space between the two Text elements
                    )
                    Text(
                        text = "(목표 숫자)",
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp, // Set a smaller font size for "(목표 숫자)"
                        modifier = Modifier.padding(top = 8.dp) // Add some space between the two Text elements
                    )
                }
            }

        }
        ,
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
                        // Rank with star icon for rank 1, 2, or 3
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val starColor = when (scoreResult.rank) {
                                1 -> Color(0xFFFFD700) // Gold color for rank 1
                                2 -> Color(0xFFC0C0C0) // Silver color for rank 2
                                3 -> Color(0xFFCD7F32) // Bronze color for rank 3
                                else -> Color.Gray // Default color for other ranks
                            }

                            if (scoreResult.rank in 1..3) {
                                Icon(
                                    imageVector = Icons.Filled.Star, // Star icon from Material Icons
                                    contentDescription = "Star",
                                    modifier = Modifier.size(16.dp), // Adjust the icon size
                                    tint = starColor // Set the appropriate color based on the rank
                                )
                            }
                            Text(
                                text = scoreResult.rank.toString(),
                                fontWeight = if (scoreResult.nickName == currentUserNickName) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center // 가운데 정렬
                            )
                        }
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


