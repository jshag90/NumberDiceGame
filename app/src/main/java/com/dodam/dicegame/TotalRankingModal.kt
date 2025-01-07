package com.dodam.dicegame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dodam.dicegame.api.getRankingUuidWithOkHttpAsync
import com.dodam.dicegame.dto.RankingDto

@Composable
fun TotalRankingModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    rankingList: List<RankingDto>
) {
    val context = LocalContext.current // Toast를 표시하기 위해 Context 필요
    val currentUuid = UUIDManager.getOrCreateUUID(context);

    val addRankingList = remember { mutableStateListOf<RankingDto>().apply { addAll(rankingList) } }
    val isCurrentUuidInRankingList = rankingList.any { it.uuid == currentUuid }

    LaunchedEffect(rankingList) {
        addRankingList.clear()
        addRankingList.addAll(rankingList)
    }

    LaunchedEffect(isCurrentUuidInRankingList) {
        if (!isCurrentUuidInRankingList) {
            getRankingUuidWithOkHttpAsync(context) { rankingDto ->
                if (rankingDto != null) {
                    addRankingList.add(rankingDto)
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("확인")
            }
        },
        title = { Text("랭킹보기") },
        text = {

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "순위",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center // 가운데 정렬
                    )
                    Text(
                        text = "ID",
                        modifier = Modifier.weight(2f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center // 가운데 정렬
                    )
                    Text(
                        text = "승점",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center // 가운데 정렬
                    )
                }

                addRankingList.forEachIndexed { index, ranking ->
                    if (index == 10) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp), // 위아래 여백 추가
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown, // 점 세 개 아이콘(Material Icons)
                                contentDescription = "More",
                                tint = Color.Gray, // 적절한 색상 선택
                                modifier = Modifier.size(15.dp) // 아이콘 크기 설정
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val starColor = when (ranking.rank) {
                                1 -> Color(0xFFFFD700) // Gold color for rank 1
                                2 -> Color(0xFFC0C0C0) // Silver color for rank 2
                                3 -> Color(0xFFCD7F32) // Bronze color for rank 3
                                else -> Color.Gray // Default color for other ranks
                            }

                            if (ranking.rank in 1..3) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Star",
                                    modifier = Modifier.size(16.dp),
                                    tint = starColor
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Face,
                                    contentDescription = "Face",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.LightGray
                                )
                            }
                            Text(
                                text = ranking.rank.toString(),
                                fontWeight = if (ranking.uuid == currentUuid) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text = ranking.uuid.substring(0, 8),
                            modifier = Modifier.weight(2f),
                            fontWeight = if (ranking.uuid == currentUuid) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = ranking.totalScore.toString(),
                            modifier = Modifier.weight(1f),
                            fontWeight = if (ranking.uuid == currentUuid) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }




            }
        }
    )
}

