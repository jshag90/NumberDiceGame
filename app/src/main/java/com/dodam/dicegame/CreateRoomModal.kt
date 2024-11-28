package com.dodam.dicegame

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CreateRoomModal(
    onDismiss: () -> Unit,
    onConfirm: (goalNumber: Int, diceCount: Int, isPublic: Boolean, entryCode: String, nickname: String) -> Unit
) {
    var goalNumber by remember { mutableStateOf("21") } // 기본값 21
    var diceCount by remember { mutableStateOf("1") } // 기본값 1개
    var isPublic by remember { mutableStateOf(true) } // 기본값 공개
    var entryCode by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") } // 닉네임 추가

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                val targetNumber = goalNumber.toIntOrNull() ?: 21
                val numDice = diceCount.toIntOrNull() ?: 1
                val code = if (isPublic) "-1" else entryCode // 공개일 때 기본값 적용
                val userNickname = nickname.ifBlank { "익명" } // 빈 닉네임일 경우 기본값 설정
                onConfirm(targetNumber, numDice, isPublic, code, userNickname)
            }) {
                Text("만들기")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("취소")
            }
        },
        title = { Text("방 만들기") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("닉네임") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = goalNumber,
                    onValueChange = { goalNumber = it },
                    label = { Text("목표 숫자") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = diceCount,
                    onValueChange = { diceCount = it },
                    label = { Text("주사위 개수") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isPublic,
                        onClick = {
                            isPublic = true
                            entryCode = "-1" // 공개로 설정할 때 입장 코드 초기화
                        }
                    )
                    Text(
                        "공개",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                isPublic = true
                                entryCode = "-1" // 공개로 설정할 때 입장 코드 초기화
                            }
                    )
                    RadioButton(
                        selected = !isPublic,
                        onClick = { isPublic = false }
                    )
                    Text(
                        "비공개",
                        modifier = Modifier
                            .clickable { isPublic = false }
                    )
                }

                // 비공개일 경우만 입장 코드 입력 필드를 활성화
                if (!isPublic) {
                    OutlinedTextField(
                        value = entryCode,
                        onValueChange = { entryCode = it },
                        label = { Text("입장 코드 (선택)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

