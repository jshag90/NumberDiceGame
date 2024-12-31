package com.dodam.dicegame

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CreateRoomModal(
    onDismiss: () -> Unit,
    onConfirm: (goalNumber: Int, diceCount: Int, isPublic: Boolean, entryCode: String, maxPlayers: Int) -> Unit
) {
    val context = LocalContext.current // Context를 가져옴
    var goalNumber by remember { mutableStateOf("21") } // 기본값 21
    var diceCount by remember { mutableStateOf("1") } // 기본값 1개
    var isPublic by remember { mutableStateOf(true) } // 기본값 공개
    var entryCode by remember { mutableStateOf("") }
    var maxPlayers by remember { mutableStateOf(5) } // 기본값 2명
    var expanded by remember { mutableStateOf(false) } // DropdownMenu 상태

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                val targetNumber = goalNumber.toIntOrNull() ?: 21
                val numDice = diceCount.toIntOrNull() ?: 1
                val code = if (isPublic) "-1" else entryCode // 공개일 때 기본값 적용

                if (!isPublic && entryCode.isBlank()) {
                    Toast.makeText(context, "입장 코드를 입력하세요.", Toast.LENGTH_SHORT).show()
                    return@TextButton
                }

                onConfirm(targetNumber, numDice, isPublic, code, maxPlayers)
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
                        modifier = Modifier.clickable {
                            isPublic = false
                            entryCode = ""
                        }
                    )
                }

                // 비공개일 경우만 입장 코드 입력 필드를 활성화
                if (!isPublic) {
                    OutlinedTextField(
                        value = entryCode,
                        onValueChange = { entryCode = it },
                        label = { Text("입장 코드") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // DropdownMenu로 인원 선택
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(8.dp)
                ) {
                    Text("최대 인원: $maxPlayers 명", modifier = Modifier.weight(1f))
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Arrow Dropdown"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        (2..10).forEach { playerCount ->
                            DropdownMenuItem(
                                text = { Text("$playerCount 명") },
                                onClick = {
                                    maxPlayers = playerCount
                                    expanded = false
                                }
                            )
                        }
                    }
                }


            }
        }
    )
}
